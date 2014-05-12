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

	// �����ݿ���л�ȡҳ�������ͳ����Ϣ
	public static void GetPageStatsFile() {
		ClsPageRules cpr = new ClsPageRules();
		cpr.getPageStats("rec_taobao_valid", "url");
		cpr.writePageStats("output/PageParams.txt");
	}

	// Ϊ���ݿ�����page��query����
	public static void AddUrlDetail() {
		ClsDb cdb = new ClsDb(true);
		cdb.addUrlPageAndQueryColumn("rec_taobao_sample_day5_heigh1", "page", "query");
		cdb.closeDb();
	}

	// �����ݿ��л�ȡ�Ự
	public static void GetSessionsFile() {
		Vector<ClsSession> sessions = ClsSession.getUserSessionsDirectly(
				"rec_taobao_sample_day5_heigh100", "", 15);
		ClsSession.writeSessionsRecordInfoFromSessions(
				"output/Sessions_sample_day5_heigh100.txt", sessions);
		ClsSession.writeSessionsStatFromSessions("output/Stat_sample_day5_heigh100.txt",
				sessions);
	}

	// �����ݿ��л�ȡ�Ự
	public static void GetSessionsPageClassSeqWithDurationAndRecordInfo() {
		String tableName = "_sample_day5";
		ClsSession.writeSessionsPageClassSeqWithDurationAndRecordInfo(
				"rec_taobao" + tableName, "", 15, "output/Sessions" + tableName
						+ "_recordInfo.txt", "output/Sessions" + tableName
						+ "_pageDescription.txt", "output/Sessions" + tableName
						+ "_pageClass.txt");
	}

	// �����ݿ��л�ȡ�Ự
	public static void GetSessionsPageDescriptionSeqWithoutDurationAndRecordInfo() {
		String tableName = "_sample_day5_heigh100";
		ClsSession.writeSessionsPageDescriptionSeqWithoutDurationAndRecordInfo(
				"rec_taobao" + tableName, "", 15, "output/Sessions" + tableName
						+ "_recordInfo.txt", "output/Sessions" + tableName
						+ "_pageDescription.txt", true);
	}

	// �����ݿ��л�ȡ�Ự
	public static void WriteSessionStatisticsToFile(int interval) {
		String tableName = "_sample_day5_heigh100";
		ClsSession.writeSessionsStatisticsToFileFromDbDirectly("rec_taobao" + tableName,
				interval, "output/Sessions" + tableName + "_" + interval
						+ "_statisInfo.txt");
	}

	// �����ݿ��л�ȡ�Ự
	public static void WriteSessionStatisticsToFiles() {
		String tableName = "_sample_day5_heigh100";
		ClsSession.writeSessionStatisticsToFilesWithIntervalRange("rec_taobao" + tableName, 10,
				30, "output/Sessions" + tableName + "_statisInfo.txt");
	}
	
	// �����ݿ��л�ȡ�Ự
	public static void WriteSessionProcessedInfoToFiles() {
		String tableName = "_valid";
		ClsSession.writeSessionsProcessedInfoToFile("rec_taobao" + tableName, "output/Sessions" + tableName + "_processedInfo.txt");
	}

	// �����ݿ��л�ȡ�Ự
	public static void GetSessionsItemCidSeqWithoutDurationAndRecordInfo() {
		String tableName = "_sample_day5_heigh100";
		ClsSession.writeSessionsItemCidSeqWithoutDurationAndRecordInfo(
				"rec_taobao" + tableName, "", 15, "output/Sessions" + tableName
						+ "_recordInfo.txt", "output/Sessions" + tableName
						+ "_itemCidSeq.txt", false, true);
	}

	// �����ݿ��л�ȡ�û��������Ϣ
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

	// �����ļ���
	public static void Test_ClsFile() {
		ClsFile cf = new ClsFile("output/TestLog.txt");
		cf.writeFile("�����ļ��Ĳ�������");
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
	// ����ҳ����
	public static void Test_ClsPage() {
		ClsPage cp = new ClsPage();
		cp.getParam("q");
		cp.getParam("qs");
		cp.getParam("q");
		System.out.println(cp.serializeParamsInfo());
	}
}