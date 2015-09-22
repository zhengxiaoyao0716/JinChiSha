package com.ustbeatclub.fragment;

import java.util.ArrayList;

import com.ustbeatclub.adapter.FoodListAdapter;
import com.ustbeatclub.jinchisha.R;
import com.ustbeatclub.local.DataManager;
import com.ustbeatclub.net.BaseHandler;
import com.ustbeatclub.net.FoodGeter;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
public class FoodListFrag extends ListFragment{
	FoodListAdapter foodListAdapter;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.frag_food_list, container, false);
		return  view;
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		refreshList();
	}

	public void onListItemClick(ListView parent, View v, int position, long id)
	{
		DataManager.INSTANCE.setFoodPos(position);
		refreshList(getActivity(), false);
	}

	//刷新列表
	private void refreshList()
	{
		// TODO Auto-generated method stub
		new Thread(new FoodGeter(new BaseHandler(getActivity())
		{
			@Override
			public void doInHandMsgMethod(Activity activity, Message msg) {
				// TODO Auto-generated method stub

				if (msg.what==1) refreshList(activity, true);
				else
					Toast.makeText(activity, "加载数据失败", Toast.LENGTH_SHORT).show();
			}
		})).start();
	}
	public void refreshList (Activity activity, boolean isRebuildData)
	{
		if (isRebuildData)
		{
			ArrayList<String> foods =
					DataManager.INSTANCE.getFoodListData();
			if (foods==null) return;

			foodListAdapter = new FoodListAdapter(activity,
					R.layout.item_food_list, foods);
			setListAdapter(foodListAdapter);
		}
		else foodListAdapter.notifyDataSetChanged();
		FoodDetailFrag foodDF = (FoodDetailFrag)getFragmentManager().findFragmentById(R.id.foodDetailFrag);
		foodDF.refreshDetail();
	}
}