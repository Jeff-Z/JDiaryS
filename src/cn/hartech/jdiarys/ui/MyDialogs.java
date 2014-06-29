package cn.hartech.jdiarys.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import cn.hartech.jdiarys.utils.Constant;

public class MyDialogs {

	/**
	 * 发起一个confirm对话框
	 * 
	 * 使用方法：

		OnClickListener action = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 这里做 用户点击确定后的动作
				ImportOrangeDiary.startImport(MainActivity.this, diaryDAO);
				
				dialog.dismiss();
			}
		};
		MyDialogs.makeConfirmDialog(this, "确定使用OrangeDiary覆盖现有数据？", action);
		
	 * 
	 * @param context
	 * @param title
	 * @param action
	 */
	public static void makeConfirmDialog(Context context, String message,
			OnClickListener action) {
		AlertDialog.Builder builder = new Builder(context);

		builder.setTitle(Constant.APP_NAME);
		builder.setMessage(message);

		builder.setPositiveButton("确定", action);

		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}
}
