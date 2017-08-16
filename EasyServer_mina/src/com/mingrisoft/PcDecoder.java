package com.mingrisoft;


import java.nio.charset.CharacterCodingException;
//import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
public class PcDecoder implements ProtocolDecoder {
	//private Charset charset = Charset.forName("utf-8");
    // 定义常量值，作为每个IoSession中保存解码内容的key值
	private static String CONTEXT = PcDecoder.class.getName()
			+ ".context";

    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        if(in.remaining() < 5){
            return MessageDecoderResult.NEED_DATA;
        }
        //服务端启动时已绑定8000端口，其专门用来处理HTTP请求
        if(session.getLocalAddress().toString().contains(":8090")){
            return MessageDecoderResult.OK;
        }else{
            return MessageDecoderResult.NOT_OK;
        }
    }
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		Context ctx = getContext(session);
		decodeAuto(ctx, in, out);
	}

	private Context getContext(IoSession session) {
		Context ctx = (Context) session.getAttribute(CONTEXT);
		if (ctx == null) {
			ctx = new Context();
			session.setAttribute(CONTEXT, ctx);
		}
		return ctx;
	}

	private void decodeAuto(Context ctx, IoBuffer in, ProtocolDecoderOutput out)
			throws CharacterCodingException {

		while (in.hasRemaining()) {
			byte b = in.get();
			ctx.getBuf().put(b);

		}
		IoBuffer t_buf = ctx.getBuf();
//		int RxlengthH = in.limit()/256;
//		int RxlengthL = in.limit()%256;
//		ctx.getBuf().put(498,(byte)RxlengthH);
//		ctx.getBuf().put(499,(byte)RxlengthL);
		t_buf.flip();
		try {
			out.write(t_buf);//.getString(charset.newDecoder()));
		} finally {
			t_buf.clear();
		}
	}

	public void dispose(IoSession session) throws Exception {
		Context ctx = (Context) session.getAttribute(CONTEXT);
		if (ctx != null) {
			session.removeAttribute(CONTEXT);
		}
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out)
			throws Exception {
	}

	private class Context {
		private IoBuffer buf;

		public Context() {
			buf = IoBuffer.allocate(18).setAutoExpand(true);
		}

		public IoBuffer getBuf() {
			return buf;
		}
	}
}

