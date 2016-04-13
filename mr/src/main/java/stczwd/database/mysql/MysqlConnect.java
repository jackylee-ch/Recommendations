package stczwd.database.mysql;

import java.rmi.server.Operation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * mysql连接的实际操作类
 * @author minelab
 */
public class MysqlConnect extends AbstractSqlConnect{
	
	public MysqlConnect(String ip, String user, String passwd,
			String database) {
		super(ip, 3306, user, passwd, database);
	}

	/**
	 * 数据表的插入
	 * @param table 插入的表名
	 * @param map 所要插入信息的column和value的对应关系
	 */
	public void mysqlInsert(String table,Map<String,String> map) {
		//创建stringBuilder对象，用来叠加完善插入部分数据
		StringBuilder columnBuilder = new StringBuilder();
		StringBuilder valueBuilder = new StringBuilder();
		//根据传入的Map完善sql的插入语句
		for (Entry<String, ?> entry : map.entrySet()) {
			//构造()内部的column部分
			columnBuilder.append(entry.getKey()+",");
			valueBuilder.append("\""+entry.getValue()+"\",");
		}
		//删除多余的，
		columnBuilder.deleteCharAt(columnBuilder.length()-1);
		valueBuilder.deleteCharAt(valueBuilder.length()-1);
		//整合插入语句
		String operation = "insert into "+table+"("+columnBuilder.toString()+") values("+valueBuilder.toString()+")";
		//根据传入的mysql命令，实现mysql的操作
		System.out.println(operation);
		super.databaseoperation(operation);
	}

	/**
	 * 数据表的更新
	 * @param table 更新的表名
	 * @param map 所要插入信息的column和value的对应关系
	 */
	public void mysqlUpdate(String table,Map<String,?> mapSet,Map<String,?> mapWhere) {
		//创建stringBuilder对象，用来叠加完善插入部分数据
		StringBuilder setBuilder = new StringBuilder();
		StringBuilder whereBuilder = new StringBuilder();
		//根据传入的mapSet完善sql更新语句的设置部分
		for (Entry<String, ?> entry : mapSet.entrySet()) {
			setBuilder.append(entry.getKey()+"=");
			setBuilder.append(entry.getValue()+",");
		}
		//根据传入的mapWhere完善sql更新语句的where部分
		for (Entry<String, ?> entry : mapWhere.entrySet()) {
			whereBuilder.append(entry.getKey()+"=");
			whereBuilder.append(entry.getValue()+" and ");
		}
		//删除多余的，
		setBuilder.deleteCharAt(setBuilder.length()-1);
		whereBuilder.delete(whereBuilder.length()-5,whereBuilder.length()-1);
		//整合插入语句
		StringBuilder operation = new StringBuilder();
		operation.append("update ").append(table).append(" set ").append(setBuilder.toString()).append(" where ").append(whereBuilder.toString());
		//根据传入的mysql命令，实现mysql的操作
		System.out.println(operation.toString());
		super.databaseoperation(operation.toString());
	}

	/**
	 * 数据表的查找
	 * @param table 查找的表名
	 * @param map 所要查找信息的column和value的对应关系
	 */
	public Boolean mysqlSelectBoolean(String table,Map<String,?> map) {
		try {
			//创建stringBuilder对象，用来叠加完善插入部分数据
			StringBuilder selectBuilder = new StringBuilder();
			//根据传入的Map完善sql的插入语句
			for (Entry<String, ?> entry : map.entrySet()) {
				//构造()内部的column部分
				selectBuilder.append("`"+entry.getKey()+"`=\""+entry.getValue()+"\" and");
			}
			//删除多余的，
			selectBuilder.delete(selectBuilder.length()-4, selectBuilder.length());
			//整合插入语句
			String operation = "select * from "+table+" where "+selectBuilder.toString();
			System.err.println(operation);
	        //根据传入的mysql命令，实现mysql的操作
	        ResultSet result = super.databasecheck(operation);
	        if (result.next()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 数据表的查找
	 * @param table 查找的表名
	 * @param map 所要查找信息的column和value的对应关系
	 */
	public ResultSet mysqlSelectResultSet(String table,Map<String,?> map) {
		//创建stringBuilder对象，用来叠加完善插入部分数据
		StringBuilder selectBuilder = new StringBuilder();
		//根据传入的Map完善sql的插入语句
		for (Entry<String, ?> entry : map.entrySet()) {
			//构造()内部的column部分
			selectBuilder.append("`"+entry.getKey()+"`=\""+entry.getValue()+"\" and");
		}
		//删除多余的，
		selectBuilder.delete(selectBuilder.length()-4, selectBuilder.length());
		//整合插入语句
		String operation = "select * from "+table+" where "+selectBuilder.toString();
		System.err.println(operation);
		//根据传入的mysql命令，实现mysql的操作
		return super.databasecheck(operation);
	}

	/**
	 * 数据表的查找
	 * @param table 查找的表名
	 * @param map 所要查找信息的column和value的对应关系
	 */
	public ResultSet mysqlItemSimilaritySelectResultSet(long itemID1, long itemID2) {
		//创建stringBuilder对象，用来叠加完善插入部分数据
		StringBuilder selectBuilder = new StringBuilder();
		selectBuilder.append("select * from ItemSimilarity where itemID1 in (").append(itemID1).append(",").append(itemID2).append(") and itemID2 in (").append(itemID1).append(",").append(itemID2).append(")");
		//根据传入的mysql命令，实现mysql的操作
		return super.databasecheck(selectBuilder.toString());
	}
	
	/**
	 * 数据表的查找，查看对于两个itemID是否有相应的更新内容
	 * @param table 查找的表名
	 * @param map 所要查找信息的column和value的对应关系
	 */
	public Boolean mysqlCheckPrefUpdateBoolean(long itemID1, long itemID2) {
		try {
			//创建stringBuilder对象，用来叠加完善插入部分数据
			StringBuilder checkPrefBuilder = new StringBuilder();
			//整合插入语句
			checkPrefBuilder.append("select * from UserItemPref where itemID in (").append(itemID1).append(",").append(itemID2).append(") and uptodate !=0 and userID in (select userID from UserItemPref where itemID = ").append(itemID1).append(") and userID  in (select userID from UserItemPref where itemID = ").append(itemID2).append(")");
			System.out.println(checkPrefBuilder.toString());
	        //根据传入的mysql命令，实现mysql的操作
	        ResultSet result = super.databasecheck(checkPrefBuilder.toString());
	        if (result.next()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return false;
	}
	
	/**
	 * 数据表的查找，查看对于两个itemID是否有相应的更新内容
	 * @param itemID1
	 * @param itemID2
	 * @param update
	 * @return
	 */
	public ResultSet mysqlCheckPrefUpdateResult(long itemID1, long itemID2, int update) {
		try {
			//创建stringBuilder对象，用来叠加完善插入部分数据
			StringBuilder CheckPrefBuilder = new StringBuilder();
			//整合插入语句
			CheckPrefBuilder.append("select * from UserItemPref where itemID=").append(itemID1).append(" and uptodate=").append(update).append(" and userID in (select userID from UserItemPref where itemID = ").append(itemID1).append(") and userID  in (select userID from UserItemPref where itemID = ").append(itemID2).append(")");
	        //根据传入的mysql命令，实现mysql的操作
			System.out.println(CheckPrefBuilder.toString());
	        ResultSet result = super.databasecheck(CheckPrefBuilder.toString());
	        while (result.next()) {
	        	//清除所有筛选出的内容
	        	StringBuilder deletenewPrefBuilder = new StringBuilder();
				deletenewPrefBuilder.append("update UserItemPref set Pref=newPref,uptodate=0,newPref=0 where userID=").append(result.getInt("userID")).append(" and itemID in (").append(itemID1).append(",").append(itemID2).append(") and uptodate!=0");
				super.databaseoperation(deletenewPrefBuilder.toString());
			}
	        result.beforeFirst();
	        return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return null;
	}
	
	/**
	 * 数据表的查找，用来获取每个用户的历史评价数目和历史评价个数
	 * @param userID 所要查询的 用户id
	 * @return
	 */
	public ResultSet mysqlUserCountResultSet(long userID) {
		try {
			//创建stringBuilder对象，用来叠加完善插入部分数据
			StringBuilder CheckPrefBuilder = new StringBuilder();
			//整合插入语句
			CheckPrefBuilder.append("select userID,COUNT(*) as count,avg(Pref) as avgPref from UserItemPref where Pref!=0 and userID = ").append(userID);
			//根据传入的mysql命令，实现mysql的操作
			ResultSet result = super.databasecheck(CheckPrefBuilder.toString());
			if (result.next()) {
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 数据表的查找，用来获取每个用户的历史评价数目和历史评价个数
	 * @param userID 所要查询的 用户id
	 * @return
	 */
	public float mysqlUserItemPrefFloat(long userID, long itemID) {
		try {
			//创建stringBuilder对象，用来叠加完善插入部分数据
			StringBuilder CheckPrefBuilder = new StringBuilder();
			//整合插入语句
			CheckPrefBuilder.append("select Pref from UserItemPref where userID=").append(userID).append(" and itemID = ").append(itemID);
			//根据传入的mysql命令，实现mysql的操作
			ResultSet result = super.databasecheck(CheckPrefBuilder.toString());
			if (result.next()) return result.getFloat("Pref");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 数据库关闭程序
	 * @return
	 */
	public Boolean mysqlClose(){
		return super.databaseclose();
	}
}
