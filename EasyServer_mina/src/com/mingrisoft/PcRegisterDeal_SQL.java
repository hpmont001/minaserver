package com.mingrisoft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PcRegisterDeal_SQL {
	Connection conn = null;

	public PcRegisterDeal_SQL(Connection conn) {
//		GetConnection connection = new GetConnection();
//		this.conn = connection.getCon();
		this.conn = conn;
	}

	// 添加数据
	public void insertPcRegister(PcRegisterinfo pcRegister) {
		try {
			PreparedStatement statement = conn.prepareStatement("insert into dbo.AccountManage (AccountName,Password,UserName,UserPhoneNum,RoleName,company,email,addr) values(?,?,?,?,?,?,?,?)");
			statement.setString(1, pcRegister.AccountName);
			statement.setString(2, pcRegister.password);
			statement.setString(3, pcRegister.userName);
			statement.setString(4, pcRegister.phone);
			statement.setString(5, pcRegister.RoleName);
			statement.setString(6, pcRegister.company);
			statement.setString(7, pcRegister.email);
			statement.setString(8, pcRegister.addr);
			statement.executeUpdate();
		} catch (SQLException e) {
			IotServer.logger.error("", e);
		}
	}

	// 查询用户名是否已存在
	public boolean CheckUserNameExist(String AccountName) {
		try {
			Statement statement = conn.createStatement();
			String sql = "select * from dbo.AccountManage where AccountName =" + '\'' + AccountName + '\'';
			
			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {
				return true;
			}

		} catch (SQLException e) {
			IotServer.logger.error("", e);
		}
		return false;
	}

	// 用户名密码验证
	public ResultSet CheckAccount(String AccountName, String password) {
		try {
			Statement statement = conn.createStatement();
			String sql = "select password,RoleName from dbo.AccountManage where AccountName =" + '\'' + AccountName.trim() + '\'';
			
			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {
				String password_db = rest.getString(1);
				if(password_db == null){
					return null;
				}
				password_db = password_db.trim();
				String RoleName = rest.getString(2);
				if(RoleName == null){
					return null;
				}
				if(RoleName.trim().isEmpty()){
					return null;
				}
				if(password_db.equals(password)){
					return rest;
				}
			}

		} catch (SQLException e) {
			IotServer.logger.error("", e);
		}
		return null;
	}

	// 查询全部注册信息
	public List<PcRegisterinfo> selectPcRegisterInfo() {
		List<PcRegisterinfo> list = new ArrayList<PcRegisterinfo>();
		try {
			Statement statement = conn.createStatement();
			ResultSet rest = statement.executeQuery("select * from dbo.AccountManage");
			while (rest.next()) {
				PcRegisterinfo pcRegister = new PcRegisterinfo();
				pcRegister.AccountName = rest.getString(1);
				pcRegister.password = rest.getString(2);
				pcRegister.userName = rest.getString(3);
				pcRegister.phone = rest.getString(4);
				pcRegister.RoleName = rest.getString(5);
				pcRegister.company = rest.getString(6);
				pcRegister.email = rest.getString(7);
				pcRegister.addr = rest.getString(8);
				list.add(pcRegister);
			}

		} catch (SQLException e) {
			IotServer.logger.error("", e);
		}

		return list;
	}

	// 更改注册状态
//	public void updateJoinDepot(PcRegisterinfo pcRegister, String AccountName) {
//		try {
//			String sql = "update dbo.Manage_Limit set manage_Limit= ?,register= ? where userName =" + '\'' + userName
//					+ '\'';
//			PreparedStatement statement = conn.prepareStatement(sql);
//			statement.setString(1, pcRegister.managerLimit);
//			statement.setString(2, pcRegister.register);
//			statement.executeUpdate();
//		} catch (SQLException e) {
//			IotServer.logger.error("", e);
//		}
//	}

	// 删除注册信息
	public void deleteJoinDepot(String AccountName) {
		String sql = "delete from dbo.AccountManage where AccountName =" + '\'' + AccountName + '\'';
		// String sql = "delete from dbo.Manage_Limit where userName =" +
		// userName;
		try {
			Statement statement = conn.createStatement();
			System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			IotServer.logger.error("", e);
		}
	}
}
