package cn.hartech.jdiarys;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import cn.hartech.jdiarys.pojo.DiaryPOJO;
import cn.hartech.jdiarys.ui.AdapterForListPage;
import cn.hartech.jdiarys.utils.Constant;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;

public class FragmentListPage extends Fragment {

	private ListView listView;

	public PageState pageState;

	// 当前本页面的状态：显示最新内容、
	public enum PageState {

		// 显示最新的日志
		LATEST_DIARY,

		// 显示搜索结果
		SEARCH_RESULT,

		// 显示指定月份的日志
		MONTH_DIARY
	}

	// 屏幕改变方向时也会跑一次该方法
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setRetainInstance(true);

		All.listPage = this;
		this.pageState = PageState.LATEST_DIARY;

		RelativeLayout layout = new RelativeLayout(this.getActivity());
		layout.setBackgroundColor(Color.WHITE);

		LayoutParams param = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		layout.setLayoutParams(param);

		initListView(layout);

		List<DiaryPOJO> diaryList = All.diaryDAO.getListByOffsetLimit(0,
				Constant.MONTH_ITEM_INIT_COUNT);
		loadDataForPage(diaryList);

		return layout;

	}

	private void initListView(ViewGroup layout) {

		// 即将被加载数据的ListView
		listView = new ListView(getActivity());

		LayoutParams param = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		listView.setLayoutParams(param);

		// 设置分割线
		listView.setDivider(null);

		// 添加滚动到底部自动加载监听器
		listView.setOnScrollListener(new ListViewOnScrollListener());

		// 处理日志双击事件
		listView.setOnItemClickListener(new ListViewOnItemClickListener());

		layout.addView(listView);
	}

	// 本入口为统一的更新本页面列表数据入口
	public void loadDataForPage(List<DiaryPOJO> diaryList) {

		// 准备列表的数据对象和展示样式
		All.adapterListPageData = new AdapterForListPage(getActivity(),
				diaryList);

		// 这里切换是否使用动画效果
		boolean useAnimation = true;

		if (useAnimation) {

			// 使用渐变加载动画效果类
			// 几种渐变效果：
			// AlphaInAnimationAdapter
			// SwingLeftInAnimationAdapter、SwingRightInAnimationAdapter
			// SwingBottomInAnimationAdapter
			AnimationAdapter animAdapter = new SwingRightInAnimationAdapter(
					All.adapterListPageData);
			// animAdapter.setInitialDelayMillis(500);

			// 绑定到ListView上
			animAdapter.setAbsListView(listView);
			listView.setAdapter(animAdapter);

		} else {
			listView.setAdapter(All.adapterListPageData);
		}

	}

	public void scrollToTop() {
		listView.setSelection(0);
	}

	/**
	 * 处理双击事件
	 * 
	 * @author jin.zheng
	 * @date 2014年6月12日
	 *
	 */
	private final class ListViewOnItemClickListener implements
			OnItemClickListener {

		// 双击事件记录最近一次点击的位置
		private int lastClickPosition;

		// 双击事件记录最近一次点击的时间
		private long lastClickTime;

		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {

			// 如果是双击,1秒内连续点击判断为双击
			if (pos == lastClickPosition
					&& (System.currentTimeMillis() - lastClickTime) < Constant.DOUBLE_CLICK_INTERVAL) {

				lastClickTime = 0;

				Actions.onDiaryDoubleClick(pos);

			} else {
				lastClickPosition = pos;
				lastClickTime = System.currentTimeMillis();
			}
		}
	}

	/**
	 * 处理滚动到底部后自动加载事件
	 * 
	 * @author jin.zheng
	 * @date 2014年6月12日
	 *
	 */
	private final class ListViewOnScrollListener implements OnScrollListener {

		// 当前ListView中最后一个Item的索引 
		private int lastItemIndex;

		// 停止滚动时触发
		// 并且ListView的最后一项的索引等于adapter的项数减一时则自动加载（因为索引是从0开始的）  
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& lastItemIndex == All.adapterListPageData.getCount() - 1) {

				if (All.listPage.pageState == PageState.LATEST_DIARY) {

					List<DiaryPOJO> diaryList = All.diaryDAO
							.getListByOffsetLimit(
									All.adapterListPageData.getCount(),
									Constant.AUTO_LOAD_DATA_COUNT);

					All.adapterListPageData.addAll(diaryList);
				}

			}
		}

		// 滚动过程中触发
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			lastItemIndex = firstVisibleItem + visibleItemCount - 1;
		}
	}

}
