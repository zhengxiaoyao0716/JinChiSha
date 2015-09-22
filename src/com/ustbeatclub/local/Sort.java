package com.ustbeatclub.local;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;

public class Sort {
	private ArrayList<int[]> posList;
	private String name;
	private String priceText;
	private String timeText;
	private Integer[] windows;
	private CharSequence suggest;
	private Bitmap image;
	private String md5;
	public Sort(int id, int rest, int floor, String name,
				String timeText, String priceText, ArrayList<Integer> windows, String md5)
	{
		posList = new ArrayList<int[]>();
		posList.add(new int[]{id, rest, floor});

		this.name = name;
		this.timeText = new StringBuilder("供应时段：").append(timeText).toString();
		this.priceText = new StringBuilder("价格区间：").append(priceText).toString();
		this.windows = (Integer[])windows.toArray(new Integer[windows.size()]);
		suggest = null;
		this.md5 = md5;
		image = null;
	}
	//位置比较
	public boolean isInPos(int rest, int floor)
	{
		for (int[] pos: posList)
		{
			if ((rest==0 || pos[1]==rest) && (floor==0 || pos[2]==floor))
				return true;
		}
		return false;
	}
	//set
	public Sort setSuggest(CharSequence suggest)
	{
		this.suggest = suggest;
		return this;
	}
	public Sort setImage(Bitmap image)
	{
		this.image = image;
		return this;
	}
	//get
	public int getId()								{ return posList.get(0)[0]; }
	public ArrayList<int[]> getPosList()            { return posList; }
	public String getName()                         { return name; }
	public Integer[] getWindows()                         { return windows; }
	//获取缩放样式的分类字符串
	public  CharSequence getSpanName()
	{
		SpannableStringBuilder sortStrBuilder = new SpannableStringBuilder(name);
		if (sortStrBuilder.length() > 6)
		{
			sortStrBuilder.insert(5, "\n");
			sortStrBuilder.setSpan(new RelativeSizeSpan(0.5f), 0, sortStrBuilder.length(),
					Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		else if (sortStrBuilder.length() > 3)sortStrBuilder.setSpan(
				new RelativeSizeSpan(0.7f), 0, sortStrBuilder.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		return sortStrBuilder;
	}
	public String getPrice()						{ return priceText; }
	public String getTime()							{ return timeText; }
	public CharSequence getSuggest()				{ return suggest; }
	public Bitmap getImage()						{ return image; }
	public String getMd5()							{ return md5; }
	//change
	public void addPos(int id, int rest, int floor)
	{
		posList.add(new int[]{id, rest, floor});
	}
}