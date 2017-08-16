package httptest;

import java.io.UnsupportedEncodingException;

public class JadyerUtil {
    private JadyerUtil(){}

    /**
     * �ж�������ַ��������Ƿ�Ϊ��
     * @return boolean ���򷵻�true���ǿ���flase
     */
    public static boolean isEmpty(String input) {
        return null==input || 0==input.length() || 0==input.replaceAll("\\s", "").length();
    }

    /**
     * �ж�������ֽ������Ƿ�Ϊ��
     * @return boolean ���򷵻�true���ǿ���flase
     */
    public static boolean isEmpty(byte[] bytes){
        return null==bytes || 0==bytes.length;
    }

    /**
     * �ֽ�����תΪ�ַ���
     * @see ���ϵͳ��֧���������<code>charset</code>�ַ���������ϵͳĬ���ַ�������ת��
     */
    public static String getString(byte[] data, String charset){
        if(isEmpty(data)){
            return "";
        }
        if(isEmpty(charset)){
            return new String(data);
        }
        try {
            return new String(data, charset);
        } catch (UnsupportedEncodingException e) {
            System.out.println("��byte����[" + data + "]תΪStringʱ�����쳣��ϵͳ��֧�ָ��ַ���[" + charset + "]");
            return new String(data);
        }
    }

    /**
     * �ַ���תΪ�ֽ�����
     * @see ���ϵͳ��֧���������<code>charset</code>�ַ���������ϵͳĬ���ַ�������ת��
     */
    public static byte[] getBytes(String data, String charset){
        data = (data==null ? "" : data);
        if(isEmpty(charset)){
            return data.getBytes();
        }
        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            System.out.println("���ַ���[" + data + "]תΪbyte[]ʱ�����쳣��ϵͳ��֧�ָ��ַ���[" + charset + "]");
            return data.getBytes();
        }
    }

    /**
     * ͨ��ASCII�뽫ʮ���Ƶ��ֽ������ʽ��Ϊʮ�������ַ���
     * @see �÷����Ὣ�ֽ������е������ֽھ���ʽ��Ϊ�ַ���
     */
    public static String buildHexStringWithASCII(byte[] data){
        return buildHexStringWithASCII(data, 0, data.length);
    }

    /**
     * ͨ��ASCII�뽫ʮ���Ƶ��ֽ������ʽ��Ϊʮ�������ַ���
     * @see �÷����������ַ�����ʮ�����ƴ�ӡ����ӡʱ���Ϊʮ��������ֵ���Ҳ�Ϊ��Ӧ���ַ���ԭ��
     * @see �ڹ����Ҳ���ַ���ԭ��ʱ���÷����ڲ�ʹ�õ���ƽ̨��Ĭ���ַ�����������byte[]����
     * @see �÷����ڽ��ֽ�תΪʮ������ʱ��Ĭ��ʹ�õ���<code>java.util.Locale.getDefault()</code>
     * @see ���String.format(String, Object...)������new String(byte[], int, int)���췽��
     * @param data   ʮ���Ƶ��ֽ�����
     * @param offset �����±꣬��Ǵ�����ĵڼ����ֽڿ�ʼ��ʽ�����
     * @param length ��ʽ���ȣ��䲻�ô������鳤�ȣ������׳�java.lang.ArrayIndexOutOfBoundsException
     * @return ��ʽ�����ʮ�������ַ���
     */
    private static String buildHexStringWithASCII(byte[] data, int offset, int length){
        int end = offset + length;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        sb.append("\r\n------------------------------------------------------------------------");
        boolean chineseCutFlag = false;
        for(int i=offset; i<end; i+=16){
            //X��x��ʾ�������ʽ��Ϊʮ����������
            sb.append(String.format("\r\n%04X: ", i-offset));
            sb2.setLength(0);
            for(int j=i; j<i+16; j++){
                if(j < end){
                    byte b = data[j];
                    //ENG ASCII
                    if(b >= 0){
                        sb.append(String.format("%02X ", b));
                        //���ɼ��ַ�
                        if(b<32 || b>126){
                            sb2.append(" ");
                        }else{
                            sb2.append((char)b);
                        }
                    //CHA ASCII
                    }else{
                        //����ǰ����ֽ�
                        if(j == i+15){
                            sb.append(String.format("%02X ", data[j]));
                            chineseCutFlag = true;
                            String s = new String(data, j, 2);
                            sb2.append(s);
                        //�����ֽ�
                        }else if(j == i&&chineseCutFlag){
                            sb.append(String.format("%02X ", data[j]));
                            chineseCutFlag = false;
                            String s = new String(data, j, 1);
                            sb2.append(s);
                        }else{
                            sb.append(String.format("%02X %02X ", data[j], data[j + 1]));
                            String s = new String(data, j, 2);
                            sb2.append(s);
                            j++;
                        }
                    }
                }else{
                    sb.append("   ");
                }
            }
            sb.append("| ");
            sb.append(sb2.toString());
        }
        sb.append("\r\n------------------------------------------------------------------------");
        return sb.toString();
    }
}

