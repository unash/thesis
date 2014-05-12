package process;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ClsPageRules {

	// ����json�ļ��е���Ϣ��һ�γ�ʼ�����ദӦ��
	private static double NumPerFlicker = 1000;// ��ʱ����˸һ�δ�Լ�����url����
	private static String jsonLogName="output/jsonLog.txt";//���json�ļ��Ĵ�����־
	
	private static String rulesJsonFileName = "data/PageInfos.json";// cprForRecord�����õ�json�ļ�λ��
	private static String rulesJsonFileCharacter = "UTF-8";// ��������
	
	// ����ͳ�Ƶ�page��Ϣ
	private JSONObject pageRules = null;
	private Map<String, ClsPage> pageStats = null;

	public static void main(String [] args)
	{
		ClsPageRules cpr=new ClsPageRules();
		System.out.println(cpr.getPageClassFromJson("item.taobao.com/item.htm"));
		System.out.println(cpr.getPageDescriptionFromJson("item.taobao.com/item.htm"));
		//cpr.getPageStats("rec_taobao_sample_day5_heigh1", "url");
		//cpr.writePageStats("output/test.txt");
	}
	
	public ClsPageRules() {
		pageRules=new JSONObject();
		initRulesJsonFromFile(rulesJsonFileName,rulesJsonFileCharacter);
		pageStats = new HashMap<String, ClsPage>();
	}

	// ���ļ��л�ȡpage.json
	public boolean initRulesJsonFromFile(String jsonFileName, String jsonFileCharacter) {
		InputStreamReader br;
		try {
			br = new InputStreamReader(new FileInputStream(jsonFileName),
					jsonFileCharacter);
			JSONTokener jsonTokerner = new JSONTokener(br);
			pageRules = new JSONObject(jsonTokerner);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}

	// ��ȡҳ����Ϣ����json������
	public String getPageDescriptionFromJson(String page) {
		// �ж�ҳ���Ƿ����
		if (pageRules.has(page)) {
			try {
				return pageRules.getJSONObject(page).getString("description");
			} catch (JSONException e) {
				// e.printStackTrace();
				// System.out.println("JSONObject['description'] not found");
				ClsFile.writeLog(jsonLogName, "JSONObject['description'] not found:"+ page);
				return "";
			}
		}
		else{
			ClsFile.writeLog(jsonLogName, "Page not found:\t"+ page);
		}
		return "";
	}
	// ��ȡҳ�����ͣ���json������
	public String getPageClassFromJson(String page) {
		// �ж�ҳ���Ƿ����
		if (pageRules.has(page)) {
			try {
				return pageRules.getJSONObject(page).getString("class");
			} catch (JSONException e) {
				e.printStackTrace();
				// System.out.println("JSONObject['description'] not found");
				ClsFile.writeLog(jsonLogName, "JSONObject['class'] not found:"+ page);
				return "";
			}
		}
		else{
			ClsFile.writeLog(jsonLogName, "Page not found:\t"+ page);
		}
		return "";
	}

	// �ж�ĳһ��ҳ���е�ĳ���ֶ��Ƿ������õ�
	public boolean paramIsUseless(String page, String param) {
			try {
				if(pageRules.has(page) && pageRules.getJSONObject(page).has("uselessParams") &&
						pageRules.getJSONObject(page).getJSONObject("uselessParams").has(param) &&
						pageRules.getJSONObject(page).getJSONObject("uselessParams").getString(param)!="")
					return true;
				else
					return false;
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println("����ParamIsUseless��,json�����д�����Ч����");
				return false;
			}			
	}

	// ��ȡ����ҳ������� ͳ��
	public boolean getPageStats(String tableName, String urlFieldName) {
		ClsDb cd = new ClsDb(true);
		String sql = "select " + urlFieldName + " from " + tableName
				+ " order by url ASC";
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime(rowNum / NumPerFlicker);// 50��Ϊһ���ȵ�
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				ct.estimating(rs.getRow());
				// ��䵱ǰ��¼
				if (rs.getString(urlFieldName) != null)
					getPageStatsSub(rs.getString(urlFieldName));
			}
			ct.estimateEnd();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("��ȡ���URLʧ��");
			return false;
		}
		return true;
	}

	//��ȡһ��ҳ�������ͳ��
	private boolean getPageStatsSub(String url) {
		ClsRecord cr = new ClsRecord();
		ClsPage cp = null;
		if (url != null && url != "") {
			cr.setVisitUrl(url);
			cr.expandRecordInfoWithUrl(false, false);
			String page = cr.getVisitPage();
			if (!pageStats.containsKey(page)) {// ���������ҳ��
				cp = new ClsPage();
			} else {
				cp = pageStats.get(page);
				pageStats.remove(page);
				cp.pageFreq++;
			}
	
			if (cr.getVisitQuery() != null && cr.getVisitQuery() != "") {
				Vector<String> params = cr.getParams();
				for (int i = 0; i < params.size(); i++) {
					cp.getParam(params.get(i));
				}
			}
			pageStats.put(page, cp);
		}
		return true;
	}

	// ���ҳ�����ͳ����Ϣ���ļ�
	public boolean writePageStats(String fileName) {
		if (pageStats == null)
			return false;
		if (fileName.equals("")) {
			fileName = ClsBase.getFileNameTime() + ".txt";
		}
		Iterator<Entry<String, ClsPage>> it = pageStats.entrySet().iterator();
		String line = "";
		ClsFile cf = new ClsFile(fileName);
		cf.writeFile("ҳ��\tҳ��Ƶ��\tҳ���������\t��������Ƶ��\n");
		while (it.hasNext()) {
			Map.Entry<String, ClsPage> entry = (Map.Entry<String, ClsPage>) it.next();
			line += entry.getKey().toString() + "\t";
			line += ((ClsPage) entry.getValue()).serializeParamsInfo();
			line += "\n";
			cf.writeFile(line);
			line = "";
		}
		cf.closeFile();
		return true;
	}
	//�����ҳ���Ӧ��ҳ������
	public void writePagesDescription(String fileName){
		if(pageRules!=null){
			Iterator<?> it = pageRules.keys();
			ClsFile cf=new ClsFile(fileName);
			String description="";
            while (it.hasNext()) {  
            	String page = (String) it.next();  
				try {
					description = pageRules.getJSONObject(page).getString("description");
				} catch (JSONException e) {
					e.printStackTrace();
				}
                cf.writeFile(description+"\t"+page+"\n");
                }
            cf.closeFile();
            }
		}
	}
