package com.mingrisoft;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

class ComConnect {
	int f_TxRequest = 0;
	String liftID = " ";
	ArrayList<String> IMSI_Muti = new ArrayList<>();
	Protocol value = null;
	int f_50ms = 0;
	int t_10ms = 0;
	int f_Sec = 0;
	int t_Sec = 0;
	int t_TxIdleSec = 0;
	long t_Txing = 0;
	int f_destroy = 0;
	HashMap<String, Protocol> map_liftID_LiftOJ = null;

	IoSession session = null;
	LiftSts__Requst liftSts__Requst = null;
	public ComConnect(HashMap<String, Protocol> map_IMSI_Lift) {
		this.map_liftID_LiftOJ = map_IMSI_Lift;
	}

	LiftSts__Requst requestLiftID(String content){
		LiftSts__Requst liftSts__Requst = null;
		if(content.contains("@")){
			return liftSts__Requst;
		}
		try {
			//JSONObject  myJson = JSONObject.parseObject(content);
	        liftSts__Requst=(LiftSts__Requst)JSONObject.parseObject(content, LiftSts__Requst.class);
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);;
		}
		//JSONArray myJsonArray = JSONArray.parseArray(content);
		return liftSts__Requst;
	}

	public void ComRxIMSI(String content) {

		liftSts__Requst = requestLiftID(content);
		if(liftSts__Requst == null){
			return;
		}
		IMSI_Muti.clear();
		for (String is : liftSts__Requst.getElevcode()) {

			if (IMSI_Muti.contains(is) == false) {
				IMSI_Muti.add(is);
			}
			if (map_liftID_LiftOJ.containsKey(is) == true) {
				value = map_liftID_LiftOJ.get(is);// 获得指定键的值
				value.t_Monitor_ing = 10;
			}
		}
		t_TxIdleSec = liftSts__Requst.getDuration();
	}

	LiftSts_com prepareLiftSts(String LiftID){

		if (map_liftID_LiftOJ.containsKey(liftID) == false) {
			LiftSts_com liftSts_com = new LiftSts_com();
			liftSts_com.Elevator_Code = liftID;
			return liftSts_com;
		}
		value = map_liftID_LiftOJ.get(liftID);// 获得指定键的值
		String elevator_Code = liftID;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String create_Time = df.format(new Date());
		String service_Mode = "" + (value.LiftSts[10] & 0x0f);
		String status = "" + (value.LiftSts[8] & 0x0f);
		String direction = "" + ((value.LiftSts[10]>>>8) & 0x0f);
		String floor = "" + (value.LiftSts[12]);
		String door = "";
		if((value.LiftSts[5] & 0x07) == 3)
		{
			door = "" + 1;		
		}
		else{
			door = "" + 0;
		}
		String door_Zone = "";
		if(((value.LiftSts[10] >>> 4) & 0x03) == 0)
		{
			door_Zone = "" + 0;		
		}
		else{
			door_Zone = "" + 1;
		}
		String speed = "" + (value.LiftSts[0]/10);
		String passenger;
		if(((value.LiftSts[38] ) & 0x02) == 0)
		{
			passenger = "" + 0;		
		}
		else{
			passenger = "" + 1;
		}
		String passenger_Num = null;
		String power = null;
		String overload;
		if((((value.LiftSts[10] )>>>7) & 0x01) == 1)
		{
			overload = "" + 2;		
		}
		else if((((value.LiftSts[10] )>>>7) & 0x01) == 1){
			overload = "" + 1;
		}
		else{
				overload = "" + 0;
		}
		String run_Time = null;
		String self_Learning = "" + (value.LiftSts[10] & 0x0f);
		if((value.LiftSts[10] & 0x0f) == 4)
		{
			self_Learning = "" + 1;		
		}
		else if((value.LiftSts[10] & 0x0f) == 8){
			self_Learning = "" + 2;
		}
		else{
			self_Learning = "" + 0;
		}
		LiftSts_com liftSts_com1 = new LiftSts_com(elevator_Code, create_Time, service_Mode, status, direction, floor, door, door_Zone, speed, passenger, passenger_Num, power, overload, run_Time, self_Learning);
		return liftSts_com1;
	}

	public String ComTxData_Multi() {

		if(t_TxIdleSec == 0){
			t_TxIdleSec = 1;
		}
		if ((f_Sec == 1)
		&&(t_Sec % t_TxIdleSec == 0)){
		} else {
			return "";
		}
		Iterator<String> iter = IMSI_Muti.iterator();
		List<LiftSts_com> liftList = new ArrayList<LiftSts_com>();
		LiftSts_com liftSts_com = null;
		while (iter.hasNext()) {
			liftID = (String) iter.next();
			liftSts_com= prepareLiftSts(liftID);
			if(liftSts_com != null){
				liftList.add(liftSts_com);
			}
		
		}	
		//将数据封装到总计类
		LiftSts_com_Array nt = new LiftSts_com_Array(true,1,"",liftList);
		String json = JSONObject.toJSONString(nt,SerializerFeature.WriteMapNullValue);

	

		f_TxRequest = 1;
		return json;
	}

}
