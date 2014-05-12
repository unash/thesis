package process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

public class ClsUser {

	private String userId;// �û�id
	private boolean userGender;// �û��Ա�
	private String userAge;//�û��������
	private String userEdu;// ��������
	private String userInd;// ������ҵ
	private String userOcc;// ְҵ
	private String userCity;// ����
	private float userIncome;// ����
	private String userBirthday = null;// �û�����
	
	private Vector<ClsSession> userSessions;// ������ �ĻỰ
	
	//�����������û�session���ϵ�����
	private int userSessionNum;//�û�ӵ�е�session����
	private int userSessionAvgLength;//ƽ��session����
	private float userSessionSearchRatio;//������Ϊ����
	private float userSessionFilterRatio;//ɸѡ��Ϊ����
	private float userSessionTurnpageRatio;//��ҳ��Ϊ����
	//���ݿ��ֶ�ӳ���
	private static Map<Integer, String> dimAge = null;
	private static Map<Integer, String> dimEdu = null;
	private static Map<Integer, String> dimInd = null;
	private static Map<Integer, String> dimOcc = null;
	private static Map<Integer, String> dimCity = null;

	public ClsUser() {//������һ��ʵ����ʱ�򣬳�ʼ����̬��Ա
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

	//���ĳһ���û���ȫ����Ϣ
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

	//����ʵ���е��û���ϸ��Ϣд���ļ�
	public boolean writeUserInfo(String fileName) {
		ClsFile cf = new ClsFile(fileName);
		cf.writeFile(serializeUserInfoHead() + "\n");
		cf.closeFile();
		ClsSession.writeSessionsRecordInfoFromSessions(fileName, userSessions);
		return true;
	}
	
	//���û��Ự�����е�ҳ����������д���ļ�
	public boolean writeUserSessionsPageClassSeqWithDuration(String fileName) {
		ClsFile cf = new ClsFile(fileName);
		cf.writeFile(serializeUserInfoHead() + "\n");
		cf.closeFile();
		ClsSession.writeSessionsPageClassSeqWithDurationFromSessions(fileName, userSessions);
		return true;
	}
	
	//���л��û���Ϣ�����ͷ
	private String serializeUserInfoHead() {
		String userInfo = "�û�ID��" + userId;
		if (userGender)
			userInfo += " | �Ա���";
		else
			userInfo += " | �Ա�Ů";
		if (userAge != null)
			userInfo += " | ���䣺" + userAge;
		if (userBirthday != null)
			userInfo += " | ���գ�" + userBirthday;
		if (userEdu != null)
			userInfo += " | �����̶ȣ�" + userEdu;
		if (userIncome != 0)
			userInfo += " | ���룺" + userIncome + "��/��";
		if (userInd != null)
			userInfo += " |��ҵ��" + userInd;
		if (userOcc != null)
			userInfo += " | ְҵ��" + userOcc;
		if (userCity != null)
			userInfo += " | ���ڳ��У�" + userCity;
		return userInfo;
	}
	//���û��ĻỰ�У���ȡ�����ڽ��������ռ�ģ�͵�
	private void getSVMParamFromSessions()
	{
		
	}
}
