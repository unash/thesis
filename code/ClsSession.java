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
	private Date sessionStartTime = null;// 会话开始时间
	private Date sessionEndTime = null;// 会话结束时间
	private Vector<ClsRecord> sessionRecords = null;

	private String sessionStartPageTypeGeneralName;// 开始的页面类型
	private String sessionStartUserActionGeneralName;// 开始的用户行为
	private String sessionPageTypeCodeSeq;// 页面类型序列
	private String sessionUserActionCodeSeq;// 用户行为序列

	private int recordsNum;

	private float sessionSearchActionRatio;// 检索行为比
	private float sessionBrowseActionRatio;// 浏览行为比
	private float sessionTradeActionRatio;// 交易行为比
	private float sessionListProActionRatio;// 列表处理行为比
	private float sessionIMActionRatio;// 资料管理行为比
	private float sessionLoginActionRatio;// 登录行为比

	private int sessionSearchPageTime;// 检索主页总时长
	private int sessionResultListTime;// 结果列表页总时长
	private int sessionObjectDetailTime;// 对象详情页总时长
	private int sessionHomepageTime;// 淘宝门户页总时长
	private int sessionActivityTime;// 会员活动页总时长
	private int sessionTradeTime;// 交易页面总时长
	private int sessionSelfInfoTime;// 个人资料页面总时长

	private float sessionSearchPageAvgDuration;// 检索主页平均停留时间
	private float sessionResultListAvgDuration;// 结果列表页平均停留时间
	private float sessionObjectDetailAvgDuration;// 对象详情页平均停留时间
	private float sessionHomepageAvgDuration;// 淘宝门户页平均停留时间
	private float sessionActivityAvgDuration;// 会员活动页平均停留时间
	private float sessionTradeAvgDuration;// 交易页面平均停留时间
	private float sessionSelfInfoAvgDuration;// 个人资料页面平均停留时间

	private String sessionStartTimePeriod;// 会话开始时间段
	private String sessionEndTimePeriod;// 会话结束时间段

	private String sessionTypeDurationSeq;// 包含时间的页面类型序列
	private String sessionTypeSeq;// 页面类型序列
	private String sessionActionSeq;// 行为类型序列

	private int sessionClassNumInvolved;// 涉及的类目数量
	private int sessionItemNumInvolved;// 涉及的类目数量
	private Vector<Integer> sessionClassIdList;
	private Vector<Long> sessionItemIdList;

	private static double NumPerFlicker = 10000;// 计时器闪烁一次大约处理的session个数

	// 计算会话的特性
	// 访问时间扩展

	public ClsSession() {
		sessionClassIdList = new Vector<Integer>();
		sessionItemIdList = new Vector<Long>();
	}

	// 根据会话时间扩展会话维度
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
				sessionStartTimePeriod = "白天";
				break;
			case 19:
			case 20:
			case 21:
			case 22:
				sessionStartTimePeriod = "晚上";
				break;
			case 23:
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				sessionStartTimePeriod = "深夜";
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
				sessionEndTimePeriod = "白天";
				break;
			case 19:
			case 20:
			case 21:
			case 22:
				sessionEndTimePeriod = "晚上";
				break;
			case 23:
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				sessionEndTimePeriod = "深夜";
				break;
			default:
				sessionEndTimePeriod = "";
			}
		}
	}

	// 根据会话页面类型与行为扩展会话维度
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

			sessionSearchPageTime = 0;// 检索主页总时长
			sessionResultListTime = 0;// 结果列表页总时长
			sessionObjectDetailTime = 0;// 对象详情页总时长
			sessionHomepageTime = 0;// 淘宝门户页总时长
			sessionActivityTime = 0;// 会员活动页总时长
			sessionTradeTime = 0;// 交易页面总时长
			sessionSelfInfoTime = 0;// 个人资料页面总时长

			sessionSearchPageAvgDuration = 0;// 检索主页平均停留时间
			sessionResultListAvgDuration = 0;// 结果列表页平均停留时间
			sessionObjectDetailAvgDuration = 0;// 对象详情页平均停留时间
			sessionHomepageAvgDuration = 0;// 淘宝门户页平均停留时间
			sessionActivityAvgDuration = 0;// 会员活动页平均停留时间
			sessionTradeAvgDuration = 0;// 交易页面平均停留时间
			sessionSelfInfoAvgDuration = 0;// 个人资料页面平均停留时间

			sessionTypeDurationSeq = "";// 包含时间的页面类型序列
			sessionTypeSeq = "";// 包含时间的行为类型序列
			sessionActionSeq = "";// 包含时间的行为类型序列

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
			sessionSearchActionRatio = (float) searchNum / recordsNum;// 检索行为比
			sessionBrowseActionRatio = (float) browseNum / recordsNum;// 浏览行为比
			sessionTradeActionRatio = (float) tradeNum / recordsNum;// 交易行为比
			sessionListProActionRatio = (float) listProNum / recordsNum;// 列表处理行为比
			sessionIMActionRatio = (float) imNum / recordsNum;// 个人资料管理行为比
			sessionLoginActionRatio = (float) loginNum / recordsNum;// 登录行为比

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

	// 计算会话的持续时间，返回以s为单位的字符串，在一次会话的所有记录都获得之后调用
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

	// 计算会话的持续时间，返回以min为单位的字符串，在一次会话的所有记录都获得之后调用
	private long getSessionDurationStringBySecond() {
		return (sessionEndTime.getTime() - sessionStartTime.getTime()) / 1000
				+ getSessionLastRecord().getVisitDuration();
	}

	// 获取会话的最后一条记录，不一定在一次会话的所有记录都获得之后调用
	public ClsRecord getSessionLastRecord() {// 获取最后一个记录
		if (!sessionRecords.isEmpty()) {
			return sessionRecords.lastElement();
		} else {
			return null;
		}
	}

	// 获取第一个记录，并对会话部分参数初始化
	public ClsRecord receiveSessionFirstRecord(ClsRecord cr, String uid,
			String ip) {// 获取第一个路径
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

	// 获取非第一个记录
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

	// 计算会话的持续时间，在一次会话的所有记录都获得之后调用
	public long getSessionDuration() {
		return (sessionEndTime.getTime() - sessionStartTime.getTime()) / 1000
				+ getSessionLastRecord().getVisitDuration();
	}

	// 获取会话的长度，在一次会话的所有记录都获得之后调用
	public int getSessionLength() {
		return this.sessionRecords.size();
	}

	// 从数据库中获取会话集合，如果用户Id为空，则获取所有用户sessions集合，并将此集合返回
	public static Vector<ClsSession> getUserSessionsDirectly(String tableName,
			String uid, int interval) {
		// 构造获取sessions的查询语句
		String sql = "";
		if (uid.equals("")) {
			sql = "select * from " + tableName + " order by uid ASC,date ASC";
		} else {
			sql = "select * from " + tableName + " where uid='" + uid
					+ "' order by date ASC";
		}
		// 创建并打开数据库连接
		ClsDb cd = new ClsDb(true);

		ClsSession currentSession = null;// 当前 的会话为空
		ClsRecord currentRecord = null;// 当前处理的记录为空

		Vector<ClsSession> sessions = new Vector<ClsSession>();

		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// 获取记录总数
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// 配置计时器
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// 去除不包含关键字段的记录
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;

				ct.estimating(rs.getRow());

				// 填充当前记录
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

				if (currentSession == null) {// 如果尚没有会话，这是第一个会话
					currentSession = new ClsSession();// 创建集合中的第一个会话
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
			if (currentSession != null) {// 处理最后一个session
				sessions.add(currentSession);
			}
			ct.estimateEnd();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("获取用户会话失败");
			return null;
		}
		return sessions;
	}

	// 将一个session（只包含记录）序列化
	public String serializeSessionRecordsInfoBody() {
		String line = sessionUserId + "\t" + sessionUserIp + "\t"
				+ sessionRecords.size() + "\t"
				+ getSessionDurationStringByMin() + "\n";
		return line;
	}

	// 将一个session（不包含记录）头描述序列化
	public static String serializeSessionRecordsInfoHead() {
		String line = "用户ID" + "\t" + "用户IP" + "\t" + "会话长度" + "\t" + "会话持续时间"
				+ "\n";
		return line;
	}

	// 序列化一个session，包含其中所有记录
	public String serializeSessionRecordsInfo() {
		String block = "用户ID：" + sessionUserId + " | 访问IP：" + sessionUserIp
				+ " | 会话持续时间：" + getSessionDurationStringByMin() + "min\n";
		for (int i = 0; i < sessionRecords.size(); i++) {
			block += sessionRecords.elementAt(i).serializeRecordInfoBody();
		}
		return block;
	}

	// 序列化一个session的页面信息，包含其中所有记录
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

	// 序列化一个session的页面类属信息，包含其中所有记录及其时间
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

	// 按照格式要求序列化,如果repeat为true，则打印所有记录；
	// 如果repeat为false，连续的重复记录只打印一个
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

	// 序列化页面统计信息
	public String serializeSessionStatistics() {
		String sequence = "";
		sequence = this.sessionUserId + "\t" + this.sessionUserIp + "\t"
				+ sessionRecords.size() + "\t" + getSessionDuration();
		return sequence;
	}

	// 按照格式要求序列化,如果repeat为true，则打印所有记录；
	// 如果repeat为false，连续的重复记录只打印一个
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

	// 按照格式要求序列化,如果repeat为true，则打印所有记录；
	// 如果repeat为false，连续的重复记录只打印一个
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

	// 打印会话详细信息
	public void printSessionRecordsInfo() {
		System.out.println(serializeSessionRecordsInfo());
	}

	// 打印输入的session集合的统计信息
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

	// 打印输入的session集合，包括其中的所有记录
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

	// 打印输入的session集合，只包含类属序列，包含页面停留时间
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

	// 临时从数据库中获取会话集合，并打印所有会话及详细信息
	public static int writeSessionsPageClassSeqWithDurationAndRecordInfo(
			String tableName, String uid, int interval, String contentFileName,
			String statFileName, String classSeqFileName) {
		// 构造获取sessions的查询语句
		String sql = "";
		if (uid.equals("")) {
			sql = "select * from " + tableName + " order by uid ASC,date ASC";
		} else {
			sql = "select * from " + tableName + " where uid='" + uid
					+ "' order by date ASC";
		}
		// 创建并打开数据库连接
		ClsDb cd = new ClsDb(true);
		ClsFile cfContent = new ClsFile(contentFileName);
		ClsFile cfStat = new ClsFile(statFileName);
		ClsFile cfClassSeq = new ClsFile(classSeqFileName);
		ClsSession currentSession = null;// 当前 的会话为空
		ClsRecord currentRecord = null;// 当前处理的记录为空

		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// 获取记录总数
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// 配置计时器
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// 去除不包含关键字段的记录
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;

				ct.estimating(rs.getRow());

				// 填充当前记录
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

				if (currentSession == null) {// 如果尚没有会话，这是第一个会话
					currentSession = new ClsSession();// 创建集合中的第一个会话
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
			if (currentSession != null) {// 处理最后一个session
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
			System.out.println("获取用户会话失败");
			return 0;
		}
		return 0;
	}

	// 从数据库中获取会话集合
	public static int writeSessionsPageDescriptionSeqWithoutDurationAndRecordInfo(
			String tableName, String uid, int interval, String contentFileName,
			String statFileName, boolean repeat) {
		// 构造获取sessions的查询语句
		String sql = "";
		if (uid.equals("")) {
			sql = "select * from " + tableName + " order by uid ASC,date ASC";
		} else {
			sql = "select * from " + tableName + " where uid='" + uid
					+ "' order by date ASC";
		}
		// 创建并打开数据库连接
		ClsDb cd = new ClsDb(true);
		ClsFile cfContent = new ClsFile(contentFileName);
		ClsFile cfStat = new ClsFile(statFileName);
		ClsSession currentSession = null;// 当前 的会话为空
		ClsRecord currentRecord = null;// 当前处理的记录为空
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// 获取记录总数
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// 配置计时器
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// 去除不包含关键字段的记录
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;

				ct.estimating(rs.getRow());

				// 填充当前记录
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

				if (currentSession == null) {// 如果尚没有会话，这是第一个会话
					currentSession = new ClsSession();// 创建集合中的第一个会话
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
			if (currentSession != null) {// 处理最后一个session
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
			System.out.println("获取用户会话失败");
			return 0;
		}
		return 0;
	}

	// 从数据库中获取品项类集合
	public static int writeSessionsItemCidSeqWithoutDurationAndRecordInfo(
			String tableName, String uid, int interval, String contentFileName,
			String cidSeqFileName, boolean isCid, boolean repeat) {
		// 构造获取sessions的查询语句
		String sql = "";
		if (uid.equals("")) {
			sql = "select * from " + tableName + " order by uid ASC,date ASC";
		} else {
			sql = "select * from " + tableName + " where uid='" + uid
					+ "' order by date ASC";
		}
		// 创建并打开数据库连接
		ClsDb cd = new ClsDb(true);
		ClsFile cfContent = new ClsFile(contentFileName);
		ClsFile cfStat = new ClsFile(cidSeqFileName);
		ClsSession currentSession = null;// 当前 的会话为空
		ClsRecord currentRecord = null;// 当前处理的记录为空
		int sessionNum = 1;// 会话计数

		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// 获取记录总数
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// 配置计时器
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();

			while (rs.next()) {
				// 去除不包含关键字段的记录
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;

				ct.estimating(rs.getRow());

				// 填充当前记录
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

				if (currentSession == null) {// 如果尚没有会话，这是第一个会话
					currentSession = new ClsSession();// 创建集合中的第一个会话
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
			if (currentSession != null) {// 处理最后一个session
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
			System.out.println("获取用户会话失败");
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

	// 从数据库中获取会话统计信息，并写入文件，连续相同页面重复计算
	public static String writeSessionsStatisticsToFileFromDbDirectly(
			String tableName, int interval, String statFileName) {
		int sessionNum = 0;// 会话数量
		long sessionSumDuration = 0;// 会话总持续时间
		int sessionSumLength = 0;// 会话总长度
		int sessionMaxLength = 0;// 最长会话长度
		// 构造获取sessions的查询语句
		String sql = "select * from " + tableName
				+ " order by uid ASC,date ASC";
		ClsDb cd = new ClsDb(true);// 创建并打开数据库连接
		ClsFile cfStat = new ClsFile(statFileName);
		ClsSession currentSession = null;// 当前 的会话为空
		ClsRecord currentRecord = null;// 当前处理的记录为空
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// 获取记录总数
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// 配置计时器
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// 去除不包含关键字段的记录
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;
				ct.estimating(rs.getRow());

				// 填充当前记录
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

				if (currentSession == null) {// 如果尚没有会话，这是第一个会话
					currentSession = new ClsSession();// 创建集合中的第一个会话
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
			if (currentSession != null) {// 处理最后一个session
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
			System.out.println("获取用户会话失败");
			return "";
		}
	}

	// 从数据库中获取会话统计信息，并写入文件，连续相同页面重复计算
	public static String serializeSessionsStatisticsByIntervalFromDbDirectly(
			String tableName, int interval, String statFileName) {
		int sessionNum = 0;// 会话数量
		long sessionSumDuration = 0;// 会话总持续时间
		int sessionSumLength = 0;// 会话总长度
		int sessionMaxLength = 0;// 最长会话长度
		// 构造获取sessions的查询语句
		String sql = "select * from " + tableName
				+ " order by uid ASC,date ASC";
		ClsDb cd = new ClsDb(true);// 创建并打开数据库连接
		ClsSession currentSession = null;// 当前 的会话为空
		ClsRecord currentRecord = null;// 当前处理的记录为空
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// 获取记录总数
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// 配置计时器
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// 去除不包含关键字段的记录
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;
				ct.estimating(rs.getRow());

				// 填充当前记录
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

				if (currentSession == null) {// 如果尚没有会话，这是第一个会话
					currentSession = new ClsSession();// 创建集合中的第一个会话
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
			if (currentSession != null) {// 处理最后一个session
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
			System.out.println("获取用户会话失败");
			return "";
		}
	}

	// 从数据库中获取会话统计信息，并写入文件，连续相同页面重复计算
	public static boolean writeSessionsProcessedInfoToFile(String tableName,
			String statFileName) {
		// 构造获取sessions的查询语句
		int currentSid = -1;
		int previousSid = -1;
		String sql = "select * from " + tableName
				+ " order by sid_by45min,uid,date limit 1000,2000";
		ClsDb cd = new ClsDb(true);// 创建并打开数据库连接
		ClsFile cfStat = new ClsFile(statFileName);
		ClsSession currentSession = null;// 当前 的会话为空
		ClsRecord currentRecord = null;// 当前处理的记录为空
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			// 获取记录总数
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / 100);// 配置计时器
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				// 去除不包含关键字段的记录
				if (rs.getString("uid") == null)
					continue;
				if (rs.getString("ip") == null)
					continue;
				if (rs.getTimestamp("date") == null)
					continue;
				ct.estimating(rs.getRow());
				// 填充当前记录
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
				if (currentSession == null) {// 如果尚没有会话，这是第一个会话
					currentSession = new ClsSession();// 创建集合中的第一个会话
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
			if (currentSession != null) {// 处理最后一个session
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
			System.out.println("获取用户会话失败");
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
		String squence = "会话ID\t用户ID\t用户IP\t会话记录数\t会话持续时间/秒\t涉及商品数\t涉及类目数\t起始访问时间段\t结束访问时间段\t会话起始时间\t会话结束时间\t起始页面类型\t起始用户行为\t检索行为比率\t浏览行为比率\t交易行为比率\t列表处理行为比率\t资料管理行为比率\t用户登录行为比率\t检索主页总时长/秒\t结果列表页总时长/秒\t对象详情页总时长/秒\t淘宝门户页总时长/秒\t会员活动页总时长/秒\t交易页面总时长/秒\t个人资料页面总时长/秒\t检索主页平均停留时间/秒\t结果列表页平均停留时间/秒\t对象详情页平均停留时间/秒\t淘宝门户页平均停留时间/秒\t会员活动页平均停留时间/秒\t交易页面平均停留时间/秒\t个人资料页面平均停留时间/秒\t页面类型序列\t用户行为序列\t页面类型序列\t行为类型序列\t页面类型序列_停留时间\n";
		return squence;
	}
	public static void writeRecords(String fileName) {
		// 构造获取sessions的查询语句
		String sql = "SELECT uid,ip,url,referurl,date,staytime,url_kw,refer_kw,newsite,url_md5,page,query,sid_by45min,Type20140407,Action20140407,Type20140407_high,Action20140407_high FROM rec_taobao_valid";
		ClsDb cd = new ClsDb(true);// 创建并打开数据库连接
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
			// 获取记录总数
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime((double) rowNum / NumPerFlicker);// 配置计时器
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
