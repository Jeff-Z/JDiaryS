package cn.hartech.jdiarys;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import cn.hartech.jdiarys.dao.DiaryDAO;
import cn.hartech.jdiarys.utils.Constant;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

	public ViewPager viewPager;
	public SlidingMenu slidingMenu;
	private ImageView imageViewSearch;

	// 屏幕改变方向时也会跑一次该方法
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 添加启动的动画
		overridePendingTransition(R.anim.slide_to_left, R.anim.slide_to_right);

		All.mainActivity = this;
		All.diaryDAO = new DiaryDAO(this);

		// 初始化左侧滑菜单
		initLeftMenu();
		// 初始化ViewPager
		initPageView();
		initViews();

		setContentView(viewPager);

		getWindowSize();

	}

	// 获取App显示的高度、宽度
	private void getWindowSize() {

		DisplayMetrics dm = new DisplayMetrics();

		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Constant.WINDOW_WIDTH = dm.widthPixels;

		Constant.WINDOW_HEIGHT = dm.heightPixels;

	}

	// 初始化各个界面控件的绑定
	private void initViews() {

		All.editTextSearchInput = (EditText) findViewById(R.id.editText_SearchInput);
		imageViewSearch = (ImageView) findViewById(R.id.imageView_search);

		// 处理按下回车键后的事件
		All.editTextSearchInput.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (KeyEvent.KEYCODE_ENTER == keyCode
						&& event.getAction() == KeyEvent.ACTION_DOWN) {

					Actions.onSearch(All.editTextSearchInput.getText()
							.toString());

					return true;
				}
				return false;
			}
		});

	}

	// 初始化左边的滑动菜单
	private void initLeftMenu() {

		slidingMenu = getSlidingMenu();

		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);

		// 全屏监听菜单滑动事件
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// 菜单上进行左滑事件会使得与菜单上的按钮onclick失效
		//		slidingMenu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// 检测到菜单关闭时触发
		slidingMenu.setOnCloseListener(new OnCloseListener() {
			@Override
			public void onClose() {

				Actions.onMenuClose();
			}
		});

		// 检测到菜单打开时触发
		slidingMenu.setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {

				Actions.onMenuOpen();
			}
		});

		setBehindContentView(R.layout.layout_menu);
	}

	private void initPageView() {

		viewPager = new ViewPager(this);
		viewPager.setId("VP".hashCode());

		viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

		viewPager.setOnPageChangeListener(new ViewPagerOnPageChangeListener());

		viewPager.setCurrentItem(0);

	}

	private final class ViewPagerOnPageChangeListener implements
			OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {

			switch (position) {

			case 0:

				getSlidingMenu().setTouchModeAbove(
						SlidingMenu.TOUCHMODE_FULLSCREEN);

				Actions.whenEnterListPage();

				break;

			case 1:

				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);

				Actions.whenEnterDiaryPage();

				break;

			default:

				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				break;
			}
		}
	}

	public class ViewPagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> mFragments;

		public ViewPagerAdapter(FragmentManager fm) {

			super(fm);

			mFragments = new ArrayList<Fragment>();

			// 在这里添加子页面
			mFragments.add(new FragmentListPage());
			mFragments.add(new FragmentDiaryPage());

		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

	}

	// 页面点击Setting按钮激发事件
	public void onClickImportDiaryData(View view) {

		// 2014年9月7日 正式启用JDiaryS
		// 把数据导入功能关闭
		if (Constant.IS_TEST_ENVIRONMENT) {
			Actions.onImportDiaryData(view);
		}
	}

	// 菜单栏上 点击后出现搜索框
	public void showSearchInput(View view) {

		toggleSearchInput();

		// 显示软键盘
		showSoftInputForSearch();
	}

	// 为搜索框打开软键盘
	public void showSoftInputForSearch() {

		All.editTextSearchInput.requestFocus();

		InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		input.showSoftInput(All.editTextSearchInput, 0);
	}

	// 为搜索框关闭软键盘
	public void hideSoftInputForSearch() {

		InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		input.hideSoftInputFromWindow(All.editTextSearchInput.getWindowToken(),
				0);
	}

	// 日记详情页面的编辑按钮
	public void onClickEditDiary(View view) {

		Actions.onClickEnableEditDiary();
	}

	// 日记详情页面的删除按钮
	public void onClickDeleteDiary(View view) {

		Actions.onClickDeleteDiary();
	}

	// 日记详情页面的保存按钮
	public void onClickSaveDiary(View view) {

		Actions.onClickSaveDiary();
	}

	// 打开或关闭搜索框
	public void toggleSearchInput() {

		if (imageViewSearch.getVisibility() == View.GONE) {

			imageViewSearch.setVisibility(View.VISIBLE);
			All.editTextSearchInput.setVisibility(View.GONE);

		} else {

			imageViewSearch.setVisibility(View.GONE);
			All.editTextSearchInput.setVisibility(View.VISIBLE);

		}
	}

	public void onClickWriteDiary(View view) {

		Actions.onClickWriteDiary();
	}

	public void onClickOpenCalander(View view) {

		Intent intent = new Intent(this, YearActivity.class);

		Bundle bundle = new Bundle();
		bundle.putLong("Params", 1234);
		intent.putExtras(bundle);

		startActivity(intent);

	}

	public void onClickOpenFavorList(View view) {

		Actions.onClickOpenFavorList();
	}

	private long mExitTime;

	// 按两次后退退出程序
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// 处理后退按钮
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			// 在显示菜单时，去掉菜单
			if (getSlidingMenu().isMenuShowing()) {
				getSlidingMenu().showContent();
				return true;
			}

			// 在首页时，处理退出
			if (viewPager.getCurrentItem() == 0) {

				if ((System.currentTimeMillis() - mExitTime) > 2000) {

					Actions.showToast("你再按一次我就滚！");
					mExitTime = System.currentTimeMillis();

				} else {
					finish();
				}

				return true;

			}

			// 不在首页时，切回首页
			viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		super.finish();

		// 添加退出的动画
		overridePendingTransition(R.anim.slide_to_left, R.anim.slide_to_right);
	}

	@Override
	protected void onDestroy() {

		if (All.diaryDAO != null) {
			All.diaryDAO.close();
		}
		super.onDestroy();
	}

}
