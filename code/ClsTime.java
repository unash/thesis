package process;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClsTime {// ʱ�������
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Test Class ClsTime...");
		ClsTime ct = new ClsTime(100);
		ct.estimateStart(1000);
	}

	private double estimatePrecision;//���㾫��
	private double estimateProgress;//�������
	private int recordTotal;//��¼����
	private int recordLast;//��һ����¼��λ��
	private double rate;//��������
	private Date timeStart = null;//��ʼʱ��
	private Date timeLast = null;//��һ�ι����ʱ��
	private Date timeCurrent = null;//��ǰ����ʱ��
	private Date timeEnd = null;//����ʱ��
	private long timeLeft;//ʣ��ʱ�����
	DecimalFormat decimalFormate = new DecimalFormat("#.##");//�����С��λ��
	SimpleDateFormat timeFormat = null;

	// precision�����ܹ���ʱ��ƽ�����ٷ�
	public ClsTime(double precision) {
		if (precision <= 0) {
			estimatePrecision = 1;
		} else {
			estimatePrecision = precision;
		}
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public ClsTime() {
		estimatePrecision = 1000;
		timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public boolean estimateStart(int total) {
		recordTotal = total;
		timeStart = Calendar.getInstance().getTime();
		timeLast = timeStart;
		estimateProgress = 1;
		System.out.println("��¼������" + recordTotal + " | ��ǰʱ��: "
				+ timeFormat.format(timeStart));
		return true;
	}

	//����ʱ�䲢��ʾ
	public boolean estimating(int recordCurrent) {
		if ((double)recordCurrent >=(double) recordTotal * estimateProgress / estimatePrecision) {
			timeCurrent = Calendar.getInstance().getTime();
			rate = (timeCurrent.getTime() - timeLast.getTime())
					/ (recordCurrent - recordLast);
			recordLast = recordCurrent;
			timeLast = timeCurrent;
			timeLeft = (long) (rate * (recordTotal - recordCurrent));
			estimateProgress++;
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date tmp = new Date();
			tmp.setTime(timeCurrent.getTime() + timeLeft);
			double processPercent=(double) estimateProgress
							/ estimatePrecision * 100;
			if(processPercent>100.0) processPercent=100.0;
			System.out.println("���ȣ�"
					+ decimalFormate.format(processPercent) + "% | ˲ʱ���ʣ� " + rate
					/ 10 + "s/ÿ������¼" + " | ʣ�ࣺ" + timeLeft / 1000 + "s | Լ����"
					+ formatter.format(tmp) + "���� ");
		}
		return true;
	}

	//����ʱ�����
	public boolean estimateEnd() {
		timeEnd = Calendar.getInstance().getTime();
		System.out.println("�����������ʱ��"
				+ decimalFormate.format((double) (timeEnd.getTime() - timeStart
						.getTime()) / 1000 / 60) + " min.");
		return true;
	}
}
