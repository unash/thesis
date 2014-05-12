package process;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class ClsRecord {

	// ҳ��������Ϣ
	private String visitUrlString;// ����·��
	private String visitUrlTitle;// ҳ�����
	private String visitUrlKeywords;// ҳ��ؼ���
	private String visitUrlDescription;// ҳ������
	private String visitPage;// ����ҳ��
	private String visitQuery;// url����
	private Vector<String> visitParams = null;
	private String visitUrlMd5;
	// ��ȡ�����ݶ���
	private org.jsoup.nodes.Document contentJsoupObject;// ҳ������,��jsoup��ʾ

	// ��Ʒ������Ϣ
	private boolean isItemDetailPage;// �Ƿ�����Ʒ����ҳ
	private int visitItemCid;
	private int visitItemRootCid;
	private long visitItemId;// �����㣬��Ϊ��Ʒҳ
	private String visitItemCname;

	// ��������--ҳ������
	private String visitPageDescription;// ����ҳ�����Ϣ
	private String visitPageClass;// ����ҳ�����Ϣ

	// ҳ���������û���Ϊ��չ
	private String visitPageTypeName;// �ⲿ��ȡ
	private String visitUserActionName;// �ⲿ��ȡ
	private String visitPageTypeGeneralName;// �������ҳ�����ͣ��ⲿ��ȡ
	private String visitUserActionGeneralName;// ��������û���Ϊ���ⲿ��ȡ
	private String visitPageTypeCode;// ҳ�����ͱ��룬�ڲ�����
	private String visitUserActionCode;// �û���Ϊ���룬�ڲ�����

	// ��Ϊ����Ϣ
	private Date visitTime = null;// ����ʱ��
	private int visitDuration;// ����ʱ��
	private String visitPreUrl;// ǰһ·��

	// -----�ྲ̬��Ϣ-----Start-----
	// ������ʽ
	private static String s_page_query = "[?]";// ����url��page��query���ַ�
	private static Pattern m_query_fields = Pattern
			.compile("([^&][^=]*)=([^&]+)");// ����query���ֶ�
	private static Pattern m_page = Pattern.compile("http[s]?://(.+)$");// ����page������
	private static Pattern itemIdParam = Pattern.compile("id=([0-9]+)");// �õ�Ʒ���id

	// ����jsoup�е���Ϣģʽ
	private static String jsoupTitle = "title";
	private static String jsoupKeywords = "meta[name=keywords]";
	private static String jsoupDescription = "meta[name=description]";

	// �������
	private static ClsPageRules cpr = new ClsPageRules();
	private static ClsStateRules csr = new ClsStateRules();

	// ��Ŀ����
	private static ClsClassification cc = new ClsClassification();

	// ��Ҫ��ȡ���ļ���Ϣ
	private static String webLogFileName = "output/crawlerLog.txt";// ץȡ��ҳ����ʱ����־����λ��

	// -----�ྲ̬��Ϣ-----End-----

	// ��ʼ����̬��Ϣ
	public ClsRecord() {
		// ���ֳ�ʼ��
		isItemDetailPage = false;
		visitUserActionGeneralName = "";
		visitPageTypeGeneralName = "";

		visitUrlString = "";// ����·��
		visitUrlTitle = "";// ҳ�����
		visitUrlKeywords = "";// ҳ��ؼ���
		visitUrlDescription = "";// ҳ������
		visitPage = "";// ����ҳ��
		visitQuery = "";// url����
		visitParams = null;
		visitUrlMd5 = "";
		contentJsoupObject = null;// ҳ������,��jsoup��ʾ
		visitItemCid = -1;
		visitItemRootCid = -1;
		visitItemId = -1;// �����㣬��Ϊ��Ʒҳ
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

	// ��query�л�ȡƷ���Id��
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

	// �Գ�Ա������ֵ
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
		// ��url�ָ���ҳ����ҳ�����
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
			visitQuery = null;// ��ѯ��Ϊ��

		// ��url�л�ȡ�����б����ж��Ƿ�����Ʒ����ҳ�����ǣ�����ȡ��Ϣ
		// ������Ʒ��������Ŀ��Ϣ
		visitParams = new Vector<String>();// ����һ����¼�Ĳ����б�
		if (visitQuery != null && visitQuery != "") {
			Matcher matcher = m_query_fields.matcher(visitQuery);
			String aQuery = "";
			while (matcher.find()) {
				// matcher.start(); matcher.end();
				visitParams.add(matcher.group(1));
				if (!cpr.paramIsUseless(visitPage, matcher.group(1))) {// ȥ��׷������Ϣ
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

	// �����ݿ��л�ȡjsoup�ĵ�
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

	// ��web�ϻ�ȡjsoup�ĵ�
	private boolean produceContentJsoupFromWeb(String url) {
		try {
			contentJsoupObject = Jsoup.connect(url).timeout(8000).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			contentJsoupObject = null;
			ClsFile.writeLog(webLogFileName, "ҳ���ȡʧ��:" + url);
			return false;
		}
		return true;
	}

	// ��url����չ��Ϣ��ͨ����������Դ
	public void expandRecordInfoWithUrl(boolean useDbPageContent,// DB�����ȼ���
			boolean useWebPageContent) {
		if (visitUrlString == null)
			return;

		producePageAndQueryFromUrl();
		// ��ȡҳ�沢���н�������ѯ���ݿ⣬���еĻ���ֱ��ץȡҳ��
		// �������ݿ��е���Ϣ�����Ѿ�ץȡ�õ�
		// ������ҳץȡ����Ϣ������ǰץȡ��
		// ���Ʋ�����useDbPageContent��useWebPageContent
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
		// ��ҳ����ץȡҳ������
		element = contentJsoupObject.select(jsoupDescription);
		visitUrlDescription = element.attr("content").toString();
		// ��ҳ����ץȡҳ��Title
		element = contentJsoupObject.select(jsoupTitle);
		visitUrlTitle = element.html().toString();
		// ��ҳ����ץȡҳ��ؼ���
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
			visitItemId = -1;// �����㣬��Ϊ��Ʒҳ
			visitItemCname = null;
			return true;
		}
		String html = contentJsoupObject.html();
		Pattern patCid = Pattern.compile(" cid:'([0-9]+)',\\s");
		if (html != "") {

			// ��jsoup�ĵ��л�ȡƷ��class,����Ʒ���id
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
		// ���л�ClsRecord��¼
		String line = "";
		line = "����ʱ��" + "\t" + "����ʱ��" + "\t" + "ҳ�����" + "\t" + "����ҳ��" + "\t"
				+ "ҳ�����" + "\t" + "ҳ����Ϣ" + "\t" + "ҳ������" + "\t" + "����·��" + "\t"
				+ "ǰһ·��" + "\t" + "ҳ��ؼ���" + "\t" + "ҳ������" + "\n";
		return line;
	}

	public String serializeRecordInfoBody() {
		// ���л�ClsRecord��¼
		String line = "";
		line = visitTime + "\t" + visitDuration + "\t" + visitUrlTitle + "\t"
				+ visitPage + "\t" + visitQuery + "\t" + visitPageDescription
				+ "\t" + visitPageClass + "\t" + visitUrlString + "\t"
				+ visitPreUrl + "\t" + visitUrlKeywords + "\t"
				+ visitUrlDescription + "\n";
		return line;
	}

}