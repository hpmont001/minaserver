package com.mingrisoft;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import httptest.JadyerUtil;
import httptest.Token;
import httptest.MinaBean;
import httptest.WebSocketUtil;
//import httptest.IUpdateViewFactory;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;

//import com.mingrisoft.IotServer.ServerThread2Cic;


import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class MyServerHandler extends IoHandlerAdapter {
	public static Logger logger = Logger.getLogger(MyServerHandler.class);
	public static HashMap<String, CicConnect> map_Secket_Cic = new HashMap<>();// 用于存储连接到服务器的用户和客户端套接字对象

	public static HashMap<String, PcConnect> map_Socket_Pc = new HashMap<>();// 用于存储连接到服务器的用户和客户端套接字对象

	public static HashMap<String, ComConnect> map_Socket_Com = new HashMap<>();// 用于存储连接到服务器的用户和客户端套接字对象
	@Override
	public void sessionCreated(IoSession session) throws Exception {

		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){
			logger.info("服务端与CIC创建连接...");
			IotServer.ta_info2Cic.append("CIC连接成功。" + session.getRemoteAddress() + "\n");

			CicConnect value = new CicConnect(IotServer.map_IMSI_Lift);// 创建并启动线程对象
			value.session = session;
			while(IotServer.f_CicIteratoring){
				
			}
			map_Secket_Cic.put(key, value);// 添加键值对
			CicConnect.CicStsFresh();
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	
			//logger.info("服务端与客户端创建连接...");
			IotServer.ta_info2Pc.append("PC端连接成功。" + session.getRemoteAddress() + "\n");
			
			PcConnect.PcStsFresh();
			
			PcConnect value = new PcConnect(IotServer.map_IMSI_Lift);// 创建并启动线程对象
			value.session = session;
			while(IotServer.f_PcIteratoring){
				
			}
			map_Socket_Pc.put(key, value);// 添加键值对
	
		}
		else if(sessionLocalIP.contains(":"+IotServer.httpPort)){	
			//logger.info("服务端与客户端创建连接...");
			IotServer.ta_info2Pc.append("第三方连接成功。" + session.getRemoteAddress() + "\n");
			
			//PcConnect.PcStsFresh();
			
			ComConnect value = new ComConnect(IotServer.map_IMSI_Lift);// 创建并启动线程对象
			value.session = session;
//			while(IotServer.f_PcIteratoring){
//				
//			}
			map_Socket_Com.put(key, value);// 添加键值对
	
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		//logger.info("服务端与客户端连接打开...");
//		String Cicport = String.valueOf(IotServer.CICPort);
//		if(sessionLocalIP.contains(Cicport)){
//		}
//		else{	
//		}
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
 		
		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){	
			if (map_Secket_Cic.containsKey(key) == false) {
				return;
			}
			CicConnect value = map_Secket_Cic.get(key);// 获得指定键的值
			IoBuffer buf = (IoBuffer) message;
			value.RxBuffer = buf.array();
			value.Rxlength = buf.limit();//array()[498]*256 + buf.array()[499];
			value.CICRxIMSI();
			value.CICRxData();
			value.liftFaultDeal();
			value.liftInsDeal();
			value.t_Txing = 0;
			
			logger.debug(value.liftID +" Rx:" + IotServer.byte2Hex(value.RxBuffer, 0, value.Rxlength) + "\n");
			
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){
			if (map_Socket_Pc.containsKey(key) == false) {
				return;
			}
			PcConnect value = map_Socket_Pc.get(key);// 获得指定键的值
			IoBuffer buf = (IoBuffer) message;
			value.RxBuffer = buf.array();
			value.Rxlength = buf.limit();
			//value.Rxlength = buf.array()[498]*256 + buf.array()[499];
			value.f_LoginSuccess = 1;
			String tempString = new String(Arrays.copyOfRange(value.RxBuffer, 0, value.Rxlength), "unicode");
			if (tempString.contains("Register:")) {
				value.PCRegiser(tempString);
			} else if (tempString.contains("Login:")) {
				value.PCLogin(tempString);
			}  else if (tempString.contains("ComErrLock:")) {
				value.PCComErrLock_Single(tempString);
			} else if (tempString.contains("Keypad:")) {
				value.PCKeypad_Single(tempString);
			} else if (tempString.contains("Lock:")) {
				value.PCLock_Single(tempString);
			}else if (value.f_LoginSuccess == 1) {
				value.PCRxIMSI();
			}
			value.t_Txing = 0;
			logger.debug("RxFromPC:"+IotServer.byte2Hex(value.RxBuffer, 0, value.Rxlength) + "\n");
		}
		else{
			//httpRx(session, message);
			webSocketRx(session, message);
		}
	}
	
	void webSocketRx(IoSession session, Object message){

		//IUpdateViewFactory.getUpdateView().log(
		//		"[messageReceived] " + message.toString());
		String key = session.getRemoteAddress().toString();
		MinaBean minaBean = (MinaBean) message;
		if (minaBean.isWebAccept()) {
			MinaBean sendMessage = minaBean;
			sendMessage.setContent(WebSocketUtil.getSecWebSocketAccept(minaBean
					.getContent()));
			session.write(sendMessage);
		}
		else if (minaBean.isWebClose()) {
			session.closeNow();
		}  else {
				if (map_Socket_Com.containsKey(key) == false) {
					return;
				}
				ComConnect value = map_Socket_Com.get(key);// 获得指定键的值
				
				value.ComRxIMSI(minaBean.getContent());
				//requestLiftID(minaBean.getContent());
				//is.write(message);
		}
	}
	
	void httpRx(IoSession session, Object message){

        String respData;
        Token token = (Token)message;
        //打印收到的原始报文
        System.out.println("渠道:" + token.getBusiType() + "  交易码:" + token.getBusiCode() + "  完整报文(HEX):"+JadyerUtil.buildHexStringWithASCII(JadyerUtil.getBytes(token.getFullMessage(), "UTF-8")));
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n------------------------------------------------------------------------------------------");
        sb.append("\r\n【通信双方】").append(session);
        sb.append("\r\n【收发标识】Receive");
        sb.append("\r\n【报文内容】").append(token.getFullMessage());
        sb.append("\r\n------------------------------------------------------------------------------------------");
        System.out.println(sb.toString());
		//根据请求的业务编码做不同的处理
        switch (token.getBusiCode()) {
            case "/":
                respData = this.buildHTTPResponseMessage("<h2>欢迎访问由Mina2.0.7编写的Web服务器</h2>");
                break;
            case "/favicon.ico":
                //<link rel="icon" href="http://jadyer.cn/favicon.ico" type="image/x-icon"/>
                //<link rel="shortcut icon" href="http://jadyer.cn/favicon.ico" type="image/x-icon"/>
                String httpResponseMessageBody = "<link rel=\"icon\" href=\"http://jadyer.cn/favicon.ico\"";
                httpResponseMessageBody += " type=\"image/x-icon\"/>\n";
                httpResponseMessageBody += "<link rel=\"shortcut icon\" href=\"http://jadyer.cn/favicon.ico\"";
                httpResponseMessageBody += " type=\"image/x-icon\"/>";
                respData = this.buildHTTPResponseMessage(httpResponseMessageBody);
                break;
            case "/login":
                System.out.println("收到请求参数=[" + token.getBusiMessage() + "]");
                respData = this.buildHTTPResponseMessage("登录成功");
                break;
            case "10005":
                System.out.println("收到请求参数=[" + token.getBusiMessage() + "]");
                respData = "00003099999999`20130707144028`";
                break;
            case "/LiftSts":
                System.out.println("收到请求参数=[" + token.getBusiMessage() + "]");
                
			String elevator_Code = "1";
			String create_Time = "1";
			String service_Mode = "1";
			String status = "1";
			String direction = "1";
			String floor = "1";
			String door = "1";
			String door_Zone = "1";
			String speed = "1";
			String passenger = "1";
			String passenger_Num = "1";
			String power = "1";
			String overload = "1";
			String run_Time = "1";
			String self_Learning = "1";
			LiftSts_com liftSts_com1 = new LiftSts_com(elevator_Code, create_Time, service_Mode, status, direction, floor, door, door_Zone, speed, passenger, passenger_Num, power, overload, run_Time, self_Learning);
					LiftSts_com liftSts_com2 = new LiftSts_com(elevator_Code, create_Time, service_Mode, status, direction, floor, door, door_Zone, speed, passenger, passenger_Num, power, overload, run_Time, self_Learning);
					//将数据添加到数组
					List<LiftSts_com> newslist = new ArrayList<LiftSts_com>();
					newslist.add(liftSts_com1);
					newslist.add(liftSts_com2);
					//将数据封装到新闻总计类
					//LiftSts_com_Array nt = new LiftSts_com_Array(newslist.size(), newslist);
					//调用GSON jar工具包封装好的toJson方法，可直接生成JSON字符串
					//JSONObject gson = new JSONObject();
					String json = JSONObject.toJSONString(liftSts_com2,SerializerFeature.WriteMapNullValue);
					//String jsonString = JSON.toJSONString(nt);
			
			respData = this.buildHTTPResponseMessage(json);
                break;
            default:
                switch (token.getBusiType()) {
                    case Token.BUSI_TYPE_TCP:
                        respData = "ILLEGAL_REQUEST";
                        break;
                    case Token.BUSI_TYPE_HTTP:
                        respData = this.buildHTTPResponseMessage(501, null);
                        break;
                    default:
                        respData = "UNKNOWN_REQUEST";
                        break;
                }
                break;
        }
        //打印应答报文
        sb.setLength(0);
        sb.append("\r\n------------------------------------------------------------------------------------------");
        sb.append("\r\n【通信双方】").append(session);
        sb.append("\r\n【收发标识】Response");
        sb.append("\r\n【报文内容】").append(respData);
        sb.append("\r\n------------------------------------------------------------------------------------------");
        System.out.println(sb.toString());
        session.write(respData);
	}
	
	/**
     * 构建HTTP响应报文
     * 该方法默认构建的是HTTP响应码为200的响应报文
     * @param httpResponseMessageBody HTTP响应报文体
     * @return 包含了HTTP响应报文头和报文体的完整报文
     */
    private String buildHTTPResponseMessage(String httpResponseMessageBody){
        return this.buildHTTPResponseMessage(HttpURLConnection.HTTP_OK, httpResponseMessageBody);
    }

    /**
     * 构建HTTP响应报文
     * 200--请求已成功，请求所希望的响应头或数据体将随此响应返回...即服务器已成功处理了请求
     * 400--由于包含语法错误，当前请求无法被服务器理解...除非进行修改，否则客户端不应该重复提交这个请求，即错误请求
     * 500--服务器遇到一个未曾预料的状况，致其无法完成请求的处理...一般该问题都会在服务器的程序码出错时出现，即服务器内部错误
     * 501--服务器不支持当前请求所需的某功能...当服务器无法识别请求，且无法支持其对任何资源的请求时，可能返回此代码，即尚未实施
     * @param httpResponseCode        HTTP响应码
     * @param httpResponseMessageBody HTTP响应报文体
     * @return 包含了HTTP响应报文头和报文体的完整报文
     */
    private String buildHTTPResponseMessage(int httpResponseCode, String httpResponseMessageBody){
        if(httpResponseCode == HttpURLConnection.HTTP_OK){
            StringBuilder sb = new StringBuilder();
            sb.append("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\nContent-Length: ");
            sb.append(JadyerUtil.getBytes(httpResponseMessageBody, "UTF-8").length);
            sb.append("\r\n\r\n");
            sb.append(httpResponseMessageBody);
            return sb.toString();
        }
        if(httpResponseCode == HttpURLConnection.HTTP_BAD_REQUEST){
            return "HTTP/1.1 400 Bad Request";
        }
        if(httpResponseCode == HttpURLConnection.HTTP_INTERNAL_ERROR){
            return "HTTP/1.1 500 Internal Server Error";
        }
        return "HTTP/1.1 501 Not Implemented";
    }
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		//logger.info("服务端发送信息成功...");
		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){
			if (map_Secket_Cic.containsKey(key) == false) {
				return;
			}
			CicConnect value = map_Secket_Cic.get(key);// 获得指定键的值
			value.t_Txing = 0;
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	

			if (map_Socket_Pc.containsKey(key) == false) {
				return;
			}
			PcConnect value = map_Socket_Pc.get(key);// 获得指定键的值
			value.t_Txing = 0;
		}
		else if(sessionLocalIP.contains(":"+IotServer.httpPort)){	

			if (map_Socket_Com.containsKey(key) == false) {
				return;
			}
			ComConnect value = map_Socket_Com.get(key);// 获得指定键的值
			value.t_Txing = 0;
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){
			if (map_Secket_Cic.containsKey(key) == false) {
				return;
			}
			CicConnect value = map_Secket_Cic.get(key);// 获得指定键的值
			value.CICClose();
			//map_Secket_Cic.remove(key, value);
			value.f_destroy = 1;
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	

			if (map_Socket_Pc.containsKey(key) == false) {
				return;
			}
			PcConnect value = map_Socket_Pc.get(key);// 获得指定键的值			
			value.PcClose();
			//map_Socket_Pc.remove(key, value);
			value.f_destroy = 1;
		}
		else if(sessionLocalIP.contains(":"+IotServer.httpPort)){	

			if (map_Socket_Com.containsKey(key) == false) {
				return;
			}
			ComConnect value = map_Socket_Com.get(key);// 获得指定键的值			
			//map_Socket_Pc.remove(key, value);
			value.f_destroy = 1;
		}
		else{	
		}

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

		//logger.info("服务端进入空闲状态...");
		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){	
			if (map_Secket_Cic.containsKey(key) == false) {
				session.closeNow();
				return;
			}
			CicConnect value = map_Secket_Cic.get(key);// 获得指定键的值

			logger.info("离线电梯"+value.liftID);
			value.CICClose();
			session.closeNow();
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	

			if (map_Socket_Pc.containsKey(key) == false) {
				session.closeNow();
				return;
			}
			PcConnect value = map_Socket_Pc.get(key);// 获得指定键的值

			value.f_SingleData = 0;
			value.IMSI_Muti.clear();
			value.PCTxHbeat();
			//session.closeNow();
		}
		else if(sessionLocalIP.contains(":"+IotServer.httpPort)){	

		}
		else{	
		}

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

	//	logger.error("服务端发送异常...", cause);
		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){
			if (map_Secket_Cic.containsKey(key) == false) {
				session.closeNow();
				return;
			}
			CicConnect value = map_Secket_Cic.get(key);// 获得指定键的值
			value.c_RetryTx ++;
			if(value.c_RetryTx >= 3){
				value.c_RetryTx = 0;
				session.closeNow();
				logger.info("离线电梯"+value.liftID);
			}
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	
			
			if (map_Socket_Pc.containsKey(key) == false) {
				session.closeNow();
				return;
			}
			PcConnect value = map_Socket_Pc.get(key);// 获得指定键的值
			value.c_RetryTx ++;
			if(value.c_RetryTx >= 3){
				value.c_RetryTx = 0;
				session.closeNow();
			}
		}
		else if(sessionLocalIP.contains(":"+IotServer.httpPort)){	

		}
		else{
		}
	}

}
