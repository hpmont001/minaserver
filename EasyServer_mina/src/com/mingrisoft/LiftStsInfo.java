package com.mingrisoft;

public class LiftStsInfo {
	String liftID = null;
	String OnlineSts = null;
	String ConnectSts = null;
	String OnlineDate = null;
	String FaultSts = null;
	String InsSts = null;
	String OfflineDate = null;
	
	public LiftStsInfo(){
		
	}
	
	public LiftStsInfo(String liftID,String OnlineSts,String OnlineDate,String FaultSts,String InsSts){
		this.liftID = liftID;
		this.OnlineSts = OnlineSts;
		this.OnlineDate = OnlineDate;
		this.FaultSts = FaultSts;
		this.InsSts = InsSts;
	}
}
