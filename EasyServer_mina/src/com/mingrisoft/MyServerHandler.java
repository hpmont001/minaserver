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
	public static HashMap<String, CicConnect> map_Secket_Cic = new HashMap<>();// ���ڴ洢���ӵ����������û��Ϳͻ����׽��ֶ���

	public static HashMap<String, PcConnect> map_Socket_Pc = new HashMap<>();// ���ڴ洢���ӵ����������û��Ϳͻ����׽��ֶ���

	public static HashMap<String, ComConnect> map_Socket_Com = new HashMap<>();// ���ڴ洢���ӵ����������û��Ϳͻ����׽��ֶ���
	@Override
	public void sessionCreated(IoSession session) throws Exception {

		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){
			logger.info("�������CIC��������...");
			IotServer.ta_info2Cic.append("CIC���ӳɹ���" + session.getRemoteAddress() + "\n");

			CicConnect value = new CicConnect(IotServer.map_IMSI_Lift);// �����������̶߳���
			value.session = session;
			while(IotServer.f_CicIteratoring){
				
			}
			map_Secket_Cic.put(key, value);// ��Ӽ�ֵ��
			CicConnect.CicStsFresh();
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	
			//logger.info("�������ͻ��˴�������...");
			IotServer.ta_info2Pc.append("PC�����ӳɹ���" + session.getRemoteAddress() + "\n");
			
			PcConnect.PcStsFresh();
			
			PcConnect value = new PcConnect(IotServer.map_IMSI_Lift);// �����������̶߳���
			value.session = session;
			while(IotServer.f_PcIteratoring){
				
			}
			map_Socket_Pc.put(key, value);// ��Ӽ�ֵ��
	
		}
		else if(sessionLocalIP.contains(":"+IotServer.httpPort)){	
			//logger.info("�������ͻ��˴�������...");
			IotServer.ta_info2Pc.append("���������ӳɹ���" + session.getRemoteAddress() + "\n");
			
			//PcConnect.PcStsFresh();
			
			ComConnect value = new ComConnect(IotServer.map_IMSI_Lift);// �����������̶߳���
			value.session = session;
//			while(IotServer.f_PcIteratoring){
//				
//			}
			map_Socket_Com.put(key, value);// ��Ӽ�ֵ��
	
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		//logger.info("�������ͻ������Ӵ�...");
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
			CicConnect value = map_Secket_Cic.get(key);// ���ָ������ֵ
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
			PcConnect value = map_Socket_Pc.get(key);// ���ָ������ֵ
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
				ComConnect value = map_Socket_Com.get(key);// ���ָ������ֵ
				
				value.ComRxIMSI(minaBean.getContent());
				//requestLiftID(minaBean.getContent());
				//is.write(message);
		}
	}
	
	void httpRx(IoSession session, Object message){

        String respData;
        Token token = (Token)message;
        //��ӡ�յ���ԭʼ����
        System.out.println("����:" + token.getBusiType() + "  ������:" + token.getBusiCode() + "  ��������(HEX):"+JadyerUtil.buildHexStringWithASCII(JadyerUtil.getBytes(token.getFullMessage(), "UTF-8")));
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n------------------------------------------------------------------------------------------");
        sb.append("\r\n��ͨ��˫����").append(session);
        sb.append("\r\n���շ���ʶ��Receive");
        sb.append("\r\n���������ݡ�").append(token.getFullMessage());
        sb.append("\r\n------------------------------------------------------------------------------------------");
        System.out.println(sb.toString());
		//���������ҵ���������ͬ�Ĵ���
        switch (token.getBusiCode()) {
            case "/":
                respData = this.buildHTTPResponseMessage("<h2>��ӭ������Mina2.0.7��д��Web������</h2>");
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
                System.out.println("�յ��������=[" + token.getBusiMessage() + "]");
                respData = this.buildHTTPResponseMessage("��¼�ɹ�");
                break;
            case "10005":
                System.out.println("�յ��������=[" + token.getBusiMessage() + "]");
                respData = "00003099999999`20130707144028`";
                break;
            case "/LiftSts":
                System.out.println("�յ��������=[" + token.getBusiMessage() + "]");
                
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
					//��������ӵ�����
					List<LiftSts_com> newslist = new ArrayList<LiftSts_com>();
					newslist.add(liftSts_com1);
					newslist.add(liftSts_com2);
					//�����ݷ�װ�������ܼ���
					//LiftSts_com_Array nt = new LiftSts_com_Array(newslist.size(), newslist);
					//����GSON jar���߰���װ�õ�toJson��������ֱ������JSON�ַ���
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
        //��ӡӦ����
        sb.setLength(0);
        sb.append("\r\n------------------------------------------------------------------------------------------");
        sb.append("\r\n��ͨ��˫����").append(session);
        sb.append("\r\n���շ���ʶ��Response");
        sb.append("\r\n���������ݡ�").append(respData);
        sb.append("\r\n------------------------------------------------------------------------------------------");
        System.out.println(sb.toString());
        session.write(respData);
	}
	
	/**
     * ����HTTP��Ӧ����
     * �÷���Ĭ�Ϲ�������HTTP��Ӧ��Ϊ200����Ӧ����
     * @param httpResponseMessageBody HTTP��Ӧ������
     * @return ������HTTP��Ӧ����ͷ�ͱ��������������
     */
    private String buildHTTPResponseMessage(String httpResponseMessageBody){
        return this.buildHTTPResponseMessage(HttpURLConnection.HTTP_OK, httpResponseMessageBody);
    }

    /**
     * ����HTTP��Ӧ����
     * 200--�����ѳɹ���������ϣ������Ӧͷ�������彫�����Ӧ����...���������ѳɹ�����������
     * 400--���ڰ����﷨���󣬵�ǰ�����޷������������...���ǽ����޸ģ�����ͻ��˲�Ӧ���ظ��ύ������󣬼���������
     * 500--����������һ��δ��Ԥ�ϵ�״���������޷��������Ĵ���...һ������ⶼ���ڷ������ĳ��������ʱ���֣����������ڲ�����
     * 501--��������֧�ֵ�ǰ���������ĳ����...���������޷�ʶ���������޷�֧������κ���Դ������ʱ�����ܷ��ش˴��룬����δʵʩ
     * @param httpResponseCode        HTTP��Ӧ��
     * @param httpResponseMessageBody HTTP��Ӧ������
     * @return ������HTTP��Ӧ����ͷ�ͱ��������������
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
		//logger.info("����˷�����Ϣ�ɹ�...");
		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){
			if (map_Secket_Cic.containsKey(key) == false) {
				return;
			}
			CicConnect value = map_Secket_Cic.get(key);// ���ָ������ֵ
			value.t_Txing = 0;
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	

			if (map_Socket_Pc.containsKey(key) == false) {
				return;
			}
			PcConnect value = map_Socket_Pc.get(key);// ���ָ������ֵ
			value.t_Txing = 0;
		}
		else if(sessionLocalIP.contains(":"+IotServer.httpPort)){	

			if (map_Socket_Com.containsKey(key) == false) {
				return;
			}
			ComConnect value = map_Socket_Com.get(key);// ���ָ������ֵ
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
			CicConnect value = map_Secket_Cic.get(key);// ���ָ������ֵ
			value.CICClose();
			//map_Secket_Cic.remove(key, value);
			value.f_destroy = 1;
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	

			if (map_Socket_Pc.containsKey(key) == false) {
				return;
			}
			PcConnect value = map_Socket_Pc.get(key);// ���ָ������ֵ			
			value.PcClose();
			//map_Socket_Pc.remove(key, value);
			value.f_destroy = 1;
		}
		else if(sessionLocalIP.contains(":"+IotServer.httpPort)){	

			if (map_Socket_Com.containsKey(key) == false) {
				return;
			}
			ComConnect value = map_Socket_Com.get(key);// ���ָ������ֵ			
			//map_Socket_Pc.remove(key, value);
			value.f_destroy = 1;
		}
		else{	
		}

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

		//logger.info("����˽������״̬...");
		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){	
			if (map_Secket_Cic.containsKey(key) == false) {
				session.closeNow();
				return;
			}
			CicConnect value = map_Secket_Cic.get(key);// ���ָ������ֵ

			logger.info("���ߵ���"+value.liftID);
			value.CICClose();
			session.closeNow();
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	

			if (map_Socket_Pc.containsKey(key) == false) {
				session.closeNow();
				return;
			}
			PcConnect value = map_Socket_Pc.get(key);// ���ָ������ֵ

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

	//	logger.error("����˷����쳣...", cause);
		String key = session.getRemoteAddress().toString();
		String sessionLocalIP = session.getLocalAddress().toString();
		if(sessionLocalIP.contains(":"+IotServer.CICPort)){
			if (map_Secket_Cic.containsKey(key) == false) {
				session.closeNow();
				return;
			}
			CicConnect value = map_Secket_Cic.get(key);// ���ָ������ֵ
			value.c_RetryTx ++;
			if(value.c_RetryTx >= 3){
				value.c_RetryTx = 0;
				session.closeNow();
				logger.info("���ߵ���"+value.liftID);
			}
		}
		else if(sessionLocalIP.contains(":"+IotServer.PCPort)){	
			
			if (map_Socket_Pc.containsKey(key) == false) {
				session.closeNow();
				return;
			}
			PcConnect value = map_Socket_Pc.get(key);// ���ָ������ֵ
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
