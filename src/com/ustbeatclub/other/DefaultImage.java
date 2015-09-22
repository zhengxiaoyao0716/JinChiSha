package com.ustbeatclub.other;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

//这个类就是一张图片
public class DefaultImage {
	//不要试图调用DefaultImage的构造器
	private DefaultImage(){}

	public final static Bitmap defaultImage
			= Bitmap.createBitmap(180, 180, Bitmap.Config.ARGB_8888);
	static
	{
		Canvas canvasTemp = new Canvas(defaultImage);
		canvasTemp.drawColor(Color.WHITE);
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		String familyName ="宋体";
		Typeface font = Typeface.create(familyName, Typeface.BOLD);
		p.setTypeface(font);
		p.setTextSize(24);
		String mstrTitle = "图片加载中";
		canvasTemp.drawText(mstrTitle,6,30,p);
	}
}