package process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

public class ClsUser {

	private String userId;// 用户id
	private boolean userGender;// 用户性别
	private String userAge;//用户年龄你个
	private String userEdu;// 教育背景
	private String userInd;// 所处行业
	private String userOcc;// 职业
	private String userCity;// 地区
	private float userIncome;// 收入
	private String userBirthday = null;// 用户生日
	
	private Vector<ClsSession> userSessions;// 所产生 的会话
	
	//下面是描述用户session集合的数据
	private int userSessionNum;//用户拥有的session个数
	private int userSessionAvgLength;//平均session长度
	private float userSessionSearchRatio;//搜索行为比率
	private float userSessionFilterRatio;//筛选行为比率
	private float userSessionTurnpageRatio;//翻页行为比率
	//数据库字段映射表
	private static Map<Integer, String> dimAge = null;
	private static Map<Integer, String> dimEdu = null;
	private static Map<Integer, String> dimInd = null;
	private static Map<Integer, String> dimOcc = null;
	private static Map<Integer, String> dimCity = null;

	public ClsUser() {//创建第一个实例的时候，初始化静态成员
		ClsDb cd = new ClsDb(true);
		if (dimAge == null) {
			dimAge = cd.dimMapper("dim_age", "age", "age_s");
		}
		if (dimEdu == null) {
			dimEdu = cd.dimMapper("dim_edu", "edu", "edu_s");
		}
		if (dimInd == null) {
			dimInd = cd.dimMapper("dim_industry", "industry", "industry_s");
		}
		if (dimOcc == null) {
			dimOcc = cd.dimMapper("dim_occupation", "occupation",
					"occupation_s");
		}
		if (dimCity == null) {
			dimCity = cd.dimMapper("dim_city", "city", "city_s");
		}
		cd.closeDb();
	}

	//获得某一个用户的全部信息
	public boolean getUserInfo(String tableName, String uid, int interval) {
		userId = uid;
		ClsDb cd = new ClsDb();
		cd.openDb();
		String sql = "select * from info_users where uid='" + uid + "'";
		ResultSet rs = cd.executeSelectQuery(sql);
		try {
			if (rs.next()) {
				if (rs.getInt("gender") == 1)
					userGender = true;
				else if (rs.getInt("gender") == 2)
					userGender = false;
				if (rs.getInt("age") != 0) {
					userAge = dimAge.get(rs.getInt("age"));
				}
				if (rs.getInt("city") != 0) {
					userCity = dimCity.get(rs.getInt("city"));
				}
				if (rs.getInt("income_pre") != 0) {
					userIncome = rs.getInt("income_pre");
				}
				if (rs.getInt("edu") != 0) {
					userEdu = dimEdu.get(rs.getInt("edu"));
				}
				if (rs.getInt("occupation") != 0) {
					userOcc = dimOcc.get(rs.getInt("occupation"));
				}
				if (rs.getInt("industry") != 0) {
					userInd = dimInd.get(rs.getInt("industry"));
				}
				if (rs.getInt("birthday") != 0) {
					userBirthday = rs.getString("birthday");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		userSessions = ClsSession.getUserSessionsDirectly(tableName, uid, interval);
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		cd.closeDb();
		// System.out.println(dimEdu.toString());
		// System.out.println(dimAge.toString());
		// System.out.println(dimOcc.toString());
		// System.out.println(dimInd.toString());
		// System.out.println(dimReg.toString());
		return true;
	}

	//将本实例中的用户详细信息写入文件
	public boolean writeUserInfo(String fileName) {
		ClsFile cf = new ClsFile(fileName);
		cf.writeFile(serializeUserInfoHead() + "\n");
		cf.closeFile();
		ClsSession.writeSessionsRecordInfoFromSessions(fileName, userSessions);
		return true;
	}
	
	//将用户会话中所有的页面类属序列写入文件
	public boolean writeUserSessionsPageClassSeqWithDuration(String fileName) {
		ClsFile cf = new ClsFile(fileName);
		cf.writeFile(serializeUserInfoHead() + "\n");
		cf.closeFile();
		ClsSession.writeSessionsPageClassSeqWithDurationFromSessions(fileName, userSessions);
		return true;
	}
	
	//序列化用户信息输出的头
	private String serializeUserInfoHead() {
		String userInfo = "用户ID：" + userId;
		if (userGender)
			userInfo += " | 性别：男";
		else
			userInfo += " | 性别：女";
		if (userAge != null)
			userInfo += " | 年龄：" + userAge;
		if (userBirthday != null)
			userInfo += " | 生日：" + userBirthday;
		if (userEdu != null)
			userInfo += " | 教育程度：" + userEdu;
		if (userIncome != 0)
			userInfo += " | 收入：" + userIncome + "万/年";
		if (userInd != null)
			userInfo += " |产业：" + userInd;
		if (userOcc != null)
			userInfo += " | 职业：" + userOcc;
		if (userCity != null)
			userInfo += " | 所在城市：" + userCity;
		return userInfo;
	}
	//从用户的会话中，获取可用于建立向量空间模型的
	private void getSVMParamFromSessions()
	{
		
	}
}
