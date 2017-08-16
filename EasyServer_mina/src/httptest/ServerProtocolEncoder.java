package httptest;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import java.nio.charset.Charset;

/**
 * 自定义的编码器
 * Created by 玄玉<http://jadyer.cn/> on 2013/07/07 14:43.
 */
public class ServerProtocolEncoder implements MessageEncoder<String> {
	
    @Override
    public void encode(IoSession session, String message, ProtocolEncoderOutput out) throws Exception {
        IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
        buffer.putString(message, Charset.forName("UTF-8").newEncoder());
        buffer.flip();
        out.write(buffer);
    }
}