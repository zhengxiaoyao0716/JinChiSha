package com.ustbeatclub.net;

import java.io.IOException;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

public class ReviewCutGeter implements Runnable {
	private Handler handler;
	private int id;

	public ReviewCutGeter(Handler handler, int id)
	{
		this.handler = handler;
		this.id = id;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();

		try {
			JSONObject result = BaseGeter.getJSONObject(
					new StringBuilder("comment/delete?id=")
							.append(id).toString());
			msg.obj = result;
			msg.what = 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			msg.what = 0;
		}
		handler.sendMessage(msg);
	}

}
