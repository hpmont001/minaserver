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
 * ʹ��Mina2.x���ͱ��ĵĹ�����
 * ----------------------------------------------------------------------------------------------
 * v1.5
 * v1.1-->�������ͽ������е��ַ���������ΪMina2.x�ṩ��<code>putString()</code>����������
 * v1.2-->����������CumulativeProtocolDecoderʵ�֣�����ӦӦ���ı���ֶ�κ���Client�����
 * v1.3-->�޸�BUG������������ʱ��Server���ܷ��ط�Լ�����ģ���ʱ����java.lang.NumberFormatException
 * v1.4-->����ȫ���쳣����
 * v1.5-->���ڱ��������������ͬ���Ŀͻ��ˣ���ȡ��IoHandler���ã���ע�����setUseReadOperation(true)
 * ----------------------------------------------------------------------------------------------
 * Created by ����<http://jadyer.cn/> on 2012/10/03 12:42.
 */
public final class MinaUtil {
    private MinaUtil(){}

    /**
     * ����TCP��Ϣ����ͨ�ŷ����쳣ʱ������Fail to get session�����᷵��"MINA_SERVER_ERROR"�ַ�����
     * @param message   �����ͱ��ĵ������ַ�����ʽ
     * @param ip        Զ��������IP��ַ
     * @param port      Զ�������Ķ˿ں�
     * @param charset   �÷�����Զ��������ͨ�ű���Ϊ�����ַ���������Ϊbyte[]���͵�Server��
     * @return Զ��������Ӧ���ĵ��ַ�����ʽ
     */
    public static String sendTCPMessage(String message, String ip, int port, String charset){
        IoConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(1000);
        //ͬ���Ŀͻ��ˣ��������ô����Ĭ��Ϊfalse
        connector.getSessionConfig().setUseReadOperation(true);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(
            new ClientProtocolEncoder(), new ClientProtocolDecode()
        ));
        //��Ϊͬ���Ŀͻ��ˣ����Բ���ҪIoHandler��Mina���Զ����һ��Ĭ�ϵ�IoHandlerʵ�֣���AbstractIoConnector��
        //connector.setHandler(this);
        IoSession session = null;
        Object respData = null;
        try{
            ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ip, port));
            //�ȴ����ӳɹ����൱�ڽ��첽ִ��תΪͬ��ִ��
            connectFuture.awaitUninterruptibly();
            //��ȡ���ӳɹ���ĻỰ����
            session = connectFuture.getSession();
            session.write(message).awaitUninterruptibly();
            //���������Ѿ�����setUseReadOperation(true)������IoSession.read()�����ſ���
            //�����ڲ�ʹ��BlockingQueue������Server��ʹ������ʱ����ܵ����ڴ�й©�����ͻ��˿����ʵ�ʹ��
            ReadFuture readFuture = session.read();
            //Wait until the message is received
            if(readFuture.awaitUninterruptibly(90, TimeUnit.SECONDS)){
                //Get the received message
                respData = readFuture.getMessage();
            }else{
                System.out.println("��ȡ[/" + ip + ":" + port + "]��ʱ");
            }
        }catch(Exception e){
            System.out.println("����ͨ��[/" + ip + ":" + port + "]ʱ�����쳣����ջ�켣����");
            e.printStackTrace();
        }finally{
            if(null != session){
                //�ر�IoSession���ò������첽�ģ�trueΪ�����رգ�falseΪ����д������flush��ر�
                //��������ǹر���TCP������ͨ������δ�ر�Client�˳���
                session.close(true);
                //�ͻ��˷�������ʱ��������ϵͳ������ص��ļ��������������ʧ��ʱ�ǵ��ͷ���Դ������ʱ�䳤�˻�����ļ����й¶
                //���ܵ��ļ����������ϵͳ����ֵʱ[ulimit -n]ʱ�����׳��쳣"java.io.IOException: Too many open files"
                //��ᵼ���������޷�������������ֱ�ӹҵ�
                session.getService().dispose();
            }
        }
        return null==respData ? "MINA_SERVER_ERROR" : respData.toString();
    }

    /**
     * �Զ��������
     */
    private static class ClientProtocolEncoder extends ProtocolEncoderAdapter {
        @Override
        public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
            IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
            //���ߵ�Ч��buffer.put(message.toString().getBytes("UTF-8"))
            buffer.putString(message.toString(), Charset.forName("UTF-8").newEncoder());
            buffer.flip();
            out.write(buffer);
        }
    }

    /**
     * �Զ�����������������ģ�000064100030010000120121101210419100000000000028`18622233125`10`��
     */
    private static class ClientProtocolDecode extends CumulativeProtocolDecoder {
        //ע������ʹ����Mina�Դ���AttributeKey�������屣����IoSession�ж���ļ�ֵ�������Ч��ֹ��ֵ�ظ�
        //ͨ����ѯAttributeKey��Դ�뷢�֣����Ĺ��췽�����õ���"����+����+AttributeKey��hashCode"�ķ�ʽ
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
            //�ж�position��limit֮���Ƿ���Ԫ��
            while(in.hasRemaining()){
                //get()��ȡbuffer��position���ֽڣ�Ȼ��position+1
                buffer.put(in.get());
                //Լ�������ĵ�ǰ6���ַ�����ʾ�����ܳ��ȣ�����6λ����ಹ0
                if(messageCount++ == 5){
                    //Set limit=position and position=0 and mark=-1
                    buffer.flip();
                    //��Server����Ӧ�����к�0x00ʱ��Mina2.x��buffer.getString(fieldSize, decoder)������break
                    //�÷����Ĵ���ϸ�ڣ����org.apache.mina.core.buffer.AbstractIoBuffer��ĵ�1718��Դ�룬��˵������
                    //Reads a NUL-terminated string from this buffer using the specified decoder and returns it
                    //ctx.setMessageLength(Integer.parseInt(buffer.getString(6, decoder)));
                    byte[] messageLength = new byte[6];
                    buffer.get(messageLength);
                    try{
                        //����������ʱ��Server���ܷ��ط�Լ�����ģ���ʱ����java.lang.NumberFormatException
                        ctx.setMessageLength(Integer.parseInt(new String(messageLength, "UTF-8")));
                    }catch(NumberFormatException e){
                        ctx.setMessageLength(in.limit());
                    }
                    //������IoBuffer��limit���
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
            private final IoBuffer innerBuffer; //�����ۻ����ݵ�IoBuffer
            private int messageCount;           //��¼�Ѷ�ȡ�ı����ֽ���
            private int messageLength;          //��¼�Ѷ�ȡ�ı���ͷ��ʶ�ı��ĳ���
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
