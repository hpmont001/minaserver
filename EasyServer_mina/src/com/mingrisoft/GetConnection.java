package com.mingrisoft;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

import javax.swing.JOptionPane;

public class GetConnection {
	private String driver = null;
	private Connection con; // 定义数据库连接类对象
	private PreparedStatement pstm;
	private String user = "sa"; // 连接数据库用户名
	private String pass = ""; // 连接数据库密码
	//private String className = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // 数据库驱动
	private String url = "jdbc:sqlserver://localhost:1433;DatabaseName=db_supermarket"; // 连接数据库的URL

	public void initParam(String paramFile) throws Exception {
		// 使用Properties类来加载属性文件
		Properties props = new Properties();
		props.load(new FileInputStream(paramFile));
		driver = props.getProperty("driverClassName");
		url = props.getProperty("url");
		user = props.getProperty("username");
		pass = props.getProperty("password");
	}

	public GetConnection() {

		if (driver==(null)) {
			try {
				File directory = new File(""); 
				String myServerPath = directory.getCanonicalPath();
				String myServerFilsePath = "/mysql.ini";
				
				//File file = new File("/D:/IOTService/mysql.ini");
				
				File file = new File(myServerPath + myServerFilsePath);
				initParam(myServerPath + myServerFilsePath);
				//initParam("/D:/IOTService/mysql.ini");
				//initParam("src/com/mingrisoft/mysql.ini");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "SQL配置文件打开失败", "警告", JOptionPane.WARNING_MESSAGE);
				//e1.printStackTrace();
			}
		}
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "加载数据库驱动失败", "警告", JOptionPane.WARNING_MESSAGE);
			System.out.println("加载数据库驱动失败！");
			IotServer.logger.error("", e);
		}
	}

	/** 创建数据库连接 */
	public Connection getCon() {

		if (driver == (null)) {
			try {
				File directory = new File(""); 
				String myServerPath = directory.getCanonicalPath();
				String myServerFilsePath = "/mysql.ini";
				
				//File file = new File("/D:/IOTService/mysql.ini");
				
				File file = new File(myServerPath + myServerFilsePath);
				initParam(myServerPath + myServerFilsePath);
				//initParam("/D:/IOTService/mysql.ini");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "SQL配置文件打开失败", "警告", JOptionPane.WARNING_MESSAGE);
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			con = DriverManager.getConnection(url, user, pass); // 获取数据库连接
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "创建数据库连接失败", "警告", JOptionPane.WARNING_MESSAGE);
			System.out.println("创建数据库连接失败！");
			con = null;
			IotServer.logger.error("", e);
		}
		return con; // 返回数据库连接对象
	}

	public void doPstm(String sql, Object[] params) {
		if (sql != null && !sql.equals("")) {
			if (params == null)
				params = new Object[0];
			getCon();
			if (con != null) {
				try {
					System.out.println(sql);
					pstm = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					for (int i = 0; i < params.length; i++) {
						pstm.setObject(i + 1, params[i]);
					}
					pstm.execute();
				} catch (SQLException e) {
					System.out.println("doPstm()方法出错！");
					IotServer.logger.error("", e);
				}
			}
		}
	}

	public ResultSet getRs() throws SQLException {
		return pstm.getResultSet();
	}

	public int getCount() throws SQLException {
		return pstm.getUpdateCount();
	}

	public void closed() {
		try {
			if (pstm != null)
				pstm.close();
		} catch (SQLException e) {
			System.out.println("关闭pstm对象失败！");
			IotServer.logger.error("", e);
		}
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("关闭con对象失败！");
			IotServer.logger.error("", e);
		}
	}
}
