
package com.mingrisoft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.swing.JOptionPane;

public class LiftStsDeal_SQL {
	Connection conn = null;

	public LiftStsDeal_SQL(Connection conn) {
		//GetConnection connection = new GetConnection();
		//this.conn = connection.getCon();
		this.conn = conn;
	}

	// 添加数据
	public void insertData(LiftStsInfo liftInfo) {
		try {
			PreparedStatement statement = conn.prepareStatement("insert into dbo.Elevator_table (Elevator_num,FaultSts,InsSts) values(?,?,?)");
			statement.setString(1, liftInfo.liftID);
			statement.setString(2, liftInfo.FaultSts);
			statement.setString(3, liftInfo.InsSts);
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
	}

	// 查询LiftSts_table LiftID是否已存在
//	public boolean CheckLiftIDExist(String liftID) {
//		try {
//			Statement statement = conn.createStatement();
//			String sql = "select * from dbo.LiftSts_table where Elevator_num =" + '\'' + liftID + '\'';
//			
//			ResultSet rest = statement.executeQuery(sql);
//			while (rest.next()) {
//				return true;
//			}
//
//		} 
//		catch (SQLException e) {
//			IotServer.logger.error("", e);
//		}
//		return false;
//	}

	// 查询 Elevator_num LiftID是否已存在
	public static boolean CheckBaseLiftIDExist(String liftID) {
		if(liftID == null
		   || liftID.isEmpty()){
			return false;
		}
		Connection conn = DBPoor.getConn();
		try {
//			GetConnection connection = new GetConnection();
//			Connection conn = connection.getCon();
			Statement statement = conn.createStatement();
			String sql = "select * from dbo.Elevator_table where Elevator_num =" + '\'' + liftID + '\'';
			
			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {
				DBPoor.closeConn(conn);
				statement.close();
				return true;
			}
			DBPoor.closeConn(conn);
			statement.close();

		} 
		catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e.toString(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
		return false;
	}

	// 查询全部信息
//	public static List<String> selectAllBaseLiftID() {
//		List<String> list = new ArrayList<String>();
//		try {
//			GetConnection connection = new GetConnection();
//			Connection conn = connection.getCon();
//			
//			Statement statement = conn.createStatement();
//			ResultSet rest = statement.executeQuery("select * from dbo.LiftSts_table");
//			while (rest.next()) {
//				String liftID = rest.getString(1);
//				list.add(liftID);
//			}
//
//		} catch (SQLException e) {
//			IotServer.logger.error("", e);
//		}
//
//		return list;
//	}

	// 查询这台电梯联系人
	public static List<String> InquireLiftContacts(String liftID) {
		Connection conn = DBPoor.getConn();
		try {
			List<String> list = new ArrayList<>();
//			GetConnection connection = new GetConnection();
//			Connection conn = connection.getCon();
			Statement statement = conn.createStatement();
			String sql = "select MaintainManName1phone,MaintainManName2phone,MaintainManName3phone from dbo.LiftAllInfo_inf where Elevator_num =" + '\'' + liftID + '\'';
			
			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {
				if(rest.getString(1) != null){
				list.add(rest.getString(1).trim());
				}
				if(rest.getString(2) != null){
				list.add(rest.getString(2).trim());
				}
				if(rest.getString(3) != null){
				list.add(rest.getString(3).trim());
				}

				DBPoor.closeConn(conn);
				statement.close();
				return list;
			}
			DBPoor.closeConn(conn);
			statement.close();

		} catch (SQLException | NullPointerException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e.toString(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
		return null;
	}

	// 查询这台电梯楼盘位置
	public static String InquireLiftPos(String liftID) {
		Connection conn = DBPoor.getConn();
		try {
//			GetConnection connection = new GetConnection();
//			Connection conn = connection.getCon();
			Statement statement = conn.createStatement();
			String sql = "select Detail_add from dbo.LiftAllInfo_inf where Elevator_num =" + '\'' + liftID + '\'';

			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {	
				if(rest.getString(1) != null){
					String string = rest.getString(1).trim();
					DBPoor.closeConn(conn);
					statement.close();
					return string;
				}			
			}
			DBPoor.closeConn(conn);
			statement.close();

		} catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e.toString(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
		return null;
	}

	// 查询电梯工号
	public static String InquireLiftID(String IMSI) {
		Connection conn = DBPoor.getConn();
		try {
//			GetConnection connection = new GetConnection();
//			Connection conn = connection.getCon();
			Statement statement = conn.createStatement();
			String sql = "select Elevator_num from dbo.Elevator_table where Register_num =" + '\'' + IMSI + '\'';
			
			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {
				String string = rest.getString(1).trim();
				DBPoor.closeConn(conn);
				statement.close();
				return string;
			}
			DBPoor.closeConn(conn);
			statement.close();

		} catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e.toString(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
		return null;
	}
	// 查询InnerID
	public String InquireInnerID(String liftID) {
		try {
			Statement statement = conn.createStatement();
			String sql = "select Inside_num from dbo.Elevator_table where Elevator_num =" + '\'' + liftID + '\'';
			
			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {	
				if(rest.getString(1) != null){
					String string = rest.getString(1).trim();
					statement.close();
					return string;
				}
			}
			statement.close();

		} catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e.toString(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
		return null;
	}
	// 查询Register_num
	public static String InquireRegister_num(String liftID) {
		Connection conn = DBPoor.getConn();
		try {
			Statement statement = conn.createStatement();
			String sql = "select Register_num from dbo.Elevator_table where Elevator_num =" + '\'' + liftID + '\'';
			
			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {		
				if(rest.getString(1) != null){
					String string = rest.getString(1).trim();
					statement.close();
					DBPoor.closeConn(conn);
					return string;
				}
			}
			statement.close();
			DBPoor.closeConn(conn);

		} catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e.toString(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
		return null;
	}


	// 查询全部信息
//	public List selectAllLiftSts() {
//		List list = new ArrayList<LiftStsInfo>();
//		try {
//			Statement statement = conn.createStatement();
//			ResultSet rest = statement.executeQuery("select Elevator_num,OnlineSts,LastDataArriveTime from dbo.Elevator_table");
//			while (rest.next()) {
//				LiftStsInfo liftInfo = new LiftStsInfo();
//				liftInfo.liftID = rest.getString(1);
//				liftInfo.OnlineSts = rest.getString(2);
//				liftInfo.LastFreshTime = rest.getString(3);
//				liftInfo.FaultSts = rest.getString(4);
//				liftInfo.InsSts = rest.getString(5);
//				list.add(liftInfo);
//			}
//
//		} catch (SQLException e) {
//			IotServer.logger.error("", e);
//		}
//
//		return list;
//	}

	// 更改状态
	public void updateLiftSts(LiftStsInfo liftInfo) {
		if(CheckBaseLiftIDExist(liftInfo.liftID) == false){
			return;			
		}
		if(CheckBaseLiftIDExist(liftInfo.liftID) == false){
			insertData(liftInfo);
			return;			
		}
		try {
			String sql = "update dbo.Elevator_table set FaultSts= ?, InsSts = ? where Elevator_num =" 
					+ '\'' + liftInfo.liftID + '\'';
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, liftInfo.FaultSts);
			statement.setString(2, liftInfo.InsSts);
			statement.executeUpdate();
			statement.close();

		} catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e.toString(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
	}
	public void updateIotModuleSts(LiftStsInfo liftInfo) {
		if(CheckBaseLiftIDExist(liftInfo.liftID) == false){
			return;			
		}
		String Register_num = InquireRegister_num(liftInfo.liftID);
		if(Register_num == null){
			return;			
		}
		
		try {
			if(liftInfo.OnlineSts.equals("在线")){
				String sql = "update dbo.Hardware_table set OnlineSts= ?, ConnectSts = ?,OnlineDate = ? where Register_num =" 
						+ '\'' + Register_num + '\'';
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, liftInfo.OnlineSts);
				statement.setString(2, liftInfo.ConnectSts);
				statement.setString(3, liftInfo.OnlineDate);
				statement.executeUpdate();
				statement.close();
			}
			else{
				String sql = "update dbo.Hardware_table set OnlineSts= ?, ConnectSts = ?,OfflineDate = ? where Register_num =" 
						+ '\'' + Register_num + '\'';
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, liftInfo.OnlineSts);
				statement.setString(2, liftInfo.ConnectSts);
				statement.setString(3, liftInfo.OfflineDate);
				statement.executeUpdate();
				statement.close();
			}
		} catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, e.toString(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
	}

	// 删除信息
//	public void deleteLiftFaultRecord(String userName) {
//		String sql = "delete from dbo.LiftSts_table";
//		try {
//			Statement statement = conn.createStatement();
//			statement.executeUpdate(sql);
//		} catch (SQLException e) {
//			IotServer.logger.error("", e);
//		}
//	}
}
