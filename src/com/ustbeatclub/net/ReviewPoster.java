package com.ustbeatclub.net;

import java.io.IOException;

import org.json.JSONObject;

import com.ustbeatclub.local.DataManager;

import android.os.Handler;
import android.os.Message;

public class ReviewPoster implements Runnable {
	private Handler handler;
	private String cmd;
	private String review;
	private int score;

	public ReviewPoster(Handler handler, int id, String review, int score)
	{
		this.handler = handler;
		this.cmd = new StringBuilder("comment/update?id=")
				.append(id).toString();
		this.review = review;
		this.score = score;
	}
	public ReviewPoster(Handler handler, String review, int score)
	{
		this.handler = handler;
		this.cmd = new StringBuilder("comment/add?food=")
				.append(DataManager.INSTANCE.getFoodId()).toString();
		this.review = review;
		this.score = score;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();

		try {
			JSONObject result = BasePoster.getJSONObject(cmd, review, score);
			msg.obj = result;
			msg.what = 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			msg.what = 0;
		}
		handler.sendMessage(msg);
	}
}