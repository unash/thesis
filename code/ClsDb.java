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


	
	// ���ݿ����һ���������������������ӣ������ֶ������ӹر� ֻ��Ҫ�������ӣ����Ա���ò���
	private static String dbServer = "****************";
	private static String dbName = "********";
	private static String dbUserName = "********";
	private static String dbPassword = "********";
	private static String dbLogName = "output/dbLog.txt";// �����ݿ���ص���־����ڴ˴�
	// һ�����ݿ�����У�ÿ��Ӧ�ö�����ҪStatement��Connection��Ϊ�������ؽ���������ΪClsDb�ĳ�Ա
	private Statement stmt = null;// ����ȫ�ֱ�����Ϊ��ֹ�ظ���������������Դ
	private Connection conn = null;// һ����ά��һ������

	// ����һ����̬��ȫ�ֿ��Ե����ݿ���󣬱����ظ������ͷ���Դ��ɵ����ģ���Ҫ�����ظ���ȡ���ݿ��е�ĳ������
	public static ClsDb globalCd = new ClsDb(true);
	private static double NumPerFlicker = 50;// ��ʱ����˸һ�δ�Լ���µļ�¼����

	public static void main(String[] args) {
		System.out.print("Test Class ClsDb...");
		insertItemDetailToTable("info_urls", 10000, "info_items");
	}

	//�����ݿ�����
	public boolean openDb() {
		String dbUrl = "jdbc:mysql://" + dbServer + ":3306/" + dbName;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// ����DriverManager�����getConnection()���������һ��Connection����
			conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			// ����һ��Statement����
			System.out.print("Connect " + dbServer + " Server Success.\n");// ���ӳɹ�
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("�޷�����mysql driver!");
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("���ݿ�" + dbUrl + "����ʧ��!");
			return false;
		}
	}

	//�ر����ݿ�����
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

	// �ڹ�������ʱ����������ݿ�
	public ClsDb(boolean openDb) {
		if (openDb) {
			this.openDb();
		}
	}

	// Ĭ�Ϲ��캯������Ҫ�ֶ������ݿ�����
	public ClsDb() {

	}

	// ִ�в�ѯsql���
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
		} // ����Statement����
		try {
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ִ�в���sql���
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
		} // ����Statement����
		try {
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// �п������ظ����´���

			// e.printStackTrace();
			return 0;
		}
	}

	// ִ���޸�sql���
	public boolean executeAlterQuery(String sql) {
		try {
			if (stmt != null)
				stmt.close();
			stmt = conn.createStatement(); // ����Statement����
			stmt.execute(sql);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// ִ�и���sql���
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
		} // ����Statement���󣬿ɻع����ɸ���
		try {
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// ����url�У���ֳ����У�page��query
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
					cr.setVisitUrl(rs.getString("url"));//����Ҫ��չurl��Ϣ
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

	// �����¼��url�У����ҳ����������Ϊ
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
			ClsTime ct = new ClsTime(rowNum / NumPerFlicker);// 50��Ϊһ���ȵ�
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			ClsStateRules csr = new ClsStateRules();// ��������url�Ĺ���
			String url = "";
			String referUrl = "";
			String url_md5 = "";
			while (rs.next()) {
				iRow = rs.getRow();
				ct.estimating(iRow);
				if (rs.getString(columnTypeName) != null
						&& rs.getString(columnActionName) != null
						&& rs.getString(columnTypeName) != ""
						&& rs.getString(columnActionName) != "") {// ����Ѿ���д������ֶ�ֵ������Ҫ��������
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
			System.out.println("�����ڴ����" + iRow + "�е�ʱ�����");
			e.printStackTrace();
			return false;
		}
	}
	//�Ͳ������߲�����ӳ��
	public Boolean addHighLevelColumn(String tableName,String mapRulesJsonFile, String columnLowLevelName,
			String columnHighLevelName)
	{
		//������
		JSONObject rulesLowToHighLevel = null;// ������� ��json����
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
			ClsTime ct = new ClsTime(rowNum / NumPerFlicker);// 50��Ϊһ���ȵ�
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				iRow = rs.getRow();
				ct.estimating(iRow);
				if (rs.getString(columnLowLevelName) != null
						&& rs.getString(columnHighLevelName) != null
						&& rs.getString(columnLowLevelName) != ""
						&& rs.getString(columnHighLevelName) != "") {// ����Ѿ���д������ֶ�ֵ������Ҫ��������
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
					System.out.println("ӳ��������ڲ��㣡"+"�ֶ�Ϊ��"+strLowLevelContent+"\n");
				}
				rs.updateString(columnHighLevelName,strHighLevelContent);
				rs.updateRow();
			}
			rs.close();
			ct.estimateEnd();
			return true;
		} catch (SQLException e) {
			System.out.println("�����ڴ����" + iRow + "�е�ʱ�����");
			e.printStackTrace();
			return false;
		}
	}
	// ���sessionId
	public Boolean addSessionIdColumn(String tableName, String columnSessionIdName,
			int interval) {
		// �����ȡsessions�Ĳ�ѯ���
		if (!addNewColumn(tableName, columnSessionIdName, "int"))
			return false;
		int intSessionId=0;
		String sql = "select id,uid,ip,date,staytime,"+columnSessionIdName+" from " + tableName
				+ " order by uid ASC,date ASC";
		ClsSession currentSession = null;// ��ǰ �ĻỰΪ��
		ClsRecord currentRecord = null;// ��ǰ����ļ�¼Ϊ��
		try {
			ResultSet rs = executeUpdateQuery(sql);
			// ��ȡ��¼����
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// ���ü�ʱ��
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				ct.estimating(rs.getRow());
				// ȥ���������ؼ��ֶεļ�¼
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

				if (currentSession == null) {// �����û�лỰ�����ǵ�һ���Ự
					currentSession = new ClsSession();// ���������еĵ�һ���Ự
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
			if (currentSession != null) {// �������һ��session
//				 cfStat.WriteFile(currentSession.SerializeSessionStatistics()
//				 + "\n");
				currentSession = null;
			}
			ct.estimateEnd();
			rs.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("��ȡ�û��Ựʧ��");
			return false;
		}
	}

	//��ȡ��Ʒ�����ϸ��Ϣ�����浽���ݿ��info_items���У�Ҫ����Ѿ�����
	//��Ϊ���һ��ץȡ���е�url��Ӧitem��Ϣ�����ܻ�Ƚ��������Էֳ�stepȥץ
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
				ClsFile.writeLog("itemDetail.txt", "�Ѿ����" + (i + step));
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
		// System.out.println("�Ӽ�¼ "+limitFrom+" ����¼ "+(limitFrom+limitNum)+" :");
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
					//��Ϊץȡ���ֶγ����˺���ExpandRecordInfoWithUrl�е��ֶΣ����Բ�ʹ��ExpandRecordInfoWithUrl
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

	// Ϊ�������
	public boolean addNewColumn(String tableName, String columnName,
			String columnType) {
		// �������Ƿ����
		String sql = "select * from " + tableName + " where 1=2;";
		ResultSet rs;
		try {
			rs = executeSelectQuery(sql);
			ResultSetMetaData rsmb = rs.getMetaData();
			boolean flag = false;// Ĭ�����в�����
			for (int i = 0; i < rsmb.getColumnCount(); i++) {
				if (rsmb.getColumnName(i + 1).toString().equals(columnName)) {
					flag = true;
					break;
				}
			}
			rs.close();
			if (!flag) {// ������в�����
				sql = "alter table " + tableName + " add " + columnName + " "
						+ columnType;
				if (executeAlterQuery(sql)) {
					System.out.println("�ɹ�������У�" + columnName);
					return true;
				} else {
					System.out.println("����� " + columnName + " ʧ��");
					return false;
				}
			}
			else
			{
				System.out.println("�� " + columnName + " �Ѿ����ڣ�ִ�к�������.");
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// �����ݿ��е�һ��ά�ȱ�����ֵӳ�䵽�ַ���
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
					ClsFile.writeLog(dbLogName, "���ڶ���url��Ϣ��" + tableName + "."
							+ md5FieldName + "=" + md5FieldValue);
				}
			} else {
				ClsFile.writeLog(dbLogName, "���ݿ��в����ڴ�url��Ϣ��" + tableName + "."
						+ md5FieldName + "=" + md5FieldValue);
			}
			return blob;
		} catch (SQLException e) {
			e.printStackTrace();
			return blob;
		}
	}
}
