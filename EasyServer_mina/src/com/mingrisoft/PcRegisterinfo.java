package com.mingrisoft;

public class PcRegisterinfo {
	String AccountName = null;
	String password = null;
	String userName = null;
	String phone = null;
	String RoleName = null;
	String company = null;
	String email = null;
	String addr = null;
	
	public PcRegisterinfo(){
		
	}

	public PcRegisterinfo(String AccountName,String userName,String phone,String password,String RoleName,String company,String email,String addr){
		this.AccountName = AccountName;
		this.password = password;
		this.userName = userName;
		this.phone = phone;
		this.RoleName = RoleName;
		this.company = company;
		this.email = email;
		this.addr = addr;
	}
}
