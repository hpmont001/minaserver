package com.mingrisoft;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSONObject;

class PcConnect {
	Socket socket;
	byte[] RxBuffer = new byte[500];
	byte[] TxBuffer = new byte[500];
	// String TxBuffer = null;
	int Rxlength = 0;
	int Txlength = 0;
	int c_RetryTx = 0;
	int f_TxRequest = 0;
	int c_Retry = 0;
	// int f_closeSocket = 0;
	int IdleCnt = 0;
	int f_LoginSuccess = 0;
	int f_SingleData = 0;
	int f_SinglePara = 0;
	Set<String> set = null;
	String liftID = " ";
	String IMSI = " ";
	String LoginPcIp = "";
	boolean Accountflag = true;// 判断标记
	ArrayList<String> IMSI_Muti = new ArrayList<>();
	Protocol value = null;
	int f_50ms = 0;
	int t_10ms = 0;
	int f_Sec = 0;
	int t_Sec = 0;
	long t_Txing = 0;
	int f_destroy = 0;
	HashMap<String, Protocol> map_IMSI_Lift = null;

	IoSession session = null;

	public PcConnect(HashMap<String, Protocol> map_IMSI_Lift) {
		this.map_IMSI_Lift = map_IMSI_Lift;
	}

	public static void PcStsFresh() {
		// int PCNumOnline = 0;
		//IotServer.ta_info2Pc.setText(null);
		// Set<String> set = IotServer.map_IP_LoginSts.keySet();// 获得集合中键的Set视图
		// Iterator<String> it = set.iterator();// 获得迭代器对象
		// while (it.hasNext()) { // 迭代器中有元素，则执行循环体
		// String key = it.next(); // 获得下一个键的名称
		// IotServer.ta_info2Pc.append("在线PcID：" + key + '\n');
		// PCNumOnline++;
		// }
		// IotServer.ta_info2Pc.append("在线PC数量：" + PCNumOnline + '\n');
	}

	List<String> requestLiftID(String content){
		try {
			JSONObject  requstInfo = JSONObject.parseObject(content);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		//JSONArray myJsonArray = JSONArray.parseArray(content);
		return null;
	}
	public void PCRxIMSI() {
		if (RxBuffer[0] == 0x55 && RxBuffer[1] == ((byte) 0xaa) && RxBuffer[2] == 15) {

			IMSI_Muti.clear();
			IMSI = IotServer.byte2Char(RxBuffer, 3, 15);
			liftID = LiftStsDeal_SQL.InquireLiftID(IMSI);
			if (map_IMSI_Lift.containsKey(liftID) == true) {
				value = map_IMSI_Lift.get(liftID);// 获得指定键的值
				value.t_Monitor_ing = 10;
				f_SingleData = 1;
			}
			//IotServer.ta_info2Pc.append("PC RequestSingle:" + IMSI + '\n');
		} else if (RxBuffer[0] == 0x44 && RxBuffer[1] == ((byte) 0xaa) && RxBuffer[2] == 15) {

			IMSI_Muti.clear();
			IMSI = IotServer.byte2Char(RxBuffer, 3, 15);
			liftID = LiftStsDeal_SQL.InquireLiftID(IMSI);
			if (map_IMSI_Lift.containsKey(liftID) == true) {
				value = map_IMSI_Lift.get(liftID);// 获得指定键的值
				value.f_FreshLiftPara = true;
				f_SinglePara = 1;
			}
		} else {
			for (int i = 0; (i + 18 <= Rxlength); i++) {
				if (RxBuffer[i] == 0x66 && RxBuffer[i + 1] == ((byte) 0xaa) && RxBuffer[i + 2] == 15) {

					int frameLength = 18;
					if ((i + frameLength) > Rxlength) {
						continue;
					}
					IMSI = IotServer.byte2Char(RxBuffer, i + 3, 15);
					if (IotServer.isNumeric(IMSI) == false) {
						continue;
					}
					liftID = LiftStsDeal_SQL.InquireLiftID(IMSI);
					if (map_IMSI_Lift.containsKey(liftID) == true) {
						value = map_IMSI_Lift.get(liftID);// 获得指定键的值
						value.t_Monitor_ing = 10;
						if (IMSI_Muti.contains(liftID) == false) {
							IMSI_Muti.add(liftID);
						}
					}
					//IotServer.ta_info2Pc.append("PC RequestAll:" + liftID + '\n');

					f_SingleData = 0;
					i += 17;
				}
			}
		}

	}

	public void PCLogin(String src) {

		try {
			String loginSts = "";
			String authority = "";
			String Company = "";
			String b[] = src.split(":");
			if (b.length <= 1) {
				return;
			}
			String a[] = b[1].split("\\|");
			if (a.length >= 2) {

				IotServer.ta_info2Pc.append("登录信息:" + '\n');
				IotServer.ta_info2Pc.append(a[0] + '\n' + a[1] + '\n');
				PcRegisterDeal_SQL pcDbConn = new PcRegisterDeal_SQL(DBPoor.getConn());

				ResultSet rest = null;
				rest = pcDbConn.CheckAccount(a[0], a[1]);
				if (rest != null) {
					IotServer.ta_info2Pc.append("登录成功");
					loginSts = "success";
					authority = "";
					Company = "";
				} else {
					loginSts = "fail";
					authority = "";
					Company = "";
				}
				DBPoor.closeConn(pcDbConn.conn);
				authority = authority.trim() + "|" + loginSts.trim() + "|" + Company.trim();
				byte[] byteArray = authority.getBytes("unicode");
				System.arraycopy(byteArray, 0, TxBuffer, 0, byteArray.length);
				f_TxRequest = 1;
				Txlength = byteArray.length;// + byteArray1.length;

			}
		} catch (IOException e) {

		}
	}

	public void PCRegiser(String src) {
		String registerInfo = null;
		String b[] = src.split(":");
		if (b.length <= 1) {
			return;
		}
		String a[] = b[1].split("\\|");
		if (a.length < 7) {
			return;
		}
		IotServer.ta_info2Pc.append("注册信息:" + '\n');
		IotServer.ta_info2Pc
				.append(a[0] + '\n' + a[1] + '\n' + a[2] + '\n' + a[3] + '\n' + "" + '\n' + a[5] + '\n' + "" + '\n');
		PcRegisterinfo tempPcRegister = new PcRegisterinfo(a[0], a[1], a[2], a[3], "", a[4], a[5], a[6]);
		PcRegisterDeal_SQL pcDbConn = new PcRegisterDeal_SQL(DBPoor.getConn());

		if (pcDbConn.CheckUserNameExist(a[0].trim()) == false) {
			pcDbConn.insertPcRegister(tempPcRegister);
			IotServer.ta_info2Pc.append("注册成功");
			registerInfo = "提交成功";
		} else {
			registerInfo = "该用户名已被注册";
		}

		try {
			byte[] byteArray = registerInfo.getBytes("unicode");
			System.arraycopy(byteArray, 0, TxBuffer, 0, byteArray.length);
			f_TxRequest = 1;
			Txlength = byteArray.length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, "编码错误", "警告", JOptionPane.WARNING_MESSAGE);

		}
		DBPoor.closeConn(pcDbConn.conn);
	}

	public void PCLock_Single(String src) {
		String Info = null;
		String b[] = src.split(":");
		if (b.length <= 1) {
			return;
		}
		String a[] = b[1].split("\\|");
		if (a.length != 3) {
			return;
		}
		IotServer.ta_info2Pc.append("锁梯:");
		IotServer.ta_info2Pc.append(a[0] + a[1] + a[2] + '\n');
		try {
			int passwordRx = Integer.valueOf(a[1]);
			int passwordCalc = (Integer.valueOf(a[0].substring(12)) % 1000) * 9 + 567;
			if (passwordRx == passwordCalc) {
				String liftID = LiftStsDeal_SQL.InquireLiftID(a[0]);
				if (IotServer.map_IMSI_Lift.containsKey(liftID) == false) {
					return;
				}
				Protocol value = IotServer.map_IMSI_Lift.get(liftID);// 获得指定键的值

				value.f_LockLift = 1;
				int i = 0;
				int Numlength = 0;
				while ((a[2].charAt(i) >= '0')
						&& (a[2].charAt(i) <= '9')) {
					Numlength++;
					i++;
				}
				value.RunningTimesLimit = Integer.valueOf(a[2].substring(0, Numlength));
				Info = "提交成功";
			} else {
				Info = "密码错误";
			}

		} catch (Exception e) {
			// TODO: handle exception
			IotServer.logger.error("", e);
		}

		try {
			byte[] byteArray = Info.getBytes("unicode");
			System.arraycopy(byteArray, 0, TxBuffer, 0, byteArray.length);
			f_TxRequest = 1;
			Txlength = byteArray.length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e, "警告", JOptionPane.WARNING_MESSAGE);

		}
	}

	public void PCKeypad_Single(String src) {
		String Info = null;
		String b[] = src.split(":");
		if (b.length <= 1) {
			return;
		}
		String a[] = b[1].split("\\|");
		if (a.length != 3) {
			return;
		}
		IotServer.ta_info2Pc.append("键盘通讯:");
		IotServer.ta_info2Pc.append(a[0] + a[1] + a[2] + '\n');
		try {

			int passwordRx = Integer.valueOf(a[1]);
			int passwordCalc = (Integer.valueOf(a[0].substring(12)) % 1000) * 9 + 567;
			if (passwordRx == passwordCalc) {
				String liftID = LiftStsDeal_SQL.InquireLiftID(a[0]);
				if (IotServer.map_IMSI_Lift.containsKey(liftID) == false) {
					return;
				}
				Protocol value = IotServer.map_IMSI_Lift.get(liftID);// 获得指定键的值

				if (a[2].contains("Keypad")) {
					value.f_KeyPadPort = "Keypad";// 2键盘通讯，1外招通讯
				} else {
					value.f_KeyPadPort = "Hallcall";// 2键盘通讯，1外招通讯
				}
				Info = "提交成功";
			} else {
				Info = "密码错误";
			}
		} catch (Exception e) {
			// TODO: handle exception
			IotServer.logger.error("", e);
		}

		try {
			byte[] byteArray = Info.getBytes("unicode");
			System.arraycopy(byteArray, 0, TxBuffer, 0, byteArray.length);
			f_TxRequest = 1;
			Txlength = byteArray.length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e, "警告", JOptionPane.WARNING_MESSAGE);

		}
	}

	public void PCComErrLock_Single(String src) {
		String Info = null;
		String b[] = src.split(":");
		if (b.length <= 1) {
			return;
		}
		String a[] = b[1].split("\\|");
		if (a.length != 3) {
			return;
		}
		IotServer.ta_info2Pc.append("通讯故障是否锁梯:");
		IotServer.ta_info2Pc.append(a[0] + a[1] + a[2] + '\n');
		try {

			int passwordRx = Integer.valueOf(a[1]);
			int passwordCalc = (Integer.valueOf(a[0].substring(12)) % 1000) * 9 + 567;
			if (passwordRx == passwordCalc) {
				String liftID = LiftStsDeal_SQL.InquireLiftID(a[0]);
				if (IotServer.map_IMSI_Lift.containsKey(liftID) == false) {
					return;
				}
				Protocol value = IotServer.map_IMSI_Lift.get(liftID);// 获得指定键的值

				if (a[2].contains("LockLift")) {
					value.f_ComErrLockLift = "LockLift";// 2锁梯
				} else {
					value.f_ComErrLockLift = "nolock";// 1不锁梯
				}
				Info = "提交成功";
			} else {
				Info = "密码错误";
			}

		} catch (Exception e) {
			// TODO: handle exception
			IotServer.logger.error("", e);
		}
		try {
			byte[] byteArray = Info.getBytes("unicode");
			System.arraycopy(byteArray, 0, TxBuffer, 0, byteArray.length);
			f_TxRequest = 1;
			Txlength = byteArray.length;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e, "警告", JOptionPane.WARNING_MESSAGE);

		}
	}

	public void PCTxPara_Single() {

		if (map_IMSI_Lift.containsKey(liftID) == false) {
			return;
		}

		value = map_IMSI_Lift.get(liftID);// 获得指定键的值
		int index = 0;
		TxBuffer[index++] = 0x44;
		TxBuffer[index++] = (byte) (0xaa & 0xff);
		TxBuffer[index++] = (byte) ((value.LiftPara.length + 15) / 256);
		TxBuffer[index++] = (byte) ((value.LiftPara.length + 15) % 256);
		for (int i = 0; i < 15; i++) {
			TxBuffer[index++] = value.Imsi[i];
		}
		for (int i = 0; i < value.LiftPara.length; i++) {
			TxBuffer[index++] = (byte) (value.LiftPara[i] >>> 8);
			TxBuffer[index++] = (byte) (value.LiftPara[i] & 0x00ff);
		}
		Txlength = index;
		f_TxRequest = 1;
	}

	public void PCTxHbeat() {

		if ((t_Sec % 10 == 2) && f_Sec == 1) {
		} else {
			return;
		}
		int index = 0;
		TxBuffer[index++] = 0x66;
		TxBuffer[index++] = (byte) (0xaa & 0xff);
		TxBuffer[index++] = 0x0f;
		for (int i = 0; i < 15; i++) {
			TxBuffer[index++] = 0;
		}
		// 当前楼层
		TxBuffer[index++] = (byte) 0;
		TxBuffer[index++] = (byte) 0;
		// 门状态
		TxBuffer[index++] = (byte) 0;
		TxBuffer[index++] = (byte) 0;
		// 电梯状态
		TxBuffer[index++] = (byte) 0;
		TxBuffer[index++] = (byte) 0;
		Txlength = index;
		f_TxRequest = 1;
	}

	public void PCTxData_Single() {

		if (f_Sec == 1) {
		} else {
			return;
		}
		if (map_IMSI_Lift.containsKey(liftID) == false) {
			return;
		}

		value = map_IMSI_Lift.get(liftID);// 获得指定键的值
		int index = 0;
		TxBuffer[index++] = 0x55;
		TxBuffer[index++] = (byte) (0xaa & 0xff);
		for (int i = 0; i < 15; i++) {
			TxBuffer[index++] = value.Imsi[i];
		}
		for (int i = 0; i < value.LiftSts.length; i++) {
			TxBuffer[index++] = (byte) (value.LiftSts[i] >>> 8);
			TxBuffer[index++] = (byte) (value.LiftSts[i] & 0x00ff);
		}
		Txlength = index;
		f_TxRequest = 1;
	}

	public void PCTxData_Multi() {

		if (f_Sec == 1) {
		} else {
			return;
		}
		Iterator<String> iter = IMSI_Muti.iterator();
		int index = 0;
		// TEST-解决第一台无法显示问题
		TxBuffer[index++] = 0x66;
		TxBuffer[index++] = (byte) (0xaa & 0xff);
		TxBuffer[index++] = 0x0f;
		for (int i = 0; i < 15; i++) {
			TxBuffer[index++] = 0;
		}
		// 当前楼层
		TxBuffer[index++] = (byte) 0;
		TxBuffer[index++] = (byte) 0;
		// 门状态
		TxBuffer[index++] = (byte) 0;
		TxBuffer[index++] = (byte) 0;
		// 电梯状态
		TxBuffer[index++] = (byte) 0;
		TxBuffer[index++] = (byte) 0;
		// 故障子码
		TxBuffer[index++] = (byte) 0;
		TxBuffer[index++] = (byte) 0;
		while (iter.hasNext()) {
			liftID = (String) iter.next();
			if (map_IMSI_Lift.containsKey(liftID) == false) {
				continue;
			}
			value = map_IMSI_Lift.get(liftID);// 获得指定键的值
			TxBuffer[index++] = 0x66;
			TxBuffer[index++] = (byte) (0xaa & 0xff);
			TxBuffer[index++] = 0x0f;
			for (int i = 0; i < 15; i++) {
				TxBuffer[index++] = value.Imsi[i];
			}
			// 当前楼层
			TxBuffer[index++] = (byte) (value.LiftSts[12] >>> 8);
			TxBuffer[index++] = (byte) (value.LiftSts[12] & 0x00ff);
			// 门状态
			TxBuffer[index++] = (byte) (value.LiftSts[5] >>> 8);
			TxBuffer[index++] = (byte) (value.LiftSts[5] & 0x00ff);
			// 电梯状态
			TxBuffer[index++] = (byte) (value.LiftSts[10] >>> 8);
			TxBuffer[index++] = (byte) (value.LiftSts[10] & 0x00ff);
			// 故障子码
			TxBuffer[index++] = (byte) (value.LiftSts[11] >>> 8);
			TxBuffer[index++] = (byte) (value.LiftSts[11] & 0x00ff);
			f_TxRequest = 1;
			Txlength = index;
		}
	}

	public void PcClose() {
		// f_closeSocket = 1;
		// if (IotServer.map_IP_LoginSts.containsKey(liftID) == false) {
		// return;
		// }
		// value = map_IMSI_Lift.get(liftID);// 获得指定键的值
		// value.f_destroy = 1;
		// if (IotServer.map_IP_LoginSts.containsKey(LoginPcIp) == true) {
		// IotServer.map_IP_LoginSts.remove(LoginPcIp,
		// IotServer.map_IP_LoginSts.get(LoginPcIp));
		// }
		PcStsFresh();
	}

}
