package httptest;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.mingrisoft.IotServer;

/**
 * 自定义的HTTP协议解码器
 * Created by 玄玉<http://jadyer.cn/> on 2013/07/07 13:44.
 */
public class ServerProtocolHTTPDecoder implements MessageDecoder {
    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        //暂时什么都不做
    }

    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if(in.remaining() < 5){
            return MessageDecoderResult.NEED_DATA;
        }
        //服务端启动时已绑定8000端口，其专门用来处理HTTP请求
        if(session.getLocalAddress().toString().contains(":"+IotServer.httpPort)){
            return this.isComplete(in) ? MessageDecoderResult.OK : MessageDecoderResult.NEED_DATA;
        }else{
            return MessageDecoderResult.NOT_OK;
        }
    }

    @Override
    public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        byte[] message = new byte[in.limit()];
        in.get(message);
        String fullMessage = JadyerUtil.getString(message, "UTF-8");
        Token token = new Token();
        token.setBusiCharset("UTF-8");
        token.setBusiType(Token.BUSI_TYPE_HTTP);
        token.setFullMessage(fullMessage);
        if(fullMessage.startsWith("GET")){
            if(fullMessage.startsWith("GET / HTTP/1.1")){
                token.setBusiCode("/");
            }else if(fullMessage.startsWith("GET /favicon.ico HTTP/1.1")){
                token.setBusiCode("/favicon.ico");
            }else{
                //GET /login?aa=bb&cc=dd&ee=ff HTTP/1.1
                if(fullMessage.substring(4, fullMessage.indexOf("\r\n")).contains("?")){
                    token.setBusiCode(fullMessage.substring(4, fullMessage.indexOf("?")));
                    token.setBusiMessage(fullMessage.substring(fullMessage.indexOf("?")+1, fullMessage.indexOf("HTTP/1.1")-1));
                    //GET /login HTTP/1.1
                }else{
                    token.setBusiCode(fullMessage.substring(4, fullMessage.indexOf("HTTP")-1));
                }
            }
        }else if(fullMessage.startsWith("POST")){
            //先获取到请求报文头中的Content-Length
            int contentLength = 0;
            if(fullMessage.contains("Content-Length:")){
                String msgLenFlag = fullMessage.substring(fullMessage.indexOf("Content-Length:") + 15);
                if(msgLenFlag.contains("\r\n")){
                    contentLength = Integer.parseInt(msgLenFlag.substring(0, msgLenFlag.indexOf("\r\n")).trim());
                    if(contentLength > 0){
                        token.setBusiMessage(fullMessage.split("\r\n\r\n")[1]);
                    }
                }
            }
            //POST /login?aa=bb&cc=dd&ee=ff HTTP/1.1
            //特别说明：此时报文体本应该是空的，即Content-Length=0，但不能排除对方偏偏在报文体中也传了参数
            //特别说明：所以这里的处理手段是busiMessage=请求URL中的参数串 + "`" + 报文体中的参数串（如果存在报文体的话）
            if(fullMessage.substring(5, fullMessage.indexOf("\r\n")).contains("?")){
                token.setBusiCode(fullMessage.substring(5, fullMessage.indexOf("?")));
                String urlParam = fullMessage.substring(fullMessage.indexOf("?")+1, fullMessage.indexOf("HTTP/1.1")-1);
                if(contentLength > 0){
                    token.setBusiMessage(urlParam + "`" + fullMessage.split("\r\n\r\n")[1]);
                }else{
                    token.setBusiMessage(urlParam);
                }
                //POST /login HTTP/1.1
            }else{
                token.setBusiCode(fullMessage.substring(5, fullMessage.indexOf("HTTP/1.1")-1));
            }
        }
        out.write(token);
        return MessageDecoderResult.OK;
    }

    /**
     * 校验HTTP请求报文是否已完整接收（目前仅授理GET和POST请求）
     * 关于HTTP请求的样例报文，可参考http://jadyer.cn/2012/11/22/linux-crlf/
     * @param in 装载HTTP请求报文的IoBuffer
     */
    private boolean isComplete(IoBuffer in){
        //先获取HTTP请求的原始报文
        byte[] messages = new byte[in.limit()];
        in.get(messages);
        String message = JadyerUtil.getString(messages, "UTF-8");
        //授理GET请求
        if(message.startsWith("GET")){
            return message.endsWith("\r\n\r\n");
        }
        //授理POST请求
        if(message.startsWith("POST")){
            if(message.contains("Content-Length:")){
                //取Content-Length后的字符串
                String msgLenFlag = message.substring(message.indexOf("Content-Length:") + 15);
                if(msgLenFlag.contains("\r\n")){
                    //取Content-Length值
                    int contentLength = Integer.parseInt(msgLenFlag.substring(0, msgLenFlag.indexOf("\r\n")).trim());
                    if(contentLength == 0){
                        return true;
                    }else if(contentLength > 0){
                        //取HTTP_POST请求报文体
                        String messageBody = message.split("\r\n\r\n")[1];
                        if(contentLength == JadyerUtil.getBytes(messageBody, "UTF-8").length){
                            return true;
                        }
                    }
                }
            }
        }
        //仅授理GET和POST请求
        return false;
    }
}
