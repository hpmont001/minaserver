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
 * 测试MinaServer
 * 这里用到的HttpUtil，详见https://github.com/jadyer/seed/blob/master/seed-comm/src/main/java/com/jadyer/seed/comm/util/HttpUtil.java
 * Created by 玄玉<http://jadyer.cn/> on 2013/07/09 19:59.
 */
public class TestMinaServer {
    @Test
    public void testTcp(){
        String message = "00004710005101101992012092222400000201307071605";
        String respData = MinaUtil.sendTCPMessage(message, "127.0.0.1", 9000, "UTF-8");
        Assert.assertEquals("00003099999999`20130707144028`", respData);
    }

    /**
     * 也可直接浏览器访问http://127.0.0.1:8000/login以及http://127.0.0.1:8000/login?a=b&c=d&e=f
     * 只要浏览器页面显示"登录成功",即表示HTTP_GET测试通过
     */
    @Test
    public void testHttpGet(){
        try {
        //先测试带参数的GET请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
            //用get方法发送http请求
            HttpGet get = new HttpGet("http://127.0.0.1:8000/login?a=b&c=d&e=f");
            System.out.println("执行get请求:...."+get.getURI());
            CloseableHttpResponse httpResponse = null;
            //发送get请求
            String    respData11 = httpClient.execute(get).toString();
        //String respData11 = HttpUtil.get("http://127.0.0.1:8000/login?a=b&c=d&e=f");
        Assert.assertEquals("登录成功", respData11);
        //再测试不带参数的GET请求
        get = new HttpGet("http://127.0.0.1:8000/login");
        System.out.println("执行get请求:...."+get.getURI());
        
        String    respData22 = httpClient.execute(get).toString();
       // String respData22 = HttpUtil.get("http://127.0.0.1:8000/login");
        Assert.assertEquals("登录成功", respData22);
        }
        catch(Exception e){
        	
        }
        finally{
        	
        }
    }

    @Test
    public void testHttpPost(){
        //先测试带报文体的POST请求(即带参数，模拟表单提交)
        String reqURL = "http://127.0.0.1:8000/login";
       // Map<String, String> params = new HashMap<>();
        //params.put("username", "Jadyer");
        //params.put("password", "xuanyu");
        String url="http://XXX..";
      //POST的URL
		try {
        HttpPost httppost=new HttpPost(reqURL);
        //建立HttpPost对象
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        //建立一个NameValuePair数组，用于存储欲传送的参数
        params.add(new BasicNameValuePair("password", "xuanyu"));
        params.add(new BasicNameValuePair("password", "xuanyu"));
        //params.put("username", "Jadyer");
        //params.put("password", "xuanyu");
        //添加参数
        httppost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
        //设置编码
        HttpResponse response=new DefaultHttpClient().execute(httppost);
        //发送Post,并返回一个HttpResponse对象
        if(response.getStatusLine().getStatusCode()==200){//如果状态码为200,就是正常返回
        String respData11=EntityUtils.toString(response.getEntity());
        
        
        //String respData11 = HttpUtil.post(reqURL, params);
        Assert.assertEquals("登录成功", respData11);
        //再测试不带报文体的POST请求（不带参数）
        

        HttpResponse respData22=new DefaultHttpClient().execute(new HttpPost(reqURL));
        //String respData22 = HttpUtil.post(reqURL, new HashMap<String, String>());
        Assert.assertEquals("登录成功", respData22);
        //最后测试一下特殊情况，即不带报文体，但在请求地址上带有参数的POST请求（建行外联平台就是这么干的）
        reqURL = "http://127.0.0.1:8000/login?username=Jadyer&password=xuanyu&aa=bb&cc=dd";
        
        

        HttpResponse respData33;
			respData33 = new DefaultHttpClient().execute(new HttpPost(reqURL));
		
        //String respData33 = HttpUtil.post(reqURL, new HashMap<String, String>());
        Assert.assertEquals("登录成功", respData33);
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
