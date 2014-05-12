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
	 * 此类用于提供一些公用的函数 此处只放静态函数，请不要将ClsBase实例化
	 */
	private static SimpleDateFormat fileNameTimeFormat = new SimpleDateFormat(
			"_yyyy-MM-dd-HH-mm");
	private static SimpleDateFormat logEntryTimeFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private static String xpathTitle = "//head/title/text()";

	// 以特定格式的时间作为文件名
	public static String getFileNameTime() {
		return fileNameTimeFormat.format(Calendar.getInstance().getTime());
	}

	// 以特定格式的时间作为日志条目
	public static String getLogEntryTime() {
		return logEntryTimeFormat.format(Calendar.getInstance().getTime());
	}

	// 对url进行解码
	public static String decodeUrl(String oldUrl, String characterSet) {
		String newUrl = "";
		try {
			newUrl = URLDecoder.decode(oldUrl, characterSet);
			return newUrl;
		} catch (Exception e) {//捕捉任意类型的错误
			// TODO Auto-generated catch block
			ClsFile.writeLog("无法对'" + oldUrl + "'进行编码. 将返回原url"
					+ e.getLocalizedMessage());
			newUrl = oldUrl;
			return newUrl;
		} 
	}

	// 通过url使用HttpURLConnection对象获取HTML文档
	public static String getHtml(String strUrl) {// 使用HttpURLConncetion 获取页面数据
		// 还有一种方式是通过HttpClient，不过这个需要添加外部库，我没有找好
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
			// getHtml(URLL);//异常后继续执行getHtml
			return "";
		}
		return "";
	}

	// 将二进制的gzip流转换成字符串
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

	// 通过XML来获取页面信息，尚存在一些bug
	public static void parseXML(String xml) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true); // never forget this!
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();

			// 解析XML文档
			// Document doc = builder.parse("books.xml");

			// 解析字符流，但是由于XML文档格式要求严格，html解析会报错
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
