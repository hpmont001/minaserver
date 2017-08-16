package com.mingrisoft;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSONObject;

public class CicConnect {
	public static Logger logger = Logger.getLogger(IotServer.class);
	// Socket socket;
	private static final int CicIdle_S = 60;
	private static final int CicHeartBeat_S = 20;
	byte RxBuffer[] = new byte[500];
	byte TxBuffer[] = new byte[100];
	int Rxlength = 0;
	int Txlength = 0;
	int step = 0;
	int f_TxRequest = 0;
	// int c_Retry = 0;
	int c_RetryTx = 0;
	// int f_closeSocket = 0;
	int IdleCnt = 0;
	Protocol value = null;
	String liftID = " ";
	String IMSI = " ";
	int f_50ms = 0;
	int t_10ms = 0;
	int f_Sec = 0;
	int t_Sec = 0;
	long t_Txing = 0;
	int f_destroy = 0;
	IoSession session = null;
	HashMap<String, Protocol> map_liftID_LiftOJ = null;

	public CicConnect(HashMap<String, Protocol> map_IMSI_Lift) {
		// this.socket = socket;
		this.map_liftID_LiftOJ = map_IMSI_Lift;
	}

	static void CicStsFresh() {
		int liftNumOnline = 0;
		//IotServer.ta_info2Cic.setText(null);
		try {
			
		Set<String> set = IotServer.map_IMSI_Lift.keySet();// ��ü����м���Set��ͼ
		Iterator<String> it = set.iterator();// ��õ���������
		while (it.hasNext()) { // ����������Ԫ�أ���ִ��ѭ����
			String key = it.next(); // �����һ����������
			Protocol value = IotServer.map_IMSI_Lift.get(key);// ���ָ������ֵ
			if (value.f_destroy == 1) {
				IotServer.map_IMSI_Lift.remove(key, value);
				break;// ��ɾ��������������������
			}
			IotServer.ta_info2Cic.append(key + '\n');
			liftNumOnline++;

		}
		} catch (Exception e) {
			// TODO: handle exception
		}
		IotServer.ta_info2Cic.append("����̨����" + liftNumOnline + '\n');

	}

	public void WriteLiftStsTable() {
		if (map_liftID_LiftOJ.containsKey(liftID) == false) {
			return;
		}
		value = map_liftID_LiftOJ.get(liftID);// ���ָ������ֵ
		LiftStsInfo liftStsInfo = new LiftStsInfo();
		liftStsInfo.liftID = liftID;
		if (value.LiftSts[11] != 0) {
			liftStsInfo.FaultSts = "��";
		} else {
			liftStsInfo.FaultSts = "��";
		}
		if ((value.LiftSts[10] & 0x0f) == 1) {
			liftStsInfo.InsSts = "��";
		} else {
			liftStsInfo.InsSts = "��";
		}
		LiftStsDeal_SQL dbConn = new LiftStsDeal_SQL(DBPoor.getConn());
		dbConn.updateLiftSts(liftStsInfo);
		DBPoor.closeConn(dbConn.conn);
	}
	public void OnlineOfflineDeal(String OnlineSts) {
		if (map_liftID_LiftOJ.containsKey(liftID) == false) {
			return;
		}
		value = map_liftID_LiftOJ.get(liftID);// ���ָ������ֵ
		LiftStsInfo liftStsInfo = new LiftStsInfo();
		liftStsInfo.liftID = liftID;
		liftStsInfo.OnlineSts = OnlineSts;
		if ((value.LiftSts[35] & 0x0f) == 1) {
			liftStsInfo.ConnectSts = "��";
		} else {
			liftStsInfo.ConnectSts = "��";
		}
		 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//
		
		if (OnlineSts.contains("����")) {
			 //�������ڸ�ʽ
			 liftStsInfo.OnlineDate = df.format(new Date());
		} else {
			liftStsInfo.OfflineDate = df.format(new Date());
		}

		LiftStsDeal_SQL dbConn = new LiftStsDeal_SQL(DBPoor.getConn());
		dbConn.updateIotModuleSts(liftStsInfo);
		DBPoor.closeConn(dbConn.conn);

	}

	public void CICRxIMSI() {

		if (RxBuffer[0] == 0x55 && RxBuffer[1] == ((byte) 0xaa) && RxBuffer[2] == 15) {

			IMSI = IotServer.byte2Char(RxBuffer, 3, 15);
			IotServer.ta_info2Cic.append("CIC IMSI:" + IMSI + "\n");
			logger.info("CIC IMSI: " + IMSI);
			if (IotServer.isNumeric(IMSI) == false) {
				return;
			}
			liftID = LiftStsDeal_SQL.InquireLiftID(IMSI);
			if (LiftStsDeal_SQL.CheckBaseLiftIDExist(liftID) == false) {
				return;
			}
			if (map_liftID_LiftOJ.containsKey(liftID) == true) {
				value = map_liftID_LiftOJ.get(liftID);// ���ָ������ֵ
				
				map_liftID_LiftOJ.remove(liftID, value);
			}
			value = new Protocol();
			map_liftID_LiftOJ.put(liftID, value);// ��Ӽ�ֵ��
			System.arraycopy(RxBuffer, 3, value.Imsi, 0, 15);
			// value.socketName = socket.toString();

			OnlineOfflineDeal("����");
		}
	}
	public static String post(String URL,JSONObject json) {

        //HttpClient client = new DefaultHttpClient();
    	CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(URL);
        
        post.setHeader("Content-Type", "application/json");
      //  post.addHeader("Authorization", "Basic YWRtaW46");
        String result = "";
        
        try {

            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
            post.setEntity(s);

            // ��������
            HttpResponse httpResponse = client.execute(post);

            // ��ȡ��Ӧ������
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                strber.append(line + "\n");
            inStream.close();

            result = strber.toString();
            System.out.println(result);
            
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                
                    System.out.println("����������ɹ�������Ӧ����");
                
            } else {
                
                System.out.println("��������ʧ��");
                
            }
            

        } catch (Exception e) {
            System.out.println("�����쳣");
            throw new RuntimeException(e);
        }

        return result;
    }

	LiftFault_com prepareLiftFault(String LiftID){

		LiftFault_com liftFault_com = new LiftFault_com();
		if (map_liftID_LiftOJ.containsKey(liftID) == false) {
			liftFault_com.Elevator_Code = liftID;
			return liftFault_com;
		}
		value = map_liftID_LiftOJ.get(liftID);// ���ָ������ֵ
		liftFault_com.Elevator_Code = liftID;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
		liftFault_com.Create_Time = df.format(new Date());
		liftFault_com.Fault_Time = liftFault_com.Create_Time;

//		00	�����޹���
//		01	��������ʱ��ȫ��·��·
//		02	���Ź���
//		03	���Ź���
//		04	�����ڿ���������ֹͣ
//		05	���������ƶ�
//		06	�綯����תʱ������������
//		07	¥��λ�ö�ʧ
//		08	�����п���
//		09	�嶥
//		10	�׵�
//		11	����
//		12	��������
//		13	���г�ʱ
//		14	����������
//		15	���ᱨ����ť����
//		99	����
		switch(value.LiftSts[11] / 100){
		case 0:
			liftFault_com.Fault_Code = "" + 0;
			break;
		case 41:
			liftFault_com.Fault_Code = "" + 1;
			break;
		case 49:
			liftFault_com.Fault_Code = "" + 2;
			break;
		case 48:
			liftFault_com.Fault_Code = "" + 3;
			break;
		case 65:
			liftFault_com.Fault_Code = "" + 5;
			break;
		case 58:
			liftFault_com.Fault_Code = "" + 7;
			break;
		case 42:
			liftFault_com.Fault_Code = "" + 8;
			break;
		case 32:
			liftFault_com.Fault_Code = "" + 11;
			break;
		case 40:
			liftFault_com.Fault_Code = "" + 13;
			break;
		default:
			liftFault_com.Fault_Code = "" + 99;
			break;
		}
		liftFault_com.Fault_Code = "" + value.LiftSts[11] / 100;
		
		
		liftFault_com.Third_Fault_Code = "" + value.LiftSts[11] / 100;
		if(((value.LiftSts[38] ) & 0x02) == 0)
		{
			liftFault_com.Trapped = "" + 0;		
		}
		else{
			liftFault_com.Trapped = "" + 1;
		}
		liftFault_com.Fault_Reason = "";

		if((value.LiftSts[5] & 0x07) == 3)
		{
			liftFault_com.door = "" + 1;		
		}
		else{
			liftFault_com.door = "" + 0;
		}
		liftFault_com.direction = "" + ((value.LiftSts[10]>>>8) & 0x0f);
		liftFault_com.floor = "" + value.LiftSts[12];
		liftFault_com.speed = "" + (value.LiftSts[0]/10);
		//LiftFault_com liftFault_com = new LiftFault_com(elevator_Code, create_Time, Fault_Time, Fault_Code, Third_Fault_Code, Trapped, Fault_Reason);
		return liftFault_com;
	}

	// ���ݹ�����Ϣ��¼
	public void liftFaultDeal() {
		if (map_liftID_LiftOJ.containsKey(liftID) == false) {
			return;
		}
		value = map_liftID_LiftOJ.get(liftID);// ���ָ������ֵ

		if (value.LiftSts[11] != 0) {
			if (value.ErrcodeBak != value.LiftSts[11]) {
				LiftFaultDeal_SQL liftDbConn = new LiftFaultDeal_SQL(DBPoor.getConn());
				List<String> tempStrPhone = new ArrayList<>();
				String tempStrAddr = " ";
				String tempStrLiftID = " ";
				LiftFaultRecord liftInfo = new LiftFaultRecord();
				liftInfo.liftID = liftID;
				liftInfo.innerID = liftDbConn.InquireInnerID(liftInfo.liftID);
				liftInfo.errorState = "δ���";
				liftInfo.errorCode = "E" + value.LiftSts[11] / 100;
				// liftInfo.errorName =
				// IotServer.errName_ErrCode(liftInfo.errorCode);
				liftInfo.errorName = liftDbConn.InquireErrorName(liftInfo.errorCode);
				liftInfo.errorFloor = "" + value.LiftSts[12];
				if ((value.LiftSts[10] & 0x0f) == 1) {
					liftInfo.InsMode = "��";
				} else {
					liftInfo.InsMode = "��";
				}

				liftInfo.Description = "";

				if (LiftStsDeal_SQL.CheckBaseLiftIDExist(liftID)
						&& liftInfo.InsMode.contains("��")) {

					String tempStrCode = value.LiftSts[11] / 100 + "��" + liftInfo.errorName;

					try {
						tempStrLiftID = liftID;// LiftStsDeal_SQL.InquireLiftID(liftID);
						tempStrPhone = LiftStsDeal_SQL.InquireLiftContacts(liftID);
						tempStrAddr = LiftStsDeal_SQL.InquireLiftPos(liftID);
						// tempStrAddr = liftPos.getString(4).trim();
					} catch (NullPointerException e) {
					}
					String tempSendMsg = "�����������������ݷ������ϣ�E" + tempStrCode + "����ַ��" + tempStrAddr + "�����ݹ���"
							+ tempStrLiftID + "��";
					liftInfo.Send_message = "δ����";
					if (tempStrPhone.isEmpty() == false) {
						Iterator<String> iterator = tempStrPhone.iterator();
						while (iterator.hasNext()) {
							String phone = iterator.next();
							if (phone != null) {
								String tempStr1 = SmsModule.sendMessage(phone, tempSendMsg);// ���ŷ���
								if (tempStr1.contains("���ͳɹ�")) {
									liftInfo.Send_message = "�ѷ���";
								}
							}
						}
					}
				}

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
				liftInfo.Report_time = df.format(new Date());
				liftDbConn.insertData(liftInfo);
				DBPoor.closeConn(liftDbConn.conn);
				//д�����״̬
				WriteLiftStsTable();
		    	Map<String, String> tempParams = new HashMap<String, String>();

		        String URI_YUNANDA = "http://admincs.yunandawulian.com/appServer/api/alarmAdd/hpmant";

		    	tempParams.put("content-type", "json");
		    	LiftFault_com liftFault_com = prepareLiftFault(liftID);

		    	JSONObject json = (JSONObject) JSONObject.toJSON(liftFault_com);//��java����ת��Ϊjson����
		    	post(URI_YUNANDA,json);
			}
		} else {
			if (value.ErrcodeBak != value.LiftSts[11]) {
				LiftFaultDeal_SQL liftDbConn = new LiftFaultDeal_SQL(DBPoor.getConn());
				List<String> tempStrPhone = new ArrayList<>();
				String tempStrAddr = " ";
				String tempStrLiftID = " ";

				LiftFaultRecord liftInfo = new LiftFaultRecord();
				liftInfo.liftID = liftID;
				liftInfo.errorState = "�ѽ��";
				liftInfo.errorCode = "E" + value.ErrcodeBak / 100;
				liftInfo.errorName = liftDbConn.InquireErrorName(liftInfo.errorCode);
				// liftInfo.errorName =
				// IotServer.errName_ErrCode(liftInfo.errorCode);
				if ((value.LiftSts[10] & 0x0f) == 1) {
					liftInfo.InsMode = "��";
				} else {
					liftInfo.InsMode = "��";
				}

				if (LiftStsDeal_SQL.CheckBaseLiftIDExist(liftID)
						&&(value.ErrcodeBak != 9999)
						&& liftInfo.InsMode.contains("��")) {//�ϵ�ʱ���ܷ��͹��Ͻ������

					try {
						tempStrPhone = LiftStsDeal_SQL.InquireLiftContacts(liftID);
						tempStrAddr = LiftStsDeal_SQL.InquireLiftPos(liftID);
						// tempStrAddr = liftPos.getString(4).trim();
						tempStrLiftID = liftID;// LiftStsDeal_SQL.InquireLiftID(liftID);
					} catch (NullPointerException e) {
					}
					String tempSendMsg = "�����������������ݹ����ѽ������ַ��" + tempStrAddr + "�����ݹ��ţ�" + tempStrLiftID + "��";

					if (tempStrPhone.isEmpty() == false) {
						Iterator<String> iterator = tempStrPhone.iterator();
						while (iterator.hasNext()) {
							String tempStr1 = SmsModule.sendMessage(iterator.next(), tempSendMsg);// ���ŷ���
							if (tempStr1.contains("���ͳɹ�")) {
								liftInfo.Send_message = "�ѷ���";
							}
						}
					}
				}

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
				liftInfo.Release_time = df.format(new Date());
				liftDbConn.updateLiftFaultRecord(liftInfo);

				DBPoor.closeConn(liftDbConn.conn);
				//д�����״̬
				WriteLiftStsTable();
			}

		}
		value.ErrcodeBak = value.LiftSts[11];

	}

	public void liftInsDeal() {
		if (map_liftID_LiftOJ.containsKey(liftID) == false) {
			return;
		}
		value = map_liftID_LiftOJ.get(liftID);// ���ָ������ֵ

		int LiftInsSts = (value.LiftSts[10] &0x0f);
		if (value.InsBak != LiftInsSts) {				
			//д�����״̬
			WriteLiftStsTable();
		}
		value.InsBak = LiftInsSts;

	}

	public void CICRxData() {
		if (map_liftID_LiftOJ.containsKey(liftID) == false) {
			return;
		}
		value = map_liftID_LiftOJ.get(liftID);// ���ָ������ֵ
		for (int i = 0; (i + 7 <= Rxlength); i++) {
			if (RxBuffer[i] == 0x02 && ((RxBuffer[i + 1] == 0x03) || (RxBuffer[i + 1] == 0x10))) {
				int frameLength;
				if(RxBuffer[i + 1] == 0x10){
					frameLength = 7;
				}
				else{
					frameLength = (RxBuffer[i + 4] & 0xff) * 2 + 7;
				}
				if ((i + frameLength) > Rxlength) {
					String Rxerr = IotServer.byte2Hex(RxBuffer, 0, RxBuffer.length);
					logger.error("CIC Rx err!" + Rxerr+ "\n");
					IotServer.ta_info2Cic.append("CIC Rx err!" + Rxerr+ "\n");
					continue;
				}
				int[] tempRx = new int[frameLength];
				for (int j = 0; j < frameLength; j++) {
					tempRx[j] = RxBuffer[i + j] & 0xff;
				}
				int CRCH = tempRx[frameLength - 1];
				int CRCL = tempRx[frameLength - 2];
				int CRCRx = (CRCH << 8) + CRCL;
				if (crc16.crcCalc_Table(tempRx, frameLength - 2) == CRCRx) {
					int DataAddr, DataLength;
					DataAddr = (tempRx[2] << 8) + tempRx[3];
					DataLength = tempRx[4];
					if (DataAddr >= 51200) {
						for (int ii = 0; (ii < DataLength) && (7 + 2 * ii < frameLength); ii++) {
							if ((ii + DataAddr - 51200) < value.LiftSts.length) {
								value.LiftSts[ii + DataAddr - 51200] = ((tempRx[5 + 2 * ii] << 8) + tempRx[6 + 2 * ii]);
							}
						}
					} else if (DataAddr < 25600) {
						value.c_FreshLiftPara++;
						if (value.c_FreshLiftPara >= 3) {
							value.c_FreshLiftPara = 0;
							value.f_FreshLiftPara = false;
						}
						for (int ii = 0; (ii < DataLength) && (7 + 2 * ii < frameLength); ii++) {
							if ((ii + DataAddr) < value.LiftPara.length) {
								value.LiftPara[ii + DataAddr] = ((tempRx[5 + 2 * ii] << 8) + tempRx[6 + 2 * ii]);
							}
						}
						value.LiftPara[84] = value.LiftSts[22];
					} else {
						if(value.f_LockLift == 1){
							value.c_LockLift++;
							if (value.c_LockLift >= 10) {
								value.c_LockLift = 0;
								value.f_LockLift = 0;
							}
						}
						else if(value.f_ComErrLockLift != null){
							value.c_ComErrLockLift++;
							if (value.c_ComErrLockLift >= 10) {
								value.c_ComErrLockLift = 0;
								value.f_ComErrLockLift = null;
							}
						}
						else if (value.f_KeyPadPort != null) {
							value.c_KeyPadPort++;
							if (value.c_KeyPadPort >= 10) {
								value.c_KeyPadPort = 0;
								value.f_KeyPadPort = null;
							}
						}
					}
					break;

				} else {
					String Rxerr = IotServer.byte2Hex(RxBuffer, 0, RxBuffer.length);
					logger.error("CIC Rx err!" + Rxerr+ "\n");
					IotServer.ta_info2Cic.append("CIC Rx err!" + Rxerr+ "\n");
				}
			}
		}
	}

	public void CicLockLift(){
		TxBuffer[0] = 0x02;
		TxBuffer[1] = 0x10;
		TxBuffer[2] = 100;
		TxBuffer[3] = 0x00;
		TxBuffer[4] = 1;
		TxBuffer[5] = (byte) (value.RunningTimesLimit / 256);
		TxBuffer[6] = (byte) (value.RunningTimesLimit % 256);
		int[] tempTx = new int[7];
		for (int j = 0; j < 7; j++) {
			tempTx[j] = (int) TxBuffer[j] & 0x00ff;
		}
		int crcCalc = 0;
		crcCalc = crc16.crcCalc_Table(tempTx, 7);
		TxBuffer[8] = (byte) (crcCalc >>> 8);
		TxBuffer[7] = (byte) (crcCalc & 0x00ff);
		Txlength = 9;
		f_TxRequest = 1;
		
	}
	public void CicTxHeartbeat(){
		TxBuffer[0] = 0x02;
		TxBuffer[1] = 0x03;
		TxBuffer[2] = (byte) 0xc8;
		TxBuffer[3] = 0x11;
		TxBuffer[4] = 1;
		int[] tempTx = new int[5];
		for (int j = 0; j < 5; j++) {
			tempTx[j] = TxBuffer[j] & 0xff;
		}
		int crcCalc = 0;
		crcCalc = crc16.crcCalc_Table(tempTx, 5);
		TxBuffer[6] = (byte) (crcCalc >>> 8);
		TxBuffer[5] = (byte) (crcCalc & 0x00ff);
		Txlength = 7;
		f_TxRequest = 1;
		
	}
	public void CicInquireData(){
		TxBuffer[0] = 0x02;
		TxBuffer[1] = 0x03;
		TxBuffer[2] = (byte) 0xc8;
		TxBuffer[3] = 0x00;
		TxBuffer[4] = 39;
		int[] tempTx = new int[5];
		for (int j = 0; j < 5; j++) {
			tempTx[j] = TxBuffer[j] & 0xff;
		}
		int crcCalc = 0;
		crcCalc = crc16.crcCalc_Table(tempTx, 5);
		TxBuffer[6] = (byte) (crcCalc >>> 8);
		TxBuffer[5] = (byte) (crcCalc & 0x00ff);
		Txlength = 7;
		f_TxRequest = 1;
		
	}
	public void CicFreshLiftPara(){
		TxBuffer[0] = 0x02;
		TxBuffer[1] = 0x03;
		if (value.c_FreshLiftPara % 2 == 0) {
			TxBuffer[2] = 0x00;
			TxBuffer[3] = (byte) 0;
			TxBuffer[4] = (byte) 100;
		} else {
			TxBuffer[2] = 0x00;
			TxBuffer[3] = (byte) 100;
			TxBuffer[4] = (byte) 79;
		}
		int[] tempTx = new int[5];
		for (int j = 0; j < 5; j++) {
			tempTx[j] = TxBuffer[j] & 0xff;
		}
		int crcCalc = 0;
		crcCalc = crc16.crcCalc_Table(tempTx, 5);
		TxBuffer[6] = (byte) (crcCalc >>> 8);
		TxBuffer[5] = (byte) (crcCalc & 0x00ff);
		Txlength = 7;
		f_TxRequest = 1;
		
	}
	public void CicComErrLockLift(){
		TxBuffer[0] = 0x02;
		TxBuffer[1] = 0x10;
		TxBuffer[2] = 0x64;
		TxBuffer[3] = (byte) 2;
		TxBuffer[4] = (byte) 1;
		TxBuffer[5] = (byte) 0;
		if (value.f_ComErrLockLift.equals("LockLift")) {
			TxBuffer[6] = 2;
		} else {
			TxBuffer[6] = 1;
		}
		int[] tempTx = new int[7];
		for (int j = 0; j < 7; j++) {
			tempTx[j] = TxBuffer[j] & 0xff;
		}
		int crcCalc = 0;
		crcCalc = crc16.crcCalc_Table(tempTx, 7);
		TxBuffer[7] = (byte) (crcCalc & 0x00ff);
		TxBuffer[8] = (byte) (crcCalc >>> 8);
		Txlength = 9;
		f_TxRequest = 1;
		
	}
	public void CicKeypadPort(){
		TxBuffer[0] = 0x02;
		TxBuffer[1] = 0x10;
		TxBuffer[2] = 0x64;
		TxBuffer[3] = (byte) 1;
		TxBuffer[4] = (byte) 1;
		TxBuffer[5] = (byte) 0;
		if (value.f_KeyPadPort.equals("Keypad")) {
			TxBuffer[6] = 0;
		} else {
			TxBuffer[6] = 1;
		}
		value.f_KeyPadPort = null;
		int[] tempTx = new int[7];
		for (int j = 0; j < 7; j++) {
			tempTx[j] = TxBuffer[j] & 0xff;
		}
		int crcCalc = 0;
		crcCalc = crc16.crcCalc_Table(tempTx, 7);
		TxBuffer[7] = (byte) (crcCalc & 0x00ff);
		TxBuffer[8] = (byte) (crcCalc >>> 8);
		Txlength = 9;
		f_TxRequest = 1;
		
	}
	public void CICTxData() {
		if (f_Sec == 1) {
		} else {
			return;
		}
		f_Sec = 0;
		if (map_liftID_LiftOJ.containsKey(liftID) == false) {
			return;
		}
		value = map_liftID_LiftOJ.get(liftID);// ���ָ������ֵ

		// value.f_LockLift = 2;
		if (value.f_LockLift != 0) {
			CicLockLift();
			return;
		} else if (value.f_FreshLiftPara == true) {
			CicFreshLiftPara();
			return;
		} else if (value.f_ComErrLockLift != null) {
			CicComErrLockLift();
			return;
		} else if (value.f_KeyPadPort != null) {
			CicKeypadPort();
			return;
		}
		if (value.t_Monitor_ing <= 1) {
			value.t_Monitor_ing = 0;
			if ((t_Sec % CicIdle_S) == 1) {
			} 
			else if ((t_Sec % CicHeartBeat_S) == 1) {
				//CicTxHeartbeat();
				return;
			} 
			else{
				return;
			}
		} else {
			value.t_Monitor_ing -= 1;
		}
		CicInquireData();
	}

	public void CICClose() {
		// f_closeSocket = 1;
		if (map_liftID_LiftOJ.containsKey(liftID) == false) {
			return;
		}
		value = map_liftID_LiftOJ.get(liftID);// ���ָ������ֵ
		OnlineOfflineDeal("����");
		value.f_destroy = 1;
		// map_IMSI_Lift.remove(liftID, value);
		CicStsFresh();
	}

}
