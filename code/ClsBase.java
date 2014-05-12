package process;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ClsBase {
	/**
	 * ���������ṩһЩ���õĺ��� �˴�ֻ�ž�̬�������벻Ҫ��ClsBaseʵ����
	 */
	private static SimpleDateFormat fileNameTimeFormat = new SimpleDateFormat(
			"_yyyy-MM-dd-HH-mm");
	private static SimpleDateFormat logEntryTimeFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private static String xpathTitle = "//head/title/text()";

	// ���ض���ʽ��ʱ����Ϊ�ļ���
	public static String getFileNameTime() {
		return fileNameTimeFormat.format(Calendar.getInstance().getTime());
	}

	// ���ض���ʽ��ʱ����Ϊ��־��Ŀ
	public static String getLogEntryTime() {
		return logEntryTimeFormat.format(Calendar.getInstance().getTime());
	}

	// ��url���н���
	public static String decodeUrl(String oldUrl, String characterSet) {
		String newUrl = "";
		try {
			newUrl = URLDecoder.decode(oldUrl, characterSet);
			return newUrl;
		} catch (Exception e) {//��׽�������͵Ĵ���
			// TODO Auto-generated catch block
			ClsFile.writeLog("�޷���'" + oldUrl + "'���б���. ������ԭurl"
					+ e.getLocalizedMessage());
			newUrl = oldUrl;
			return newUrl;
		} 
	}

	// ͨ��urlʹ��HttpURLConnection�����ȡHTML�ĵ�
	public static String getHtml(String strUrl) {// ʹ��HttpURLConncetion ��ȡҳ������
		// ����һ�ַ�ʽ��ͨ��HttpClient�����������Ҫ����ⲿ�⣬��û���Һ�
		StringBuffer html = new StringBuffer();
		try {
			// System.out.println(strUrl);
			String line;
			InputStream urlStream;
			URL objUrl = new java.net.URL(strUrl);
			HttpURLConnection connection = (java.net.HttpURLConnection) objUrl
					.openConnection();
			connection.setRequestProperty("User-agent", "IE/6.0");
			connection.setConnectTimeout(4000);
			connection.setReadTimeout(15000);
			connection.connect();
			urlStream = connection.getInputStream();
			java.io.BufferedReader reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(urlStream, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				html.append(line);
			}
		} catch (Exception e) {
			// getHtml(URLL);//�쳣�����ִ��getHtml
			return "";
		}
		return "";
	}

	// �������Ƶ�gzip��ת�����ַ���
	public static String gzipToString(Blob blob, String characterSet) {
		String content = "";
		if (blob == null)
			return "";
		else {
			BufferedInputStream bis;
			try {
				bis = new BufferedInputStream(blob.getBinaryStream());
				GZIPInputStream gunzip = new GZIPInputStream(bis);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[256];
				int n;
				while ((n = gunzip.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				content = out.toString(characterSet);
				return content;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ClsFile.writeLog(e.toString());
				return content;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ClsFile.writeLog(e.toString());
				return content;
			} 
		}
	}

	// ͨ��XML����ȡҳ����Ϣ���д���һЩbug
	public static void parseXML(String xml) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true); // never forget this!
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();

			// ����XML�ĵ�
			// Document doc = builder.parse("books.xml");

			// �����ַ�������������XML�ĵ���ʽҪ���ϸ�html�����ᱨ��
			StringReader sr = new StringReader(xml);
			InputSource is = new InputSource(sr);
			Document doc = builder.parse(is);

			XPathFactory xpf = XPathFactory.newInstance();
			XPath xpath = xpf.newXPath();
			XPathExpression expr = xpath.compile(xpathTitle);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				System.out.println(nodes.item(i).getNodeValue());
			}
		} catch (ParserConfigurationException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (SAXException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
