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

	// 来自json文件中的信息，一次初始化，多处应用
	private static double NumPerFlicker = 1000;// 计时器闪烁一次大约处理的url个数
	private static String jsonLogName="output/jsonLog.txt";//针对json文件的错误日志
	
	private static String rulesJsonFileName = "data/PageInfos.json";// cprForRecord所调用的json文件位置
	private static String rulesJsonFileCharacter = "UTF-8";// 编码类型
	
	// 来自统计的page信息
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

	// 从文件中获取page.json
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

	// 获取页面信息，从json对象中
	public String getPageDescriptionFromJson(String page) {
		// 判断页面是否存在
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
	// 获取页面类型，从json对象中
	public String getPageClassFromJson(String page) {
		// 判断页面是否存在
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

	// 判断某一个页面中的某个字段是否是无用的
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
				System.out.println("函数ParamIsUseless中,json对象中存在无效对象");
				return false;
			}			
	}

	// 获取所有页面参数的 统计
	public boolean getPageStats(String tableName, String urlFieldName) {
		ClsDb cd = new ClsDb(true);
		String sql = "select " + urlFieldName + " from " + tableName
				+ " order by url ASC";
		try {
			ResultSet rs = cd.executeSelectQuery(sql);
			rs.last();
			int rowNum = rs.getRow();
			ClsTime ct = new ClsTime(rowNum / NumPerFlicker);// 50个为一精度点
			ct.estimateStart(rowNum);
			rs.first();
			rs.previous();
			while (rs.next()) {
				ct.estimating(rs.getRow());
				// 填充当前记录
				if (rs.getString(urlFieldName) != null)
					getPageStatsSub(rs.getString(urlFieldName));
			}
			ct.estimateEnd();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("获取表的URL失败");
			return false;
		}
		return true;
	}

	//获取一个页面参数的统计
	private boolean getPageStatsSub(String url) {
		ClsRecord cr = new ClsRecord();
		ClsPage cp = null;
		if (url != null && url != "") {
			cr.setVisitUrl(url);
			cr.expandRecordInfoWithUrl(false, false);
			String page = cr.getVisitPage();
			if (!pageStats.containsKey(page)) {// 不包含这个页面
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

	// 输出页面参数统计信息到文件
	public boolean writePageStats(String fileName) {
		if (pageStats == null)
			return false;
		if (fileName.equals("")) {
			fileName = ClsBase.getFileNameTime() + ".txt";
		}
		Iterator<Entry<String, ClsPage>> it = pageStats.entrySet().iterator();
		String line = "";
		ClsFile cf = new ClsFile(fileName);
		cf.writeFile("页面\t页面频次\t页面参数个数\t参数出现频次\n");
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
	//输出与页面对应的页面描述
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
