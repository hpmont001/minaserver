package com.mingrisoft;

public class LiftFaultRecord {
	String liftID = null;
	String innerID = null;
	String errorState = null;
	String errorName = null;
	String errorCode = null;
	String errorFloor = null;
	String InsMode = null;
	String Description = null;
	String Send_message = null;
	String Report_time = null;
	String Release_time = null;
	
	public LiftFaultRecord(){
		
	}
	
	public LiftFaultRecord(String liftID,String innerID,String errorState,String errorName,String errorCode,String nowFloor,String check_State,String Description,String Send_message,String Report_time,String managerLimit){
		this.liftID = liftID;
		this.innerID = innerID;
		this.errorState = errorState;
		this.errorName = errorName;
		this.errorCode = errorCode;
		this.errorFloor = nowFloor;
		this.InsMode = check_State;
		this.Description = Description;
		this.Send_message = Send_message;
		this.Report_time = Report_time;
		this.Release_time = managerLimit;
	}
	
	

}
