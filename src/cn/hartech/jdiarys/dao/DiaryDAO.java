package cn.hartech.jdiarys.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;
import cn.hartech.jdiarys.Actions;
import cn.hartech.jdiarys.pojo.DiaryPOJO;
import cn.hartech.jdiarys.utils.MyUtility;

public class DiaryDAO extends DBHelper {

	public DiaryDAO(Context context) {

		super(context);
	}

	public int add(DiaryPOJO diary) {

		String insertSQL = "insert into diary_content values(null, ?, ?, ?, ?, ?, ?, ?)";

		db.execSQL(
				insertSQL,
				new Object[] {
						MyUtility.format_yyyy_MM_dd_HH_mm_ss(diary.diaryTime),
						diary.diaryYear, diary.diaryMonth, diary.diaryDay,
						diary.diaryHour, diary.content, diary.tags });

		Cursor cursor = db.rawQuery("select LAST_INSERT_ROWID()", null);
		cursor.moveToFirst();

		int id = cursor.getInt(0);

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}

		return id;
	}

	public void addList(List<DiaryPOJO> diaryList) throws Exception {

		db.beginTransaction();

		try {

			for (DiaryPOJO diaryPOJO : diaryList) {

				add(diaryPOJO);
			}

			db.setTransactionSuccessful();

		} catch (Exception e) {

			Log.e("JDiaryS", "批量插入数据库失败", e);

			throw e;

		} finally {
			db.endTransaction();
		}
	}

	public void update(DiaryPOJO diary) {

		if (diary == null || diary._id == 0) {
			return;
		}

		String updateSQL = "update diary_content set diary_time = ?, "
				+ "diary_year = ?, diary_month = ?, diary_day = ?, "
				+ "diary_hour = ?, content = ?, tags = ? where _id = ?";

		db.execSQL(
				updateSQL,
				new Object[] {
						MyUtility.format_yyyy_MM_dd_HH_mm_ss(diary.diaryTime),
						diary.diaryYear, diary.diaryMonth, diary.diaryDay,
						diary.diaryHour, diary.content, diary.tags, diary._id });
	}

	// 从offset位置开始，返回count条记录
	public List<DiaryPOJO> getListByOffsetLimit(int offset, int count) {

		Cursor cursor = db.rawQuery("select * from diary_content "
				+ "order by diary_time desc limit ? offset ?", new String[] {
				String.valueOf(count), String.valueOf(offset) });

		return getDiaryListFromCursor(cursor);
	}

	// 根据年份月份获取列表
	public List<DiaryPOJO> getListByYearMonth(int year, int month) {

		Cursor cursor = db.rawQuery("select * from diary_content "
				+ "where diary_year = ? and diary_month = ? "
				+ "order by diary_time desc",
				new String[] { String.valueOf(year), String.valueOf(month) });

		return getDiaryListFromCursor(cursor);
	}

	// 根据自己拼接的SQL查询数据
	public List<DiaryPOJO> getListWithSQL(String sql, String[] values) {

		try {

			Cursor cursor = db.rawQuery(sql, values);

			return getDiaryListFromCursor(cursor);

		} catch (Exception ex) {

			Actions.showToast(ex.getMessage(), Toast.LENGTH_LONG);
			Log.e("JDiaryS", "DB Exception", ex);

			return null;
		}
	}

	public int delete(DiaryPOJO diary) {

		return db.delete("diary_content", "_id = ?",
				new String[] { String.valueOf(diary._id) });
	}

	public int deleteAllRecords() {

		return db.delete("diary_content", null, null);
	}

	private List<DiaryPOJO> getDiaryListFromCursor(Cursor cursor) {

		List<DiaryPOJO> list = new ArrayList<DiaryPOJO>();

		while (cursor.moveToNext()) {

			DiaryPOJO diary = new DiaryPOJO();

			diary._id = cursor.getLong(0);

			diary.diaryTime = MyUtility.parse_yyyy_MM_dd_HH_mm_ss(cursor
					.getString(1));

			diary.diaryYear = cursor.getInt(2);
			diary.diaryMonth = cursor.getInt(3);
			diary.diaryDay = cursor.getInt(4);
			diary.diaryHour = cursor.getInt(5);

			diary.content = cursor.getString(6);

			diary.tags = cursor.getString(7);

			list.add(diary);
		}

		cursor.close();
		cursor = null;

		return list;
	}

}
