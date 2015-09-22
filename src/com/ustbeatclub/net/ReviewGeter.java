package com.ustbeatclub.net;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.ustbeatclub.local.DataManager;
import com.ustbeatclub.local.Food;

public class ReviewGeter implements Runnable {
	private Handler handler;
	private DataManager dataManager;

	public ReviewGeter(Handler handler)
	{
		this.handler = handler;
		dataManager = DataManager.INSTANCE;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();

		try {
			getReview();
			msg.what = 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			dataManager.setFoods(new ArrayList<Food>());
			msg.what = 0;
		}
		handler.sendMessage(msg);
	}
	private void getReview() throws IOException{
		// TODO Auto-generated method stub

		JSONArray result = BaseGeter.getJSONArrayResult(
				new StringBuilder("comment/get?food=")
						.append(dataManager.getFoodId()).toString());
		if (result==null) return;

		for (int i = 0; i < result.length(); i++)
		{
			JSONObject jO = result.optJSONObject(i);
			dataManager.addReview(jO.optInt("id", 0),
					jO.optString("content", null), jO.optInt("time", 0));
		}
	}
}