package cn.hartech.jdiarys.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class MyUtility {

	public static final SimpleDateFormat DF_HH_mm_ss = new SimpleDateFormat(
			"HH:mm:ss", Constant.LOCAL);

	public static final SimpleDateFormat DF_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Constant.LOCAL);

	public static final SimpleDateFormat DF_yyyy_MM_dd = new SimpleDateFormat(
			"yyyy-MM-dd", Constant.LOCAL);

	public static final SimpleDateFormat DF_yyyy_MM_dd_HH_mm = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm", Constant.LOCAL);

	/**
	 * 判断是否为整数
	 * 
	 * @author Jeff.Z
	 * @date 2014年9月14日
	 * @param text
	 * @return
	 */
	public static boolean isInteger(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static void fireLog() {

		Log.e("FIRE LOG!!", "" + new Date());
	}

	// 这里主要是加上异常处理和同步处理
	public static Date parse_yyyy_MM_dd_HH_mm_ss(String dateStr) {

		if (dateStr == null || dateStr.trim().equals("")) {
			return null;
		}

		try {

			synchronized (DF_yyyy_MM_dd_HH_mm_ss) {
				return DF_yyyy_MM_dd_HH_mm_ss.parse(dateStr);
			}

		} catch (Exception e) {

			return null;
		}

	}

	// 这里主要是加上异常处理和同步处理
	public static String format_yyyy_MM_dd_HH_mm_ss(Date date) {

		if (date == null) {
			return null;
		}

		try {

			synchronized (DF_yyyy_MM_dd_HH_mm_ss) {
				return DF_yyyy_MM_dd_HH_mm_ss.format(date);
			}

		} catch (Exception e) {

			return "";
		}
	}

	// 保留小数位数
	public static String setScale(double value, int scale) {

		BigDecimal decimal = new BigDecimal(value);

		// 四舍五入，保留两位小数
		value = decimal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();

		return String.valueOf(value);
	}

	// 输入总共秒数，输出：5分07秒
	public static String getMinuteAndSecond(int seconds) {

		int minute = seconds / 60;

		seconds = seconds % 60;

		String result = String.valueOf(minute);

		result += "分";

		if (seconds < 10) {
			result += "0" + seconds;
		} else {
			result += seconds;
		}

		result += "秒";

		return result;
	}

	// 返回：日，六 等
	public static String getWeekChineseWeekDay(Date date) {

		@SuppressWarnings("deprecation")
		int weekDay = date.getDay();

		// 0 = Sunday, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 
		// 4 = Thursday, 5 = Friday, 6 = Saturday
		switch (weekDay) {

		case 0:
			return "日";

		case 1:
			return "一";

		case 2:
			return "二";

		case 3:
			return "三";

		case 4:
			return "四";

		case 5:
			return "五";

		case 6:
			return "六";

		default:
			return "";
		}
	}

	/**
	 * 获取当前储存卡里本App的数据目录，如果没有则返回""，将保存在/data/data/yourApp目录内
	 * 
	 * 如果有卡，返回类似："/mnt/sdcard/stock/"
	 * 
	 * @return
	 */
	public static String getAppFolder() {

		// 是否有储存卡
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			try {

				String scardDir = Environment.getExternalStorageDirectory()
						.getCanonicalPath();

				String appFolder = "";

				if (Constant.IS_TEST_ENVIRONMENT) {
					appFolder = Constant.APP_FOLDER_TEST_ENV;
				} else {
					appFolder = Constant.APP_FOLDER;
				}

				return scardDir + "/" + appFolder + "/";

			} catch (IOException e) {
				return "";
			}

		} else {

			return "";
		}
	}
}
