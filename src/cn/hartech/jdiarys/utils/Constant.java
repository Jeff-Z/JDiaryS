package cn.hartech.jdiarys.utils;

import java.util.Locale;

public class Constant {

	// 本App的名字（出现在对话框顶部等）
	public static final String APP_NAME = "JDiaryS!";

	// 本app在SD Card上的目录位置
	public static final String APP_FOLDER = "JDiaryS";

	// 配置文件名称
	public static final String PROPERTY_FILE_NAME = "property.txt";

	// 数据库名称
	public static final String DB_FILE_NAME = "jdiary_database.db";

	// 整个工程的统一时区
	public static final Locale LOCAL = Locale.CHINA;

	// 启动时页面显示多少条数据
	public static final int MONTH_ITEM_INIT_COUNT = 50;

	// 每次自动加载时加载多少条数据
	public static final int AUTO_LOAD_DATA_COUNT = 50;

	// 双击列表时的间隔时间（毫秒）
	public static final int DOUBLE_CLICK_INTERVAL = 300;

	// App显示的屏幕高度、宽度（启动时自动获取并作为全局参数在这里）
	public static int WINDOW_HEIGHT, WINDOW_WIDTH;

}
