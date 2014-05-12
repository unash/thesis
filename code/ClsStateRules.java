package process;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
/*
 * 实际数据中存在一些类似
 *http://item.taobao.com/item.htm?http://item.taobao.com/item.htm?spm=a1z10.3.w18096144894.19.Q1ersk&id=19853079486&
 *这样的url，因而导致最终没有处理这样的url，所以需要后期手工处理这些字段。
 * */
public class ClsStateRules {

	// 与json文件相关的变量
	private static String stateJsonFileName = "data/stateRules.json";
	private static String typeCodeJsonFileName = "data/CodeType.json";// 页面类型编码文件
	private static String actionCodeJsonFileName = "data/CodeAction.json";// 用户行为编码文件
	private static String jsonFileType = "UTF-8";
	//初始化的对象
	private JSONObject stateRules = null;// 保存规则 的json对象
	private JSONObject stateTypeCode= null;// 页面类型编码对象
	private JSONObject stateActionCode= null;// 用户行为编码对象
	// 与url相关的信息
	private String currentUrl;
	private String previousUrl;
	private String currentPage;// 当前页面
	private String previousPage;// 前导页面
	private String currentParameter;// 当前页面参数串
	private String previousParameter;// 前导页面参数串
	private HashMap<String, String> currentHmParameters;
	private HashMap<String, String> previousHmParameters;
	private HashMap<String, String> diffHmParameters;// 不同的参数
	private String currentUrl_md5;
	private boolean diffPreAndCur;
	Document jsoupCurrentUrlDoc;
	// 与输出相关的成员
	private String DEFAULT_PAGE_TYPE_NAME;
	private String DEFAULT_USER_ACTION_NAME;
	private String pageTypeName;
	private String userActionName;

	// 正则表达式
	private static String s_page_query = "[?]";// 分离url中page与query的字符
	private static Pattern m_query_fields = Pattern
			.compile("([^&][^=]*)=([^&]+)");// 分离query中字段
	private static Pattern m_page = Pattern.compile("http[s]?://(.+)$");// 分离page中的协议与路径

	public static void main(String[] args) {
		ClsStateRules csr = new ClsStateRules();
		String previousUrl = "http://search.taobao.com/search?id";
		String currentUrl = "http://search.taobao.com/search?";
		csr.initStateVariable(currentUrl, previousUrl,"");
		
		csr.printDiffParameters();
		System.out.println(csr.getUserActionName());
		System.out.println(csr.getPageTypeName());
	}

	// 初始化对象,对象要么为null要么有值；字符串要么为“”要么有值
	public ClsStateRules() {//初始化静态信息
		currentUrl = "";
		previousUrl = "";
		currentPage = "";
		previousPage = "";
		currentParameter = "";
		previousParameter = "";
		diffPreAndCur=false;
		currentHmParameters = new HashMap<String, String>();
		previousHmParameters = new HashMap<String, String>();
		diffHmParameters = new HashMap<String, String>();
		
		initStateJsonFromFile(stateJsonFileName, jsonFileType);
		initTypeCodeJsonFromFile(typeCodeJsonFileName,jsonFileType);
		initActionCodeJsonFromFile(actionCodeJsonFileName,jsonFileType);
		try {
			DEFAULT_PAGE_TYPE_NAME=stateRules.getJSONObject("beyondListPages").getString("defaultTypeName");
			DEFAULT_USER_ACTION_NAME=stateRules.getJSONObject("beyondListPages").getString("defaultActionName");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getGeneralTypeCode(String generalTypeName)
	{
		if (stateTypeCode != null && stateTypeCode.has(generalTypeName)) {
			try {
				return stateTypeCode.getString(generalTypeName);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		} else {
			return "";
		}
	}
	public String getGeneralActionCode(String generalActionName)
	{
		if (stateActionCode != null && stateActionCode.has(generalActionName)) {
			try {
				return stateActionCode.getString(generalActionName);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		} else {
			return "";
		}
	}
	
	// 从文件中获取page.json
	public boolean initStateJsonFromFile(String jsonFileName, String jsonFileCharacter) {
		stateJsonFileName = jsonFileName;
		InputStreamReader br;
		try {
			br = new InputStreamReader(new FileInputStream(jsonFileName),
					jsonFileCharacter);
			JSONTokener jsonTokerner = new JSONTokener(br);
			stateRules = new JSONObject(jsonTokerner);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	// 从文件中获取page.json
	public boolean initTypeCodeJsonFromFile(String jsonFileName, String jsonFileCharacter) {
		InputStreamReader br;
		try {
			br = new InputStreamReader(new FileInputStream(jsonFileName),
					jsonFileCharacter);
			JSONTokener jsonTokerner = new JSONTokener(br);
			stateTypeCode = new JSONObject(jsonTokerner);
			System.out.println("编码文件 "+jsonFileName+" 读取成功!");
			return true;
		} catch (Exception e) {
			System.out.println("编码文件 "+jsonFileName+" 读取失败!");
			e.printStackTrace();
			return false;
		}
	}
	// 从文件中获取action.json
	public boolean initActionCodeJsonFromFile(String jsonFileName, String jsonFileCharacter) {
		InputStreamReader br;
		try {
			br = new InputStreamReader(new FileInputStream(jsonFileName),
					jsonFileCharacter);
			JSONTokener jsonTokerner = new JSONTokener(br);
			stateActionCode = new JSONObject(jsonTokerner);
			System.out.println("编码文件 "+jsonFileName+" 读取成功!");
			return true;
		} catch (Exception e) {
			System.out.println("编码文件 "+jsonFileName+" 读取失败!");
			e.printStackTrace();
			return false;
		}
	}

	// 初始化确定状态所需要的信息，这一步结束之后，url中就不存在null了
	public void initStateVariable(String url, String referUrl,String url_md5) {
		if (url == null)
			currentUrl = "";
		else
			currentUrl = url;
		if (referUrl == null)
			previousUrl = "";
		else
			previousUrl = referUrl;
		if (url_md5 == null)
			currentUrl_md5 = "";
		else
			currentUrl_md5 = url_md5;
		preProCurrentUrl();
		preProPreviousUrl();
		proDiffHmParameters();
		if(previousPage.equals(currentPage))
		{
			diffPreAndCur=true;
		}
		try {
			applyStateRules();
		} catch (JSONException e) {
			System.out.println("在应用json规则的时候出错啦!");
			e.printStackTrace();
		}
	}

	private void preProCurrentUrl() {// 此处需要对url作一定的清洗
		currentHmParameters = null;
		// 将url分隔成页面与页面参数
		String tmpUrl[] = currentUrl.split(s_page_query);
		currentPage = "";
		currentParameter = "";
		currentHmParameters = new HashMap<String, String>();// 查询串为空
		if (tmpUrl.length >= 1) {
			currentPage = tmpUrl[0];
			Matcher m = m_page.matcher(currentPage);
			if (m.find()) {
				currentPage = m.group(1);
			} else {
				currentPage = "";
			}
		}
		if (tmpUrl.length >= 2) {
			currentParameter = tmpUrl[1];
			// 将参数放到hashmap中
			if (!currentParameter.equals("")) {
				currentHmParameters = new HashMap<String, String>();
				Matcher matcher = m_query_fields.matcher(currentParameter);
				while (matcher.find()) {
					currentHmParameters.put(matcher.group(1),
							ClsBase.decodeUrl(matcher.group(2), "gb2312"));
				}
			}
		}
	}

	private void preProPreviousUrl() {// 此处需要对url作一定的清洗
		previousHmParameters = null;
		// 将url分隔成页面与页面参数
		String tmpUrl[] = previousUrl.split(s_page_query);
		previousPage = "";
		previousParameter = "";
		previousHmParameters = new HashMap<String, String>();// 查询串为空
		if (tmpUrl.length >= 1) {
			previousPage = tmpUrl[0];
			Matcher m = m_page.matcher(previousPage);
			if (m.find()) {
				previousPage = m.group(1);
			} else {
				previousPage = "";
			}
		}
		if (tmpUrl.length >= 2) {
			previousParameter = tmpUrl[1];
			// 将参数放到hashmap中
			if (!previousParameter.equals("")) {
				previousHmParameters = new HashMap<String, String>();
				Matcher matcher = m_query_fields.matcher(previousParameter);
				while (matcher.find()) {
					previousHmParameters.put(matcher.group(1),
							ClsBase.decodeUrl(matcher.group(2), "gb2312"));
				}
			}
		}

	}

	private void proDiffHmParameters() {
		diffHmParameters = new HashMap<String, String>();
		Iterator<String> iterator = currentHmParameters.keySet().iterator();
		while (iterator.hasNext()) {
			String param = (String) iterator.next();
			String value = currentHmParameters.get(param);
			if (previousHmParameters.containsKey(param)) {
				if (!previousHmParameters.get(param).equals(value)) {
					diffHmParameters.put(param, "mod");
				}
			} else {
				diffHmParameters.put(param, "add");
			}
		}
		iterator = previousHmParameters.keySet().iterator();
		while (iterator.hasNext()) {
			String param = (String) iterator.next();
			if (!previousHmParameters.containsKey(param)) {
				diffHmParameters.put(param, "del");
			}
		}
	}

	public void printDiffParameters() {
		Iterator<String> iterator = diffHmParameters.keySet().iterator();
		while (iterator.hasNext()) {
			String param = (String) iterator.next();
			System.out.println(param + ":"
					+ (String) diffHmParameters.get(param) + ";");
		}
	}

	// 从数据库中获取jsoup文档
		public boolean getJsoupDocFromDb(String tableName, String fieldName, String url_md5) {
			if (url_md5 != null && url_md5 != "") {
				String html = ClsBase.gzipToString(
						ClsDb.globalCd.getUrlBlobFromDb(tableName, fieldName, url_md5),
						"utf-8");
				if (html != "") {
					jsoupCurrentUrlDoc = Jsoup.parse(html);
					return true;
				} else {
					return false;
				}
			} else {
				jsoupCurrentUrlDoc = null;
				return false;
			}
		}
		// 从web上获取jsoup文档
		public boolean getJsoupDocFromWeb(String url) {
			try {
				jsoupCurrentUrlDoc = Jsoup.connect(url).timeout(8000).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				jsoupCurrentUrlDoc = null;
				//ClsFile.writeLog(webLogName, "页面获取失败:" + url);
				return false;
			}
			return true;
		}
	private void applyStateRules() throws JSONException {
		pageTypeName = DEFAULT_PAGE_TYPE_NAME;
		userActionName = DEFAULT_USER_ACTION_NAME;
		if (!stateRules.isNull(currentPage))// 如果能找到这个页面
		{
			JSONObject jsonPage = stateRules.getJSONObject(currentPage);
			if(!jsonPage.isNull("similarPage") && !stateRules.isNull(jsonPage.getString("similarPage")) )
			{
				jsonPage=stateRules.getJSONObject(jsonPage.getString("similarPage"));
			}
			if (!jsonPage.isNull("pageTypeRules")) {
				JSONObject jsonTypeRules = jsonPage
						.getJSONObject("pageTypeRules");
				if (!jsonTypeRules.isNull("defaultTypeName")) {
					pageTypeName = jsonTypeRules.getString("defaultTypeName");
				}

				if (currentHmParameters.size() == 0)// 如果参数个数为0
				{
					if (!jsonTypeRules.isNull("hasNoParams")) {
						pageTypeName = jsonTypeRules.getString("hasNoParams");
					}
				} else if (!jsonTypeRules.isNull("withParams")) {
					JSONArray jsonTypeRulesWithParams = jsonTypeRules
							.getJSONArray("withParams");
					for (int i = 0; i < jsonTypeRulesWithParams.length(); i++) {
						JSONObject jsonTypeRule = (JSONObject) jsonTypeRulesWithParams
								.get(i);
						if (canApplyTypeRule(jsonTypeRule
								.getJSONObject("typeCondition"))) {
							pageTypeName = (String) jsonTypeRule
									.getString("typeName");
							break;
						}
					}
				}
			}
			if (!jsonPage.isNull("userActionRules")) {
				JSONObject jsonActionRules = jsonPage
						.getJSONObject("userActionRules");
				if (!jsonActionRules.isNull("defaultActionName")) {
					userActionName = jsonActionRules
							.getString("defaultActionName");
				}
				// 判断previous页面是否为空
				if (previousPage.equals("")) {
					if (!jsonActionRules.isNull("invalidPreUrl")) {
						userActionName = jsonActionRules
								.getString("invalidPreUrl");
					}
				}
				// 两个页面不相同
				else if (!diffPreAndCur) {
					if (!jsonActionRules.isNull("diffWithPrePage")) {
						if (!jsonActionRules.getJSONObject("diffWithPrePage")
								.isNull(previousPage)) {
							userActionName = jsonActionRules.getJSONObject(
									"diffWithPrePage").getString(previousPage);
						} else if (!jsonActionRules.getJSONObject(
								"diffWithPrePage").isNull("defaultPage")) {
							userActionName = jsonActionRules.getJSONObject(
									"diffWithPrePage").getString("defaultPage");
						}
					}
				}
				// 两个页面相同
				else {
					if(!jsonActionRules.isNull("sameWithPrePage"))
					{
						for (int i = 0; i < jsonActionRules
								.getJSONArray("sameWithPrePage").length(); i++) {
							JSONObject jsonActionRule = (JSONObject) jsonActionRules
									.getJSONArray("sameWithPrePage").get(i);
							if (canApplyActionRule(jsonActionRule)) {
								userActionName = (String) jsonActionRule
										.getString("actionName");
								break;
							}
						}
					}
				}
			}
		} else {
			// TODO Auto-generated catch block
			// 找不到这个页面
			if (!getJsoupDocFromDb("info_urls","url_md5",currentUrl_md5))// 获取网页jsoup文档, 如果没有获取到jsoup文档
			{
				//System.out.println("根据url的md5获取文档失败！");
				if(!getJsoupDocFromWeb(currentUrl))
				{
					//System.out.println("根据url获取文档失败！");
				}
			}
			JSONArray jsonUrlRules;
			try {
				jsonUrlRules = stateRules.getJSONObject("beyondListPages")
						.getJSONArray("urlRules");
				for (int i = 0; i < jsonUrlRules.length(); i++) {
					JSONObject jsonUrlRule = (JSONObject) jsonUrlRules.get(i);
					if (canApplyBeyondListPageRule(jsonUrlRule
							.getJSONObject("urlCondition"))) {
						pageTypeName = (String) jsonUrlRule
								.getString("typeName");
						userActionName=(String) jsonUrlRule
								.getString("defaultActionName");
						break;
					}
				}
				// e.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public boolean canApplyTypeRule(JSONObject jsonTypeRule) {
		Iterator<?> iterator = jsonTypeRule.keys();
		boolean can = true;
		while (iterator.hasNext()) {
			String param = (String) iterator.next();
			String valuePattern = "";
			String value = "";
			if (currentHmParameters.containsKey(param)) {
				value = currentHmParameters.get(param);
				try {
					valuePattern = jsonTypeRule.getString(param);
					if (!Pattern.matches(valuePattern, value)) {
						can = false;
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					can = false;
					e.printStackTrace();
					break;
				}
			} else {
				can = false;
				break;
			}
		}
		return can;
	}

	private boolean canApplyActionRule(JSONObject jsonActionRule) {
		boolean can = true;
			String paramChangeString="";
			try {
				paramChangeString = jsonActionRule.getString("paramChange");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println("在json中寻找参数paramChange时出错!");
				e.printStackTrace();
			}
			String[] onlyAndParamChange=paramChangeString.split("[|]");
			for(int i=0;i<onlyAndParamChange.length;i++ )
			{
				String onlyElementParamChange[]=onlyAndParamChange[i].split("[&]");
				can=true;
				
				for(int j=0;j<onlyElementParamChange.length;j++)
				{
					String paramKey=onlyElementParamChange[j].split("=")[0];
					String paramValue=onlyElementParamChange[j].split("=")[1];
					if(!diffHmParameters.containsKey(paramKey) || !diffHmParameters.get(paramKey).equals(paramValue))
					{
						can=false;
						break;
					}
				}
				if(can)
					return true;
			}
			return false;
	}

	private boolean canApplyBeyondListPageRule(JSONObject unknownPageRule) {
		boolean can = true;
		try {
			String urlPattern = unknownPageRule.getString("url");
			String contentPattern = unknownPageRule.getString("content_key");
			if (!Pattern.matches(urlPattern, currentUrl)) {
				return false;
			}
			if(jsoupCurrentUrlDoc==null)
			{
				return false;
			}
			Elements element = jsoupCurrentUrlDoc.select(contentPattern);
			if(element==null)
			{
				return false;
			}
			else if (!element.html().toString().split("：")[0].trim().equals("店铺")) {
				can = false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return can;
	}

	public String getPageTypeName() {
		return pageTypeName;
	}

	public String getUserActionName() {
		return userActionName;
	}
}
