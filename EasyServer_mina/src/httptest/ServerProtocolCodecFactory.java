package httptest;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import com.mingrisoft.CicEncoder;

//import httptest.MinaEncoder.BaseSocketBeanEncoder;

public class ServerProtocolCodecFactory extends DemuxingProtocolCodecFactory {
    public ServerProtocolCodecFactory(){
        super.addMessageEncoder(Object.class, CicEncoder.class);
        super.addMessageEncoder(String.class, ServerProtocolEncoder.class);
        super.addMessageEncoder(MinaBean.class, new MinaEncoder());
        super.addMessageDecoder(ServerProtocolTCPDecoder.class);
        super.addMessageDecoder(ServerProtocolHTTPDecoder.class);
        super.addMessageDecoder(com.mingrisoft.CicDecoder.class);
        super.addMessageDecoder(MinaDecoder.class);
    }
}
