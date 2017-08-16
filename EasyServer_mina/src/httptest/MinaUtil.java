package httptest;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * 使用Mina2.x发送报文的工具类
 * ----------------------------------------------------------------------------------------------
 * v1.5
 * v1.1-->编码器和解码器中的字符处理，升级为Mina2.x提供的<code>putString()</code>方法来处理
 * v1.2-->解码器采用CumulativeProtocolDecoder实现，以适应应答报文被拆分多次后发送Client的情况
 * v1.3-->修复BUG：请求报文有误时，Server可能返回非约定报文，此时会抛java.lang.NumberFormatException
 * v1.4-->增加全局异常捕获
 * v1.5-->由于本工具类的作用是同步的客户端，故取消IoHandler设置，但注意必须setUseReadOperation(true)
 * ----------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2012/10/03 12:42.
 */
public final class MinaUtil {
    private MinaUtil(){}

    /**
     * 发送TCP消息（当通信发生异常时，比如Fail to get session，它会返回"MINA_SERVER_ERROR"字符串）
     * @param message   待发送报文的中文字符串形式
     * @param ip        远程主机的IP地址
     * @param port      远程主机的端口号
     * @param charset   该方法与远程主机间通信报文为编码字符集（编码为byte[]发送到Server）
     * @return 远程主机响应报文的字符串形式
     */
    public static String sendTCPMessage(String message, String ip, int port, String charset){
        IoConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(1000);
        //同步的客户端，必须设置此项，其默认为false
        connector.getSessionConfig().setUseReadOperation(true);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
            new ClientProtocolEncoder(), new ClientProtocolDecode()
        ));
        //作为同步的客户端，可以不需要IoHandler，Mina会自动添加一个默认的IoHandler实现（即AbstractIoConnector）
        //connector.setHandler(this);
        IoSession session = null;
        Object respData = null;
        try{
            ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ip, port));
            //等待连接成功，相当于将异步执行转为同步执行
            connectFuture.awaitUninterruptibly();
            //获取连接成功后的会话对象
            session = connectFuture.getSession();
            session.write(message).awaitUninterruptibly();
            //由于上面已经设置setUseReadOperation(true)，所以IoSession.read()方法才可用
            //因其内部使用BlockingQueue，所以Server端使用它的时候可能导致内存泄漏，但客户端可以适当使用
            ReadFuture readFuture = session.read();
            //Wait until the message is received
            if(readFuture.awaitUninterruptibly(90, TimeUnit.SECONDS)){
                //Get the received message
                respData = readFuture.getMessage();
            }else{
                System.out.println("读取[/" + ip + ":" + port + "]超时");
            }
        }catch(Exception e){
            System.out.println("请求通信[/" + ip + ":" + port + "]时发生异常，堆栈轨迹如下");
            e.printStackTrace();
        }finally{
            if(null != session){
                //关闭IoSession，该操作是异步的，true为立即关闭，false为所有写操作都flush后关闭
                //这里仅仅是关闭了TCP的连接通道，并未关闭Client端程序
                session.close(true);
                //客户端发起连接时，会请求系统分配相关的文件句柄，而在连接失败时记得释放资源，否则时间长了会造成文件句柄泄露
                //当总的文件句柄数超过系统设置值时[ulimit -n]时，会抛出异常"java.io.IOException: Too many open files"
                //这会导致新连接无法创建，服务器直接挂掉
                session.getService().dispose();
            }
        }
        return null==respData ? "MINA_SERVER_ERROR" : respData.toString();
    }

    /**
     * 自定义编码器
     */
    private static class ClientProtocolEncoder extends ProtocolEncoderAdapter {
        @Override
        public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
            IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
            //二者等效：buffer.put(message.toString().getBytes("UTF-8"))
            buffer.putString(message.toString(), Charset.forName("UTF-8").newEncoder());
            buffer.flip();
            out.write(buffer);
        }
    }

    /**
     * 自定义解码器（样例报文：000064100030010000120121101210419100000000000028`18622233125`10`）
     */
    private static class ClientProtocolDecode extends CumulativeProtocolDecoder {
        //注意这里使用了Mina自带的AttributeKey类来定义保存在IoSession中对象的键值，其可有效防止键值重复
        //通过查询AttributeKey类源码发现，它的构造方法采用的是"类名+键名+AttributeKey的hashCode"的方式
        private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");
        private Context getContext(IoSession session){
            Context context = (Context)session.getAttribute(CONTEXT);
            if(null == context){
                context = new Context();
                session.setAttribute(CONTEXT, context);
            }
            return context;
        }
        @Override
        protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
            Context ctx = this.getContext(session);
            IoBuffer buffer = ctx.innerBuffer;
            int messageCount = ctx.getMessageCount();
            //判断position和limit之间是否有元素
            while(in.hasRemaining()){
                //get()读取buffer的position的字节，然后position+1
                buffer.put(in.get());
                //约定：报文的前6个字符串表示报文总长度，不足6位则左侧补0
                if(messageCount++ == 5){
                    //Set limit=position and position=0 and mark=-1
                    buffer.flip();
                    //当Server的响应报文中含0x00时，Mina2.x的buffer.getString(fieldSize, decoder)方法会break
                    //该方法的处理细节，详见org.apache.mina.core.buffer.AbstractIoBuffer类的第1718行源码，其说明如下
                    //Reads a NUL-terminated string from this buffer using the specified decoder and returns it
                    //ctx.setMessageLength(Integer.parseInt(buffer.getString(6, decoder)));
                    byte[] messageLength = new byte[6];
                    buffer.get(messageLength);
                    try{
                        //请求报文有误时，Server可能返回非约定报文，此时会抛java.lang.NumberFormatException
                        ctx.setMessageLength(Integer.parseInt(new String(messageLength, "UTF-8")));
                    }catch(NumberFormatException e){
                        ctx.setMessageLength(in.limit());
                    }
                    //让两个IoBuffer的limit相等
                    buffer.limit(in.limit());
                }
            }
            ctx.setMessageCount(messageCount);
            if(ctx.getMessageLength() == buffer.position()){
                buffer.flip();
                byte[] message = new byte[buffer.limit()];
                buffer.get(message);
                out.write(new String(message, "UTF-8"));
                ctx.reset();
                return true;
            }else{
                return false;
            }
        }
        private class Context{
            private final IoBuffer innerBuffer; //用于累积数据的IoBuffer
            private int messageCount;           //记录已读取的报文字节数
            private int messageLength;          //记录已读取的报文头标识的报文长度
            Context(){
                innerBuffer = IoBuffer.allocate(100).setAutoExpand(true);
            }
            int getMessageCount() {
                return messageCount;
            }
            void setMessageCount(int messageCount) {
                this.messageCount = messageCount;
            }
            int getMessageLength() {
                return messageLength;
            }
            void setMessageLength(int messageLength) {
                this.messageLength = messageLength;
            }
            void reset(){
                //Set limit=capacity and position=0 and mark=-1
                this.innerBuffer.clear();
                this.messageCount = 0;
                this.messageLength = 0;
            }
        }
    }
}
