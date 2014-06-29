package cn.hartech.jdiarys.engine.importdiary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import cn.hartech.jdiarys.dao.DiaryDAO;
import cn.hartech.jdiarys.pojo.DiaryPOJO;
import cn.hartech.jdiarys.utils.Constant;
import cn.hartech.jdiarys.utils.MyUtility;

/**
 * 把Orange Diary最新的日志备份文件导入本数据库。
 * 
 * @author jin.zheng
 * @date 2014年6月8日
 *
 */
public class ImportOrangeDiary {

	// 用于解析CSV文件中的日期字符串
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd_HH:mm", Constant.LOCAL);

	public static void startImport(Context context, DiaryDAO diaryDAO) {

		List<DiaryPOJO> diaryList = getOrangeDiaryList(context);

		if (diaryList == null) {
			return;
		}

		diaryDAO.deleteAllRecords();

		try {

			diaryDAO.addList(diaryList);

			Toast.makeText(context, "共导入" + diaryList.size() + "条日记记录。",
					Toast.LENGTH_SHORT).show();

		} catch (Exception e) {

			Toast.makeText(context, "插入数据库失败：msg=" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}

	}

	private static List<DiaryPOJO> getOrangeDiaryList(Context context) {

		// 从OrangeDiary软件的备份目录中获取最新的备份文件
		File csvFile = getOrangeExportFile();
		if (csvFile == null) {
			return null;
		}

		String log = "检测到最新的Orange日记备份文件：\r\n"
				+ MyUtility.DF_yyyy_MM_dd_HH_mm_ss.format(csvFile
						.lastModified());
		Log.e("JDiaryS", log);
		//		Toast.makeText(context, log, Toast.LENGTH_SHORT).show();

		// 加入红黑树，这样每插入一条记录即马上按照日期排序
		Set<DiaryPOJO> daily = new TreeSet<DiaryPOJO>(new DateComparator());

		CsvReader csvReader = null;

		try {

			// 指定文件地址，CSV分隔符，文件编码
			csvReader = new CsvReader(csvFile.getAbsolutePath(), ',',
					Charset.forName("UTF-8"));

			// 跳过表头
			csvReader.readHeaders();

			while (csvReader.readRecord()) {

				// 读取每行数据以数组形式返回
				String[] str = csvReader.getValues();

				if (str != null && str.length > 0) {

					DiaryPOJO diary = new DiaryPOJO();
					diary.content = str[2];
					diary.diaryTime = dateFormat.parse(str[3]);
					diary.tags = str[4];

					diary.makeOtherDateValues();

					daily.add(diary);
				}
			}

		} catch (FileNotFoundException e) {

			Toast.makeText(context,
					"ERROR: 文件 " + csvFile.getAbsolutePath() + " 找不到。",
					Toast.LENGTH_SHORT).show();

			Log.e("JDiaryS", "解析CVS文件出错", e);
			return null;

		} catch (IOException e) {

			Toast.makeText(context, "ERROR: 解析CSV文件中出现异常", Toast.LENGTH_SHORT)
					.show();

			Log.e("JDiaryS", "解析CVS文件出错", e);
			return null;

		} catch (ParseException e) {

			Toast.makeText(context, "ERROR: 解析CSV中日期格式出现问题", Toast.LENGTH_SHORT)
					.show();

			Log.e("JDiaryS", "解析CVS文件出错", e);
			return null;

		} finally {
			if (csvReader != null) {
				csvReader.close();
			}
		}

		return new ArrayList<DiaryPOJO>(daily);
	}

	// 从OrangeDiary软件的备份目录中获取最新的备份文件
	private static File getOrangeExportFile() {

		String scardDir;
		try {
			scardDir = Environment.getExternalStorageDirectory()
					.getCanonicalPath();
		} catch (IOException e) {
			Log.e("JDiaryS", "获取SDCard卡路径时出错。", e);
			return null;
		}

		File backupDir = new File(scardDir + "/OrangeDiaryPro/export/");

		File[] files = backupDir.listFiles();

		File latestFile = files[0];

		for (File file : files) {
			if (file.lastModified() > latestFile.lastModified()) {
				latestFile = file;
			}
		}

		return latestFile;
	}
}
