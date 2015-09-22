package com.ustbeatclub.net;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.ustbeatclub.local.DataManager;
import com.ustbeatclub.local.FileIO;
import com.ustbeatclub.local.Sort;

public class SortGeter implements Runnable {
	private Handler handler;
	private ArrayList<Sort> sorts;
	private DataManager dataManager;

	public SortGeter(Handler handler)
	{
		this.handler = handler;
		sorts = new ArrayList<Sort>();
		dataManager = DataManager.INSTANCE;
		dataManager.setSorts(sorts);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();

		try {
			getSorts();
			msg.what = 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			dataManager.setSorts(new ArrayList<Sort>());
			msg.what = 0;
		}
		handler.sendMessage(msg);
	}

	private void getSorts() throws IOException
	{
		int rest, floor;
		rest = dataManager.getRest();
		floor = dataManager.getFloor();

		if (rest==0&&floor==0)
		{
			getSorts(1, 1);getSorts(2, 1);
			getSorts(1, 2);getSorts(2, 2);
			getSorts(1, 3);getSorts(2, 3);
		}
		else if (rest==0)
		{
			getSorts(1, floor);getSorts(2, floor);
		}
		else if (floor==0)
		{
			getSorts(rest, 1);
			getSorts(rest, 2);
			getSorts(rest, 3);
		}
		else getSorts(rest, floor);

		dataManager.setSorts(sorts);
	}
	private void getSorts(int rest, int floor) throws IOException
	{
		JSONArray result = BaseGeter.getJSONArrayResult(
				new StringBuilder("category?floor=").append(floor + 3 * (rest - 1)).toString());
		if (result==null) return;

		for(int i = 0; i < result.length(); i++)
		{
			JSONObject jO= result.optJSONObject(i);

			String category = jO.optString("category", null);
			int j;
			for (j = 0; j < sorts.size(); j++)
			{
				if (sorts.get(j).getName().equals(category))
				{
					sorts.get(j).addPos(jO.optInt("id", 0), rest, floor);
					break;
				}
			}

			ArrayList<Integer> windows = new ArrayList<Integer>();
			JSONArray jA = jO.optJSONArray("windows");
			int lenth = jA.length();
			for (int k = 0; k < lenth; k++)
				windows.add(jA.optInt(k));

			int id = jO.optInt("id", 0);
			FileIO fileIO = FileIO.imageFileIO(new StringBuilder("image/category?id=")
					.append(id).append("_extral").toString());
			String md5 = jO.optString("img_md", null);
			fileIO.writeFile(md5.getBytes());

			if (j==sorts.size()) sorts.add(new Sort(id, rest, floor,
					category, jO.optString("time", null), jO.optString("price", null),
					windows, md5));
		}
	}
}