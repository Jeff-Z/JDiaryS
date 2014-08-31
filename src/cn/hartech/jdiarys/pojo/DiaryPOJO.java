package cn.hartech.jdiarys.pojo;

import java.util.Calendar;
import java.util.Date;

import cn.hartech.jdiarys.utils.Constant;

/**
 * 代表一条日历记录
 * 
 * @author jin.zheng
 * @date 2014年6月9日
 *
 */
public class DiaryPOJO {

	public long _id;

	public Date diaryTime;

	public int diaryYear;

	public int diaryMonth;

	public int diaryDay;

	public int diaryHour;

	public String content;

	public String tags;

	public Date modifyTime;

	public int modifyCount;

	public boolean isDelete = false;

	public static DiaryPOJO createDiaryPOJO() {

		DiaryPOJO diary = new DiaryPOJO();

		diary.diaryTime = new Date();
		diary.makeOtherDateValues();

		return diary;

	}

	// 根据diaryTime Date对象获取year、month、day、hour等值
	public void makeOtherDateValues() {

		if (diaryTime == null) {
			return;
		}

		Calendar calendar = Calendar.getInstance(Constant.LOCAL);
		calendar.setTime(diaryTime);

		diaryYear = calendar.get(Calendar.YEAR);
		diaryMonth = calendar.get(Calendar.MONTH) + 1;
		diaryDay = calendar.get(Calendar.DAY_OF_MONTH);
		diaryHour = calendar.get(Calendar.HOUR_OF_DAY);
	}

}
