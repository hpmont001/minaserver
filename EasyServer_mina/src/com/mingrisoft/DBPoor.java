package com.mingrisoft;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
 
import javax.sql.DataSource;
 
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.dbcp2.BasicDataSourceFactory;  
 
/**
 * tomcat数据库连接池管理类<br>
 * 使用为tomcat部署环境<br>
 * 需要在类路径下准备数据库连接配置文件dbcp.properties
 * 
 * @author 宋信强
 * @mail songxinqiang123@gmail.com
 * 
 * @time 2013-12-27
 * 
 */
public class DBPoor {
    private static final Log log = LogFactory.getLog(DBPoor.class);
    private static final String configFile = "/mysql.ini";
    public static String configFilePath = null;
 
    private static DataSource dataSource;
 
    static {
        Properties dbProperties = new Properties();
        try {
			File directory = new File(""); 
			String myServerPath = directory.getCanonicalPath();
			//String myServerFilsePath = configFile;
			
			//File file = new File("/D:/IOTService/mysql.ini");
			configFilePath = myServerPath + configFile;
			File file = new File(configFilePath);
            dbProperties.load(new FileInputStream(file));
            dataSource = BasicDataSourceFactory.createDataSource(dbProperties);
 
            Connection conn = getConn();
            DatabaseMetaData mdm = conn.getMetaData();
            log.info("Connected to " + mdm.getDatabaseProductName() + " "
                    + mdm.getDatabaseProductVersion());
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            log.error("初始化连接池失败：" + e);
        }
    }
 
    private DBPoor() {
    }
 
    /**
     * 获取链接，用完后记得关闭
     * 
     * @see {@link DBManager#closeConn(Connection)}
     * @return
     */
    public static final Connection getConn() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            log.error("获取数据库连接失败：" + e);
        }
        return conn;
    }
 
    /**
     * 关闭连接
     * 
     * @param conn
     *            需要关闭的连接
     */
    public static void closeConn(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            log.error("关闭数据库连接失败：" + e);
        }
    }
    public static void closeConn(Connection conn,PreparedStatement pStatement,ResultSet rSet) {
        try {

			if(rSet.isClosed() == false){
				rSet.close();
			}
			if(pStatement.isClosed() == false){
				pStatement.close();
			}
            if (conn != null && !conn.isClosed()) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            log.error("关闭数据库连接失败：" + e);
        }
    }
    public static void closeConn(Connection conn,Statement statement,ResultSet rSet) {
        try {

			if(rSet.isClosed() == false){
				rSet.close();
			}
			if(statement.isClosed() == false){
				statement.close();
			}
            if (conn != null && !conn.isClosed()) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            log.error("关闭数据库连接失败：" + e);
        }
    }
 
}
