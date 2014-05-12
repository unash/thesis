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
 * ʵ�������д���һЩ����
 *http://item.taobao.com/item.htm?http://item.taobao.com/item.htm?spm=a1z10.3.w18096144894.19.Q1ersk&id=19853079486&
 *������url�������������û�д���������url��������Ҫ�����ֹ�������Щ�ֶΡ�
 * */
public class ClsStateRules {

	// ��json�ļ���صı���
	private static String stateJsonFileName = "data/stateRules.json";
	private static String typeCodeJsonFileName = "data/CodeType.json";// ҳ�����ͱ����ļ�
	private static String actionCodeJsonFileName = "data/CodeAction.json";// �û���Ϊ�����ļ�
	private static String jsonFileType = "UTF-8";
	//��ʼ���Ķ���
	private JSONObject stateRules = null;// ������� ��json����
	private JSONObject stateTypeCode= null;// ҳ�����ͱ������
	private JSONObject stateActionCode= null;// �û���Ϊ�������
	// ��url��ص���Ϣ
	private String currentUrl;
	private String previousUrl;
	private String currentPage;// ��ǰҳ��
	private String previousPage;// ǰ��ҳ��
	private String currentParameter;// ��ǰҳ�������
	private String previousParameter;// ǰ��ҳ�������
	private HashMap<String, String> currentHmParameters;
	private HashMap<String, String> previousHmParameters;
	private HashMap<String, String> diffHmParameters;// ��ͬ�Ĳ���
	private String currentUrl_md5;
	private boolean diffPreAndCur;
	Document jsoupCurrentUrlDoc;
	// �������صĳ�Ա
	private String DEFAULT_PAGE_TYPE_NAME;
	private String DEFAULT_USER_ACTION_NAME;
	private String pageTypeName;
	private String userActionName;

	// ������ʽ
	private static String s_page_query = "[?]";// ����url��page��query���ַ�
	private static Pattern m_query_fields = Pattern
			.compile("([^&][^=]*)=([^&]+)");// ����query���ֶ�
	private static Pattern m_page = Pattern.compile("http[s]?://(.+)$");// ����page�е�Э����·��

	public static void main(String[] args) {
		ClsStateRules csr = new ClsStateRules();
		String previousUrl = "http://search.taobao.com/search?id";
		String currentUrl = "http://search.taobao.com/search?";
		csr.initStateVariable(currentUrl, previousUrl,"");
		
		csr.printDiffParameters();
		System.out.println(csr.getUserActionName());
		System.out.println(csr.getPageTypeName());
	}

	// ��ʼ������,����ҪôΪnullҪô��ֵ���ַ���ҪôΪ����Ҫô��ֵ
	public ClsStateRules() {//��ʼ����̬��Ϣ
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
	
	// ���ļ��л�ȡpage.json
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
	// ���ļ��л�ȡpage.json
	public boolean initTypeCodeJsonFromFile(String jsonFileName, String jsonFileCharacter) {
		InputStreamReader br;
		try {
			br = new InputStreamReader(new FileInputStream(jsonFileName),
					jsonFileCharacter);
			JSONTokener jsonTokerner = new JSONTokener(br);
			stateTypeCode = new JSONObject(jsonTokerner);
			System.out.println("�����ļ� "+jsonFileName+" ��ȡ�ɹ�!");
			return true;
		} catch (Exception e) {
			System.out.println("�����ļ� "+jsonFileName+" ��ȡʧ��!");
			e.printStackTrace();
			return false;
		}
	}
	// ���ļ��л�ȡaction.json
	public boolean initActionCodeJsonFromFile(String jsonFileName, String jsonFileCharacter) {
		InputStreamReader br;
		try {
			br = new InputStreamReader(new FileInputStream(jsonFileName),
					jsonFileCharacter);
			JSONTokener jsonTokerner = new JSONTokener(br);
			stateActionCode = new JSONObject(jsonTokerner);
			System.out.println("�����ļ� "+jsonFileName+" ��ȡ�ɹ�!");
			return true;
		} catch (Exception e) {
			System.out.println("�����ļ� "+jsonFileName+" ��ȡʧ��!");
			e.printStackTrace();
			return false;
		}
	}

	// ��ʼ��ȷ��״̬����Ҫ����Ϣ����һ������֮��url�оͲ�����null��
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
			System.out.println("��Ӧ��json�����ʱ�������!");
			e.printStackTrace();
		}
	}

	private void preProCurrentUrl() {// �˴���Ҫ��url��һ������ϴ
		currentHmParameters = null;
		// ��url�ָ���ҳ����ҳ�����
		String tmpUrl[] = currentUrl.split(s_page_query);
		currentPage = "";
		currentParameter = "";
		currentHmParameters = new HashMap<String, String>();// ��ѯ��Ϊ��
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
			// �������ŵ�hashmap��
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

	private void preProPreviousUrl() {// �˴���Ҫ��url��һ������ϴ
		previousHmParameters = null;
		// ��url�ָ���ҳ����ҳ�����
		String tmpUrl[] = previousUrl.split(s_page_query);
		previousPage = "";
		previousParameter = "";
		previousHmParameters = new HashMap<String, String>();// ��ѯ��Ϊ��
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
			// �������ŵ�hashmap��
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

	// �����ݿ��л�ȡjsoup�ĵ�
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
		// ��web�ϻ�ȡjsoup�ĵ�
		public boolean getJsoupDocFromWeb(String url) {
			try {
				jsoupCurrentUrlDoc = Jsoup.connect(url).timeout(8000).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				jsoupCurrentUrlDoc = null;
				//ClsFile.writeLog(webLogName, "ҳ���ȡʧ��:" + url);
				return false;
			}
			return true;
		}
	private void applyStateRules() throws JSONException {
		pageTypeName = DEFAULT_PAGE_TYPE_NAME;
		userActionName = DEFAULT_USER_ACTION_NAME;
		if (!stateRules.isNull(currentPage))// ������ҵ����ҳ��
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

				if (currentHmParameters.size() == 0)// �����������Ϊ0
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
				// �ж�previousҳ���Ƿ�Ϊ��
				if (previousPage.equals("")) {
					if (!jsonActionRules.isNull("invalidPreUrl")) {
						userActionName = jsonActionRules
								.getString("invalidPreUrl");
					}
				}
				// ����ҳ�治��ͬ
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
				// ����ҳ����ͬ
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
			// �Ҳ������ҳ��
			if (!getJsoupDocFromDb("info_urls","url_md5",currentUrl_md5))// ��ȡ��ҳjsoup�ĵ�, ���û�л�ȡ��jsoup�ĵ�
			{
				//System.out.println("����url��md5��ȡ�ĵ�ʧ�ܣ�");
				if(!getJsoupDocFromWeb(currentUrl))
				{
					//System.out.println("����url��ȡ�ĵ�ʧ�ܣ�");
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
				System.out.println("��json��Ѱ�Ҳ���paramChangeʱ����!");
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
			else if (!element.html().toString().split("��")[0].trim().equals("����")) {
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
