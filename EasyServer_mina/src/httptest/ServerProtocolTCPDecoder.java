package httptest;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

/**
 * �Զ����TCPЭ�������
 * ----------------------------------------------------------------------------------------------
 * �յ����ݰ�ʱ���������Ȼ�ִ��decodable()������ͨ����ȡ�����жϵ�ǰ���ݰ��Ƿ�ɽ���decode
 * ��decodable()��������MessageDecoderResult.OKʱ�����Ż����decode()��������ʽdecode���ݰ�
 * ��decode()�������ж�ȡ������Ӱ�����ݰ��Ĵ�С��decode��Ҫ�ж�Э������Щ�Ѿ�decode�꣬��Щ��ûdecode
 * decode��ɺ�ͨ��ProtocolDecoderOutput.write()�����������MessageDecoderResult.OK��ʾdecode���
 * ----------------------------------------------------------------------------------------------
 * Created by ����<http://jadyer.cn/> on 2013/07/07 13:44.
 */
public class ServerProtocolTCPDecoder implements MessageDecoder {
    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        //��ʱʲô������
    }

    /**
     * �÷����൱��Ԥ��ȡ�������ж��Ƿ��ǿ��õĽ������������IoBuffer��ȡ����Ӱ�����ݰ��Ĵ�С
     * �÷���������IoBuffer�Ḵԭ�����Բ��ص��ĵ��ø÷���ʱ��position�Ѿ����ڻ�������ʼλ��
     */
    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        //TCP���ĸ�ʽԼ��Ϊǰ6���ֽڱ�ʾ�������峤�ȣ����Ȳ���6λʱ����
        //��7λ��ʼ����ҵ����룬ҵ�����̶�����Ϊ5����12λ��ʼ��ҵ������
        if(in.remaining() < 6){
            return MessageDecoderResult.NEED_DATA;
        }
        //���������ʱ�Ѱ�9000�˿ڣ���ר����������TCP����
        if(session.getLocalAddress().toString().contains(":9000")){
            byte[] messageLength = new byte[6];
            in.get(messageLength);
            if(in.limit() >= Integer.parseInt(JadyerUtil.getString(messageLength, "UTF-8"))){
                return MessageDecoderResult.OK;
            }else{
                return MessageDecoderResult.NEED_DATA;
            }
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
        token.setBusiType(Token.BUSI_TYPE_TCP);
        token.setBusiCode(fullMessage.substring(6, 11));
        token.setBusiMessage(fullMessage);
        token.setFullMessage(fullMessage);
        out.write(token);
        return MessageDecoderResult.OK;
    }
}
