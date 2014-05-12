package process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class ClsSession {

	private String sessionUserId;
	private String sessionUserIp;
	private int sessionId;
	private static SimpleDateFormat timeFormat = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss");
	private Date sessionStartTime = null;// �Ự��ʼʱ��
	private Date sessionEndTime = null;// �Ự����ʱ��
	private Vector<ClsRecord> sessionRecords = null;

	private String sessionStartPageTypeGeneralName;// ��ʼ��ҳ������
	private String sessionStartUserActionGeneralName;// ��ʼ���û���Ϊ
	private String sessionPageTypeCodeSeq;// ҳ����������
	private String sessionUserActionCodeSeq;// �û���Ϊ����

	private int recordsNum;

	private float sessionSearchActionRatio;// ������Ϊ��
	private float sessionBrowseActionRatio;// �����Ϊ��
	private float sessionTradeActionRatio;// ������Ϊ��
	private float sessionListProActionRatio;// �б�����Ϊ��
	private float sessionIMActionRatio;// ���Ϲ�����Ϊ��
	private float sessionLoginActionRatio;// ��¼��Ϊ��

	private int sessionSearchPageTime;// ������ҳ��ʱ��
	private int sessionResultListTime;// ����б�ҳ��ʱ��
	private int sessionObjectDetailTime;// ��������ҳ��ʱ��
	private int sessionHomepageTime;// �Ա��Ż�ҳ��ʱ��
	private int sessionActivityTime;// ��Ա�ҳ��ʱ��
	private int sessionTradeTime;// ����ҳ����ʱ��
	private int sessionSelfInfoTime;// ��������ҳ����ʱ��

	private float sessionSearchPageAvgDuration;// ������ҳƽ��ͣ��ʱ��
	private float sessionResultListAvgDuration;// ����б�ҳƽ��ͣ��ʱ��
	private float sessionObjectDetailAvgDuration;// ��������ҳƽ��ͣ��ʱ��
	private float sessionHomepageAvgDuration;// �Ա��Ż�ҳƽ��ͣ��ʱ��
	private float sessionActivityAvgDuration;// ��Ա�ҳƽ��ͣ��ʱ��
	private float sessionTradeAvgDuration;// ����ҳ��ƽ��ͣ��ʱ��
	private float sessionSelfInfoAvgDuration;// ��������ҳ��ƽ��ͣ��ʱ��

	private String sessionStartTimePeriod;// �Ự��ʼʱ���
	private String sessionEndTimePeriod;// �Ự����ʱ���

	private String sessionTypeDurationSeq;// ����ʱ���ҳ����������
	private String sessionTypeSeq;// ҳ����������
	private String sessionActionSeq;// ��Ϊ��������

	private int sessionClassNumInvolved;// �漰����Ŀ����
	private int sessionItemNumInvolved;// �漰����Ŀ����
	private Vector<Integer> sessionClassIdList;
	private Vector<Long> sessionItemIdList;

	private static double NumPerFlicker = 10000;// ��ʱ����˸һ�δ�Լ�����session����

	// ����Ự������
	// ����ʱ����չ

	public ClsSession() {
		sessionClassIdList = new Vector<Integer>();
		sessionItemIdList = new Vector<Long>();
	}

	// ���ݻỰʱ����չ�Ựά��
	public void expandSessionTimeInfoWithTime() {
		if (sessionStartTime != null) {
			switch (sessionStartTime.getHours()) {
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
				sessionStartTimePeriod = "����";
				break;
			case 19:
			case 20:
			case 21:
			case 22:
				sessionStartTimePeriod = "����";
				break;
			case 23:
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				sessionStartTimePeriod = "��ҹ";
				break;
			default:
				sessionStartTimePeriod = "";
			}
		}
		if (sessionEndTime != null) {
			switch (sessionEndTime.getHours()) {
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
				sessionEndTimePeriod = "����";
				break;
			case 19:
			case 20:
			case 21:
			case 22:
				sessionEndTimePeriod = "����";
				break;
			case 23:
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				sessionEndTimePeriod = "��ҹ";
				break;
			default:
				sessionEndTimePeriod = "";
			}
		}
	}

	// ���ݻỰҳ����������Ϊ��չ�Ựά��
	public void expandSessionStateInfoWithRecordsInfo() {
		sessionClassIdList = new Vector<Integer>();
		sessionItemIdList = new Vector<Long>();

		sessionClassNumInvolved = 0;
		sessionItemNumInvolved = 0;
		sessionPageTypeCodeSeq = "";
		sessionUserActionCodeSeq = "";

		if (sessionRecords != null) {
			sessionStartPageTypeGeneralName = sessionRecords.get(0)
					.getVisitPageTypeGeneralName();
			sessionStartUserActionGeneralName = sessionRecords.get(0)
					.getVisitUserActionGeneralName();
			recordsNum = sessionRecords.size();
			
			int searchNum = 0;
			int browseNum = 0;
			int tradeNum = 0;
			int listProNum = 0;
			int imNum = 0;
			int loginNum=0;
			
			int searchHomePageNum = 0;
			int resultListPageNum = 0;
			int objectDetailPageNum = 0;
			int taobaoHomePageNum = 0;
			int activityPageNum = 0;
			int tradePageNum = 0;
			int selfInfoPageNum = 0;

			sessionSearchPageTime = 0;// ������ҳ��ʱ��
			sessionResultListTime = 0;// ����б�ҳ��ʱ��
			sessionObjectDetailTime = 0;// ��������ҳ��ʱ��
			sessionHomepageTime = 0;// �Ա��Ż�ҳ��ʱ��
			sessionActivityTime = 0;// ��Ա�ҳ��ʱ��
			sessionTradeTime = 0;// ����ҳ����ʱ��
			sessionSelfInfoTime = 0;// ��������ҳ����ʱ��

			sessionSearchPageAvgDuration = 0;// ������ҳƽ��ͣ��ʱ��
			sessionResultListAvgDuration = 0;// ����б�ҳƽ��ͣ��ʱ��
			sessionObjectDetailAvgDuration = 0;// ��������ҳƽ��ͣ��ʱ��
			sessionHomepageAvgDuration = 0;// �Ա��Ż�ҳƽ��ͣ��ʱ��
			sessionActivityAvgDuration = 0;// ��Ա�ҳƽ��ͣ��ʱ��
			sessionTradeAvgDuration = 0;// ����ҳ��ƽ��ͣ��ʱ��
			sessionSelfInfoAvgDuration = 0;// ��������ҳ��ƽ��ͣ��ʱ��

			sessionTypeDurationSeq = "";// ����ʱ���ҳ����������
			sessionTypeSeq = "";// ����ʱ�����Ϊ��������
			sessionActionSeq = "";// ����ʱ�����Ϊ��������

			long currentRecordItemId = -1;
			int currentRecordItemRootCid = -1;
			String pageTypeCode = "";
			String userActionCode = "";
			for (int i = 0; i < recordsNum; i++) {
				sessionTypeDurationSeq += sessionRecords.get(i)
						.getVisitPageTypeName()
						+ "="
						+ sessionRecords.get(i).getVisitDuration() + ";";
				sessionTypeSeq += sessionRecords.get(i).getVisitPageTypeName()
						+ ";";
				sessionActionSeq += sessionRecords.get(i)
						.getVisitUserActionName() + ";";

				pageTypeCode = sessionRecords.get(i).getVisitPageTypeCode();
				userActionCode = sessionRecords.get(i).getVisitUserActionCode();
				currentRecordItemId = sessionRecords.get(i).getItemId();
				currentRecordItemRootCid = sessionRecords.get(i)
						.getItemRootCid();
				if (currentRecordItemId != -1
						&& !sessionItemIdList.contains(currentRecordItemId)) {
					sessionItemNumInvolved++;
					sessionItemIdList.add(currentRecordItemId);
				}
				if (currentRecordItemRootCid != -1
						&& !sessionClassIdList
								.contains(currentRecordItemRootCid)) {
					sessionClassNumInvolved++;
					sessionClassIdList.add(currentRecordItemRootCid);
				}
				if (userActionCode.equals("S")) {
					searchNum++;
				} else if (userActionCode.equals("B")) {
					browseNum++;
				} else if (userActionCode.equals("T")) {
					tradeNum++;
				} else if (userActionCode.equals("P")) {
					listProNum++;
				} else if (userActionCode.equals("M")) {
					imNum++;
				} else if (userActionCode.equals("L")) {
					loginNum++;
				}

				if (pageTypeCode.equals("S")) {
					searchHomePageNum++;
					sessionSearchPageTime += sessionRecords.get(i)
							.getVisitDuration();
				} else if (pageTypeCode.equals("L")) {
					resultListPageNum ++;
					sessionResultListTime += sessionRecords.get(i)
							.getVisitDuration();
				} else if (pageTypeCode.equals("D")) {
					objectDetailPageNum ++;
					sessionObjectDetailTime += sessionRecords.get(i)
							.getVisitDuration();
				} else if (pageTypeCode.equals("H")) {
					taobaoHomePageNum ++;
					sessionHomepageTime += sessionRecords.get(i)
							.getVisitDuration();
				} else if (pageTypeCode.equals("A")) {
					activityPageNum ++;
					sessionActivityTime += sessionRecords.get(i)
							.getVisitDuration();
				} else if (pageTypeCode.equals("T")) {
					tradePageNum ++;
					sessionTradeTime += sessionRecords.get(i)
							.getVisitDuration();
				} else if (pageTypeCode.equals("M")) {
					selfInfoPageNum ++;
					sessionSelfInfoTime += sessionRecords.get(i)
							.getVisitDuration();
				}
				sessionPageTypeCodeSeq += pageTypeCode;
				sessionUserActionCodeSeq += userActionCode;
			}
			sessionSearchActionRatio = (float) searchNum / recordsNum;// ������Ϊ��
			sessionBrowseActionRatio = (float) browseNum / recordsNum;// �����Ϊ��
			sessionTradeActionRatio = (float) tradeNum / recordsNum;// ������Ϊ��
			sessionListProActionRatio = (float) listProNum / recordsNum;// �б�����Ϊ��
			sessionIMActionRatio = (float) imNum / recordsNum;// �������Ϲ�����Ϊ��
			sessionLoginActionRatio = (float) loginNum / recordsNum;// ��¼��Ϊ��

			if (searchHomePageNum != 0)
				sessionSearchPageAvgDuration = (float) sessionSearchPageTime
						/ searchHomePageNum;
			if (resultListPageNum != 0)
				sessionResultListAvgDuration = (float) sessionResultListTime
						/ resultListPageNum;
			if (objectDetailPageNum != 0)
				sessionObjectDetailAvgDuration = (float) sessionObjectDetailTime
						/ objectDetailPageNum;
			if (taobaoHomePageNum != 0)
				sessionHomepageAvgDuration = (float) sessionHomepageTime
						/ taobaoHomePageNum;
			if (activityPageNum != 0)
				sessionActivityAvgDuration = (float) sessionActivityTime
						/ activityPageNum;
			if (tradePageNum != 0)
				sessionTradeAvgDuration = (float) sessionTradeTime
						/ tradePageNum;
			if (selfInfoPageNum != 0)
				sessionSelfInfoAvgDuration = (float) sessionSelfInfoTime
						/ selfInfoPageNum;

		}
	}

	// ����Ự�ĳ���ʱ�䣬������sΪ��λ���ַ�������һ�λỰ�����м�¼�����֮�����
	private String getSessionDurationStringByMin() {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("#.##");
		if (sessionRecords.size() == 0)
			return "0 min";
		return df
				.format((double) ((sessionEndTime.getTime() - sessionStartTime
						.getTime()) / 1000 + getSessionLastRecord()
						.getVisitDuration()) / 60);
	}

	// ����Ự�ĳ���ʱ�䣬������minΪ��λ���ַ�������һ�λỰ�����м�¼�����֮�����
	private long getSessionDurationStringBySecond() {
		return (sessionEndTime.getTime() - sessionStartTime.getTime()) / 1000
				+ getSessionLastRecord().getVisitDuration();
	}

	// ��ȡ�Ự�����һ����¼����һ����һ�λỰ�����м�¼�����֮�����
	public ClsRecord getSessionLastRecord() {// ��ȡ���һ����¼
		if (!sessionRecords.isEmpty()) {
			return sessionRecords.lastElement();
		} else {
			return null;
		}
	}

	// ��ȡ��һ����¼�����ԻỰ���ֲ�����ʼ��
	public ClsRecord receiveSessionFirstRecord(ClsRecord cr, String uid,
			String ip) {// ��ȡ��һ��·��
		if (cr != null) {
			sessionRecords = new Vector<ClsRecord>();
			sessionUserId = uid;
			sessionUserIp = ip;
			sessionRecords.add(cr);
			sessionStartTime = cr.getVisitTime();
			sessionEndTime = cr.getVisitTime();
		}
		return cr;
	}

	// ��ȡ�ǵ�һ����¼
	public ClsRecord receiveSessionLaterRecord(ClsRecord cr) {
		if (cr != null) {
			this.sessionRecords.add(cr);
			sessionEndTime = cr.getVisitTime();
		}
		return cr;
	}

	public String getSessionUserId() {
		return sessionUserId;
	}

	public String getSessionUserIp() {
		return sessionUserIp;
	}

	public Date getSessionTimeEnd() {
		return sessionEndTime;
	}

	// ����Ự�ĳ���ʱ�䣬��һ�λỰ�����м�¼�����֮�����
	public long getSessionDuration() {
		return (sessionEndTime.getTime() - sessionStartTime.getTime()) / 1000
				+ getSessionLastRecord().getVisitDuration();
	}

	// ��ȡ�Ự�ĳ��ȣ���һ�λỰ�����м�¼�����֮�����
	public int getSessionLength() {
		return this.sessionRecords.size();
	}

	// �����ݿ��л�ȡ�Ự���ϣ�����û�IdΪ�գ����ȡ�����û�sessions���ϣ������˼��Ϸ���
	public static Vector<ClsSession> getUserSessionsDirectly(String tableName,
			String uid, int interval) {
		// �����ȡsessions�Ĳ�ѯ���
		String sql = "";
		if (uid.equals("")) {
			sql = "select * from " + tableName + " order by uid ASC,date ASC";
		} else {
			sql = "select * from " + tableName + " where uid='" + uid
					+ "' order by date ASC";
		}
		// �����������ݿ�����
		ClsDb cd = new ClsDb(true);

		ClsSession currentSession = null;// ��ǰ �ĻỰΪ��
		ClsRecord currentRecord = null;// ��ǰ����ļ�¼Ϊ��

		Vector<ClsSession> sessions = new Vector<ClsSession>();

		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// ��ȡ��¼����
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// ���ü�ʱ��
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// ȥ���������ؼ��ֶεļ�¼
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;

				ct.estimating(rs.getRow());

				// ��䵱ǰ��¼
				currentRecord = new ClsRecord();
				if (rs.getString("page") != null)
					currentRecord.setVisitPage(rs.getString("page"));
				else
					currentRecord.setVisitPage("");
				if (rs.getString("query") != null)
					currentRecord.setVisitQuery(rs.getString("query"));
				if (rs.getString("url_md5") != null)
					currentRecord.setVisitUrlMd5(rs.getString("url_md5"));
				if (rs.getString("url") != null) {
					currentRecord.setVisitUrl(rs.getString("url"));
					currentRecord.expandRecordInfoWithUrl(true, false);
				}
				if (rs.getTimestamp("date") != null)
					currentRecord.setVisitTime(rs.getTimestamp("date"));
				if (rs.getString("referurl") != null)
					currentRecord.setVisitPreUrl(rs.getString("referurl"));
				if (rs.getInt("staytime") != 0)
					currentRecord.setVisitDuration(rs.getInt("staytime"));

				if (currentSession == null) {// �����û�лỰ�����ǵ�һ���Ự
					currentSession = new ClsSession();// ���������еĵ�һ���Ự
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					continue;
				}

				if (currentSession.getSessionUserId().equals(
						rs.getString("uid"))
						&& currentSession.getSessionUserIp().equals(
								rs.getString("ip"))
						&& (currentRecord.getVisitTime().getTime() - currentSession
								.getSessionTimeEnd().getTime()) / 1000 / 60 < interval) {
					currentSession.receiveSessionLaterRecord(currentRecord);
				} else {
					sessions.add(currentSession);
					currentSession = new ClsSession();
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
				}
			}
			if (currentSession != null) {// �������һ��session
				sessions.add(currentSession);
			}
			ct.estimateEnd();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("��ȡ�û��Ựʧ��");
			return null;
		}
		return sessions;
	}

	// ��һ��session��ֻ������¼�����л�
	public String serializeSessionRecordsInfoBody() {
		String line = sessionUserId + "\t" + sessionUserIp + "\t"
				+ sessionRecords.size() + "\t"
				+ getSessionDurationStringByMin() + "\n";
		return line;
	}

	// ��һ��session����������¼��ͷ�������л�
	public static String serializeSessionRecordsInfoHead() {
		String line = "�û�ID" + "\t" + "�û�IP" + "\t" + "�Ự����" + "\t" + "�Ự����ʱ��"
				+ "\n";
		return line;
	}

	// ���л�һ��session�������������м�¼
	public String serializeSessionRecordsInfo() {
		String block = "�û�ID��" + sessionUserId + " | ����IP��" + sessionUserIp
				+ " | �Ự����ʱ�䣺" + getSessionDurationStringByMin() + "min\n";
		for (int i = 0; i < sessionRecords.size(); i++) {
			block += sessionRecords.elementAt(i).serializeRecordInfoBody();
		}
		return block;
	}

	// ���л�һ��session��ҳ����Ϣ�������������м�¼
	public String serializeSessionPagesDescriptionSeqWithoutDuration() {
		String squence = sessionUserId + "\t" + sessionUserIp + "\t"
				+ getSessionDurationStringByMin() + "\t"
				+ sessionRecords.size();
		for (int i = 0; i < sessionRecords.size(); i++) {
			squence += "\t"
					+ sessionRecords.elementAt(i).getVisitPageDescription();
		}
		return squence;
	}

	// ���л�һ��session��ҳ��������Ϣ�������������м�¼����ʱ��
	public String serializeSessionPagesClassSeqWithDuration() {
		String squence = sessionUserId
				+ "\t"
				+ sessionUserIp
				+ "\t"
				+ (int) Math.ceil(Double
						.valueOf(getSessionDurationStringByMin()));
		if (sessionRecords.size() >= 1) {
			squence += "\t" + sessionRecords.elementAt(0).getVisitPageClass()
					+ "=" + sessionRecords.elementAt(0).getVisitDuration();
		}
		for (int i = 1; i < sessionRecords.size(); i++) {
			squence += ";" + sessionRecords.elementAt(i).getVisitPageClass()
					+ "=" + sessionRecords.elementAt(i).getVisitDuration();
		}
		return squence;
	}

	// ���ո�ʽҪ�����л�,���repeatΪtrue�����ӡ���м�¼��
	// ���repeatΪfalse���������ظ���¼ֻ��ӡһ��
	public String serializeSessionPagesInfoSeqWithoutDuration(boolean repeat) {
		String sequence = "";
		int num = 0;
		for (int i = 0; i < sessionRecords.size(); i++) {
			if (repeat
					|| i < 1
					|| !sessionRecords
							.elementAt(i - 1)
							.getVisitPageDescription()
							.equals(sessionRecords.elementAt(i)
									.getVisitPageDescription())) {
				sequence += "\t"
						+ sessionRecords.elementAt(i).getVisitPageDescription();
				num++;
			}
		}
		sequence = this.sessionUserId + "\t" + this.sessionUserIp + "\t" + num
				+ sequence;
		return sequence;
	}

	// ���л�ҳ��ͳ����Ϣ
	public String serializeSessionStatistics() {
		String sequence = "";
		sequence = this.sessionUserId + "\t" + this.sessionUserIp + "\t"
				+ sessionRecords.size() + "\t" + getSessionDuration();
		return sequence;
	}

	// ���ո�ʽҪ�����л�,���repeatΪtrue�����ӡ���м�¼��
	// ���repeatΪfalse���������ظ���¼ֻ��ӡһ��
	public String serializeSessionItemsCidSeqWithoutDuration(boolean repeat) {
		String sequence = "";
		int num = 0;
		int visitItemCid = -1;
		int lastVisitItemCid = -1;
		for (int i = 0; i < sessionRecords.size(); i++) {
			visitItemCid = sessionRecords.elementAt(i).getVisitContentItemCid();
			if (visitItemCid != -1
					&& (repeat || lastVisitItemCid != visitItemCid)) {
				sequence += "\t" + visitItemCid;
				lastVisitItemCid = visitItemCid;
				num++;
			}
		}
		sequence = this.sessionUserId + "\t" + this.sessionUserIp + "\t" + num
				+ sequence;
		return sequence;
	}

	// ���ո�ʽҪ�����л�,���repeatΪtrue�����ӡ���м�¼��
	// ���repeatΪfalse���������ظ���¼ֻ��ӡһ��
	public String serializeSessionItemsCnameSeqWithoutDuration(boolean repeat) {
		String sequence = "";
		int num = 0;
		String visitItemCname = "";
		String lastVisitItemCname = "";
		for (int i = 0; i < sessionRecords.size(); i++) {
			visitItemCname = sessionRecords.elementAt(i).getVisitItemCname();
			if (!visitItemCname.equals("")
					&& (repeat || !lastVisitItemCname.equals(visitItemCname))) {
				sequence += "\t" + visitItemCname;
				lastVisitItemCname = visitItemCname;
				num++;
			}
		}
		sequence = this.sessionUserId + "\t" + this.sessionUserIp + "\t" + num
				+ sequence;
		return sequence;
	}

	// ��ӡ�Ự��ϸ��Ϣ
	public void printSessionRecordsInfo() {
		System.out.println(serializeSessionRecordsInfo());
	}

	// ��ӡ�����session���ϵ�ͳ����Ϣ
	public static boolean writeSessionsStatFromSessions(String fileName,
			Vector<ClsSession> sessions) {
		if (sessions == null)
			return false;
		if (fileName.equals("")) {
			fileName = ClsBase.getFileNameTime() + ".txt";
		}
		ClsFile cf = new ClsFile(fileName);
		cf.writeFile(ClsSession.serializeSessionRecordsInfoHead());
		for (int i = 0; i < sessions.size(); i++) {
			cf.writeFile(sessions.elementAt(i)
					.serializeSessionRecordsInfoBody());
		}
		cf.closeFile();
		return true;
	}

	// ��ӡ�����session���ϣ��������е����м�¼
	public static boolean writeSessionsRecordInfoFromSessions(String fileName,
			Vector<ClsSession> sessions) {
		if (sessions == null)
			return false;
		if (fileName.equals("")) {
			fileName = ClsBase.getFileNameTime() + ".txt";
		}
		ClsFile cf = new ClsFile(fileName);
		cf.writeFile(ClsRecord.serializeRecordInfoHead());
		for (int i = 0; i < sessions.size(); i++) {
			cf.writeFile(sessions.elementAt(i).serializeSessionRecordsInfo()
					+ "\n");
		}
		cf.closeFile();
		return true;
	}

	// ��ӡ�����session���ϣ�ֻ�����������У�����ҳ��ͣ��ʱ��
	public static boolean writeSessionsPageClassSeqWithDurationFromSessions(
			String fileName, Vector<ClsSession> sessions) {
		if (sessions == null)
			return false;
		if (fileName.equals("")) {
			fileName = ClsBase.getFileNameTime() + ".txt";
		}
		ClsFile cf = new ClsFile(fileName);
		cf.writeFile(ClsRecord.serializeRecordInfoHead());
		for (int i = 0; i < sessions.size(); i++) {
			cf.writeFile(sessions.elementAt(i)
					.serializeSessionPagesClassSeqWithDuration() + "\n");
		}
		cf.closeFile();
		return true;
	}

	// ��ʱ�����ݿ��л�ȡ�Ự���ϣ�����ӡ���лỰ����ϸ��Ϣ
	public static int writeSessionsPageClassSeqWithDurationAndRecordInfo(
			String tableName, String uid, int interval, String contentFileName,
			String statFileName, String classSeqFileName) {
		// �����ȡsessions�Ĳ�ѯ���
		String sql = "";
		if (uid.equals("")) {
			sql = "select * from " + tableName + " order by uid ASC,date ASC";
		} else {
			sql = "select * from " + tableName + " where uid='" + uid
					+ "' order by date ASC";
		}
		// �����������ݿ�����
		ClsDb cd = new ClsDb(true);
		ClsFile cfContent = new ClsFile(contentFileName);
		ClsFile cfStat = new ClsFile(statFileName);
		ClsFile cfClassSeq = new ClsFile(classSeqFileName);
		ClsSession currentSession = null;// ��ǰ �ĻỰΪ��
		ClsRecord currentRecord = null;// ��ǰ����ļ�¼Ϊ��

		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// ��ȡ��¼����
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// ���ü�ʱ��
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// ȥ���������ؼ��ֶεļ�¼
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;

				ct.estimating(rs.getRow());

				// ��䵱ǰ��¼
				currentRecord = new ClsRecord();
				if (rs.getString("page") != null)
					currentRecord.setVisitPage(rs.getString("page"));
				else
					currentRecord.setVisitPage("");
				if (rs.getString("query") != null)
					currentRecord.setVisitQuery(rs.getString("query"));
				if (rs.getString("url_md5") != null)
					currentRecord.setVisitUrlMd5(rs.getString("url_md5"));
				if (rs.getString("url") != null) {
					currentRecord.setVisitUrl(rs.getString("url"));
					currentRecord.expandRecordInfoWithUrl(true, false);
				}
				if (rs.getString("referurl") != null) {
					currentRecord.setVisitPreUrl(rs.getString("referurl"));
				}
				if (rs.getTimestamp("date") != null)
					currentRecord.setVisitTime(rs.getTimestamp("date"));
				if (rs.getInt("staytime") != 0)
					currentRecord.setVisitDuration(rs.getInt("staytime"));

				if (currentSession == null) {// �����û�лỰ�����ǵ�һ���Ự
					currentSession = new ClsSession();// ���������еĵ�һ���Ự
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
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
					currentRecord = null;
				} else {
					cfContent.writeFile(currentSession
							.serializeSessionRecordsInfo());
					cfStat.writeFile(currentSession
							.serializeSessionPagesDescriptionSeqWithoutDuration()
							+ "\n");
					cfClassSeq
							.writeFile(currentSession
									.serializeSessionPagesClassSeqWithDuration()
									+ "\n");
					currentSession = null;
					currentSession = new ClsSession();
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					currentRecord = null;
				}
			}
			if (currentSession != null) {// �������һ��session
				cfContent.writeFile(currentSession
						.serializeSessionRecordsInfo());
				cfStat.writeFile(currentSession
						.serializeSessionPagesDescriptionSeqWithoutDuration()
						+ "\n");
				cfClassSeq.writeFile(currentSession
						.serializeSessionPagesClassSeqWithDuration() + "\n");
				currentSession = null;
			}
			ct.estimateEnd();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("��ȡ�û��Ựʧ��");
			return 0;
		}
		return 0;
	}

	// �����ݿ��л�ȡ�Ự����
	public static int writeSessionsPageDescriptionSeqWithoutDurationAndRecordInfo(
			String tableName, String uid, int interval, String contentFileName,
			String statFileName, boolean repeat) {
		// �����ȡsessions�Ĳ�ѯ���
		String sql = "";
		if (uid.equals("")) {
			sql = "select * from " + tableName + " order by uid ASC,date ASC";
		} else {
			sql = "select * from " + tableName + " where uid='" + uid
					+ "' order by date ASC";
		}
		// �����������ݿ�����
		ClsDb cd = new ClsDb(true);
		ClsFile cfContent = new ClsFile(contentFileName);
		ClsFile cfStat = new ClsFile(statFileName);
		ClsSession currentSession = null;// ��ǰ �ĻỰΪ��
		ClsRecord currentRecord = null;// ��ǰ����ļ�¼Ϊ��
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// ��ȡ��¼����
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// ���ü�ʱ��
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// ȥ���������ؼ��ֶεļ�¼
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;

				ct.estimating(rs.getRow());

				// ��䵱ǰ��¼
				currentRecord = new ClsRecord();
				if (rs.getString("page") != null)
					currentRecord.setVisitPage(rs.getString("page"));
				else
					currentRecord.setVisitPage("");
				if (rs.getString("query") != null)
					currentRecord.setVisitQuery(rs.getString("query"));
				if (rs.getString("url_md5") != null)
					currentRecord.setVisitUrlMd5(rs.getString("url_md5"));
				if (rs.getString("url") != null) {
					currentRecord.setVisitUrl(rs.getString("url"));
					currentRecord.expandRecordInfoWithUrl(false, false);
				}
				if (rs.getString("referurl") != null) {
					currentRecord.setVisitPreUrl(rs.getString("referurl"));
				}
				if (rs.getTimestamp("date") != null)
					currentRecord.setVisitTime(rs.getTimestamp("date"));
				if (rs.getInt("staytime") != 0)
					currentRecord.setVisitDuration(rs.getInt("staytime"));

				if (currentSession == null) {// �����û�лỰ�����ǵ�һ���Ự
					currentSession = new ClsSession();// ���������еĵ�һ���Ự
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
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
					currentRecord = null;
				} else {
					cfContent.writeFile(currentSession
							.serializeSessionRecordsInfo());
					cfStat.writeFile(currentSession
							.serializeSessionPagesInfoSeqWithoutDuration(repeat)
							+ "\n");
					currentSession = null;
					currentSession = new ClsSession();
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					currentRecord = null;
				}
			}
			if (currentSession != null) {// �������һ��session
				cfContent.writeFile(currentSession
						.serializeSessionRecordsInfo());
				cfStat.writeFile(currentSession
						.serializeSessionPagesInfoSeqWithoutDuration(repeat)
						+ "\n");
				currentSession = null;
			}
			ct.estimateEnd();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("��ȡ�û��Ựʧ��");
			return 0;
		}
		return 0;
	}

	// �����ݿ��л�ȡƷ���༯��
	public static int writeSessionsItemCidSeqWithoutDurationAndRecordInfo(
			String tableName, String uid, int interval, String contentFileName,
			String cidSeqFileName, boolean isCid, boolean repeat) {
		// �����ȡsessions�Ĳ�ѯ���
		String sql = "";
		if (uid.equals("")) {
			sql = "select * from " + tableName + " order by uid ASC,date ASC";
		} else {
			sql = "select * from " + tableName + " where uid='" + uid
					+ "' order by date ASC";
		}
		// �����������ݿ�����
		ClsDb cd = new ClsDb(true);
		ClsFile cfContent = new ClsFile(contentFileName);
		ClsFile cfStat = new ClsFile(cidSeqFileName);
		ClsSession currentSession = null;// ��ǰ �ĻỰΪ��
		ClsRecord currentRecord = null;// ��ǰ����ļ�¼Ϊ��
		int sessionNum = 1;// �Ự����

		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// ��ȡ��¼����
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// ���ü�ʱ��
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();

			while (rs.next()) {
				// ȥ���������ؼ��ֶεļ�¼
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;

				ct.estimating(rs.getRow());

				// ��䵱ǰ��¼
				currentRecord = new ClsRecord();
				if (rs.getString("page") != null)
					currentRecord.setVisitPage(rs.getString("page"));
				else
					currentRecord.setVisitPage("");
				if (rs.getString("query") != null)
					currentRecord.setVisitQuery(rs.getString("query"));
				if (rs.getString("url_md5") != null)
					currentRecord.setVisitUrlMd5(rs.getString("url_md5"));
				if (rs.getString("url") != null) {
					currentRecord.setVisitUrl(rs.getString("url"));
					currentRecord.expandRecordInfoWithUrl(true, false);
				}
				if (rs.getString("referurl") != null) {
					currentRecord.setVisitPreUrl(rs.getString("referurl"));
				}
				if (rs.getTimestamp("date") != null)
					currentRecord.setVisitTime(rs.getTimestamp("date"));
				if (rs.getInt("staytime") != 0)
					currentRecord.setVisitDuration(rs.getInt("staytime"));

				if (currentSession == null) {// �����û�лỰ�����ǵ�һ���Ự
					currentSession = new ClsSession();// ���������еĵ�һ���Ự
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
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
					currentRecord = null;
				} else {
					cfContent.writeFile(currentSession
							.serializeSessionRecordsInfo());
					if (isCid) {
						cfStat.writeFile(sessionNum
								+ "\t"
								+ currentSession
										.serializeSessionItemsCidSeqWithoutDuration(repeat)
								+ "\n");
					} else {
						cfStat.writeFile(sessionNum
								+ "\t"
								+ currentSession
										.serializeSessionItemsCnameSeqWithoutDuration(repeat)
								+ "\n");
					}
					sessionNum++;
					currentSession = null;
					currentSession = new ClsSession();
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					currentRecord = null;
				}
			}
			if (currentSession != null) {// �������һ��session
				cfContent.writeFile(currentSession
						.serializeSessionRecordsInfo());
				if (isCid) {
					cfStat.writeFile(sessionNum
							+ "\t"
							+ currentSession
									.serializeSessionItemsCidSeqWithoutDuration(repeat)
							+ "\n");
				} else {
					cfStat.writeFile(sessionNum
							+ "\t"
							+ currentSession
									.serializeSessionItemsCnameSeqWithoutDuration(repeat)
							+ "\n");
				}
				sessionNum++;
				currentSession = null;
			}
			ct.estimateEnd();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("��ȡ�û��Ựʧ��");
			return 0;
		}
		return 0;
	}

	public static void writeSessionStatisticsToFilesWithIntervalRange(
			String tableName, int startInterval, int endInterval,
			String statFileName) {
		ClsFile cfStat = new ClsFile(statFileName);
		for (int i = startInterval; i <= endInterval; i++) {

			cfStat.writeFile(serializeSessionsStatisticsByIntervalFromDbDirectly(
					tableName, i, statFileName) + "\n");
		}
	}

	// �����ݿ��л�ȡ�Ựͳ����Ϣ����д���ļ���������ͬҳ���ظ�����
	public static String writeSessionsStatisticsToFileFromDbDirectly(
			String tableName, int interval, String statFileName) {
		int sessionNum = 0;// �Ự����
		long sessionSumDuration = 0;// �Ự�ܳ���ʱ��
		int sessionSumLength = 0;// �Ự�ܳ���
		int sessionMaxLength = 0;// ��Ự����
		// �����ȡsessions�Ĳ�ѯ���
		String sql = "select * from " + tableName
				+ " order by uid ASC,date ASC";
		ClsDb cd = new ClsDb(true);// �����������ݿ�����
		ClsFile cfStat = new ClsFile(statFileName);
		ClsSession currentSession = null;// ��ǰ �ĻỰΪ��
		ClsRecord currentRecord = null;// ��ǰ����ļ�¼Ϊ��
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// ��ȡ��¼����
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// ���ü�ʱ��
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// ȥ���������ؼ��ֶεļ�¼
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;
				ct.estimating(rs.getRow());

				// ��䵱ǰ��¼
				currentRecord = new ClsRecord();
				if (rs.getString("page") != null)
					currentRecord.setVisitPage(rs.getString("page"));
				else
					currentRecord.setVisitPage("");
				if (rs.getString("query") != null)
					currentRecord.setVisitQuery(rs.getString("query"));
				if (rs.getString("url_md5") != null)
					currentRecord.setVisitUrlMd5(rs.getString("url_md5"));
				if (rs.getString("url") != null) {
					currentRecord.setVisitUrl(rs.getString("url"));
					// currentRecord.ExpandRecordInfoWithUrl(false, false,
					// false);
				}
				if (rs.getString("referurl") != null) {
					currentRecord.setVisitPreUrl(rs.getString("referurl"));
				}
				if (rs.getTimestamp("date") != null)
					currentRecord.setVisitTime(rs.getTimestamp("date"));
				if (rs.getInt("staytime") != 0)
					currentRecord.setVisitDuration(rs.getInt("staytime"));

				if (currentSession == null) {// �����û�лỰ�����ǵ�һ���Ự
					currentSession = new ClsSession();// ���������еĵ�һ���Ự
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
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
					currentRecord = null;
				} else {
					cfStat.writeFile(currentSession
							.serializeSessionStatistics() + "\n");
					if (currentSession.getSessionLength() > sessionMaxLength) {
						sessionMaxLength = currentSession.getSessionLength();
					}
					sessionNum++;
					sessionSumDuration += currentSession.getSessionDuration();
					sessionSumLength += currentSession.getSessionLength();
					currentSession = null;
					currentSession = new ClsSession();
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					currentRecord = null;
				}
			}
			if (currentSession != null) {// �������һ��session
				cfStat.writeFile(currentSession.serializeSessionStatistics()
						+ "\n");
				if (currentSession.getSessionLength() > sessionMaxLength) {
					sessionMaxLength = currentSession.getSessionLength();
				}
				sessionNum++;
				sessionSumDuration += currentSession.getSessionDuration();
				sessionSumLength += currentSession.getSessionLength();
				currentSession = null;
			}
			ct.estimateEnd();
			rs.close();
			return "" + interval + "\t" + sessionNum + "\t"
					+ sessionSumDuration + "\t" + sessionSumLength + "\t"
					+ sessionMaxLength + "\t" + (float) sessionSumDuration
					/ sessionNum + "\t" + (float) sessionSumLength / sessionNum;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("��ȡ�û��Ựʧ��");
			return "";
		}
	}

	// �����ݿ��л�ȡ�Ựͳ����Ϣ����д���ļ���������ͬҳ���ظ�����
	public static String serializeSessionsStatisticsByIntervalFromDbDirectly(
			String tableName, int interval, String statFileName) {
		int sessionNum = 0;// �Ự����
		long sessionSumDuration = 0;// �Ự�ܳ���ʱ��
		int sessionSumLength = 0;// �Ự�ܳ���
		int sessionMaxLength = 0;// ��Ự����
		// �����ȡsessions�Ĳ�ѯ���
		String sql = "select * from " + tableName
				+ " order by uid ASC,date ASC";
		ClsDb cd = new ClsDb(true);// �����������ݿ�����
		ClsSession currentSession = null;// ��ǰ �ĻỰΪ��
		ClsRecord currentRecord = null;// ��ǰ����ļ�¼Ϊ��
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// ��ȡ��¼����
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// ���ü�ʱ��
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// ȥ���������ؼ��ֶεļ�¼
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;
				ct.estimating(rs.getRow());

				// ��䵱ǰ��¼
				currentRecord = new ClsRecord();
				if (rs.getString("page") != null)
					currentRecord.setVisitPage(rs.getString("page"));
				else
					currentRecord.setVisitPage("");
				if (rs.getString("query") != null)
					currentRecord.setVisitQuery(rs.getString("query"));
				if (rs.getString("url_md5") != null)
					currentRecord.setVisitUrlMd5(rs.getString("url_md5"));
				if (rs.getString("url") != null) {
					currentRecord.setVisitUrl(rs.getString("url"));
					// currentRecord.ExpandRecordInfoWithUrl(false, false,
					// false);
				}
				if (rs.getString("referurl") != null) {
					currentRecord.setVisitPreUrl(rs.getString("referurl"));
				}
				if (rs.getTimestamp("date") != null)
					currentRecord.setVisitTime(rs.getTimestamp("date"));
				if (rs.getInt("staytime") != 0)
					currentRecord.setVisitDuration(rs.getInt("staytime"));

				if (currentSession == null) {// �����û�лỰ�����ǵ�һ���Ự
					currentSession = new ClsSession();// ���������еĵ�һ���Ự
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
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
					currentRecord = null;
				} else {
					if (currentSession.getSessionLength() > sessionMaxLength) {
						sessionMaxLength = currentSession.getSessionLength();
					}
					sessionNum++;
					sessionSumDuration += currentSession.getSessionDuration();
					sessionSumLength += currentSession.getSessionLength();
					currentSession = null;
					currentSession = new ClsSession();
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					currentRecord = null;
				}
			}
			if (currentSession != null) {// �������һ��session
				if (currentSession.getSessionLength() > sessionMaxLength) {
					sessionMaxLength = currentSession.getSessionLength();
				}
				sessionNum++;
				sessionSumDuration += currentSession.getSessionDuration();
				sessionSumLength += currentSession.getSessionLength();
				currentSession = null;
			}
			ct.estimateEnd();
			rs.close();
			return "" + interval + "\t" + sessionNum + "\t"
					+ sessionSumDuration + "\t" + sessionSumLength + "\t"
					+ sessionMaxLength + "\t" + (float) sessionSumDuration
					/ sessionNum + "\t" + (float) sessionSumLength / sessionNum;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("��ȡ�û��Ựʧ��");
			return "";
		}
	}

	// �����ݿ��л�ȡ�Ựͳ����Ϣ����д���ļ���������ͬҳ���ظ�����
	public static boolean writeSessionsProcessedInfoToFile(String tableName,
			String statFileName) {
		// �����ȡsessions�Ĳ�ѯ���
		int currentSid = -1;
		int previousSid = -1;
		String sql = "select * from " + tableName
				+ " order by sid_by45min,uid,date limit 1000,2000";
		ClsDb cd = new ClsDb(true);// �����������ݿ�����
		ClsFile cfStat = new ClsFile(statFileName);
		ClsSession currentSession = null;// ��ǰ �ĻỰΪ��
		ClsRecord currentRecord = null;// ��ǰ����ļ�¼Ϊ��
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// ��ȡ��¼����
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / 100);// ���ü�ʱ��
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// ȥ���������ؼ��ֶεļ�¼
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;
				ct.estimating(rs.getRow());
				// ��䵱ǰ��¼
				currentRecord = new ClsRecord();
				if (rs.getString("page") != null)
					currentRecord.setVisitPage(rs.getString("page"));
				else
					currentRecord.setVisitPage("");
				if (rs.getInt("sid_by45min") != 0) {
					currentSid = rs.getInt("sid_by45min");

				}
				if (rs.getString("query") != null)
					currentRecord.setVisitQuery(rs.getString("query"));
				if (rs.getString("url_md5") != null)
					currentRecord.setVisitUrlMd5(rs.getString("url_md5"));
				if (rs.getString("url") != null) {
					currentRecord.setVisitUrl(rs.getString("url"));
				}
				if (rs.getString("referurl") != null) {
					currentRecord.setVisitPreUrl(rs.getString("referurl"));
				}
				if (rs.getString("Type20140407") != null) {
					currentRecord.setVisitPageTypeName(rs
							.getString("Type20140407"));
				}
				if (rs.getString("Action20140407") != null) {
					currentRecord.setVisitUserActionName(rs
							.getString("Action20140407"));
				}
				if (rs.getString("Type20140407_high") != null) {
					currentRecord.setVisitPageTypeGeneralName(rs
							.getString("Type20140407_high"));
				}
				if (rs.getString("Action20140407_high") != null) {
					currentRecord.setVisitUserActionGeneralName(rs
							.getString("Action20140407_high"));
				}
				if (rs.getTimestamp("date") != null)
					currentRecord.setVisitTime(rs.getTimestamp("date"));
				if (rs.getInt("staytime") != 0)
					currentRecord.setVisitDuration(rs.getInt("staytime"));
				currentRecord.expandRecordInfoWithUrl(true, true);
				if (currentSession == null) {// �����û�лỰ�����ǵ�һ���Ự
					currentSession = new ClsSession();// ���������еĵ�һ���Ự
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					currentSession.sessionId = currentSid;
					previousSid = currentSid;
					currentRecord = null;
					cfStat.writeFile(currentSession
							.serializeSessionsProcessedInfoHead());
					continue;
				} else if (previousSid == currentSid) {
					currentSession.receiveSessionLaterRecord(currentRecord);
					currentRecord = null;
				} else {
					currentSession.expandSessionStateInfoWithRecordsInfo();
					currentSession.expandSessionTimeInfoWithTime();
					cfStat.writeFile(currentSession
							.serializeSessionsProcessedInfo());
					currentSession = null;
					currentSession = new ClsSession();
					previousSid = currentSid;
					currentSession.sessionId = currentSid;
					currentSession.receiveSessionFirstRecord(currentRecord,
							rs.getString("uid"), rs.getString("ip"));
					currentRecord = null;
				}
			}
			if (currentSession != null) {// �������һ��session
				currentSession.expandSessionStateInfoWithRecordsInfo();
				currentSession.expandSessionTimeInfoWithTime();
				cfStat.writeFile(currentSession
						.serializeSessionsProcessedInfo());
				currentSession = null;
			}
			ct.estimateEnd();
			rs.close();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("��ȡ�û��Ựʧ��");
			return false;
		}
	}

	private String serializeSessionsProcessedInfo() {
		String squence = sessionId + "\t" + sessionUserId + "\t"
				+ sessionUserIp + "\t" + sessionRecords.size() + "\t"
				+ getSessionDurationStringBySecond() + "\t"
				+ this.sessionItemNumInvolved + "\t"
				+ this.sessionClassNumInvolved + "\t"
				+ this.sessionStartTimePeriod + "\t"
				+ this.sessionEndTimePeriod + "\t"
				+ timeFormat.format(this.sessionStartTime) + "\t"
				+ timeFormat.format(this.sessionEndTime) + "\t"
				+ this.sessionStartPageTypeGeneralName + "\t"
				+ this.sessionStartUserActionGeneralName + "\t"
				+ this.sessionSearchActionRatio + "\t"
				+ this.sessionBrowseActionRatio + "\t"
				+ this.sessionTradeActionRatio + "\t"
				+ this.sessionListProActionRatio + "\t"
				+ this.sessionIMActionRatio + "\t"
				+ this.sessionLoginActionRatio + "\t"

				+ this.sessionSearchPageTime + "\t"
				+ this.sessionResultListTime + "\t"
				+ this.sessionObjectDetailTime + "\t"
				+ this.sessionHomepageTime + "\t" + this.sessionActivityTime
				+ "\t" + this.sessionTradeTime + "\t"
				+ this.sessionSelfInfoTime + "\t"

				+ this.sessionSearchPageAvgDuration + "\t"
				+ this.sessionResultListAvgDuration + "\t"
				+ this.sessionObjectDetailAvgDuration + "\t"
				+ this.sessionHomepageAvgDuration + "\t"
				+ this.sessionActivityAvgDuration + "\t"
				+ this.sessionTradeAvgDuration + "\t"
				+ this.sessionSelfInfoAvgDuration + "\t"

				+ this.sessionPageTypeCodeSeq + "\t"
				+ this.sessionUserActionCodeSeq + "\t" + this.sessionTypeSeq
				+ "\t" + this.sessionActionSeq + "\t"
				+ this.sessionTypeDurationSeq + "\n";
		// for (int i = 0; i < sessionRecords.size(); i++) {
		// squence += "\t"
		// + sessionRecords.elementAt(i).getVisitPageDescription();
		// }
		return squence;
	}

	private String serializeSessionsProcessedInfoHead() {
		String squence = "�ỰID\t�û�ID\t�û�IP\t�Ự��¼��\t�Ự����ʱ��/��\t�漰��Ʒ��\t�漰��Ŀ��\t��ʼ����ʱ���\t��������ʱ���\t�Ự��ʼʱ��\t�Ự����ʱ��\t��ʼҳ������\t��ʼ�û���Ϊ\t������Ϊ����\t�����Ϊ����\t������Ϊ����\t�б�����Ϊ����\t���Ϲ�����Ϊ����\t�û���¼��Ϊ����\t������ҳ��ʱ��/��\t����б�ҳ��ʱ��/��\t��������ҳ��ʱ��/��\t�Ա��Ż�ҳ��ʱ��/��\t��Ա�ҳ��ʱ��/��\t����ҳ����ʱ��/��\t��������ҳ����ʱ��/��\t������ҳƽ��ͣ��ʱ��/��\t����б�ҳƽ��ͣ��ʱ��/��\t��������ҳƽ��ͣ��ʱ��/��\t�Ա��Ż�ҳƽ��ͣ��ʱ��/��\t��Ա�ҳƽ��ͣ��ʱ��/��\t����ҳ��ƽ��ͣ��ʱ��/��\t��������ҳ��ƽ��ͣ��ʱ��/��\tҳ����������\t�û���Ϊ����\tҳ����������\t��Ϊ��������\tҳ����������_ͣ��ʱ��\n";
		return squence;
	}
	public static void writeRecords(String fileName) {
		// �����ȡsessions�Ĳ�ѯ���
		String sql = "SELECT uid,ip,url,referurl,date,staytime,url_kw,refer_kw,newsite,url_md5,page,query,sid_by45min,Type20140407,Action20140407,Type20140407_high,Action20140407_high FROM rec_taobao_valid";
		ClsDb cd = new ClsDb(true);// �����������ݿ�����
		ClsFile cfStat = new ClsFile(fileName);
		String uid = "";
		String ip = "";
		String url = "";
		String referurl = "";
		Date date = new Date();
		int staytime = 0;
		String url_kw = "";
		String refer_kw = "";
		String newsite = "";
		String url_md5 = "";
		String page = "";
		String query = "";
		int sid_by45min = 0;
		String Type20140407 = "";
		String Action20140407 = "";
		String Type20140407_high = "";
		String Action20140407_high = "";
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// ��ȡ��¼����
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// ���ü�ʱ��
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				if (rs.getString("uid") == null) {
					uid = "NULL";
				} else {
					uid = rs.getString("uid");
				}
				if (rs.getString("ip") == null) {
					ip = "NULL";
				} else {
					ip = rs.getString("ip");
				}
				if (rs.getString("url") == null) {
					url = "NULL";
				} else {
					url = rs.getString("url");
				}
				if (rs.getString("referurl") == null) {
					referurl = "NULL";
				} else {
					referurl = rs.getString("referurl");
				}
				if (rs.getTimestamp("date") == null) {
					date = null;
				} else {
					date = rs.getTimestamp("date");
				}
				if (rs.getInt("staytime") == 0) {
					staytime = 0;
				} else {
					staytime = rs.getInt("staytime");
				}
				if (rs.getString("url_kw") == null) {
					url_kw = "NULL";
				} else {
					url_kw = rs.getString("url_kw");
				}
				if (rs.getString("refer_kw") == null) {
					refer_kw = "NULL";
				} else {
					refer_kw = rs.getString("refer_kw");
				}
				if (rs.getString("newsite") == null) {
					newsite = "NULL";
				} else {
					newsite = rs.getString("newsite");
				}
				if (rs.getString("url_md5") == null) {
					url_md5 = "NULL";
				} else {
					url_md5 = rs.getString("url_md5");
				}
				if (rs.getString("page") == null) {
					page = "NULL";
				} else {
					page = rs.getString("page");
				}
				if (rs.getString("query") == null) {
					query = "NULL";
				} else {
					query = rs.getString("query");
				}
				if (rs.getInt("sid_by45min") == 0) {
					sid_by45min = 0;
				} else {
					sid_by45min = rs.getInt("sid_by45min");
				}
				if (rs.getString("Type20140407") == null) {
					Type20140407 = "NULL";
				} else {
					Type20140407 = rs.getString("Type20140407");
				}
				if (rs.getString("Action20140407") == null) {
					Action20140407 = "NULL";
				} else {
					Action20140407 = rs.getString("Action20140407");
				}
				if (rs.getString("Type20140407_high") == null) {
					Type20140407_high = "NULL";
				} else {
					Type20140407_high = rs.getString("Type20140407_high");
				}
				if (rs.getString("Action20140407_high") == null) {
					Action20140407_high = "NULL";
				} else {
					Action20140407_high = rs.getString("Action20140407_high");
				}
				cfStat.writeFile("uid\tip\turl\treferurl\tdate\tstaytime\turl_kw\trefer_kw\tnewsite\turl_md5\tpage\tquery\tsid_by45min\tType20140407\tAction20140407\tType20140407_high\tAction20140407_high\n");
				cfStat.writeFile(uid + "\t" + ip + "\t" + url + "\t" + referurl
						+ "\t" + timeFormat.format(date) + "\t" + staytime
						+ "\t" + url_kw + "\t" + refer_kw + "\t" + newsite
						+ "\t" + url_md5 + "\t" + page + "\t" + query + "\t"
						+ sid_by45min + "\t" + Type20140407 + "\t"
						+ Action20140407 + "\t" + Type20140407_high + "\t"
						+ Action20140407_high + "\n");
				ct.estimating(rs.getRow());
			}
			ct.estimateEnd();
			rs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
