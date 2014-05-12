package process;

import java.sql.SQLException;
import java.util.Vector;

public class ClsMain {

	/**
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//WriteSessionStatisticsToFiles();
		 //WriteSessionStatisticsToFile(15);
		// GetSessionsPageDescriptionSeqWithoutDurationAndRecordInfo();
		WriteSessionProcessedInfoToFiles();
	}

	// 从数据库表中获取页面参数的统计信息
	public static void GetPageStatsFile() {
		ClsPageRules cpr = new ClsPageRules();
		cpr.getPageStats("rec_taobao_valid", "url");
		cpr.writePageStats("output/PageParams.txt");
	}

	// 为数据库表添加page与query两列
	public static void AddUrlDetail() {
		ClsDb cdb = new ClsDb(true);
		cdb.addUrlPageAndQueryColumn("rec_taobao_sample_day5_heigh1", "page", "query");
		cdb.closeDb();
	}

	// 从数据库中获取会话
	public static void GetSessionsFile() {
		Vector<ClsSession> sessions = ClsSession.getUserSessionsDirectly(
				"rec_taobao_sample_day5_heigh100", "", 15);
		ClsSession.writeSessionsRecordInfoFromSessions(
				"output/Sessions_sample_day5_heigh100.txt", sessions);
		ClsSession.writeSessionsStatFromSessions("output/Stat_sample_day5_heigh100.txt",
				sessions);
	}

	// 从数据库中获取会话
	public static void GetSessionsPageClassSeqWithDurationAndRecordInfo() {
		String tableName = "_sample_day5";
		ClsSession.writeSessionsPageClassSeqWithDurationAndRecordInfo(
				"rec_taobao" + tableName, "", 15, "output/Sessions" + tableName
						+ "_recordInfo.txt", "output/Sessions" + tableName
						+ "_pageDescription.txt", "output/Sessions" + tableName
						+ "_pageClass.txt");
	}

	// 从数据库中获取会话
	public static void GetSessionsPageDescriptionSeqWithoutDurationAndRecordInfo() {
		String tableName = "_sample_day5_heigh100";
		ClsSession.writeSessionsPageDescriptionSeqWithoutDurationAndRecordInfo(
				"rec_taobao" + tableName, "", 15, "output/Sessions" + tableName
						+ "_recordInfo.txt", "output/Sessions" + tableName
						+ "_pageDescription.txt", true);
	}

	// 从数据库中获取会话
	public static void WriteSessionStatisticsToFile(int interval) {
		String tableName = "_sample_day5_heigh100";
		ClsSession.writeSessionsStatisticsToFileFromDbDirectly("rec_taobao" + tableName,
				interval, "output/Sessions" + tableName + "_" + interval
						+ "_statisInfo.txt");
	}

	// 从数据库中获取会话
	public static void WriteSessionStatisticsToFiles() {
		String tableName = "_sample_day5_heigh100";
		ClsSession.writeSessionStatisticsToFilesWithIntervalRange("rec_taobao" + tableName, 10,
				30, "output/Sessions" + tableName + "_statisInfo.txt");
	}
	
	// 从数据库中获取会话
	public static void WriteSessionProcessedInfoToFiles() {
		String tableName = "_valid";
		ClsSession.writeSessionsProcessedInfoToFile("rec_taobao" + tableName, "output/Sessions" + tableName + "_processedInfo.txt");
	}

	// 从数据库中获取会话
	public static void GetSessionsItemCidSeqWithoutDurationAndRecordInfo() {
		String tableName = "_sample_day5_heigh100";
		ClsSession.writeSessionsItemCidSeqWithoutDurationAndRecordInfo(
				"rec_taobao" + tableName, "", 15, "output/Sessions" + tableName
						+ "_recordInfo.txt", "output/Sessions" + tableName
						+ "_itemCidSeq.txt", false, true);
	}

	// 从数据库中获取用户的浏览信息
	public static void GetUserSessionsFile() {
		ClsUser cu = new ClsUser();
		cu.getUserInfo("rec_taobao_sample_day5", "11751222721456154472", 15);
		cu.writeUserInfo("output/day5_user_sessions.txt");
		cu.writeUserSessionsPageClassSeqWithDuration("output/day5_user_sessions_class.txt");
	}

	public static void TestAddState() {
		ClsDb cd=new ClsDb(true);
		cd.addUrlTypeAndActionColumn("rec_taobao_valid", "Type20140403",
				"Action20140403");
	}

	//
	public static void GetPageInfos() {
		ClsPageRules cpr = new ClsPageRules();
		cpr.writePagesDescription("output/pageinfos.txt");
	}

	// 测试文件类
	public static void Test_ClsFile() {
		ClsFile cf = new ClsFile("output/TestLog.txt");
		cf.writeFile("这是文件的测试用例");
		cf.closeFile();
	}

	public static void FillHighLevel() {
		ClsDb.globalCd
				.addHighLevelColumn("rec_taobao_sample_day5_heigh100",
						"data/HighLevelRules_type.json", "Type20140331",
						"Type20140331_high");
	}
public static void FillSessionId()
{
	ClsDb.globalCd.addSessionIdColumn("rec_taobao_sample_day5_heigh100", "sessionId0402", 15);
	}
	// 测试页面类
	public static void Test_ClsPage() {
		ClsPage cp = new ClsPage();
		cp.getParam("q");
		cp.getParam("qs");
		cp.getParam("q");
		System.out.println(cp.serializeParamsInfo());
	}
}