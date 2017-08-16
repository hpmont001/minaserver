package com.mingrisoft;

import java.util.HashMap;

public class Protocol {
	//String Imsi = "";
	byte Imsi[] = new byte[18];
	int LiftSts[] = new int[100];
	//地址	名称	范围
	//51200（高8位从200开始）	电梯实际速度	0.000~4.000
	//51201	运行转速	0~9999转/分
	//51202	输出电压	0~999V
	//51203	输出电流	0.1~999.9A
	//51204	输出频率	0.01Hz~100.00Hz
	//51205	门机状态	BIT0~BIT2：前门门机状态
//			000：开门中
//			001：开门到位
//			010：关门中
//			011：关门到位
//			100：门机故障 
//			101:门机停机
//			BIT3~BIT5：后门门机状态
//			000：开门中
//			001：开门到位
//			010：关门中
//			011：关门到位
//			100：门机故障
//			101：门机停机
	//51206	运行次数高位	
	//51207	运行次数低位	
	//51208	运行停机	1 运行0 停机
	//51209	群控模式	1 群控，0单梯
	//51210	电梯状态	该功能码由一个16个二进制数组成，其中从最低位到最高位分别代表
//			BIT0~BIT3：电梯状态
//			 0000：自动
//			 0001：检修
//			 0010：应急运行
//			 0011：井道自学习
//			 0100：消防返基站
//			 0101：消防员模式
//			 0110：司机模式
//			 0111：独立运行
//			 1000：自动返平层
//			1001：VIP运行
//			
//			BIT4：上平层
//			BIT5：下平层
//			BIT6：门锁
//			Bit7：系统超载
//			BIT11-BIT8 运行方向
//			0：停止
//			1：上行
//			2：下行
//			BIT12：故障
//			BIT13：系统满载
//			BIT14：锁梯
//			BIT15：安全回路
	//51211	故障子码	
	//51212	当前楼层	
	//51213	16~1内召楼层登记状态	16个二进制数代表该层有无登记
	//51214	32~17内召楼层登记状态	=1：该层有登记
	//51215	48~33内召楼层登记状态	=0：该层无登记
	//51216	16~1外召上行楼层登记状态	
	//51217	32~17外召上行楼层登记状态	
	//51218	48~33外召上行楼层登记状态	
	//51219	16~1外召下行楼层登记状态	
	//51220	32~17外召下行楼层登记状态	
	//51221	48~33外召下行楼层登记状态	
	//51222	总楼层	0-99
	//    Uint16 CTBIo1;				//51223		//D03.01 Io1
	//    Uint16 CTBIo2;				//24		//Do3.02 io2
	//    Uint16 CTBInGroup1; 		//25			//D03.03 轿顶板输入状态显示1                             
	//    Uint16 CTBInGroup2; 		//26			//D03.04 轿顶板输入状态显示2                                        
	//    Uint16 CTBOutGroup1;		//27			//D03.05 轿顶板输出状态显示2 
	//    Uint16 CTBOutGroup2;		//28			//D03.06轿顶板板输出状态显示3	
	// Uint16 MCBIo1;				//29 		//D02.01 IO1                        
	// Uint16 MCBIo2;				//30 		//D02.02 IO2
	// Uint16 MCBInGroup1; 			//31 		//D02.03 主控板输入状态显示1                             
	//	 Uint16 MCBInGroup2; 			 //32		//D02.04 主控板输入状态显示2
	//	 Uint16 MCBInGroup3;			 //33		//D02.05 主控板输入状态显示3                                      
	// Uint16 MCBOutGroup;			 //34		//D02.06 主控板输出状态显示1 
	// u16 f_MCBSts;       //35   与主板通讯状态
//    u16 NetSts;       //36   信号质量
//    u16 RunningRTimesLimit;       //37   运行次数限定值
//    u16 ErrMaskE68;       //38 状态字   bit0：保留             bit1：轿厢有人

	int ErrcodeBak;
	int InsBak;
	int LiftPara[] = new int[200];
	int t_Monitor_ing;//
	boolean f_FreshLiftPara;
	int c_FreshLiftPara;//连续读取10次
	String f_ComErrLockLift;//0无效、1不锁梯、2锁梯
	int c_ComErrLockLift;//连续10次
	String f_KeyPadPort;
	int c_KeyPadPort;//连续10次
	int f_LockLift;
	int c_LockLift;//连续10次
	int RunningTimesLimit;//
	String socketName;//
	int f_closeSocket;
	int f_destroy;
	
	public Protocol(){
		t_Monitor_ing = 3;//上电读取6S数据
		ErrcodeBak = 9999;//上电时清除上次记录的故障
		InsBak = 9999;//上电时清除检修记录
		f_FreshLiftPara = true;
	}
}
