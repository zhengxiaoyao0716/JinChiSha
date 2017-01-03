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
		Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create("宋体", Typeface.BOLD));
        paint.setTextSize(24);

        Canvas canvasTemp = new Canvas(faceBitmap);
        canvasTemp.drawColor(Color.WHITE);
        canvasTemp.drawText("点我拍照", 6, 30, paint);
	}
}