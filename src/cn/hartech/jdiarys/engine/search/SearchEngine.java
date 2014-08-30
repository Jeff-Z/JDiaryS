package cn.hartech.jdiarys.engine.search;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.hartech.jdiarys.All;
import cn.hartech.jdiarys.pojo.DiaryPOJO;

/**
 * 本APP的搜索入口
 * 
 * @author Jeff.Z
 * @date 2014年8月30日
 */
public class SearchEngine {

	/**
	 * 这里是统一入口，主要是做分发
	 * 
	 * 1, 支持多关键字搜索：如 "巴菲特 经济"
	 * 
	 * 2, 支持SQL搜索模式，可以输入SQL中的where子句，如:
	 * 		"s content='abc' and hour=4"
	 * 		"s length(content)>100"
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

		// 多关键字模式
		else {

			return searchWithMutiWords(text);
		}

	}

	/**
	 * 支持多关键字搜索：如 "巴菲特 经济"
	 * 
	 * @param words
	 * @return
	 */
	private static List<DiaryPOJO> searchWithMutiWords(String words) {

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
				+ " order by diary_time desc";

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
				+ " order by diary_time desc";

		List<DiaryPOJO> result = All.diaryDAO.getListWithSQL(sql, null);

		return result;
	}
}
