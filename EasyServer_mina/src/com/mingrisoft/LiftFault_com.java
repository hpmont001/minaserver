package com.mingrisoft;

public class LiftFault_com {
	public String Elevator_Code;
	public String Create_Time;
	public String Fault_Time;
	public String Fault_Code;
	public String Third_Fault_Code;
	public String Trapped;
	public String Fault_Reason;
	public String direction;
	public String door;
	public String floor;
	public String speed;
	
	public LiftFault_com() {
		super();
		// TODO Auto-generated constructor stub
	}


	public LiftFault_com(String elevator_Code, String create_Time, String fault_Time, String fault_Code,
			String third_Fault_Code, String trapped, String fault_Reason) {
		super();
		Elevator_Code = elevator_Code;
		Create_Time = create_Time;
		Fault_Time = fault_Time;
		Fault_Code = fault_Code;
		Third_Fault_Code = third_Fault_Code;
		Trapped = trapped;
		Fault_Reason = fault_Reason;
	}

	
}
