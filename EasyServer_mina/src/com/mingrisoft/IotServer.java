package com.mingrisoft;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

//import tcpip_mina.Demo1Server;

import com.mingrisoft.LiftStsDeal_SQL;
//import com.myScreen.BorderTest;

import httptest.ServerProtocolCodecFactory;
import httptest.WebSocketUtil;

public class IotServer extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int SessionIdle_S = 150;
	private static final int CicTxTimeout_mS = 50000;
	private static final int PcTxTimeout_mS = 5000;
	public static Logger logger = Logger.getLogger(IotServer.class);
	private static String Log4jP = "log4j.properties";
	public static int CICPort = 0;
	public static int PCPort = 0;
	public static int httpPort = 8000;
	public static boolean f_CicIteratoring = false;
	public static boolean f_PcIteratoring = false;
	// public static boolean f_PcIteratoring = false;

	private JButton button_0 = null;
	private JButton button_1 = null;
	private JButton button_2 = null;
	private JButton button_3 = null;
	private JButton button_4 = null;
	private JPanel pane = null;
	private JScrollPane s_pan = null;
	// public static int f_50ms = 0;
	public static int t_10ms = 0;
	// public static int f_Sec = 0;
	// public static int t_Sec = 0;

	// GetConnection connection = null;
	// Connection conn = null;

	static JTextArea ta_info2Cic;
	static JTextArea ta_info2Pc;
	// private ServerSocket server; // 声明ServerSocket对象
	// private Socket socket; // 声明Socket对象socket
	// private ServerSocket server2PC; // 声明ServerSocket对象
	// private Socket socket2PC; // 声明Socket对象socket
	public static HashMap<String, Protocol> map_IMSI_Lift = new HashMap<>();// 用于存储连接到服务器的用户和客户端套接字对象

	// public static HashMap<String, String> map_IP_LoginSts = new
	// HashMap<>();//

	// ArrayList<PcRegisterinfo> pcArraylist = null;
	// PcRegisterDeal_SQL pcDbConn = null;
	// LiftFaultDeal_SQL liftDbConn = null;

	// AccountCheck accountCheck = null;

	public void initParam(String paramFile) throws Exception {
		// 使用Properties类来加载属性文件
		Properties props = new Properties();
		props.load(new FileInputStream(paramFile));
		PCPort = Integer.valueOf(props.getProperty("PCPort"));
		CICPort = Integer.valueOf(props.getProperty("CICPort"));
		httpPort = Integer.valueOf(props.getProperty("httpPort"));
	}

	@SuppressWarnings("unused")
	private static String toStringMethod(byte[] arr, int startIndex, int length) {
		// 自定义一个字符缓冲区，
		StringBuilder sb = new StringBuilder();
		// sb.append("[ ");
		// 遍历int数组，并将int数组中的元素转换成字符串储存到字符缓冲区中去
		for (int i = 0; i < length; i++) {
			sb.append((arr[startIndex + i] & 0xff) + " ");
		}
		return sb.toString();
	}

	// byte数组转16进制
	public static String byte2Hex(byte[] b, int start, int length) {
		StringBuilder ImsiTtmp = new StringBuilder();
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i + start] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ImsiTtmp.append(hex.toUpperCase());
			ImsiTtmp.append(' ');
		}
		return ImsiTtmp.toString();
	}

	// byte2char
	public static String byte2Char(byte[] b, int start, int length) {
		StringBuilder ImsiTtmp = new StringBuilder();
		for (int i = 0; i < 15; i++) {
			char c = (char) (b[i + start]);
			ImsiTtmp.append(c);
		}
		return ImsiTtmp.toString();
	}

	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			// System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static void liftOnlineCheck(IotServer frame) {
		// GetConnection connection = new GetConnection();
		Connection conn = DBPoor.getConn();// connection.getCon();
		LiftStsInfo liftStsInfo = new LiftStsInfo();
		String liftID = "";
		String OnlineSts = "";
		Protocol value = null;
		try {
			Statement statement = conn.createStatement();
			String sql = "select Elevator_num,OnlineSts from dbo.LiftAllInfo_inf";

			ResultSet rest = statement.executeQuery(sql);
			while (rest.next()) {

				if (rest.getString(1) == null) {
					continue;
				}
				if (rest.getString(2) == null) {
					liftStsInfo.liftID = rest.getString(1).trim();
					liftStsInfo.FaultSts = "否";
					liftStsInfo.InsSts = "否";
					liftStsInfo.ConnectSts = "否";
					liftStsInfo.OnlineSts = "离线";
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
					liftStsInfo.OfflineDate = df.format(new Date());
					// liftStsInfo.OfflineDate = "否";
					LiftStsDeal_SQL dbConn = new LiftStsDeal_SQL(DBPoor.getConn());
					dbConn.updateLiftSts(liftStsInfo);
					dbConn.updateIotModuleSts(liftStsInfo);
					DBPoor.closeConn(dbConn.conn);
					continue;
				}
				liftID = rest.getString(1).trim();
				OnlineSts = rest.getString(2).trim();
				boolean liftInMap = IotServer.map_IMSI_Lift.containsKey(liftID);
				boolean liftInOnlineTable = OnlineSts.contains("在线");
				if (liftInMap && !liftInOnlineTable) {
					value = IotServer.map_IMSI_Lift.get(liftID);// 获得指定键的值

					liftStsInfo.liftID = liftID;
					if (value.LiftSts[11] != 0) {
						liftStsInfo.FaultSts = "是";
					} else {
						liftStsInfo.FaultSts = "否";
					}
					if ((value.LiftSts[10] & 0x0f) == 1) {
						liftStsInfo.InsSts = "是";
					} else {
						liftStsInfo.InsSts = "否";
					}
					if ((value.LiftSts[35] & 0x0f) == 1) {
						liftStsInfo.ConnectSts = "是";
					} else {
						liftStsInfo.ConnectSts = "否";
					}
					liftStsInfo.OnlineSts = "在线";
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
					liftStsInfo.OnlineDate = df.format(new Date());
					LiftStsDeal_SQL dbConn = new LiftStsDeal_SQL(DBPoor.getConn());
					dbConn.updateLiftSts(liftStsInfo);
					dbConn.updateIotModuleSts(liftStsInfo);
					DBPoor.closeConn(dbConn.conn);
					//
					// liftStsInfo.LastFreshTime = df.format(new Date());
				} else if (!liftInMap && liftInOnlineTable) {
					liftStsInfo.liftID = liftID;
					liftStsInfo.FaultSts = "否";
					liftStsInfo.InsSts = "否";
					liftStsInfo.ConnectSts = "否";
					liftStsInfo.OnlineSts = "离线";
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
					liftStsInfo.OfflineDate = df.format(new Date());
					// liftStsInfo.OfflineDate = "否";
					LiftStsDeal_SQL dbConn = new LiftStsDeal_SQL(DBPoor.getConn());
					dbConn.updateLiftSts(liftStsInfo);
					dbConn.updateIotModuleSts(liftStsInfo);
					DBPoor.closeConn(dbConn.conn);
				}
			}

			DBPoor.closeConn(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("", e);
			JOptionPane.showMessageDialog(null, "SQL错误-在线离线写入错误", "警告", JOptionPane.WARNING_MESSAGE);

		}

	}

	static void CicDeal() {
		try {

			Set<String> set = MyServerHandler.map_Secket_Cic.keySet();// 获得集合中键的Set视图
			Iterator<String> it = set.iterator();// 获得迭代器对象
			while (it.hasNext()) { // 迭代器中有元素，则执行循环体
				String key = it.next(); // 获得下一个键的名称
				CicConnect value = MyServerHandler.map_Secket_Cic.get(key);// 获得指定键的值
				if (value.f_destroy == 1) {
					logger.info(key);
					MyServerHandler.map_Secket_Cic.remove(key, value);
					break;// 有删除动作，必须跳出迭代
				}

				if (value.liftID != null) {
					value.CICTxData();
					if (value.f_TxRequest == 1) {
						byte txBuffer[] = new byte[value.Txlength];
						System.arraycopy(value.TxBuffer, 0, txBuffer, 0, value.Txlength);
						value.session.write(txBuffer);
						if (value.t_Txing == 0) {
							value.t_Txing = System.currentTimeMillis();
						}
						value.f_TxRequest = 0;

						logger.debug(
								value.liftID + " Tx:" + IotServer.byte2Hex(value.TxBuffer, 0, value.Txlength) + "\n");

					}
					// if(value.t_Txing != 0){
					// if((System.currentTimeMillis() - value.t_Txing) >=
					// CicTxTimeout_mS){
					// value.CICClose();
					// value.session.closeNow();
					// break;// 有删除动作，必须跳出迭代
					// }
					// }
					value.f_Sec = 0;
				}

				value.t_10ms++;
				if (value.t_10ms >= 100) {
					value.t_10ms = 0;
					value.t_Sec++;
					if (value.t_Sec >= 3600) {
						value.t_Sec = 0;
					}
					value.f_Sec = 1;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	static void PcDeal() {
		try {
			Set<String> set = MyServerHandler.map_Socket_Pc.keySet();// 获得集合中键的Set视图
			Iterator<String> it = set.iterator();// 获得迭代器对象
			while (it.hasNext()) { // 迭代器中有元素，则执行循环体
				String key = it.next(); // 获得下一个键的名称
				PcConnect value = MyServerHandler.map_Socket_Pc.get(key);// 获得指定键的值
				if (value.f_destroy == 1) {
					MyServerHandler.map_Socket_Pc.remove(key, value);
					break;// 有删除动作，必须跳出迭代
				}
				if (value.f_LoginSuccess == 1) {
					if (value.f_SinglePara == 1) {
						value.PCTxPara_Single();
						value.f_SinglePara = 0;
					} else if (value.f_SingleData == 1) {
						value.PCTxData_Single();
					} else if (value.IMSI_Muti.isEmpty() == false) {
						value.PCTxData_Multi();
					}
					if (value.f_TxRequest == 1) {
						byte txBuffer[] = new byte[value.Txlength];
						System.arraycopy(value.TxBuffer, 0, txBuffer, 0, value.Txlength);
						value.session.write(txBuffer);
						value.f_TxRequest = 0;
						logger.debug("Tx2Pc:" + byte2Hex(value.TxBuffer, 0, value.Txlength) + "\n");
						if (value.t_Txing == 0) {
							value.t_Txing = System.currentTimeMillis();
						}
						value.f_TxRequest = 0;

					}

					if (value.t_Txing != 0) {
						if ((System.currentTimeMillis() - value.t_Txing) >= PcTxTimeout_mS) {
							value.PcClose();
							value.session.closeNow();
							break;// 有删除动作，必须跳出迭代
						}
					}
					value.f_Sec = 0;
				} else {

				}

				value.t_10ms++;
				if (value.t_10ms >= 100) {
					value.t_10ms = 0;
					value.t_Sec++;
					if (value.t_Sec >= 3600) {
						value.t_Sec = 0;
					}
					value.f_Sec = 1;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	static void ComDeal() {
		try {
			Set<String> set = MyServerHandler.map_Socket_Com.keySet();// 获得集合中键的Set视图
			Iterator<String> it = set.iterator();// 获得迭代器对象
			while (it.hasNext()) { // 迭代器中有元素，则执行循环体
				String key = it.next(); // 获得下一个键的名称
				ComConnect value = MyServerHandler.map_Socket_Com.get(key);// 获得指定键的值
				if (value.f_destroy == 1) {
					MyServerHandler.map_Socket_Com.remove(key, value);
					break;// 有删除动作，必须跳出迭代
				}
				String txJson = null;
				if (value.IMSI_Muti.isEmpty() == false) {
					txJson = value.ComTxData_Multi();
				}
				if (value.f_TxRequest == 1) {
					value.session.write(WebSocketUtil.encode(txJson));
					value.f_TxRequest = 0;
					logger.debug("Tx2Pc:" + txJson + "\n");
					if (value.t_Txing == 0) {
						value.t_Txing = System.currentTimeMillis();
					}
					value.f_TxRequest = 0;

				}

				if (value.t_Txing != 0) {
					if ((System.currentTimeMillis() - value.t_Txing) >= PcTxTimeout_mS) {
						value.session.closeNow();
						break;// 有删除动作，必须跳出迭代
					}
				}
				value.f_Sec = 0;

				value.t_10ms++;
				if (value.t_10ms >= 100) {
					value.t_10ms = 0;
					value.t_Sec++;
					if (value.t_Sec >= 3600) {
						value.t_Sec = 0;
					}
					value.f_Sec = 1;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	static void iotTask(IotServer frame) {
		f_CicIteratoring = true;
		CicDeal();
		f_CicIteratoring = false;

		f_PcIteratoring = true;
		PcDeal();
		f_PcIteratoring = false;

		ComDeal();
		if (t_10ms % 1000 == 990) {
			// liftOnlineCheck(frame);
		} else if (t_10ms % 1000 == 10) {
			// liftOnlineCheck(frame);
		}

	}

	private static void initLog4jProperties() {
		// 未打包时读取配置
		// String file = this.getClass().getClassLoader()
		// .getResource(Log4jP).getFile();
		// if(new java.io.File(file).exists())
		// {
		// PropertyConfigurator.configure(file);
		// System.out.println("未打包时读取配置");
		// return;
		// }

		// 读取jar包外配置文件
		String file = System.getProperty("user.dir") + "/" + Log4jP;
		if (new java.io.File(file).exists()) {
			PropertyConfigurator.configure(file);
			System.out.println("读取jar包外配置文件");
			return;
		} else {

			JOptionPane.showMessageDialog(null, "读取jar包外配置文件失败" + file, "警告", JOptionPane.WARNING_MESSAGE);

		}

	}

	public static void main(String args[]) {
		// PropertyConfigurator.configure(System.getProperty("user.dir") +
		// "/conf/log4j.properties");
		initLog4jProperties();
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		IotServer frame = new IotServer();
		frame.setVisible(true);
		try {
			File directory = new File("");
			String myServerPath = directory.getCanonicalPath();
			String myServerFilsePath = "/mysql.ini";

			// File file = new File("/D:/IOTService/mysql.ini");

			File file = new File(myServerPath + myServerFilsePath);
			if (file.exists() == false) {
				// 创建文件夹
				File f = new File(myServerPath);
				if (!f.exists()) {
					f.mkdirs();
				}
				file.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				String str = "PCPort = 8090" + "\r\n" + "CICPort= 8080" + "\r\n"
						+ "driver=com.microsoft.sqlserver.jdbc.SQLServerDriver" + "\r\n"
						+ "url=jdbc:sqlserver://127.0.0.1;instanceName = SQLEXPRESS;databaseName=hpmoont_database"
						+ "\r\n" + "user=sa" + "\r\n" + "pass=123456" + "\r\n";
				byte txt[] = str.getBytes();
				fileOutputStream.write(txt);
				fileOutputStream.close();
				// frame.initParam("/D:/IOTService/mysql.ini");
				frame.initParam(myServerPath + myServerFilsePath);
			} else {
				// frame.initParam("/D:/IOTService/mysql.ini");
				frame.initParam(myServerPath + myServerFilsePath);
			}

		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "配置文件打开失败", "警告", JOptionPane.WARNING_MESSAGE);

			logger.info(e1);
			// e1.printStackTrace();
			System.exit(0);

		}

		IoAcceptor myAcceptor = null;
		try {

			// 创建一个非阻塞的server端的Socket
			myAcceptor = new NioSocketAcceptor();
			// 设置过滤器（使用Mina提供的文本换行符编解码器）
			// myAcceptor.getFilterChain().addLast("codec", new
			// ProtocolCodecFilter(new IotCodecFactory()));
			// myAcceptor.getSessionConfig().setWriteTimeout(10000);

		//	myAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
			myAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerProtocolCodecFactory()));
			// myAcceptor.getFilterChain().addLast("executor", new
			// ExecutorFilter());

			// 设置读取数据的缓冲区大小
			// cicAcceptor.getSessionConfig().setMinReadBufferSize(3);
			myAcceptor.getSessionConfig().setMaxReadBufferSize(2048);
			// 读写通道180秒内无操作进入空闲状态
			myAcceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, SessionIdle_S);
			// 绑定逻辑处理器
			myAcceptor.setHandler(new MyServerHandler());
			// 绑定端口
			myAcceptor.bind(new InetSocketAddress(CICPort));
			logger.info("CICPort启动成功...     端口号为：" + CICPort);
			myAcceptor.bind(new InetSocketAddress(PCPort));
			logger.info("PCPort启动成功...     端口号为：" + PCPort);
			myAcceptor.bind(new InetSocketAddress(httpPort));
			logger.info("httpPort启动成功...     端口号为：" + httpPort);
		} catch (Exception e) {
			logger.error("网络启动异常....", e);
			JOptionPane.showMessageDialog(null, e, "警告", JOptionPane.WARNING_MESSAGE);

			// server.closed();
			System.exit(0);
			logger.error("", e);
		}
		// frame.createSocket2Cic();
		// frame.createSocket2PC();

		while (true) {
			// CicDeal cicDeal =
			// map_IMSI_Cic.get(session.getRemoteAddress().toString());//
			// 获得指定键的值

			try {
				iotTask(frame);
				t_10ms++;
				Thread.sleep(10);
			} catch (InterruptedException e) {
				logger.error("Main方法异常");
				JOptionPane.showMessageDialog(null, "Main方法异常", "警告", JOptionPane.WARNING_MESSAGE);

			}

		}
	}

	/**
	 * Create the frame
	 */
	public IotServer() {
		super();
		addWindowListener(new WindowAdapter() {
			public void windowIconified(final WindowEvent e) {
				setVisible(false);
			}
		});
		setTitle("物联网服务器");
		// this.setLayout(new FlowLayout());
		setBounds(100, 100, 600, 266);

		ta_info2Cic = new JTextArea();
		ta_info2Cic.setPreferredSize(new Dimension(300, 320));

		ta_info2Pc = new JTextArea();
		ta_info2Pc.setPreferredSize(new Dimension(300, 320));

		button_0 = new JButton("注册信息");
		button_0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PcRegisterDeal_SQL dbConn = new PcRegisterDeal_SQL(DBPoor.getConn());
				ArrayList<PcRegisterinfo> pcArraylist = (ArrayList<PcRegisterinfo>) dbConn.selectPcRegisterInfo();
				DBPoor.closeConn(dbConn.conn);
				new AccountCheck(pcArraylist);
			}
		});

		button_1 = new JButton("故障信息");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LiftFaultDeal_SQL dbConn = new LiftFaultDeal_SQL(DBPoor.getConn());
				ArrayList<LiftFaultRecord> pcArraylist = (ArrayList<LiftFaultRecord>) dbConn.selectAllLiftFaultRecord();
				DBPoor.closeConn(dbConn.conn);
				new LiftFaultTable(pcArraylist);
			}
		});

		button_2 = new JButton("Pc监控信息");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PcMonitor(ta_info2Pc);
			}
		});

		button_3 = new JButton("清空");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ta_info2Cic.setText(null);
				CicConnect.CicStsFresh();
				PcConnect.PcStsFresh();
			}
		});

		button_4 = new JButton("CIC命令");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CICCmd();
			}
		});

		pane = new JPanel();
		pane.add(button_0, BorderLayout.SOUTH);
		pane.add(button_1, BorderLayout.SOUTH);
		pane.add(button_2, BorderLayout.SOUTH);
		pane.add(button_3, BorderLayout.SOUTH);
		pane.add(button_4, BorderLayout.SOUTH);
		getContentPane().add(pane, BorderLayout.SOUTH);

		s_pan = new JScrollPane();
		s_pan.setViewportView(ta_info2Cic);
		getContentPane().add(s_pan, BorderLayout.CENTER);

		// 托盘
		if (SystemTray.isSupported()) { // 判断是否支持系统托盘
			URL url = IotServer.class.getResource("server.png"); // 获取图片所在的URL
			ImageIcon icon = new ImageIcon(url); // 实例化图像对象
			Image image = icon.getImage(); // 获得Image对象
			TrayIcon trayIcon = new TrayIcon(image); // 创建托盘图标
			trayIcon.addMouseListener(new MouseAdapter() { // 为托盘添加鼠标适配器
				public void mouseClicked(MouseEvent e) { // 鼠标事件
					if (e.getClickCount() == 2) { // 判断是否双击了鼠标
						showFrame(); // 调用方法显示窗体
					}
				}
			});
			trayIcon.setToolTip("系统托盘"); // 添加工具提示文本
			PopupMenu popupMenu = new PopupMenu(); // 创建弹出菜单
			MenuItem exit = new MenuItem("退出"); // 创建菜单项
			exit.addActionListener(new ActionListener() { // 添加事件监听器
				public void actionPerformed(final ActionEvent arg0) {
					System.exit(0); // 退出系统
				}
			});
			popupMenu.add(exit); // 为弹出菜单添加菜单项
			trayIcon.setPopupMenu(popupMenu); // 为托盘图标加弹出菜弹
			SystemTray systemTray = SystemTray.getSystemTray(); // 获得系统托盘对象
			try {
				systemTray.add(trayIcon); // 为系统托盘加托盘图标
			} catch (Exception e) {

				JOptionPane.showMessageDialog(null, "托盘加载错误", "警告", JOptionPane.WARNING_MESSAGE);

				logger.error("", e);
			}
		}
	}

	public void showFrame() {
		this.setVisible(true); // 显示窗体
		this.setState(Frame.NORMAL);
	}
}
