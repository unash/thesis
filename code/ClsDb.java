package process;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.Blob;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ClsDb {


	
	// 数据库对象一旦建立，将持续保持连接，除非手动将连接关闭 只需要保持连接，其成员作用不大
	private static String dbServer = "****************";
	private static String dbName = "********";
	private static String dbUserName = "********";
	private static String dbPassword = "********";
	private static String dbLogName = "output/dbLog.txt";// 与数据库相关的日志存放在此处
	// 一个数据库对象中，每次应用都会需要Statement与Connection，为避免来回建立，故作为ClsDb的成员
	private Statement stmt = null;// 设立全局变量，为防止重复创建对象，消耗资源
	private Connection conn = null;// 一个类维护一个连接

	// 建立一个静态的全局可以的数据库对象，避免重复建立释放资源造成的消耗，主要用于重复获取数据库中的某类资料
	public static ClsDb globalCd = new ClsDb(true);
	private static double NumPerFlicker = 50;// 计时器闪烁一次大约更新的记录个数

	public static void main(String[] args) {
		System.out.print("Test Class ClsDb...");
		insertItemDetailToTable("info_urls", 10000, "info_items");
	}

	//打开数据库连接
	public boolean openDb() {
		String dbUrl = "jdbc:mysql://" + dbServer + ":3306/" + dbName;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// 调用DriverManager对象的getConnection()方法，获得一个Connection对象
			conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			// 创建一个Statement对象
			System.out.print("Connect " + dbServer + " Server Success.\n");// 连接成功
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("无法加载mysql driver!");
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("数据库" + dbUrl + "连接失败!");
			return false;
		}
	}

	//关闭数据库连接
	public boolean closeDb() {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	// 在构造对象的时候就连接数据库
	public ClsDb(boolean openDb) {
		if (openDb) {
			this.openDb();
		}
	}

	// 默认构造函数，需要手动打开数据库连接
	public ClsDb() {

	}

	// 执行查询sql语句
	public ResultSet executeSelectQuery(String sql) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} // 创建Statement对象
		try {
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 执行插入sql语句
	public int executeInsertQuery(String sql) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return 0;
			}
		}
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} // 创建Statement对象
		try {
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// 有可能码重复导致错误

			// e.printStackTrace();
			return 0;
		}
	}

	// 执行修改sql语句
	public boolean executeAlterQuery(String sql) {
		try {
			if (stmt != null)
				stmt.close();
			stmt = conn.createStatement(); // 创建Statement对象
			stmt.execute(sql);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 执行更新sql语句
	public ResultSet executeUpdateQuery(String sql) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} // 创建Statement对象，可回滚，可更新
		try {
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 处理url列，拆分成两列，page与query
	public Boolean addUrlPageAndQueryColumn(String tableName, String columnPageName,
			String columnQueryName) {
		if (!addNewColumn(tableName, columnPageName, "TEXT"))
			return false;
		if (!addNewColumn(tableName, columnQueryName, "TEXT"))
			return false;
		String sql = "select * from " + tableName + "";
		ResultSet rs;
		try {
			rs = executeUpdateQuery(sql);

			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime(rowNum / NumPerFlicker);
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			ClsRecord cr = new ClsRecord();
			while (rs.next()) {
				ct.estimating(rs.getRow());
				if (rs.getString("url") != null) {
					cr.setVisitUrl(rs.getString("url"));//不需要扩展url信息
				} else {
					cr.setVisitUrl("");
				}
				rs.updateString(columnPageName, cr.getVisitPage());
				rs.updateString(columnQueryName, cr.getVisitQuery());
				rs.updateRow();
			}
			rs.close();
			ct.estimateEnd();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 处理记录的url列，标记页面类型与行为
	public Boolean addUrlTypeAndActionColumn(String tableName, String columnTypeName,
			String columnActionName) {
		if (!addNewColumn(tableName, columnTypeName, "TEXT"))
			return false;
		if (!addNewColumn(tableName, columnActionName, "TEXT"))
			return false;
		String sql = "select id,url,referurl,url_md5," + columnTypeName + ","
				+ columnActionName + " from " + tableName + "";
		ResultSet rs;
		int iRow = 0;
		try {
			rs = executeUpdateQuery(sql);
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime(rowNum / NumPerFlicker);// 50个为一精度点
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			ClsStateRules csr = new ClsStateRules();// 创建处理url的规则
			String url = "";
			String referUrl = "";
			String url_md5 = "";
			while (rs.next()) {
				iRow = rs.getRow();
				ct.estimating(iRow);
				if (rs.getString(columnTypeName) != null
						&& rs.getString(columnActionName) != null
						&& rs.getString(columnTypeName) != ""
						&& rs.getString(columnActionName) != "") {// 如果已经填写了这个字段值，则不需要继续处理
					continue;
				}
				if (rs.getString("url") != null) {
					url = rs.getString("url");
				}
				if (rs.getString("referurl") != null) {
					referUrl = rs.getString("referurl");
				}
				if (rs.getString("url_md5") != null) {
					url_md5 = rs.getString("url_md5");
				}
				if (referUrl.equals("null/##"))
					referUrl = "";
				csr.initStateVariable(url, referUrl, url_md5);
				rs.updateString(columnTypeName, csr.getPageTypeName());
				rs.updateString(columnActionName, csr.getUserActionName());
				rs.updateRow();
			}
			rs.close();
			ct.estimateEnd();
			return true;
		} catch (SQLException e) {
			System.out.println("程序在处理第" + iRow + "行的时候出错");
			e.printStackTrace();
			return false;
		}
	}
	//低层概念像高层概念的映射
	public Boolean addHighLevelColumn(String tableName,String mapRulesJsonFile, String columnLowLevelName,
			String columnHighLevelName)
	{
		//泛化表
		JSONObject rulesLowToHighLevel = null;// 保存规则 的json对象
		InputStreamReader br;
		try {
			br = new InputStreamReader(new FileInputStream(mapRulesJsonFile), "UTF-8");
			JSONTokener jsonTokerner = new JSONTokener(br);
			rulesLowToHighLevel = new JSONObject(jsonTokerner);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (!addNewColumn(tableName, columnHighLevelName, "TEXT"))
			return false;
		String sql = "select id," + columnLowLevelName + ","
				+ columnHighLevelName + " from " + tableName + "";
		ResultSet rs;
		int iRow = 0;
		String strLowLevelContent = "";
		String strHighLevelContent = "";
		try {
			rs = executeUpdateQuery(sql);
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime(rowNum / NumPerFlicker);// 50个为一精度点
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				iRow = rs.getRow();
				ct.estimating(iRow);
				if (rs.getString(columnLowLevelName) != null
						&& rs.getString(columnHighLevelName) != null
						&& rs.getString(columnLowLevelName) != ""
						&& rs.getString(columnHighLevelName) != "") {// 如果已经填写了这个字段值，则不需要继续处理
					continue;
				}
				if (rs.getString(columnLowLevelName) != null) {
					strLowLevelContent = rs.getString(columnLowLevelName);
				}
				if(rulesLowToHighLevel.has(strLowLevelContent))
				{
					try {
						strHighLevelContent=rulesLowToHighLevel.getString(strLowLevelContent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				else
				{
					System.out.println("映射表规则存在不足！"+"字段为："+strLowLevelContent+"\n");
				}
				rs.updateString(columnHighLevelName,strHighLevelContent);
				rs.updateRow();
			}
			rs.close();
			ct.estimateEnd();
			return true;
		} catch (SQLException e) {
			System.out.println("程序在处理第" + iRow + "行的时候出错");
			e.printStackTrace();
			return false;
		}
	}
	// 填充sessionId
	public Boolean addSessionIdColumn(String tableName, String columnSessionIdName,
			int interval) {
		// 构造获取sessions的查询语句
		if (!addNewColumn(tableName, columnSessionIdName, "int"))
			return false;
		int intSessionId=0;
		String sql = "select id,uid,ip,date,staytime,"+columnSessionIdName+" from " + tableName
				+ " order by uid ASC,date ASC";
		ClsSession currentSession = null;// 当前 的会话为空
		ClsRecord currentRecord = null;// 当前处理的记录为空
		try {
			ResultSet rs = executeUpdateQuery(sql);
			// 获取记录总数
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// 配置计时器
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				ct.estimating(rs.getRow());
				// 去除不包含关键字段的记录
				currentRecord = new ClsRecord();
				
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;
				else
					currentRecord.setVisitTime(rs.getTimestamp("date"));
				if (rs.getInt("staytime") != 0)
					currentRecord.setVisitDuration(rs.getInt("staytime"));

				if (currentSession == null) {// 如果尚没有会话，这是第一个会话
					currentSession = new ClsSession();// 创建集合中的第一个会话
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					intSessionId++;
					rs.updateInt(columnSessionIdName,intSessionId);
					rs.updateRow();
					currentRecord = null;
					continue;
				}
				if (currentSession.getSessionUserId().equals(
						rs.getString("uid"))
						&& currentSession.getSessionUserIp().equals(
								rs.getString("ip"))
						&& (currentRecord.getVisitTime().getTime() - currentSession
								.getSessionTimeEnd().getTime()) / 1000 / 60 < interval) {
					currentSession.receiveSessionLaterRecord(currentRecord);
					rs.updateInt(columnSessionIdName,intSessionId);
					rs.updateRow();
					currentRecord = null;
				} else {
//					 cfStat.WriteFile(currentSession
//					 .SerializeSessionStatistics() + "\n");
					intSessionId++;
					rs.updateInt(columnSessionIdName,intSessionId);
					rs.updateRow();
					currentSession = null;
					currentSession = new ClsSession();
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					currentRecord = null;
				}
			}
			if (currentSession != null) {// 处理最后一个session
//				 cfStat.WriteFile(currentSession.SerializeSessionStatistics()
//				 + "\n");
				currentSession = null;
			}
			ct.estimateEnd();
			rs.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("获取用户会话失败");
			return false;
		}
	}

	//获取商品项的详细信息，保存到数据库的info_items表中，要求表已经建好
	//因为如果一次抓取所有的url对应item信息，可能会比较慢；所以分成step去抓
	public static void insertItemDetailToTable(String urlInfoTableName, int step, String itemInfoTableName) {
		String sql = "select count(*) as num from " + urlInfoTableName;
		ClsDb cd = new ClsDb(true);
		ResultSet rs = cd.executeSelectQuery(sql);
		int sum = 0;
		try {
			rs.next();
			sum = rs.getInt("num");
	
			for (int i = 0; i < sum; i += step) {
				insertItemDetailToTableChild(urlInfoTableName, i, step, itemInfoTableName);
				ClsFile.writeLog("itemDetail.txt", "已经完成" + (i + step));
			}
			rs.close();
			cd.closeDb();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void insertItemDetailToTableChild(String urlInfoTableName, int limitFrom,
			int limitNum, String itemInfoTableName) {
		String itemPageString = "item.taobao.com/item.htm";
		Pattern patCid = Pattern.compile(" cid:'([0-9]+)', ");
		Pattern patShopId = Pattern.compile(" shopId=([0-9]+); ");
		Pattern patName = Pattern.compile("<title>([^<]+)</title>");
		Pattern patSellerId = Pattern
				.compile("\\sseller:\\{id:([0-9]+),status:0\\},");
		Pattern patSellerNick = Pattern
				.compile("\\ssellerNick:\"([^\"]+)\",\\s");
		String cid = "", shopId = "", sellerId = "", sellerNick = "", name = "";
		// System.out.println("从记录 "+limitFrom+" 到记录 "+(limitFrom+limitNum)+" :");
		String sqlUrls = "select * from " + urlInfoTableName + " order by id limit "
				+ limitFrom + "," + limitNum;
		ClsDb cdUrls = new ClsDb(true);
		ClsDb cdItems = new ClsDb(true);
		ResultSet rsUrls;
		try {
			rsUrls = cdUrls.executeSelectQuery(sqlUrls);
			rsUrls.last();
			int rowNum = rsUrls.getRow();
			ClsTime ct = new ClsTime(rowNum / NumPerFlicker);
			ct.estimateStart(rowNum);
			rsUrls.first();
			rsUrls.previous();
			ClsRecord cr = new ClsRecord();
			while (rsUrls.next()) {
				ct.estimating(rsUrls.getRow());
				if (rsUrls.getString("url") != null) {
					cr.setVisitUrl(rsUrls.getString("url"));
					//因为抓取的字段超过了函数ExpandRecordInfoWithUrl中的字段，所以不使用ExpandRecordInfoWithUrl
					//cr.ExpandRecordInfoWithUrl(false, false, false);
					if (cr.getVisitPage().equals(itemPageString)) {
						if (rsUrls.getBlob("content") != null) {
							String html = ClsBase.gzipToString(
									rsUrls.getBlob("content"), "utf-8");
							if (html != "") {
								Matcher matcher = patCid.matcher(html);
								if (matcher.find())
									cid = matcher.group(1);
								else
									cid = "";

								matcher = patName.matcher(html);
								if (matcher.find())
									name = matcher.group(1);
								else
									name = "";

								matcher = patShopId.matcher(html);
								if (matcher.find())
									shopId = matcher.group(1);
								else
									shopId = "";

								matcher = patSellerId.matcher(html);
								if (matcher.find())
									sellerId = matcher.group(1);
								else
									sellerId = "";

								matcher = patSellerNick.matcher(html);
								if (matcher.find())
									sellerNick = matcher.group(1);
								else
									sellerNick = "";

								cdItems.executeInsertQuery("insert into "+itemInfoTableName+" values('"
										+ cr.getItemIdFromQuery()
										+ "','"
										+ name
										+ "','"
										+ cid
										+ "','"
										+ shopId
										+ "','"
										+ sellerId
										+ "','"
										+ sellerNick
										+ "')");
							}
						}
					}
				}
			}
			rsUrls.close();
			ct.estimateEnd();
			cdUrls.closeDb();
			cdItems.closeDb();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	// 为表添加列
	public boolean addNewColumn(String tableName, String columnName,
			String columnType) {
		// 测试列是否存在
		String sql = "select * from " + tableName + " where 1=2;";
		ResultSet rs;
		try {
			rs = executeSelectQuery(sql);
			ResultSetMetaData rsmb = rs.getMetaData();
			boolean flag = false;// 默认这列不存在
			for (int i = 0; i < rsmb.getColumnCount(); i++) {
				if (rsmb.getColumnName(i + 1).toString().equals(columnName)) {
					flag = true;
					break;
				}
			}
			rs.close();
			if (!flag) {// 如果此列不存在
				sql = "alter table " + tableName + " add " + columnName + " "
						+ columnType;
				if (executeAlterQuery(sql)) {
					System.out.println("成功添加新列：" + columnName);
					return true;
				} else {
					System.out.println("添加列 " + columnName + " 失败");
					return false;
				}
			}
			else
			{
				System.out.println("列 " + columnName + " 已经存在，执行后续操作.");
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 将数据库中的一个维度表由数值映射到字符串
	public Map<Integer, String> dimMapper(String tableName, String keyColumn,
			String valueColumn) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		String sql = "select " + keyColumn + "," + valueColumn + " from "
				+ tableName + "";
		ResultSet rs = null;
		try {
			rs = executeSelectQuery(sql);
			while (rs.next()) {
				map.put(rs.getInt(keyColumn), rs.getString(valueColumn));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		try {
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return map;
	}

	public Blob getUrlBlobFromDb(String tableName, String md5FieldName,
			String md5FieldValue) {
		String sql = "select * from " + tableName + " where " + md5FieldName
				+ "='" + md5FieldValue + "'";
		ResultSet rs = executeSelectQuery(sql);
		Blob blob = null;
		try {
			if (rs.next()) {
				if (rs.getBlob("content") != null) {
					blob = rs.getBlob("content");
					// System.out.println(blob.toString());
				} else if (rs.next()) {
					ClsFile.writeLog(dbLogName, "存在多条url信息！" + tableName + "."
							+ md5FieldName + "=" + md5FieldValue);
				}
			} else {
				ClsFile.writeLog(dbLogName, "数据库中不存在此url信息！" + tableName + "."
						+ md5FieldName + "=" + md5FieldValue);
			}
			return blob;
		} catch (SQLException e) {
			e.printStackTrace();
			return blob;
		}
	}
}
