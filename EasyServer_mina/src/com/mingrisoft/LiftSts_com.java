package com.mingrisoft;

public class LiftSts_com {
	public String Elevator_Code;
	public String Create_Time;
	public String Service_Mode;
	public String Status;
	public String Direction;
	public String Floor;
	public String Door;
	public String Door_Zone;
	public String Speed;
	public String Passenger;
	public String Passenger_Num;
	public String Power;
	public String Overload;
	public String Run_Time;
	public String Self_Learning;
	
	public LiftSts_com() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LiftSts_com(String elevator_Code, String create_Time, String service_Mode, String status, String direction,
			String floor, String door, String door_Zone, String speed, String passenger, String passenger_Num,
			String power, String overload, String run_Time, String self_Learning) {
		
		Elevator_Code = elevator_Code;
		Create_Time = create_Time;
		Service_Mode = service_Mode;
		Status = status;
		Direction = direction;
		Floor = floor;
		Door = door;
		Door_Zone = door_Zone;
		Speed = speed;
		Passenger = passenger;
		Passenger_Num = passenger_Num;
		Power = power;
		Overload = overload;
		Run_Time = run_Time;
		Self_Learning = self_Learning;
	}
	
}
