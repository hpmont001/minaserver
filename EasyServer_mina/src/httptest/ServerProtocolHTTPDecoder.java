package httptest;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.mingrisoft.IotServer;

/**
 * �Զ����HTTPЭ�������
 * Created by ����<http://jadyer.cn/> on 2013/07/07 13:44.
 */
public class ServerProtocolHTTPDecoder implements MessageDecoder {
    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        //��ʱʲô������
    }

    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if(in.remaining() < 5){
            return MessageDecoderResult.NEED_DATA;
        }
        //���������ʱ�Ѱ�8000�˿ڣ���ר����������HTTP����
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
            //�Ȼ�ȡ��������ͷ�е�Content-Length
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
            //�ر�˵������ʱ�����屾Ӧ���ǿյģ���Content-Length=0���������ų��Է�ƫƫ�ڱ�������Ҳ���˲���
            //�ر�˵������������Ĵ����ֶ���busiMessage=����URL�еĲ����� + "`" + �������еĲ�������������ڱ�����Ļ���
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
     * У��HTTP�������Ƿ����������գ�Ŀǰ������GET��POST����
     * ����HTTP������������ģ��ɲο�http://jadyer.cn/2012/11/22/linux-crlf/
     * @param in װ��HTTP�����ĵ�IoBuffer
     */
    private boolean isComplete(IoBuffer in){
        //�Ȼ�ȡHTTP�����ԭʼ����
        byte[] messages = new byte[in.limit()];
        in.get(messages);
        String message = JadyerUtil.getString(messages, "UTF-8");
        //����GET����
        if(message.startsWith("GET")){
            return message.endsWith("\r\n\r\n");
        }
        //����POST����
        if(message.startsWith("POST")){
            if(message.contains("Content-Length:")){
                //ȡContent-Length����ַ���
                String msgLenFlag = message.substring(message.indexOf("Content-Length:") + 15);
                if(msgLenFlag.contains("\r\n")){
                    //ȡContent-Lengthֵ
                    int contentLength = Integer.parseInt(msgLenFlag.substring(0, msgLenFlag.indexOf("\r\n")).trim());
                    if(contentLength == 0){
                        return true;
                    }else if(contentLength > 0){
                        //ȡHTTP_POST��������
                        String messageBody = message.split("\r\n\r\n")[1];
                        if(contentLength == JadyerUtil.getBytes(messageBody, "UTF-8").length){
                            return true;
                        }
                    }
                }
            }
        }
        //������GET��POST����
        return false;
    }
}
