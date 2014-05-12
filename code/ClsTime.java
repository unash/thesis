package process;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClsTime {// 时间估计类
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Test Class ClsTime...");
		ClsTime ct = new ClsTime(100);
		ct.estimateStart(1000);
	}

	private double estimatePrecision;//估算精度
	private double estimateProgress;//估算进度
	private int recordTotal;//记录总数
	private int recordLast;//上一个记录的位置
	private double rate;//运行速率
	private Date timeStart = null;//开始时间
	private Date timeLast = null;//上一次估算的时间
	private Date timeCurrent = null;//当前进度时间
	private Date timeEnd = null;//结束时间
	private long timeLeft;//剩余时间估计
	DecimalFormat decimalFormate = new DecimalFormat("#.##");//输出的小数位数
	SimpleDateFormat timeFormat = null;

	// precision代表将总共的时间平均多少份
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
		System.out.println("记录总数：" + recordTotal + " | 当前时间: "
				+ timeFormat.format(timeStart));
		return true;
	}

	//估算时间并显示
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
			System.out.println("进度："
					+ decimalFormate.format(processPercent) + "% | 瞬时速率： " + rate
					/ 10 + "s/每百条记录" + " | 剩余：" + timeLeft / 1000 + "s | 约到："
					+ formatter.format(tmp) + "结束 ");
		}
		return true;
	}

	//估算时间结束
	public boolean estimateEnd() {
		timeEnd = Calendar.getInstance().getTime();
		System.out.println("处理结束！用时："
				+ decimalFormate.format((double) (timeEnd.getTime() - timeStart
						.getTime()) / 1000 / 60) + " min.");
		return true;
	}
}
