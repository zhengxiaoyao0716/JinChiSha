package com.ustbeatclub.jinchisha;

import java.util.ArrayList;

import com.ustbeatclub.adapter.SortListAdapter;
import com.ustbeatclub.jinchisha.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.ustbeatclub.local.DataManager;
import com.ustbeatclub.local.Sort;
import com.ustbeatclub.net.BaseHandler;
import com.ustbeatclub.net.SortGeter;
import com.ustbeatclub.net.SortImageGeter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	DataManager dataManager;
	FeedbackAgent agent;
	int versionCode = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SharedPreferences sharedPrefs
				= getSharedPreferences("prefsData", MODE_PRIVATE);

		try {
			versionCode = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (sharedPrefs.getInt("versionCode", 0)!=versionCode)
		{
			Editor shareEditor = sharedPrefs.edit();
			shareEditor.putInt("versionCode", versionCode);
			shareEditor.commit();
			Intent intent = new Intent(MainActivity.this, GuideActivity.class);
			startActivity(intent);
		}

		dataManager = DataManager.INSTANCE
				.setRest(sharedPrefs.getInt("rest", 0))
				.setFloor(sharedPrefs.getInt("floor", 0))
				.setRank(sharedPrefs.getInt("rank", 0));
		ArrayList<int[]> prefers = new ArrayList<int[]>();
		int size = sharedPrefs.getInt("prefersSize", 0);
		for (int i = 0; i < size; i++)
		{
			int[] prefer = new int[]{
					sharedPrefs.getInt(
							new StringBuilder("prefersId").append(i).toString(), 0),
					sharedPrefs.getInt(
							new StringBuilder("prefersRest").append(i).toString(), 0),
					sharedPrefs.getInt(
							new StringBuilder("prefersFloor").append(i).toString(), 0),
			};
			prefers.add(prefer);
		}
		dataManager.setPrefers(prefers);

		ImageView restImageView = (ImageView)findViewById(R.id.restImageView);
		restImageView.setOnTouchListener(onRestTouchListener);
		restImageView.setOnClickListener(onRestClickListener);

		refreshList();
		//分类列表
		ListView sortLV = (ListView)findViewById(R.id.sortListView);
		sortLV.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				// TODO Auto-generated method stub Intent intent = new Intent();
				dataManager.setSortPos(position).setFoodPos(0);

				Intent intent = new Intent(MainActivity.this, SecondActivity.class);
				startActivity(intent);
			}
		});
		//umeng, 检测反馈
		agent = new FeedbackAgent(this);
		agent.sync();
		//uemng, 自动更新
		UmengUpdateAgent.update(this);
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		//umeng, 统计
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		//umeng, 统计
		MobclickAgent.onPause(this);

		SharedPreferences sharedPrefs
				= getSharedPreferences("prefsData", MODE_PRIVATE);
		Editor shareEditor = sharedPrefs.edit();
		shareEditor.putInt("rest", dataManager.getRest());
		shareEditor.putInt("floor", dataManager.getFloor());
		shareEditor.putInt("rank", dataManager.getRank());
		ArrayList<int[]> prefers = dataManager.getPrefers();
		shareEditor.putInt("prefersSize", prefers.size());
		for (int i = 0; i < prefers.size(); i++)
		{
			shareEditor.putInt(
					new StringBuilder("prefersId").append(i).toString(), prefers.get(i)[0]);
			shareEditor.putInt(
					new StringBuilder("prefersRest").append(i).toString(), prefers.get(i)[1]);
			shareEditor.putInt(
					new StringBuilder("prefersFloor").append(i).toString(), prefers.get(i)[2]);
		}
		shareEditor.commit();
		dataManager.clearSortImgTasks();
		if (sortImageGeter!=null)
		{
			sortImageGeter.interrupt();;
			sortImageGeter = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.feedbackItem:
			{
				agent.startFeedbackActivity();
				break;
			}
			case R.id.collectItem:
			{
				ArrayList<int[]> preferFoods = dataManager.setSortPos(-1).getPrefers();
				if (preferFoods.size()==0)
				{
					Toast.makeText(this, "您没有收藏美食", Toast.LENGTH_SHORT).show();
					break;
				}
				Intent intent = new Intent(MainActivity.this, SecondActivity.class);
				startActivity(intent);

				break;
			}
		}
		return true;
	}

	//餐厅的点选、滑动动作
	float downX = 0;//, upX = 0;
	OnTouchListener onRestTouchListener = new OnTouchListener(){
		@Override
		public boolean onTouch(View view, MotionEvent event)
		{
			// TODO: Implement this method
			if (MotionEvent.ACTION_DOWN==event.getAction())
			{
				downX = event.getX();
			}
			else if (MotionEvent.ACTION_UP==event.getAction())
			{
				float disX = event.getX() - downX;
				if (disX > 30) dataManager.changeRest(1);
				else if (disX < -30) dataManager.changeRest(-1);
				else view.performClick();

				ImageView restImageView = (ImageView)findViewById(R.id.restImageView);
				restImageView.setImageResource(R.drawable.rest0 + dataManager.getRest());
				refreshList();
			}
			return true;
		}
	};
	OnClickListener onRestClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			float vW = v.getWidth();
			if (downX < vW/3) dataManager.setRest(0);
			else if (downX < vW - vW/3) dataManager.setRest(1);
			else dataManager.setRest(2);
		}
	};

	public void onFloorChangeClick(View view)
	{
		if (view.getId()==R.id.floorLImageView) dataManager.changeFloor(-1);
		else dataManager.changeFloor(1);
		TextView floorTextView = (TextView)findViewById(R.id.floorTextView);
		floorTextView.setText(dataManager.getFloorStr());
		refreshList();
	}
	//菜品列表
	ListView sortLV;
	//菜品分类适配器
	SortListAdapter sortLA;
	//加载分类图片线程
	SortImageGeter sortImageGeter;
	private void refreshList() {
		// TODO Auto-generated method stub
		dataManager.clearSortImgTasks();
		if (sortImageGeter!=null)
		{
			sortImageGeter.interrupt();;
			sortImageGeter = null;
		}
		/*
		 * 
		//广告条
		ImageView adIV = (ImageView)findViewById(R.id.adImageView);
		adIV.setImageResource(R.drawable.advertise0);
		 */
		//餐厅
		ImageView restIV = (ImageView)findViewById(R.id.restImageView);
		restIV.setImageResource(R.drawable.rest0 + dataManager.getRest());
		//楼层
		TextView floorTextView = (TextView)findViewById(R.id.floorTextView);
		floorTextView.setText(dataManager.getFloorStr());

		//加载食物分类列表
		new Thread(new SortGeter(new BaseHandler(this)
		{
			@Override
			public void doInHandMsgMethod(Activity activity, Message msg) {
				// TODO Auto-generated method stub

				ArrayList<Sort> sorts = dataManager.getSortListData();
				sortLA = new SortListAdapter(activity, R.layout.item_sort_list, sorts);
				sortLV = (ListView)activity.findViewById(R.id.sortListView);
				sortLV.setAdapter(sortLA);

				if (msg.what==1)
				{
					//加载分类图片线程
					sortImageGeter = new SortImageGeter(new BaseHandler(activity, false)
					{
						@Override
						public void doInHandMsgMethod(Activity activity,
													  Message msg) {
							// TODO Auto-generated method stub

							int startPos = sortLV.getFirstVisiblePosition();
							for (int eachPos = startPos;
								 eachPos<=sortLV.getLastVisiblePosition(); eachPos++)
							{
								if (msg.arg1==eachPos)
									sortLA.getView(eachPos,
											(View)sortLV.getChildAt(eachPos - startPos), sortLV);
							}
							//sortLA.notifyDataSetChanged();
						}
					});
					sortImageGeter.start();
				}
				else
				{
					Toast.makeText(activity, "加载数据失败", Toast.LENGTH_SHORT).show();
				}
			}
		})).start();
	}
}