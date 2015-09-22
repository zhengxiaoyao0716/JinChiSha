package com.ustbeatclub.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;

public abstract class BaseHandler extends Handler
{
	Activity activity;
	ProgressDialog progressDialog;
	public BaseHandler(Activity activity)
	{
		this(activity, true);
	}
	public BaseHandler(Activity activity, boolean isShowProgress)
	{
		this.activity = activity;
		progressDialog = null;
		if (isShowProgress)
		{
			//加载效果
			progressDialog = new ProgressDialog(activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("请稍候...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
	}
	@Override
	public void handleMessage(Message msg)
	{
		super.handleMessage(msg);

		if (progressDialog!=null) progressDialog.dismiss();
		doInHandMsgMethod(activity, msg);
	};
	public abstract void doInHandMsgMethod(Activity activity, Message msg);
}