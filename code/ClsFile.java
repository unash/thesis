package process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClsFile {

	/**
	 * @param args
	 *            ��Ҫ�������ļ�����Ľ���������д���ļ���
	 */
	FileWriter fwFile = null;
	private static int waitTime = 2;// ���Եȴ�ʱ��
	private static int retrySum = 2;// ���Դ���
	private int retryNum;
	private String fileName;
	private static String globalLogName="output/log.txt";//ȫ����־�ļ��ĵ�ַ
	
	// ��׼���캯��
	public ClsFile(String fileName) {
		this.fileName = fileName;
			openFile();
	}

	public static void writeLog(String log){//д��ȫ����־������Ҫ�����ļ���ַ
		writeLog(globalLogName,log);
	}

	// ȫ���ļ������ڴ������о�����־��ÿ����ǰ�򿪣�����͹رա�
	public static void writeLog(String fileName, String log) {
		ClsFile globalLog = new ClsFile(fileName);
		String timeNow = ClsBase.getLogEntryTime();
		globalLog.writeFile(timeNow + "\t" + log+"\n");
		globalLog.closeFile();
	}

	// �ж��ļ��Ƿ����
	public Boolean fileExist(String fileName) {
		File f = new File(fileName);
		if (f.exists())
			return true;
		else
			return false;
	}

	// ���ļ����ļ�����Ҫʵ��ָ��
	private boolean openFile() {
		if (retryNum >= retrySum) {
			System.out.println("����������Դ�����������ֹ!");
			System.exit(0);
			return false;
		}
		try {
			if (!fileExist(fileName)) {
				System.out.println("���򽫴����ļ�" + fileName);
			} else {
				// System.out.println("�ļ�" + fileUrl + "�Ѿ����ڣ������ݽ����������");
			}
			fwFile = new FileWriter(fileName, true);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("�ļ���ʧ�ܣ��޷�д�벢���棡");
			e.printStackTrace();
			if (fwFile != null) {
				try {
					fwFile.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("�����޷�����Ĵ���");
					return false;
				}
			}
			retryNum++;
			System.out.println("�ļ���ʧ�ܣ��ȴ�" + waitTime + "s������...");
			try {
				Thread.sleep(waitTime * 1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				System.out.println("�����޷�����Ĵ���");
				e1.printStackTrace();
				return false;
			}
			openFile();
			System.out.println("�ļ����ڼ䷢�����󣬵������Ѿ���ȷ����");
			return true;
		}
	}

	public boolean writeFile(String line) {
		if (fwFile == null)
			return false;
		try {
			fwFile.write(line);
			fwFile.flush();//��һ�仰������ϣ�������Щ����д����ȥ
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("�ļ�д��ʧ�ܣ�");
			try {
				fwFile.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("�����޷�����Ĵ���");
				e1.printStackTrace();
				return false;
			}
			try {
				fwFile.close();
			} catch (IOException e1) {
				System.out.println("�����޷�����Ĵ���");
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
				// System.out.println("�ļ��رճɹ���");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("�ļ��ر�ʧ�ܣ�");
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
}