package com.ustbeatclub.net;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.ustbeatclub.local.DataManager;
import com.ustbeatclub.local.FileIO;
import com.ustbeatclub.local.Food;

public class FoodGeter implements Runnable {
	private Handler handler;
	private ArrayList<Food> foods;
	private DataManager dataManager;

	public FoodGeter(Handler handler)
	{
		this.handler = handler;
		foods = new ArrayList<Food>();
		dataManager = DataManager.INSTANCE;
		dataManager.setFoods(foods);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();

		try {
			getFoods();
			msg.what = 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			dataManager.setFoods(new ArrayList<Food>());
			msg.what = 0;
		}
		handler.sendMessage(msg);
	}
	private void getFoods() throws IOException
	{
		ArrayList<int[]> list;
		int sortPos = dataManager.getSortPos();

		if (sortPos < 0)
		{
			list = dataManager.getPrefers();
			for (int[] pos: list)
			{
				JSONObject result = BaseGeter.getJSONObjectResult(
						new StringBuilder("food?id=").append(pos[0]).toString());
				if (result==null) return;

				addFood(pos, result);
			}
		}
		else
		{
			list = dataManager.getSorts().get(sortPos).getPosList();
			for (int[] pos: list)
			{
				JSONArray result = BaseGeter.getJSONArrayResult(
						new StringBuilder("food?category=").append(pos[0]).toString());
				if (result==null) return;

				for(int i = 0; i < result.length(); i++)
				{
					JSONObject jO= result.optJSONObject(i);
					addFood(pos, jO);
				}
			}
		}

		dataManager.setFoods(foods);
	}
	private void addFood(int[] pos, JSONObject jO) throws IOException
	{
		int time = 0;
		time |= jO.optInt("breakfast", 0)==1 ? 1 : 0;
		time |= jO.optInt("lunch", 0)==1 ? 2 : 0;
		time |= jO.optInt("dinner", 0)==1 ? 4 : 0;
		time |= jO.optInt("supper", 0)==1 ? 8 : 0;

		JSONArray priceJA = jO.optJSONArray("prices");
		int length = priceJA.length();
		String[] priceStrs = new String[length];
		for (int j = 0; j < length; j++)
		{
			JSONObject priceJO = priceJA.optJSONObject(j);
			priceStrs[j] = new StringBuilder()
					.append(priceJO.optDouble("number", 0)).append("0")
					.append(priceJO.optString("unit", null)).toString();
		}
		int id = jO.optInt("id", 0);
		FileIO fileIO = FileIO.imageFileIO(new StringBuilder("image/food?id=")
				.append(id).append("_extral").toString());
		String md5 = jO.optString("img_md", null);
		fileIO.writeFile(md5.getBytes());

		foods.add(new Food(id, new int[]{pos[1], pos[2], jO.optInt("windowno")},
				jO.optString("food", null), time,
				(float)priceJA.optJSONObject(0).optDouble("number", 0), priceStrs,
				jO.optInt("popular", 0), jO.optInt("nutrition", 0), md5));
	}
}