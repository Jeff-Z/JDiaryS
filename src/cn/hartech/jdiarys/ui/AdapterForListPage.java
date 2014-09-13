package cn.hartech.jdiarys.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.hartech.jdiarys.R;
import cn.hartech.jdiarys.pojo.DiaryPOJO;

import com.nhaarman.listviewanimations.ArrayAdapter;

public class AdapterForListPage extends ArrayAdapter<DiaryPOJO> {

	private LayoutInflater inflater;

	public AdapterForListPage(Context context, List<DiaryPOJO> items) {

		// 注入数据对象
		super(items);
		this.inflater = LayoutInflater.from(context);
	}

	/**
	 * 在这里返回你要展示的条目的View
	 * 
	 * position: 将要展示的位置
	 * convertView：这个是之前这里创建的，已经滚出屏幕范围的view对象，android会希望我们重用它，
	 * 				我们仅需改变里面的值，这样就不用重建对象耗时间内存了。（当然使用前先判断null，是否之前创建过的）。
	 * parent：即将加载这个View的父view
	 */
	@Override
	public View getView(int position, View view, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if (view == null) {

			view = inflater.inflate(R.layout.view_list_diary, null, false);

			viewHolder = new ViewHolder();

			viewHolder.textViewTitleYear = (TextView) view
					.findViewById(R.id.view_content_title_year);
			viewHolder.textViewTitleMonth = (TextView) view
					.findViewById(R.id.view_content_title_month);
			viewHolder.textViewBody = (TextView) view
					.findViewById(R.id.view_content_body);
			viewHolder.imageViewFavor = (ImageView) view
					.findViewById(R.id.imageView_favor_icon);

			view.setTag(viewHolder);

		} else {

			viewHolder = (ViewHolder) view.getTag();
		}

		DiaryPOJO diary = getItem(position);

		viewHolder.textViewTitleYear.setText(diary.diaryYear + "年");

		viewHolder.textViewTitleMonth.setText(diary.diaryMonth + "月"
				+ diary.diaryDay + "日");

		viewHolder.textViewBody.setText(diary.content);

		UIUtility.setFavor(viewHolder, diary.isFavor);

		return view;

	}

	/**
	 * 这里使用一个ViewHolder对象来保存这个View每次需要改变内容的元素
	 * 
	 * 这样就不用每次初始化View是都findViewById一遍了。
	 * 
	 * @author Jeff.Z
	 * @date 2014年9月14日
	 */
	public static class ViewHolder {
		TextView textViewTitleYear;
		TextView textViewTitleMonth;
		TextView textViewBody;
		ImageView imageViewFavor;
	}
}