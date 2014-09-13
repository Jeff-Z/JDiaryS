package cn.hartech.jdiarys.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.hartech.jdiarys.R;
import cn.hartech.jdiarys.ui.AdapterForListPage.ViewHolder;

public class UIUtility {

	/**
	 * 设置为是否是收藏的样式
	 * 
	 * @author Jeff.Z
	 * @date 2014年9月14日
	 * @param viewHolder
	 * @param isFavor
	 */
	public static void setFavor(ViewHolder viewHolder, boolean isFavor) {

		setFavor(viewHolder.imageViewFavor, viewHolder.textViewBody, isFavor);
	}

	/**
	 * 设置为是否是收藏的样式
	 * 
	 * @author Jeff.Z
	 * @date 2014年9月14日
	 * @param imageViewFavor
	 * @param textViewBody
	 * @param isFavor
	 */
	public static void setFavor(ImageView imageViewFavor,
			TextView textViewBody, boolean isFavor) {

		if (isFavor) {

			imageViewFavor.setVisibility(View.VISIBLE);

			setBackgroundResource(textViewBody,
					R.drawable.textview_border_favor);

		} else {

			imageViewFavor.setVisibility(View.GONE);

			setBackgroundResource(textViewBody, R.drawable.textview_border_show);
		}
	}

	/**
	 * 
	 * 由于一个View设置背景后，会把它的padding都设置为背景的padding
	 * 
	 * 这里做成设置前先把padding保存起来
	 * 
	 * @author Jeff.Z
	 * @date 2014年9月14日
	 * @param view
	 * @param resid
	 */
	private static void setBackgroundResource(View view, int resid) {

		int pl = view.getPaddingLeft();
		int pt = view.getPaddingTop();
		int pr = view.getPaddingRight();
		int pb = view.getPaddingBottom();

		view.setBackgroundResource(resid);

		view.setPadding(pl, pt, pr, pb);
	}

}
