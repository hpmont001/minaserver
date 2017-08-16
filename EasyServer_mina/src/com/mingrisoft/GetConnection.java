package com.mingrisoft;

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

import javax.swing.JOptionPane;

public class GetConnection {
	private String driver = null;
	private Connection con; // �������ݿ����������
	private PreparedStatement pstm;
	private String user = "sa"; // �������ݿ��û���
	private String pass = ""; // �������ݿ�����
	//private String className = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // ���ݿ�����
	private String url = "jdbc:sqlserver://localhost:1433;DatabaseName=db_supermarket"; // �������ݿ��URL

	public void initParam(String paramFile) throws Exception {
		// ʹ��Properties�������������ļ�
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
				JOptionPane.showMessageDialog(null, "SQL�����ļ���ʧ��", "����", JOptionPane.WARNING_MESSAGE);
				//e1.printStackTrace();
			}
		}
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "�������ݿ�����ʧ��", "����", JOptionPane.WARNING_MESSAGE);
			System.out.println("�������ݿ�����ʧ�ܣ�");
			IotServer.logger.error("", e);
		}
	}

	/** �������ݿ����� */
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
				JOptionPane.showMessageDialog(null, "SQL�����ļ���ʧ��", "����", JOptionPane.WARNING_MESSAGE);
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			con = DriverManager.getConnection(url, user, pass); // ��ȡ���ݿ�����
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "�������ݿ�����ʧ��", "����", JOptionPane.WARNING_MESSAGE);
			System.out.println("�������ݿ�����ʧ�ܣ�");
			con = null;
			IotServer.logger.error("", e);
		}
		return con; // �������ݿ����Ӷ���
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
					System.out.println("doPstm()��������");
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
			System.out.println("�ر�pstm����ʧ�ܣ�");
			IotServer.logger.error("", e);
		}
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("�ر�con����ʧ�ܣ�");
			IotServer.logger.error("", e);
		}
	}
}
