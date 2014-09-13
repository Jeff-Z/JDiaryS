package cn.hartech.jdiarys;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.hartech.jdiarys.pojo.DiaryPOJO;
import cn.hartech.jdiarys.ui.UIUtility;
import cn.hartech.jdiarys.utils.Constant;

public class FragmentDiaryPage extends Fragment {

	private TextView textViewYear;
	private TextView textViewMonth;
	public TextView textViewContent;
	public EditText editTextContent;
	private ImageView imageViewFavor;
	private ScrollView scrollView;
	private LinearLayout layoutEditPanel;
	private Button buttonEdit, buttonDelete, buttonSave;

	// 当前对应的Diary数据对象
	public DiaryPOJO diaryPOJO;

	public PageState pageState = PageState.ADDING;

	// 当前本页面的状态：显示最新内容、
	public enum PageState {

		// 显示日志
		SHOWING,

		// 编辑日记
		EDITING,

		// 添加日记
		ADDING
	}

	// 屏幕改变方向时也会跑一次该方法
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setRetainInstance(true);

		All.diaryPage = this;

		LinearLayout layout = new LinearLayout(this.getActivity());
		layout.setBackgroundColor(Color.WHITE);

		layout.setLayoutParams(makeOneLayoutParam());

		initViews(inflater, layout);

		setMode(pageState);

		return layout;

	}

	private void initViews(LayoutInflater inflater, LinearLayout layout) {

		scrollView = new ScrollView(getActivity());

		scrollView.setLayoutParams(makeOneLayoutParam());

		layout.addView(scrollView);

		View view = inflater.inflate(R.layout.view_one_diary, scrollView);

		textViewYear = (TextView) view
				.findViewById(R.id.view_content_title_year);
		textViewMonth = (TextView) view
				.findViewById(R.id.view_content_title_month);

		textViewContent = (TextView) view.findViewById(R.id.view_content_body);
		textViewContent.setOnClickListener(new DoubleClickListener());
		textViewContent.setOnLongClickListener(new MyLongClickListener());

		editTextContent = (EditText) view
				.findViewById(R.id.view_content_body_edit);
		imageViewFavor = (ImageView) view
				.findViewById(R.id.imageView_favor_icon);

		layoutEditPanel = (LinearLayout) view
				.findViewById(R.id.view_edit_panel);

		buttonEdit = (Button) view.findViewById(R.id.view_button_edit);
		buttonDelete = (Button) view.findViewById(R.id.view_button_delete);
		buttonSave = (Button) view.findViewById(R.id.view_button_save);

		// 设置内容区域的最小高度为屏幕高度，这样下面的按钮区域就始终在屏幕底部以下
		RelativeLayout contentPanel = (RelativeLayout) view
				.findViewById(R.id.view_content_panel);
		contentPanel.setMinimumHeight(Constant.WINDOW_HEIGHT);

		// 设置编辑、删除按钮各为屏幕宽度的一半
		buttonEdit.setWidth(Constant.WINDOW_WIDTH / 2);

	}

	// 在编辑时被推到系统后台
	@Override
	public void onPause() {

		Actions.whenLeaveApp();

		super.onPause();
	}

	/**
	 * 自动保存日志内容，不管是编辑模式，还是添加模式
	 * 
	 *  如果有做保存，返回true，否则返回false
	 * 
	 * @return
	 */
	public boolean addOrUpdateDiary() {

		String newText = editTextContent.getText().toString();

		if (pageState == PageState.ADDING) {

			if (newText == null || newText.trim().equals("")) {
				return false;
			}

			diaryPOJO.content = newText;
			textViewContent.setText(newText);

			diaryPOJO._id = All.diaryDAO.add(diaryPOJO);
			All.adapterListPageData.add(0, diaryPOJO);

			All.listPage.scrollToTop();

			pageState = PageState.EDITING;

			Actions.showToast("添加完毕！");

			return true;

		} else if (pageState == PageState.EDITING) {

			if (newText == null || newText.equals(diaryPOJO.content)) {

				return false;
			}

			diaryPOJO.content = newText;
			textViewContent.setText(newText);

			All.diaryDAO.update(diaryPOJO);

			All.adapterListPageData.notifyDataSetChanged();

			Actions.showToast("保存好啦~");

			return true;
		}

		return false;
	}

	/**
	 * 设置当前页面状态是显示日记、添加 还是 编辑
	 * 
	 * @param pageState
	 */
	public void setMode(PageState pageState) {

		switch (pageState) {

		case ADDING:

			this.pageState = PageState.ADDING;

			diaryPOJO = DiaryPOJO.createDiaryPOJO();
			showDiaryData();

			showEditor(true);

			break;

		case SHOWING:

			this.pageState = PageState.SHOWING;

			showDiaryData();

			showEditor(false);

			scrollView.setScrollY(0);

			break;

		case EDITING:

			this.pageState = PageState.EDITING;

			showEditor(true);

			break;

		default:
			break;
		}

	}

	/**
	 * 设置是否为编辑器模式
	 * 
	 * @param show
	 */
	public void showEditor(boolean show) {

		if (show) {

			// 编辑模式
			textViewContent.setVisibility(View.GONE);
			editTextContent.setVisibility(View.VISIBLE);

			buttonEdit.setVisibility(View.GONE);
			buttonDelete.setVisibility(View.GONE);
			buttonSave.setVisibility(View.VISIBLE);

			textViewYear.setTextColor(0xFFC0EBB8);
			textViewMonth.setTextColor(0xFFC0EBB8);

		} else {

			// 显示模式
			textViewContent.setVisibility(View.VISIBLE);
			editTextContent.setVisibility(View.GONE);

			buttonEdit.setVisibility(View.VISIBLE);
			buttonDelete.setVisibility(View.VISIBLE);
			buttonSave.setVisibility(View.GONE);

			textViewYear.setTextColor(0xFFEBEBEB);
			textViewMonth.setTextColor(0xFFF2F2F2);
		}
	}

	public void showDiaryData() {

		if (diaryPOJO == null) {
			return;
		}

		textViewYear.setText(diaryPOJO.diaryYear + "年");
		textViewMonth.setText(diaryPOJO.diaryMonth + "月" + diaryPOJO.diaryDay
				+ "日");
		textViewContent.setText(diaryPOJO.content);
		editTextContent.setText(diaryPOJO.content);

		UIUtility.setFavor(imageViewFavor, textViewContent, diaryPOJO.isFavor);

	}

	// 为编辑框打开软键盘
	public void showSoftInputForEditor() {

		editTextContent.requestFocus();

		InputMethodManager input = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		input.showSoftInput(editTextContent, 0);
	}

	// 为编辑框关闭软键盘
	public void hideSoftInputForEditor() {

		InputMethodManager input = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		input.hideSoftInputFromWindow(editTextContent.getWindowToken(), 0);
	}

	private LayoutParams makeOneLayoutParam() {

		return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
	}

	/**
	 * 处理双击事件
	 * 
	 * @author jin.zheng
	 * @date 2014年6月12日
	 *
	 */
	private final class DoubleClickListener implements OnClickListener {

		// 双击事件记录最近一次点击的时间
		private long lastClickTime;

		@Override
		public void onClick(View v) {

			// 如果是双击,1秒内连续点击判断为双击
			if ((System.currentTimeMillis() - lastClickTime) < Constant.DOUBLE_CLICK_INTERVAL) {

				lastClickTime = 0;

				Actions.onClickEnableEditDiary();

			} else {
				lastClickTime = System.currentTimeMillis();
			}

		}
	}

	/**
	 * 处理用户长按文字内容时，自动复制到剪贴板
	 * 
	 * @author Jeff.Z
	 * @date 2014年8月30日
	 */
	private final class MyLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {

			String content = ((TextView) v).getText().toString();

			Actions.onLongClickDiaryContent(content);

			return true;
		}
	}

}
