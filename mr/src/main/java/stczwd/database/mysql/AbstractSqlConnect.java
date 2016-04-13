package stczwd.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接的抽象对象，用来存储数据库信息和优化数据库方法
 * @author minelab
 */
public abstract class AbstractSqlConnect implements AbstractDatabaseConnect{

	private String ip;
	private int port;
	private String user;
	private String passwd;
	private String database;
	private static Connection dbconnection;

	/**
	 * 构造函数，在接收数据库信息的同时，创建数据库连接
	 * @param ip 数据库的ip地址
	 * @param port 数据库的端口
	 * @param user 数据库的管理用户名
	 * @param passwd 数据库的管理用户名对应的密码
	 * @param database 所要连接的数据库
	 */
	public AbstractSqlConnect(String ip, int port, String user, String passwd, String database) {
		//super();
		//对输入的json数据进行解析
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.passwd = passwd;
		this.database = database;
		databaseconnect();
	}

	/**
	 * 数据库连接方法，用来在构造对象时建立与数据库的链接
	 */
	public void databaseconnect() {
		String jdbcUrl= "jdbc:mysql://"+ip+":"+port+"/"+database+"?"
				+ "user="+user+"&password="+passwd
				//确保长连接正常运作，确保mysql连接数限制，保证mysql连接正常
				+"&autoReconnect=true&failOverReadOnly=false&maxReconnects=10"
				//设置通道字符编码是utf-8模式
				+"&useUnicode=true&characterEncoding=utf-8";
		try {
			Class.forName("com.mysql.jdbc.Driver");//动态加载mysql驱动
			dbconnection = DriverManager.getConnection(jdbcUrl);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 数据表的创建、插入、更新、删除
	 * @param operation 数据库操作语句，查询除外
	 */
	public void databaseoperation(String operation) {
		try {
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			Statement stmt = dbconnection.createStatement();
			//根据传入的mysql命令，实现mysql的操作
			int result = stmt.executeUpdate(operation);
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 数据库的查询方法
	 * @param checkoperation 数据库的查询语句
	 * @return 返回查询的结果
	 */
	public ResultSet databasecheck(String checkoperation) {
		ResultSet rs=null;
		try {
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			Statement stmt = dbconnection.createStatement();
			//根据传入的mysql命令，实现mysql的操作
			rs = stmt.executeQuery(checkoperation);
			//while(rs.next())
			//{
			//	System.out.println(rs.getString(1) + "\t" + rs.getString(2));
			//}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 数据库的关闭
	 */
	public Boolean databaseclose() {
		try {
			dbconnection.close();
			if (!dbconnection.isClosed()) return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

}
