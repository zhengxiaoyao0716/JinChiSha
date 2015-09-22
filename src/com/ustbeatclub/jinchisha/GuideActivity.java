package com.ustbeatclub.jinchisha;

import java.util.ArrayList;
import java.util.List;

import com.ustbeatclub.adapter.GuidePagerAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity implements OnClickListener, OnPageChangeListener{

	private ViewPager viewPager;
	private GuidePagerAdapter guidePagerAdapter;
	private List<View> views;

	//引导图片资源
	private static final int[] pics =
			{R.drawable.guide0, R.drawable.guide1, R.drawable.guide2, R.drawable.guide3};

	//底部小店图片
	private ImageView[] dots ;

	//记录当前选中位置
	private int currentIndex;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		views = new ArrayList<View>();

		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		//初始化引导图片列表
		for(int i=0; i<pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			views.add(iv);
		}
		viewPager = (ViewPager) findViewById(R.id.guideViewPager);
		//初始化Adapter
		guidePagerAdapter = new GuidePagerAdapter(views);
		viewPager.setAdapter(guidePagerAdapter);
		//绑定回调
		viewPager.setOnPageChangeListener(this);

		//初始化底部小点
		initDots();

	}

	private void initDots() {
		LinearLayout dotLinearLayout = (LinearLayout) findViewById(R.id.dotLinearLayout);

		dots = new ImageView[pics.length];

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(16, 16, 16, 0);
		//循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			dots[i] = new ImageView(this);
			dots[i].setImageResource(R.drawable.dot);
			dots[i].setLayoutParams(layoutParams);
			dots[i].setEnabled(true);//都设为灰色
			dots[i].setClickable(true);
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
			dotLinearLayout.addView(dots[i]);
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);//设置为白色，即选中状态
	}

	/**
	 *设置当前的引导页
	 */
	private void setCurView(int position)
	{
		if (position < 0 || position >= pics.length) {
			return;
		}

		viewPager.setCurrentItem(position);
	}

	/**
	 *这只当前引导小点的选中
	 */
	private void setCurDot(int positon)
	{
		if (positon < 0 || positon >= pics.length || currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(false);
		dots[currentIndex].setEnabled(true);

		currentIndex = positon;
	}

	//当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	//当当前页面被滑动时调用
	boolean isEnd;
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		if (isEnd&&arg0==pics.length - 1) finish();
		isEnd = (arg0==pics.length - 1);
	}

	//当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		//设置底部小点选中状态
		setCurDot(arg0);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer)v.getTag();
		setCurView(position);
		setCurDot(position);
	}
}