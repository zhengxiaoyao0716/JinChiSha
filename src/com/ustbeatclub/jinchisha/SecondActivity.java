package com.ustbeatclub.jinchisha;

import com.umeng.analytics.MobclickAgent;
import com.ustbeatclub.fragment.FoodListFrag;
import com.ustbeatclub.local.DataManager;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SecondActivity extends Activity {
	DataManager dataManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);

		dataManager = DataManager.INSTANCE;

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setTitle (dataManager.getSortStr());
		actionBar.setDisplayShowHomeEnabled(false);

		int[] rankTVId = {R.id.defaultRankTV, R.id.priceRankTV, R.id.hotRankTV, R.id.timeRankTV};
		TextView rankTV = (TextView)findViewById(rankTVId[dataManager.getRank()]);
		rankTV.setBackgroundColor(getResources().getColor(R.color.light_color));
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
			{
				MobclickAgent.onKillProcess(this);
				finish();
				break;
			}
		}
		return true;
	}

	public void onRankClick(View view)
	{
		// TODO Auto-generated method stub
		int[] rankTVId = {R.id.defaultRankTV, R.id.priceRankTV, R.id.hotRankTV, R.id.timeRankTV};
		TextView rankTV = (TextView)findViewById(rankTVId[dataManager.getRank()]);
		rankTV.setBackgroundColor(getResources().getColor(R.color.bg_color));

		switch(view.getId())
		{
			case R.id.defaultRankTV: dataManager.setRank(0);break;
			case R.id.priceRankTV: dataManager.setRank(1);break;
			case R.id.hotRankTV: dataManager.setRank(2);break;
			case R.id.timeRankTV: dataManager.setRank(3);break;
		}
		dataManager.setFoodPos(0);
		view.setBackgroundColor(getResources().getColor(R.color.light_color));

		FoodListFrag foodLF = (FoodListFrag)getFragmentManager().findFragmentById(R.id.foodListFrag);
		foodLF.refreshList(this, true);
	}
}