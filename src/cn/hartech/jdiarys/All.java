package cn.hartech.jdiarys;

import android.widget.EditText;
import cn.hartech.jdiarys.dao.DiaryDAO;
import cn.hartech.jdiarys.ui.AdapterForListPage;

/**
 * 界面上每个元素初始化后都注册到本类中，供全局使用
 * 
 * @author Jeff.Z
 * @date 2014-6-9
 *
 */
public class All {

	public static MainActivity mainActivity;

	public static DiaryDAO diaryDAO;

	public static FragmentDiaryPage diaryPage;

	public static FragmentListPage listPage;

	public static EditText editTextSearchInput;

	public static AdapterForListPage adapterListPageData;

}
