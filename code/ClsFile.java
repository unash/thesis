package process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClsFile {

	/**
	 * @param args
	 *            主要用于与文件对象的交互，比如写入文件等
	 */
	FileWriter fwFile = null;
	private static int waitTime = 2;// 重试等待时间
	private static int retrySum = 2;// 重试次数
	private int retryNum;
	private String fileName;
	private static String globalLogName="output/log.txt";//全局日志文件的地址
	
	// 标准构造函数
	public ClsFile(String fileName) {
		this.fileName = fileName;
			openFile();
	}

	public static void writeLog(String log){//写入全局日志，不需要输入文件地址
		writeLog(globalLogName,log);
	}

	// 全局文件，用于处理所有警告日志，每次用前打开，用完就关闭。
	public static void writeLog(String fileName, String log) {
		ClsFile globalLog = new ClsFile(fileName);
		String timeNow = ClsBase.getLogEntryTime();
		globalLog.writeFile(timeNow + "\t" + log+"\n");
		globalLog.closeFile();
	}

	// 判读文件是否存在
	public Boolean fileExist(String fileName) {
		File f = new File(fileName);
		if (f.exists())
			return true;
		else
			return false;
	}

	// 打开文件，文件名需要实现指定
	private boolean openFile() {
		if (retryNum >= retrySum) {
			System.out.println("超过最大重试次数，程序终止!");
			System.exit(0);
			return false;
		}
		try {
			if (!fileExist(fileName)) {
				System.out.println("程序将创建文件" + fileName);
			} else {
				// System.out.println("文件" + fileUrl + "已经存在，新内容将附加在其后");
			}
			fwFile = new FileWriter(fileName, true);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("文件打开失败！无法写入并保存！");
			e.printStackTrace();
			if (fwFile != null) {
				try {
					fwFile.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("程序无法解决的错误！");
					return false;
				}
			}
			retryNum++;
			System.out.println("文件打开失败，等待" + waitTime + "s后重试...");
			try {
				Thread.sleep(waitTime * 1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				System.out.println("程序无法解决的错误！");
				e1.printStackTrace();
				return false;
			}
			openFile();
			System.out.println("文件打开期间发生错误，但程序已经正确处理！");
			return true;
		}
	}

	public boolean writeFile(String line) {
		if (fwFile == null)
			return false;
		try {
			fwFile.write(line);
			fwFile.flush();//这一句话必须加上，否则有些数据写不进去
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("文件写入失败！");
			try {
				fwFile.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("程序无法解决的错误！");
				e1.printStackTrace();
				return false;
			}
			try {
				fwFile.close();
			} catch (IOException e1) {
				System.out.println("程序无法解决的错误！");
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			openFile();
			e.printStackTrace();
			writeFile(line);
			return true;
		}
	}

	public boolean closeFile() {
		if (fwFile == null)
			return false;
		else {
			try {
				fwFile.flush();
				fwFile.close();
				// System.out.println("文件关闭成功！");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("文件关闭失败！");
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
}