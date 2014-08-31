package cn.hartech.jdiarys.engine.search;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.hartech.jdiarys.All;
import cn.hartech.jdiarys.pojo.DiaryPOJO;

/**
 * 日记搜索引擎
 * 
 * @author Jeff.Z
 * @date 2014年8月30日
 */
public class SearchEngine {

	/**
	 * 这里是统一入口，主要是做分发
	 * 
	 * 下面同时也是判断顺序：
	 * 
	 * 1, 支持SQL搜索模式，可以输入SQL中的where子句，如:
	 * 		"s content='abc' and hour=4"
	 * 		"s length(content)>100"
	 * 
	 * 2, 日期搜索模式：
	 * 		输入 "20120506" 或  "201205" 或  "2012" 返回该时间段的日记
	 * 
	 * 3, 支持多关键字或集搜索：
	 * 		如 "令狐冲 或  笑傲江湖"
	 * 
	 * 4, 支持多关键字并集搜索：
	 * 		如 "巴菲特 经济"
	 * 
	 * @author Jeff
	 * @date 2014年8月30日
	 * @param text
	 * @return
	 */
	public static List<DiaryPOJO> search(String text) {

		if (text == null || text.trim().equals("")) {
			return null;
		}
		text = text.trim();

		// SQL搜索模式
		if (text.startsWith("s ")) {

			return searchWithSQL(text);

		}

		// 日期搜索模式
		else if (isDateSearchMode(text)) {

			return searchWithDate(text);
		}

		// 多关键字"或"模式
		else if (text.contains(" 或 ") || text.contains(" or ")) {

			return searchWithMutiORWords(text);
		}

		// 多关键字"并"模式
		else {

			return searchWithMutiANDWords(text);
		}

	}

	/**
	 * 支持多关键字或集搜索：
	 * 		如 "令狐冲 或  笑傲江湖"
	 * 
	 * @author Jeff.Z
	 * @date 2014年8月31日
	 * @param text
	 * @return
	 */
	private static List<DiaryPOJO> searchWithMutiORWords(String words) {

		words = words.replace(" 或 ", " ");
		words = words.replace(" or ", " ");

		// 取出用空格隔开的多个关键字
		String[] wordsArray = StringUtils.split(words);

		String condition = "";

		for (int i = 0; i < wordsArray.length; i++) {

			if (i == 0) {
				condition = " content like ? ";
			} else {
				condition += " or content like ? ";
			}

			wordsArray[i] = "%" + wordsArray[i] + "%";
		}

		String sql = "select * from diary_content where ( " + condition
				+ " ) and is_delete = 0 order by diary_time desc";

		List<DiaryPOJO> result = All.diaryDAO.getListWithSQL(sql, wordsArray);

		return result;
	}

	/**
	 * 
	 * 日期搜索模式：
	 * 	输入 "20120506" 或  "201205" 或  "2012" 返回该时间段的日记
	 * 
	 * 
	 * @author Jeff.Z
	 * @date 2014年8月30日
	 * @param text
	 * @return
	 */
	private static List<DiaryPOJO> searchWithDate(String text) {

		List<DiaryPOJO> result = null;

		// 经过前面的检查，这里进来的肯定是长度为4|6|8的数字

		if (text.length() == 4) {

			String sql = "select * from diary_content "
					+ " where diary_year = " + text
					+ " and is_delete = 0 order by diary_time ";

			result = All.diaryDAO.getListWithSQL(sql, null);

		} else if (text.length() == 6) {

			String sql = "select * from diary_content "
					+ " where diary_year = " + text.substring(0, 4)
					+ " and diary_month = " + text.substring(4, 6)
					+ " and is_delete = 0 order by diary_time ";

			result = All.diaryDAO.getListWithSQL(sql, null);

		} else if (text.length() == 8) {

			String sql = "select * from diary_content "
					+ " where diary_year = " + text.substring(0, 4)
					+ " and diary_month = " + text.substring(4, 6)
					+ " and diary_day = " + text.substring(6, 8)
					+ " and is_delete = 0 order by diary_time ";

			result = All.diaryDAO.getListWithSQL(sql, null);

		}

		return result;
	}

	/**
	 * 判断是否是日期搜索模式
	 * 
	 * 日期搜索模式：
	 * 	输入 "20120506" 或  "201205" 或  "2012" 返回该时间段的日记
	 * 
	 * @author Jeff.Z
	 * @date 2014年8月30日
	 * @param text
	 * @return
	 */
	private static boolean isDateSearchMode(String text) {

		if (!(text.length() == 4 || text.length() == 6 || text.length() == 8)) {
			return false;
		}

		if (!(text.startsWith("20") || text.startsWith("19"))) {
			return false;
		}

		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 支持多关键字搜索：如 "巴菲特 经济"
	 * 
	 * @param words
	 * @return
	 */
	private static List<DiaryPOJO> searchWithMutiANDWords(String words) {

		// 取出用空格隔开的多个关键字
		String[] wordsArray = StringUtils.split(words);

		String condition = "";

		for (int i = 0; i < wordsArray.length; i++) {

			if (i == 0) {
				condition = " content like ?";
			} else {
				condition += " and content like ?";
			}

			wordsArray[i] = "%" + wordsArray[i] + "%";
		}

		String sql = "select * from diary_content where " + condition
				+ " and is_delete = 0 order by diary_time desc";

		List<DiaryPOJO> result = All.diaryDAO.getListWithSQL(sql, wordsArray);

		return result;
	}

	/**
	 * 
	 * 支持SQL搜索模式，可以输入SQL中的where子句，如:
	 * 		"s content='abc' and hour=4"
	 * 		"s length(content)>100"
	 * 
	 * @param substring
	 * @return
	 */
	private static List<DiaryPOJO> searchWithSQL(String text) {

		// 去掉前面的s
		String where = text.substring(1);

		String sql = "select * from diary_content where " + where
				+ " and is_delete = 0  order by diary_time desc";

		List<DiaryPOJO> result = All.diaryDAO.getListWithSQL(sql, null);

		return result;
	}
}
