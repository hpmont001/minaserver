package httptest;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.DemuxingProtocolDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import com.mingrisoft.IotServer;

/**
 * 瑙ｇ爜鍣紙鏆傛椂鏍规嵁ismask == 1鏈夋帺鐮佹潵鍒ゆ柇鏄惁涓烘彙鎵嬫暟鎹級
 * 
 * <br>
 * 鏁版嵁浜や簰鍗忚璇存槑:<br>
 * 銆愮涓�涓瓧鑺傘�戞殏鏃舵病鐢紝涓嶈繘琛岃鏄庯紱 <br>
 * 銆愮浜屼釜瀛楄妭銆戠涓�浣嶅瓨鏀炬帺鐮侊紙1:鏈夋帺鐮侊紱0:鏃犳帺鐮侊級锛� <br>
 * 鍚�7浣嶈〃绀轰紶杈撶殑鍐呭闀垮害锛堢敱浜�7浣嶆渶澶氬彧鑳芥弿杩�127鎵�浠ヨ繖涓�间細浠ｈ〃涓夌鎯呭喌, <br>
 * 绗竴绉嶆槸娑堟伅鍐呭灏戜簬126瀛樺偍娑堟伅闀垮害, <br>
 * 绗簩绉嶆槸娑堟伅闀垮害澶т簬绛変簬126涓斿皯浜嶶INT16鐨勬儏鍐垫鍊间负126, <br>
 * 绗笁绉嶆槸娑堟伅闀垮害澶т簬UINT16鐨勬儏鍐典笅姝ゅ�间负127; <br>
 * 鍚庝袱绉嶆儏鍐电殑娑堟伅闀垮害瀛樺偍鍒扮揣闅忓悗闈㈢殑byte[],鍒嗗埆鏄疷INT16(2涓瓧鑺�)鍜孶INT64(4涓瓧鑺�)锛� <br>
 * 銆愮涓変釜瀛楄妭-绗叚涓瓧鑺� 鎴栫浜斾釜瀛楄妭-绗叓涓瓧鑺� 鎴栫涓冧釜瀛楄妭-绗崄涓�涓瓧鑺傦紙涓夌鎯呭喌锛夈�戞帺鐮佸唴瀹�<br>
 * 銆愮涓冧釜瀛楄妭鍙婁箣鍚庣殑瀛楄妭 鎴栫涔濅釜瀛楄妭鍙婁箣鍚庣殑瀛楄妭 鎴栫鍗佷簩涓瓧鑺傚強涔嬪悗鐨勫瓧鑺傦紙涓夌鎯呭喌锛夈�戜紶杈撶殑鐪熸鍐呭<br>
 * 
 * @author jian.cao
 * 
 */
//public class MinaDecoder extends DemuxingProtocolDecoder {
//	public static final byte MASK = 0x1;// 1000 0000
//	public static final byte HAS_EXTEND_DATA = 126;
//	public static final byte HAS_EXTEND_DATA_CONTINUE = 127;
//	public static final byte PAYLOADLEN = 0x7F;// 0111 1111
//
//	public MinaDecoder() {
//		addMessageDecoder(new BaseSocketBeanDecoder());
//	}
//
//	class BaseSocketBeanDecoder extends MessageDecoderAdapter {
//		public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
//			if (in.remaining() < 2) {
//				return NEED_DATA;
//			}
//			in.get();// 绗竴涓瓧鑺�
//			byte head2 = in.get();// 绗簩涓瓧鑺�
//			byte datalength = (byte) (head2 & PAYLOADLEN);// 寰楀埌绗簩涓瓧鑺傚悗涓冧綅鐨勫��
//			int length = 0;
//			if (datalength < HAS_EXTEND_DATA) {// 绗竴绉嶆槸娑堟伅鍐呭灏戜簬126瀛樺偍娑堟伅闀垮害
//				length = datalength;
//			} else if (datalength == HAS_EXTEND_DATA) {// 绗簩绉嶆槸娑堟伅闀垮害澶т簬绛変簬126涓斿皯浜嶶INT16鐨勬儏鍐垫鍊间负126
//				if (in.remaining() < 2) {
//					return NEED_DATA;
//				}
//				byte[] extended = new byte[2];
//				in.get(extended);
//				int shift = 0;
//				length = 0;
//				for (int i = extended.length - 1; i >= 0; i--) {
//					length = length + ((extended[i] & 0xFF) << shift);
//					shift += 8;
//				}
//			} else if (datalength == HAS_EXTEND_DATA_CONTINUE) {// 绗笁绉嶆槸娑堟伅闀垮害澶т簬UINT16鐨勬儏鍐典笅姝ゅ�间负127
//				if (in.remaining() < 4) {
//					return NEED_DATA;
//				}
//				byte[] extended = new byte[4];
//				in.get(extended);
//				int shift = 0;
//				length = 0;
//				for (int i = extended.length - 1; i >= 0; i--) {
//					length = length + ((extended[i] & 0xFF) << shift);
//					shift += 8;
//				}
//			}
//
//			int ismask = head2 >> 7 & MASK;// 寰楀埌绗簩涓瓧鑺傜涓�浣嶇殑鍊�
//			if (ismask == 1) {// 鏈夋帺鐮�
//				if (in.remaining() < 4 + length) {
//					return NEED_DATA;
//				} else {
//					return OK;
//				}
//			} else {// 鏃犳帺鐮�
//				if (in.remaining() < length) {
//					return NEED_DATA;
//				} else {
//					return OK;
//				}
//			}
//		}
//
//		public MessageDecoderResult decode(IoSession session, IoBuffer in,
//				ProtocolDecoderOutput out) throws Exception {
//			in.get();
//			byte head2 = in.get();
//			byte datalength = (byte) (head2 & PAYLOADLEN);
//			if (datalength < HAS_EXTEND_DATA) {
//			} else if (datalength == HAS_EXTEND_DATA) {
//				byte[] extended = new byte[2];
//				in.get(extended);
//			} else if (datalength == HAS_EXTEND_DATA_CONTINUE) {
//				byte[] extended = new byte[4];
//				in.get(extended);
//			}
//
//			int ismask = head2 >> 7 & MASK;
//			MinaBean message = new MinaBean();
//			byte[] date = null;
//			if (ismask == 1) {// 鏈夋帺鐮�
//				// 鑾峰彇鎺╃爜
//				byte[] mask = new byte[4];
//				in.get(mask);
//
//				date = new byte[in.remaining()];
//				in.get(date);
//				for (int i = 0; i < date.length; i++) {
//					// 鏁版嵁杩涜寮傛垨杩愮畻
//					date[i] = (byte) (date[i] ^ mask[i % 4]);
//				}
//			} else {
//				date = new byte[in.remaining()];
//				in.get(date);
//				message.setWebAccept(true);
//			}
//			message.setContent(new String(date, "UTF-8"));
//			out.write(message);
//			return OK;
//		}
//	}
//}
public class MinaDecoder extends MessageDecoderAdapter {
	public static final byte MASK = 0x1;// 1000 0000
	public static final byte HAS_EXTEND_DATA = 126;
	public static final byte HAS_EXTEND_DATA_CONTINUE = 127;
	public static final byte PAYLOADLEN = 0x7F;// 0111 1111
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {

        if(session.getLocalAddress().toString().contains(":"+IotServer.CICPort)
        		||session.getLocalAddress().toString().contains(":"+IotServer.PCPort)){
            return MessageDecoderResult.NOT_OK;
        }else{
        }
		
		if (in.remaining() < 2) {
			return NEED_DATA;
		}
		in.get();// 绗竴涓瓧鑺�
		byte head2 = in.get();// 绗簩涓瓧鑺�
		byte datalength = (byte) (head2 & PAYLOADLEN);// 寰楀埌绗簩涓瓧鑺傚悗涓冧綅鐨勫��
		int length = 0;
		if (datalength < HAS_EXTEND_DATA) {// 绗竴绉嶆槸娑堟伅鍐呭灏戜簬126瀛樺偍娑堟伅闀垮害
			length = datalength;
		} else if (datalength == HAS_EXTEND_DATA) {// 绗簩绉嶆槸娑堟伅闀垮害澶т簬绛変簬126涓斿皯浜嶶INT16鐨勬儏鍐垫鍊间负126
			if (in.remaining() < 2) {
				return NEED_DATA;
			}
			byte[] extended = new byte[2];
			in.get(extended);
			int shift = 0;
			length = 0;
			for (int i = extended.length - 1; i >= 0; i--) {
				length = length + ((extended[i] & 0xFF) << shift);
				shift += 8;
			}
		} else if (datalength == HAS_EXTEND_DATA_CONTINUE) {// 绗笁绉嶆槸娑堟伅闀垮害澶т簬UINT16鐨勬儏鍐典笅姝ゅ�间负127
			if (in.remaining() < 4) {
				return NEED_DATA;
			}
			byte[] extended = new byte[4];
			in.get(extended);
			int shift = 0;
			length = 0;
			for (int i = extended.length - 1; i >= 0; i--) {
				length = length + ((extended[i] & 0xFF) << shift);
				shift += 8;
			}
		}

		int ismask = head2 >> 7 & MASK;// 寰楀埌绗簩涓瓧鑺傜涓�浣嶇殑鍊�
		if (ismask == 1) {// 鏈夋帺鐮�
			if (in.remaining() < 4 + length) {
				return NEED_DATA;
			} else {
				return OK;
			}
		} else {// 鏃犳帺鐮�
			if (in.remaining() < length) {
				return NEED_DATA;
			} else {
				return OK;
			}
		}
	}

	public MessageDecoderResult decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		MinaBean message = new MinaBean();
		byte head1 = in.get();// 第一个字节
		if((head1 & 0x0f) == 0x08){

			message.setisWebClose(true);
		}
		byte head2 = in.get();
		byte datalength = (byte) (head2 & PAYLOADLEN);
		if (datalength < HAS_EXTEND_DATA) {
		} else if (datalength == HAS_EXTEND_DATA) {
			byte[] extended = new byte[2];
			in.get(extended);
		} else if (datalength == HAS_EXTEND_DATA_CONTINUE) {
			byte[] extended = new byte[4];
			in.get(extended);
		}

		int ismask = head2 >> 7 & MASK;
		byte[] date = null;
		if (ismask == 1) {// 鏈夋帺鐮�
			// 鑾峰彇鎺╃爜
			byte[] mask = new byte[4];
			in.get(mask);

			date = new byte[in.remaining()];
			in.get(date);
			for (int i = 0; i < date.length; i++) {
				// 鏁版嵁杩涜寮傛垨杩愮畻
				date[i] = (byte) (date[i] ^ mask[i % 4]);
			}
		} else {
			date = new byte[in.remaining()];
			in.get(date);
			message.setWebAccept(true);
		}
		message.setContent(new String(date, "UTF-8"));
		out.write(message);
		return OK;
	}
}
