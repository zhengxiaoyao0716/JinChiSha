package com.ustbeatclub.net;

import java.io.IOException;

import com.ustbeatclub.local.DataManager;
import com.ustbeatclub.local.Sort;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class SortImageGeter extends Thread{
	private Handler handler;
	DataManager dataManager;

	public SortImageGeter(Handler handler)
	{
		this.handler = handler;
		dataManager = DataManager.INSTANCE;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		//Long lastTime = System.currentTimeMillis();

		while (!isInterrupted())
		{
			Message msg = handler.obtainMessage();
			SortImgTask sortImgTask;

			if ((sortImgTask = dataManager.getSortImgTask())==null)
				continue;
			try {
				getImage(sortImgTask);
				msg.arg1 = sortImgTask.getPos();
				handler.sendMessage(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			/*
			 * 
			if (System.currentTimeMillis() - lastTime > 500)
			{
				handler.sendMessage(msg);
				lastTime = System.currentTimeMillis();
			}
			 */
		}
	}
	private void getImage(SortImgTask sortImgTask) throws IOException {
		// TODO Auto-generated method stub
		String cmd = new StringBuilder("image/category?id=")
				.append(sortImgTask.getId()).toString();
		Bitmap image = BaseGeter.getBitmapResult(cmd, sortImgTask.getMd5());
		Sort sort = null;
		try {
			sort = dataManager.getSorts().get(sortImgTask.getPos());
		} catch(IndexOutOfBoundsException e) {
			throw new IOException();
		}
		if (sortImgTask.getId()==sort.getId()) sort.setImage(image);
		else throw new IOException();
	}
}