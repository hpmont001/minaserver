
package com.mingrisoft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class LiftFaultDeal_SQL {
	Connection conn = null;

	public LiftFaultDeal_SQL(Connection conn) {
		//GetConnection connection = new GetConnection();
		//this.conn = connection.getCon();
		this.conn = conn;
	}

	// 添加数据
	public void insertData(LiftFaultRecord liftInfo) {
		try {
			PreparedStatement statement = conn.prepareStatement("insert into dbo.History_Inquiry values(?,?,?,?,?,?,?,?)");
			statement.setString(1, liftInfo.liftID);
			statement.setString(2, liftInfo.errorCode);
			statement.setString(3, liftInfo.errorState);
			statement.setString(4, liftInfo.InsMode);
			statement.setString(5, liftInfo.errorFloor);
			statement.setString(6, liftInfo.Send_message);
			statement.setString(7, liftInfo.Report_time);
			statement.setString(8, liftInfo.Release_time);
			statement.executeUpdate();
		} catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, "SQL错误-故障写入错误", "警告", JOptionPane.WARNING_MESSAGE);
			
		}
	}

	// 查询Errname
//	public String InquireErrName(String liftID) {
//		try {
//			Statement statement = conn.createStatement();
//			String sql = "select * from dbo.Elevator_table where Elevator_num =" + '\'' + liftID + '\'';
//			
//			ResultSet rest = statement.executeQuery(sql);
//			while (rest.next()) {				
//				return rest.getString(2);
//			}
//
//		} catch (SQLException e) {
//			IotServer.logger.error("", e);
//			JOptionPane.showMessageDialog(null, "SQL错误-内部编号写入错误", "警告", JOptionPane.WARNING_MESSAGE);
//			
//		}
//		return null;
//	}
	// 查询InnerID
	public String InquireInnerID(String liftID) {
		try {
			Statement statement = conn.createStatement();
			String sql = "select Inside_num from dbo.Elevator_table where Elevator_num =" + '\'' + liftID + '\'';
			
			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {				
				return rest.getString(1);
			}

		} catch (SQLException e) {
			IotServer.logger.error("", e.getCause());
			JOptionPane.showMessageDialog(null, e.getCause(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
		return null;
	}
	// 查询ErrName
	public String InquireErrorName(String Error_Code) {
		try {
			Statement statement = conn.createStatement();
			String sql = "select ErrorName from dbo.FaultHelpTable where Error_Code =" + '\'' + Error_Code + '\'';
			
			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {				
				return rest.getString(1);
			}

		} catch (SQLException e) {
			IotServer.logger.error("", e.getCause());
			JOptionPane.showMessageDialog(null, e.getCause(), "警告", JOptionPane.WARNING_MESSAGE);
			
		}
		return null;
	}


	// 查询全部故障信息
	public List<LiftFaultRecord> selectAllLiftFaultRecord() {
		List<LiftFaultRecord> list = new ArrayList<LiftFaultRecord>();
		try {
			Statement statement = conn.createStatement();
			ResultSet rest = statement.executeQuery("select * from dbo.History_Inquiry");
			while (rest.next()) {
				LiftFaultRecord liftInfo = new LiftFaultRecord();
				liftInfo.liftID = rest.getString(1);
				liftInfo.errorCode = rest.getString(2);
				liftInfo.errorState = rest.getString(3);
				liftInfo.InsMode = rest.getString(4);
				liftInfo.errorFloor = rest.getString(5);
				liftInfo.Send_message = rest.getString(6);
				liftInfo.Report_time = rest.getString(7);
				liftInfo.Release_time = rest.getString(8);
				list.add(liftInfo);		
			}

		} catch (SQLException e) {
			IotServer.logger.error("", e);
		}

		return list;
	}

	// 更改状态
	public void updateLiftFaultRecord(LiftFaultRecord liftInfo) {
		try {
			String sql = "update dbo.History_Inquiry set FaultSts= ?,Release_time= ? where Elevator_num =" 
					+ '\'' + liftInfo.liftID + '\''
					//+ " and Error_code =" + '\'' + liftInfo.errorCode + '\''
					+ " and FaultSts = '未解除'";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, liftInfo.errorState);
			statement.setString(2, liftInfo.Release_time);
			statement.executeUpdate();
		} catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, "SQL错误--故障解除写入错误", "警告", JOptionPane.WARNING_MESSAGE);
			
		}
	}
	// 更改Ins状态
	public void updateLiftInsSts(String LiftID,String InsSts,String FaultSts) {
		try {
			String sql = "update dbo.Elevator_table set InsSts= ?,FaultSts= ? where Elevator_num =" 
					+ '\'' + LiftID + '\'';
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, InsSts);
			statement.setString(2, FaultSts);
			statement.executeUpdate();
		} catch (SQLException e) {
			IotServer.logger.error("", e);
			JOptionPane.showMessageDialog(null, "SQL错误--故障解除写入错误", "警告", JOptionPane.WARNING_MESSAGE);
			
		}
	}

	// 删除信息
	public void deleteLiftFaultRecord(String userName) {
		String sql = "delete from dbo.History_Inquiry";
		try {
			Statement statement = conn.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			IotServer.logger.error("", e);
		}
	}
}
