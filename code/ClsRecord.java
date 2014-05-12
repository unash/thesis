package process;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class ClsRecord {

	// 页面内容信息
	private String visitUrlString;// 访问路径
	private String visitUrlTitle;// 页面标题
	private String visitUrlKeywords;// 页面关键词
	private String visitUrlDescription;// 页面描述
	private String visitPage;// 访问页面
	private String visitQuery;// url参数
	private Vector<String> visitParams = null;
	private String visitUrlMd5;
	// 获取的内容对象
	private org.jsoup.nodes.Document contentJsoupObject;// 页面内容,用jsoup表示

	// 商品内容信息
	private boolean isItemDetailPage;// 是否是商品详情页
	private int visitItemCid;
	private int visitItemRootCid;
	private long visitItemId;// 若非零，则为商品页
	private String visitItemCname;

	// 补充内容--页面描述
	private String visitPageDescription;// 访问页面的信息
	private String visitPageClass;// 访问页面的信息

	// 页面类型与用户行为扩展
	private String visitPageTypeName;// 外部获取
	private String visitUserActionName;// 外部获取
	private String visitPageTypeGeneralName;// 泛化后的页面类型，外部获取
	private String visitUserActionGeneralName;// 泛化后的用户行为，外部获取
	private String visitPageTypeCode;// 页面类型编码，内部产生
	private String visitUserActionCode;// 用户行为编码，内部产生

	// 行为类信息
	private Date visitTime = null;// 访问时间
	private int visitDuration;// 持续时间
	private String visitPreUrl;// 前一路径

	// -----类静态信息-----Start-----
	// 正则表达式
	private static String s_page_query = "[?]";// 分离url中page与query的字符
	private static Pattern m_query_fields = Pattern
			.compile("([^&][^=]*)=([^&]+)");// 分离query中字段
	private static Pattern m_page = Pattern.compile("http[s]?://(.+)$");// 分离page中内容
	private static Pattern itemIdParam = Pattern.compile("id=([0-9]+)");// 得到品项的id

	// 来自jsoup中的信息模式
	private static String jsoupTitle = "title";
	private static String jsoupKeywords = "meta[name=keywords]";
	private static String jsoupDescription = "meta[name=description]";

	// 编码规则
	private static ClsPageRules cpr = new ClsPageRules();
	private static ClsStateRules csr = new ClsStateRules();

	// 类目泛化
	private static ClsClassification cc = new ClsClassification();

	// 需要读取的文件信息
	private static String webLogFileName = "output/crawlerLog.txt";// 抓取网页出错时的日志保留位置

	// -----类静态信息-----End-----

	// 初始化静态信息
	public ClsRecord() {
		// 各种初始化
		isItemDetailPage = false;
		visitUserActionGeneralName = "";
		visitPageTypeGeneralName = "";

		visitUrlString = "";// 访问路径
		visitUrlTitle = "";// 页面标题
		visitUrlKeywords = "";// 页面关键词
		visitUrlDescription = "";// 页面描述
		visitPage = "";// 访问页面
		visitQuery = "";// url参数
		visitParams = null;
		visitUrlMd5 = "";
		contentJsoupObject = null;// 页面内容,用jsoup表示
		visitItemCid = -1;
		visitItemRootCid = -1;
		visitItemId = -1;// 若非零，则为商品页
		visitItemCname = "";
	}

	public String getVisitUrlKeywords() {
		return visitUrlKeywords;
	}

	public void setVisitUrlKeywords(String visitUrlKeywords) {
		this.visitUrlKeywords = visitUrlKeywords;
	}

	public String getVisitPageDescription() {
		if (visitPageDescription == null || visitPageDescription == "")
			return "null";
		else
			return visitPageDescription;
	}

	public String getVisitPageClass() {
		if (visitPageClass == null || visitPageClass == "")
			return "null";
		else
			return visitPageClass;
	}

	public int getVisitContentItemCid() {
		return visitItemCid;
	}

	public String getVisitItemCname() {
		if (visitItemCname == null || visitItemCname == "")
			return "";
		else
			return visitItemCname;
	}

	// 从query中获取品项的Id。
	public String getItemIdFromQuery() {
		if (visitQuery != "") {
			Matcher m = itemIdParam.matcher(visitQuery);
			if (m.find()) {
				return m.group(1);
			}
		}
		return "";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClsRecord cr = new ClsRecord();
		System.out.println(ClsBase.decodeUrl(
				"%B7%AB%B2%BC%D0%AC+%BA%F1%B5%D7+%CF%C4", "gb2312"));
		cr.visitUrlString = "http://item.taobao.com/item.htm?spm=a230r.1.14.131.6oZsJn&id=14941378521";

		cr.expandRecordInfoWithUrl(true, true);
		cr.expandRecordInfoWithStateRules();
		cr.expandRecordInfoWithPageRules();
		System.out.println(cr.getItemIdFromQuery());
		System.out.println(cr.serializeRecordInfoBody());
	}

	// 对成员变量赋值
	public void setVisitUrlMd5(String visitUrlMd5) {
		this.visitUrlMd5 = visitUrlMd5;
	}

	public void setVisitUrl(String visitUrl) {
		this.visitUrlString = visitUrl;
	}

	public void setVisitPage(String visitPage) {
		this.visitPage = visitPage;
	}

	public void setVisitQuery(String visitQuery) {
		this.visitQuery = visitQuery;
	}

	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}

	public void setVisitDuration(int duration) {
		this.visitDuration = duration;
	}

	public void setVisitPreUrl(String preUrl) {
		this.visitPreUrl = preUrl;
	}

	public Date getVisitTime() {
		return visitTime;
	}

	public String getVisitPage() {
		return visitPage;
	}

	public String getVisitQuery() {
		return visitQuery;
	}

	public int getVisitDuration() {
		// TODO Auto-generated method stub
		return visitDuration;
	}

	public Vector<String> getParams() {
		return visitParams;
	}

	public String getVisitPageTypeName() {
		return visitPageTypeName;
	}

	public void setVisitPageTypeName(String visitPageTypeName) {
		this.visitPageTypeName = visitPageTypeName;
	}

	public String getVisitUserActionName() {
		return visitUserActionName;
	}

	public void setVisitUserActionName(String visitUserActionName) {
		this.visitUserActionName = visitUserActionName;
	}

	public String getVisitPageTypeGeneralName() {
		return visitPageTypeGeneralName;
	}

	public void setVisitPageTypeGeneralName(String visitPageTypeGeneralName) {
		this.visitPageTypeGeneralName = visitPageTypeGeneralName;
	}

	public String getVisitUserActionGeneralName() {
		return visitUserActionGeneralName;
	}

	public void setVisitUserActionGeneralName(String visitUserActionGeneralName) {
		this.visitUserActionGeneralName = visitUserActionGeneralName;
	}

	public String getVisitPageTypeCode() {
		return visitPageTypeCode;
	}

	public String getVisitUserActionCode() {
		return visitUserActionCode;
	}

	public int getItemRootCid() {
		return visitItemRootCid;
	}

	public long getItemId() {
		return visitItemId;
	}

	public void setItemId(long intItemId) {
		this.visitItemId = intItemId;
	}

	public boolean expandRecordInfoWithStateRules() {
		visitPageTypeCode = csr.getGeneralTypeCode(visitPageTypeGeneralName);
		visitUserActionCode = csr
				.getGeneralActionCode(visitUserActionGeneralName);
		return true;
	}

	public boolean expandRecordInfoWithPageRules() {
		if (visitPage != null && visitPage != "") {
			visitPageDescription = cpr.getPageDescriptionFromJson(visitPage);
			visitPageClass = cpr.getPageClassFromJson(visitPage);
		}
		return true;
	}

	public boolean producePageAndQueryFromUrl() {
		if (visitUrlString == null) {
			return false;
		}
		// 将url分隔成页面与页面参数
		String tmpUrl[] = visitUrlString.split(s_page_query);
		if (tmpUrl.length >= 1) {
			visitPage = tmpUrl[0];
			Matcher m = m_page.matcher(visitPage);
			if (m.find()) {
				visitPage = m.group(1);
				// System.out.println(visitPage);
			} else {
				visitPage = null;
			}
		}
		if (tmpUrl.length >= 2)
			visitQuery = tmpUrl[1];
		else
			visitQuery = null;// 查询串为空

		// 从url中获取参数列表，并判断是否是商品详情页，若是，则提取信息
		// 处理商品详情与类目信息
		visitParams = new Vector<String>();// 属于一个记录的参数列表
		if (visitQuery != null && visitQuery != "") {
			Matcher matcher = m_query_fields.matcher(visitQuery);
			String aQuery = "";
			while (matcher.find()) {
				// matcher.start(); matcher.end();
				visitParams.add(matcher.group(1));
				if (!cpr.paramIsUseless(visitPage, matcher.group(1))) {// 去掉追踪性信息
					if (!aQuery.equals("")) {
						aQuery += ";";
					}
					aQuery += matcher.group(1) + "="
							+ ClsBase.decodeUrl(matcher.group(2), "gb2312");
					if (matcher.group(1).equals("id")
							&& visitPage.equals("item.taobao.com/item.htm")) {
						isItemDetailPage = true;
						try{
						visitItemId = Long.parseLong(matcher.group(2));
						}
						catch(Exception e)
						{
							visitItemId=-1;
						}
					} 
				}
			}
			visitQuery = aQuery;
		}
		return true;
	}

	// 从数据库中获取jsoup文档
	private boolean produceContentJsoupFromDb(String tableName,
			String fieldName, String md5) {
		if (md5 != null && md5 != "") {
			String html = ClsBase.gzipToString(
					ClsDb.globalCd.getUrlBlobFromDb(tableName, fieldName, md5),
					"utf-8");
			if (html.trim() != "") {
				contentJsoupObject = Jsoup.parse(html);
				return true;
			} else {
				return false;
			}
		} else {
			contentJsoupObject = null;
			return false;
		}
	}

	// 从web上获取jsoup文档
	private boolean produceContentJsoupFromWeb(String url) {
		try {
			contentJsoupObject = Jsoup.connect(url).timeout(8000).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			contentJsoupObject = null;
			ClsFile.writeLog(webLogFileName, "页面获取失败:" + url);
			return false;
		}
		return true;
	}

	// 从url中扩展信息，通过其他数据源
	public void expandRecordInfoWithUrl(boolean useDbPageContent,// DB的优先级高
			boolean useWebPageContent) {
		if (visitUrlString == null)
			return;

		producePageAndQueryFromUrl();
		// 获取页面并进行解析，查询数据库，不行的话就直接抓取页面
		// 来自数据库中的信息——已经抓取好的
		// 来自网页抓取的信息——当前抓取的
		// 控制参数：useDbPageContent，useWebPageContent
		if (useDbPageContent) {
			if (!produceContentJsoupFromDb("info_urls", "url_md5", visitUrlMd5)
					&& useWebPageContent) {
				produceContentJsoupFromWeb(visitUrlString);
			}
			produceUrlInfoWithContentJsoup();
			produceItemInfoWithContentJsoup();
		}
		expandRecordInfoWithPageRules();
		expandRecordInfoWithStateRules();
	}

	private boolean produceUrlInfoWithContentJsoup() {
		if (contentJsoupObject == null) {
			return false;
		}
		Elements element = null;
		// 从页面中抓取页面描述
		element = contentJsoupObject.select(jsoupDescription);
		visitUrlDescription = element.attr("content").toString();
		// 从页面中抓取页面Title
		element = contentJsoupObject.select(jsoupTitle);
		visitUrlTitle = element.html().toString();
		// 从页面中抓取页面关键词
		element = contentJsoupObject.select(jsoupKeywords);
		visitUrlKeywords = element.attr("content").toString();
		return true;
	}

	private boolean produceItemInfoWithContentJsoup() {
		if (contentJsoupObject == null) {
			return false;
		}
		if (!isItemDetailPage) {
			visitItemCid = -1;
			visitItemRootCid = -1;
			visitItemId = -1;// 若非零，则为商品页
			visitItemCname = null;
			return true;
		}
		String html = contentJsoupObject.html();
		Pattern patCid = Pattern.compile(" cid:'([0-9]+)',\\s");
		if (html != "") {

			// 从jsoup文档中获取品项class,返回品项的id
			Matcher matcher = patCid.matcher(html);
			if (matcher.find()) {
				visitItemCid = Integer.parseInt(matcher.group(1));
				visitItemCname = cc.getCnameFromCid(visitItemCid);
				if (visitItemCid != -1) {
					visitItemRootCid = cc.getRootFromNode(visitItemCid);
					// visitItemRootCid=classHmMapper.containsKey(Integer.parseInt(visitUrlItemId));
				} else {
					visitItemRootCid = -1;
				}
			} else {
				visitItemCid = -1;
				visitItemCname = null;
			}
		}
		return true;
	}

	public static String serializeRecordInfoHead() {
		// 序列化ClsRecord记录
		String line = "";
		line = "访问时刻" + "\t" + "持续时间" + "\t" + "页面标题" + "\t" + "访问页面" + "\t"
				+ "页面参数" + "\t" + "页面信息" + "\t" + "页面类属" + "\t" + "访问路径" + "\t"
				+ "前一路径" + "\t" + "页面关键词" + "\t" + "页面描述" + "\n";
		return line;
	}

	public String serializeRecordInfoBody() {
		// 序列化ClsRecord记录
		String line = "";
		line = visitTime + "\t" + visitDuration + "\t" + visitUrlTitle + "\t"
				+ visitPage + "\t" + visitQuery + "\t" + visitPageDescription
				+ "\t" + visitPageClass + "\t" + visitUrlString + "\t"
				+ visitPreUrl + "\t" + visitUrlKeywords + "\t"
				+ visitUrlDescription + "\n";
		return line;
	}

}