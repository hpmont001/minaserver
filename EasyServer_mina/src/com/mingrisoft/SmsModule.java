//package com.hpmont.bean;
package com.mingrisoft;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

/*
 * 浠ヤ笅閲囩敤浜戠墖缃戞彁渚涚殑API鎺ュ彛瀹炵幇鐨勫彂鐭俊鍔熻兘
 * */

public class SmsModule {
	//鏌ヨ处鎴蜂俊鎭殑http鍦板潃
    //private static String URI_GET_USER_INFO = "https://sms.yunpian.com/v2/user/get.json";

    //鏅鸿兘鍖归厤妯＄増鍙戦�佹帴鍙ｇ殑http鍦板潃(鍗曟潯鍙戦��)
    private static String URI_SEND_SMS = "https://sms.yunpian.com/v2/sms/single_send.json";

    //妯℃澘鍙戦�佹帴鍙ｇ殑http鍦板潃
    //private static String URI_TPL_SEND_SMS = "https://sms.yunpian.com/v2/sms/tpl_single_send.json";
    
    //鍙戦�佽闊抽獙璇佺爜鎺ュ彛鐨刪ttp鍦板潃
    //private static String URI_SEND_VOICE = "https://voice.yunpian.com/v2/voice/send.json";//涓嶉渶瑕�

    //缂栫爜鏍煎紡銆傚彂閫佺紪鐮佹牸寮忕粺涓�鐢║TF-8
    private static String ENCODING = "UTF-8";
    
    //apiKey銆傜洰鍓嶄娇鐢ㄧ殑鏄垜鐨勬祴璇曡处鍙�
    //private static String APIKEY="e38783d5fb5e69ab050a4f19c4434cdc";//璐︽埛锛歾yc992@126.com
    private static String APIKEY="552ba66ecdc4654a59bea6ce0b36711f";//璐︽埛锛歾hangyongchao@hpmont.com 瀵嗙爜锛歨pmontking
    
    /**
	 *缁欐寚瀹氱殑鎵嬫満鍙峰彂閫佺煭娑堟伅
	 * @param 
	 * @return 鍏充簬鐢ㄦ埛浠ｇ爜銆佷骇鍝佸瀷鍙枫�佷骇鍝佷俊鎭�佷环鏍笺�佹椂闂淬�佸娉ㄧ殑LIST
	 */
    public static String sendMessage(String tempPhoneNumber,String tempMsg)
    {
    	
    	Map<String, String> tempParams = new HashMap<String, String>();
    	
    	//娣诲姞apiKey
    	tempParams.put("apikey", APIKEY);
    	
    	//娣诲姞鐭俊娑堟伅鍐呭
    	tempParams.put("text", tempMsg);
    	
    	//娣诲姞鐭俊鍙风爜
    	tempParams.put("mobile", tempPhoneNumber);
    	
        return post(URI_SEND_SMS, tempParams);
    }
    
    /**
     * 鍩轰簬HttpClient 4.3鐨勯�氱敤POST鏂规硶
     *
     * @param url       鎻愪氦鐨刄RL
     * @param paramsMap 鎻愪氦<鍙傛暟锛屽��>Map
     * @return 鎻愪氦鍝嶅簲
     */
    public static String post(String url,Map<String, String> paramsMap)
    {
    	CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        CloseableHttpResponse response = null;
        try 
        {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> tempParamList = new ArrayList<NameValuePair>();
                Iterator<?> entries = paramsMap.entrySet().iterator();
                while (entries.hasNext()) {  
                	  
                    Map.Entry tempEntry = (Map.Entry) entries.next();  
                    NameValuePair tempPair = new BasicNameValuePair(tempEntry.getKey().toString(), tempEntry.getValue().toString());
                    tempParamList.add(tempPair);
                }
                
                method.setEntity(new UrlEncodedFormEntity(tempParamList, ENCODING));
            }
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) 
            {
                responseText = EntityUtils.toString(entity);
            }
        } 
        catch (Exception e) 
        {
        	IotServer.logger.error("", e);
        } 
        finally 
        {
            try 
            {
                response.close();
            } 
            catch (Exception e) 
            {
            	IotServer.logger.error("", e);
            }
        }
        return responseText;
    }
}
