package com.ustbeatclub.net;

import java.io.IOException;

import com.ustbeatclub.local.DataManager;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class FoodImgGeter implements Runnable {
	private Handler handler;
	private int id;
	private String md5;

	public FoodImgGeter(Handler handler, int id, String md5)
	{
		this.handler = handler;
		this.id = id;
		this.md5 = md5;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();

		try {
			getImage();
			handler.sendMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	private void getImage() throws IOException {
		// TODO Auto-generated method stub
		String cmd = new StringBuilder("image/food?id=")
				.append(id).toString();
		DataManager dataManager = DataManager.INSTANCE;
		Bitmap image = BaseGeter.getBitmapResult(cmd, md5);
		try {
			if (id==dataManager.getFoodId()) dataManager.setFoodImage(image);
			else throw new IOException();
		} catch (IndexOutOfBoundsException e) {
			throw new IOException();
		}
	}
}
