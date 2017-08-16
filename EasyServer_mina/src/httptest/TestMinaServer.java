package httptest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
//import com.msxf.open.mpp.sdk.util.HttpUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ����MinaServer
 * �����õ���HttpUtil�����https://github.com/jadyer/seed/blob/master/seed-comm/src/main/java/com/jadyer/seed/comm/util/HttpUtil.java
 * Created by ����<http://jadyer.cn/> on 2013/07/09 19:59.
 */
public class TestMinaServer {
    @Test
    public void testTcp(){
        String message = "00004710005101101992012092222400000201307071605";
        String respData = MinaUtil.sendTCPMessage(message, "127.0.0.1", 9000, "UTF-8");
        Assert.assertEquals("00003099999999`20130707144028`", respData);
    }

    /**
     * Ҳ��ֱ�����������http://127.0.0.1:8000/login�Լ�http://127.0.0.1:8000/login?a=b&c=d&e=f
     * ֻҪ�����ҳ����ʾ"��¼�ɹ�",����ʾHTTP_GET����ͨ��
     */
    @Test
    public void testHttpGet(){
        try {
        //�Ȳ��Դ�������GET����
        CloseableHttpClient httpClient = HttpClients.createDefault();
            //��get��������http����
            HttpGet get = new HttpGet("http://127.0.0.1:8000/login?a=b&c=d&e=f");
            System.out.println("ִ��get����:...."+get.getURI());
            CloseableHttpResponse httpResponse = null;
            //����get����
            String    respData11 = httpClient.execute(get).toString();
        //String respData11 = HttpUtil.get("http://127.0.0.1:8000/login?a=b&c=d&e=f");
        Assert.assertEquals("��¼�ɹ�", respData11);
        //�ٲ��Բ���������GET����
        get = new HttpGet("http://127.0.0.1:8000/login");
        System.out.println("ִ��get����:...."+get.getURI());
        
        String    respData22 = httpClient.execute(get).toString();
       // String respData22 = HttpUtil.get("http://127.0.0.1:8000/login");
        Assert.assertEquals("��¼�ɹ�", respData22);
        }
        catch(Exception e){
        	
        }
        finally{
        	
        }
    }

    @Test
    public void testHttpPost(){
        //�Ȳ��Դ��������POST����(����������ģ����ύ)
        String reqURL = "http://127.0.0.1:8000/login";
       // Map<String, String> params = new HashMap<>();
        //params.put("username", "Jadyer");
        //params.put("password", "xuanyu");
        String url="http://XXX..";
      //POST��URL
		try {
        HttpPost httppost=new HttpPost(reqURL);
        //����HttpPost����
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        //����һ��NameValuePair���飬���ڴ洢�����͵Ĳ���
        params.add(new BasicNameValuePair("password", "xuanyu"));
        params.add(new BasicNameValuePair("password", "xuanyu"));
        //params.put("username", "Jadyer");
        //params.put("password", "xuanyu");
        //��Ӳ���
        httppost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
        //���ñ���
        HttpResponse response=new DefaultHttpClient().execute(httppost);
        //����Post,������һ��HttpResponse����
        if(response.getStatusLine().getStatusCode()==200){//���״̬��Ϊ200,������������
        String respData11=EntityUtils.toString(response.getEntity());
        
        
        //String respData11 = HttpUtil.post(reqURL, params);
        Assert.assertEquals("��¼�ɹ�", respData11);
        //�ٲ��Բ����������POST���󣨲���������
        

        HttpResponse respData22=new DefaultHttpClient().execute(new HttpPost(reqURL));
        //String respData22 = HttpUtil.post(reqURL, new HashMap<String, String>());
        Assert.assertEquals("��¼�ɹ�", respData22);
        //������һ����������������������壬���������ַ�ϴ��в�����POST���󣨽�������ƽ̨������ô�ɵģ�
        reqURL = "http://127.0.0.1:8000/login?username=Jadyer&password=xuanyu&aa=bb&cc=dd";
        
        

        HttpResponse respData33;
			respData33 = new DefaultHttpClient().execute(new HttpPost(reqURL));
		
        //String respData33 = HttpUtil.post(reqURL, new HashMap<String, String>());
        Assert.assertEquals("��¼�ɹ�", respData33);
    }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
