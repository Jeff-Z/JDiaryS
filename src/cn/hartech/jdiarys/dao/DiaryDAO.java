package cn.hartech.jdiarys.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
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
	public List<DiaryPOJO> getLastItemsByOffsetLimit(int offset, int count) {

		Cursor cursor = db.rawQuery("select * from diary_content "
				+ "order by diary_time desc limit ? offset ?", new String[] {
				String.valueOf(count), String.valueOf(offset) });

		return getDiaryListFromCursor(cursor);
	}

	public List<DiaryPOJO> getDiaryByYearMonth(int year, int month) {

		Cursor cursor = db.rawQuery("select * from diary_content "
				+ "where diary_year = ? and diary_month = ? "
				+ "order by diary_time desc",
				new String[] { String.valueOf(year), String.valueOf(month) });

		return getDiaryListFromCursor(cursor);
	}

	/**
	 * 1, 支持多关键字搜索：如 "巴菲特 经济"
	 * 
	 * 2, 支持SQL搜索模式，可以输入SQL中的where子句，如:
	 * 		"s content='abc' and hour=4"
	 * 		"s len(content)>100"
	 * 
	 * @param text
	 * @return
	 */
	public List<DiaryPOJO> getDiaryBySearch(String text) {

		if (text == null || text.trim().equals("")) {
			return null;
		}

		text = text.trim();

		// 支持SQL搜索模式
		if (text.startsWith("s")) {

			return searchWithSQL(text);
		}

		// 取出用空格隔开的多个关键字
		String[] textArray = StringUtils.split(text);

		String condition = "";

		for (int i = 0; i < textArray.length; i++) {

			if (i == 0) {
				condition = " content like ?";
			} else {
				condition += " and content like ?";
			}

			textArray[i] = "%" + textArray[i] + "%";
		}

		Cursor cursor = db.rawQuery("select * from diary_content where"
				+ condition + " order by diary_time desc", textArray);

		return getDiaryListFromCursor(cursor);
	}

	/**
	 * 
	 * 支持SQL搜索模式，可以输入SQL中的where子句，如:
	 * 		"s content='abc' and hour=4"
	 * 		"s len(content)>100"
	 * 
	 * @param substring
	 * @return
	 */
	private List<DiaryPOJO> searchWithSQL(String where) {

		where = where.substring(1);

		Cursor cursor = null;

		try {

			cursor = db.rawQuery("select * from diary_content where " + where
					+ " order by diary_time desc", null);

		} catch (SQLiteException ex) {

			Actions.showToast(ex.getMessage(), Toast.LENGTH_LONG);

			return null;
		}

		return getDiaryListFromCursor(cursor);
	}

	public int delete(DiaryPOJO diary) {

		return db.delete("_content", "_id = ?",
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
