package cn.hartech.jdiarys.engine.importdiary;

import java.util.Comparator;

import cn.hartech.jdiarys.pojo.DiaryPOJO;

public class DateComparator implements Comparator<DiaryPOJO> {

	public int compare(DiaryPOJO o1, DiaryPOJO o2) {

		long i = o1.diaryTime.getTime() - o2.diaryTime.getTime();

		if (i > 0) {
			return 1;
		} else {
			return -1;
		}

	}
}
