package cn.hartech.jdiarys;

import java.util.List;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import cn.hartech.jdiarys.FragmentListPage.PageState;
import cn.hartech.jdiarys.engine.importdiary.ImportOrangeDiary;
import cn.hartech.jdiarys.engine.search.SearchEngine;
import cn.hartech.jdiarys.pojo.DiaryPOJO;
import cn.hartech.jdiarys.ui.MyDialogs;

/**
 * 把界面所有**用户交互**的动作都拎出来，这里作为Controller
 * 用户所有点击都在这里作为出发点
 * 
 * 这里的方法都是静态方法
 * 
 * @author jin.zheng
 * @date 2014年6月10日
 *
 */
public class Actions {

	// 页面跳出提示信息的统一入口
	public static void showToast(String text) {

		showToast(text, Toast.LENGTH_SHORT);
	}

	public static void showToast(String text, int time) {

		Toast.makeText(All.mainActivity, text, time).show();
	}

	// 用户搜索点击回车后
	public static void onSearch(String text) {

		List<DiaryPOJO> diaryList = SearchEngine.search(text);

		if (diaryList == null) {
			return;
		} else if (diaryList.size() == 0) {
			showToast("搜索不到内容");
			return;
		}

		showToast("共搜索到" + diaryList.size() + "条记录。");

		All.listPage.pageState = PageState.SEARCH_RESULT;

		All.listPage.loadDataForPage(diaryList);

		All.listPage.scrollToTop();

		All.mainActivity.showContent();

	}

	// 用户点击导入Orange Diary数据时
	public static void onImportDiaryData(View view) {

		OnClickListener action = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ImportOrangeDiary.startImport(All.mainActivity, All.diaryDAO);
				dialog.dismiss();
			}
		};

		MyDialogs.makeConfirmDialog(All.mainActivity, "确定使用OrangeDiary覆盖现有数据？",
				action);

	}

	// 用户在列表页面双击某一个日记记录时触发
	public static void onDiaryDoubleClick(int position) {

		DiaryPOJO diary = All.adapterListPageData.getItem(position);

		All.diaryPage.diaryPOJO = diary;
		All.diaryPage.listViewPosition = position;

		All.diaryPage.setMode(FragmentDiaryPage.PageState.SHOWING);

		All.mainActivity.viewPager.setCurrentItem(1);

	}

	// 用户点击编辑按钮
	public static void onClickEnableEditDiary() {

		All.diaryPage.setMode(FragmentDiaryPage.PageState.EDITING);
		All.diaryPage.requestFocusForEditor();

	}

	// 在编辑时被推到系统后台
	public static void whenLeaveApp() {

		All.diaryPage.addOrUpdateDiary();

	}

	// 用户点击删除日记
	public static void onClickDeleteDiary() {

		OnClickListener action = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				All.diaryDAO.delete(All.diaryPage.diaryPOJO);

				All.adapterListPageData.remove(All.diaryPage.listViewPosition);

				All.mainActivity.viewPager.setCurrentItem(0);

				All.diaryPage.setMode(FragmentDiaryPage.PageState.ADDING);

				dialog.dismiss();
			}
		};

		MyDialogs.makeConfirmDialog(All.mainActivity, "确定删除该日记？", action);
	}

	// 当进入列表页面时触发
	public static void whenEnterListPage() {

		InputMethodManager imm = (InputMethodManager) All.mainActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				All.diaryPage.editTextContent.getWindowToken(), 0);

		All.diaryPage.addOrUpdateDiary();

	}

	// 当进入日记详情页面时触发
	public static void whenEnterDiaryPage() {

		// 第一次打开APP时，切入详情页时，调起键盘
		if (All.diaryPage.pageState == FragmentDiaryPage.PageState.ADDING) {
			All.diaryPage.requestFocusForEditor();
		}
	}

	// 用户点击菜单上的写日记按钮时
	public static void onClickWriteDiary() {

		All.mainActivity.slidingMenu.showContent();

		All.diaryPage.setMode(FragmentDiaryPage.PageState.ADDING);

		All.mainActivity.viewPager.setCurrentItem(1);

		All.diaryPage.requestFocusForEditor();

	}

	// 用户编辑后，点击保存按钮
	public static void onClickSaveDiary() {

		if (All.diaryPage.pageState == FragmentDiaryPage.PageState.ADDING) {

			if (All.diaryPage.addOrUpdateDiary()) {
				All.diaryPage.setMode(FragmentDiaryPage.PageState.SHOWING);
			} else {
				Actions.showToast("什么都没写就想保存？");
			}

		} else if (All.diaryPage.pageState == FragmentDiaryPage.PageState.EDITING) {

			All.diaryPage.addOrUpdateDiary();
			All.diaryPage.setMode(FragmentDiaryPage.PageState.SHOWING);
		}
	}

	// 用户长按日记详情 - 复制到剪贴板
	public static void onLongClickDiaryContent(String content) {

		ClipboardManager clipboard = (ClipboardManager) All.mainActivity
				.getSystemService(Context.CLIPBOARD_SERVICE);

		ClipData clipData = ClipData.newPlainText(
				"Copy Text Content From JDiaryS", content);
		clipboard.setPrimaryClip(clipData);

		Actions.showToast("复制好啦~~");

	}
}
