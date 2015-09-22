package com.ustbeatclub.fragment;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.ustbeatclub.jinchisha.R;
import com.ustbeatclub.local.DataManager;
import com.ustbeatclub.local.FileIO;
import com.ustbeatclub.net.BaseHandler;
import com.ustbeatclub.net.FoodImgGeter;
import com.ustbeatclub.net.ReviewCutGeter;
import com.ustbeatclub.net.ReviewGeter;
import com.ustbeatclub.net.ReviewPoster;
import com.ustbeatclub.other.DefaultImage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class FoodDetailFrag extends Fragment implements OnClickListener {
	Activity parent;
	ViewGroup viewGroup;
	Thread imageGeterThread;
	DataManager dataManager;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		viewGroup = container;
		View view = inflater.inflate(R.layout.frag_food_detail, container, false);
		return  view;
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		parent = getActivity();
		dataManager = null;
		parent.findViewById(R.id.pubReviewButton).setOnClickListener(this);
		parent.findViewById(R.id.collectImageButton).setOnClickListener(this);
		parent.findViewById(R.id.seeReviewButton).setOnClickListener(this);
	}
	protected void refreshDetail()
	{
		dataManager = DataManager.INSTANCE;
		//食物名
		TextView foodTV = (TextView)parent.findViewById(R.id.foodTextView);
		//foodTV.setText(dataManager.getFoodSpanStr(parent));
		foodTV.setText(dataManager.getFoodStr());
		//图片
		final ImageView foodIV = (ImageView)parent.findViewById(R.id.foodImageView);
		Bitmap image = dataManager.getFoodImage();
		if (image==null)
		{
			foodIV.setImageBitmap(DefaultImage.defaultImage);
			imageGeterThread = new Thread(new FoodImgGeter(new BaseHandler(parent, false){

				@Override
				public void doInHandMsgMethod(Activity activity, Message msg) {
					// TODO Auto-generated method stub
					foodIV.setImageBitmap(dataManager.getFoodImage());
				}}, dataManager.getFoodId(), dataManager.getFoodMd5() ));
			imageGeterThread.start();
		}
		else foodIV.setImageBitmap(image);
		//地点
		TextView positionTV = (TextView)parent.findViewById(R.id.positionTextView);
		positionTV.setText(dataManager.getPositionStr());
		//时间段
		int[] bgColor = {getResources().getColor(R.color.bg_color),
				getResources().getColor(R.color.light_color)};
		int[] textColor = {getResources().getColor(R.color.normal_text_color),
				getResources().getColor(R.color.light_text_color)};
		int time = dataManager.getTime();

		TextView morningTV = (TextView)parent.findViewById(R.id.breakfastTextView);
		morningTV.setBackgroundColor(bgColor[(time&1)==0 ? 0 : 1]);
		morningTV.setTextColor(textColor[(time&1)==0 ? 0 : 1]);

		TextView noonTV = (TextView)parent.findViewById(R.id.launchTextView);
		noonTV.setBackgroundColor(bgColor[(time&2)==0 ? 0 : 1]);
		noonTV.setTextColor(textColor[(time&2)==0 ? 0 : 1]);

		TextView eveningTV = (TextView)parent.findViewById(R.id.dinnerTextView);
		eveningTV.setBackgroundColor(bgColor[(time&4)==0 ? 0 : 1]);
		eveningTV.setTextColor(textColor[(time&4)==0 ? 0 : 1]);

		TextView nightTV = (TextView)parent.findViewById(R.id.nightSnackTextView);
		nightTV.setBackgroundColor(bgColor[(time&8)==0 ? 0 : 1]);
		nightTV.setTextColor(textColor[(time&8)==0 ? 0 : 1]);
		//价格
		TextView priceTV = (TextView)parent.findViewById(R.id.priceTextView);
		priceTV.setText(dataManager.getPriceSpanStr());
		//人气指数
		RatingBar hotScoreRB = (RatingBar)parent.findViewById(R.id.hotScoreRatingBar);
		hotScoreRB.setRating(dataManager.getHot());
		//营养指数
		RatingBar healthScoreRB = (RatingBar)parent.findViewById(R.id.healthScoreRatingBar);
		healthScoreRB.setRating(dataManager.getHealth());
		//收藏
		ImageButton collectIB = (ImageButton)parent.findViewById(R.id.collectImageButton);
		collectIB.setImageResource(R.drawable.collection0 + (dataManager.isCollect() ? 1 : 0));
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (dataManager==null)
		{
			Toast.makeText(parent, "您还未选择一个食物。", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (view.getId())
		{
			case R.id.pubReviewButton:
			{
				pubReviewCard();
				break;
			}
			case R.id.collectImageButton:
			{
				dataManager.changeCollect(parent);
				ImageButton collectIB = (ImageButton)view;
				collectIB.setImageResource(R.drawable.collection0 + (dataManager.isCollect() ? 1 : 0));
				break;
			}
			case R.id.seeReviewButton:
			{
				seeReviewCard();
				break;
			}
		}
	}
	private void pubReviewCard()
	{
		final View reviewDialog = (View)parent.getLayoutInflater()
				.inflate(R.layout.dialog_review, viewGroup);
		//读取本地
		final FileIO fileIO = FileIO.reviewFileIO(new StringBuilder("review ")
				.append(dataManager.getFoodId()).toString());

		byte[] data = null;
		try {
			data = fileIO.readFile();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String jsonString = null;
		if (data!=null) jsonString = new String(data);
		JSONObject reviewJO = null;
		if (jsonString!=null)
		{
			try {
				reviewJO = new JSONObject(jsonString);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		//删改
		if (reviewJO!=null)
		{
			//设置星星
			final RatingBar pubScoreRB = (RatingBar)reviewDialog.findViewById(R.id.pubScoreRatingBar);
			int last_score = reviewJO.optInt("score", 0);
			pubScoreRB.setRating(last_score);
			StringBuilder titleStr = new StringBuilder("我的评价：");
			switch (last_score)
			{
				case 0:
					titleStr.append("很差");
					break;
				case 1:
					titleStr.append("较差");
					break;
				case 2:
					titleStr.append("一般");
					break;
				case 3:
					titleStr.append("挺好");
					break;
				case 4:
					titleStr.append("很好");
					break;
				case 5:
					titleStr.append("完美");
					break;
			}

			EditText reviewET = (EditText)reviewDialog.findViewById(R.id.reviewEditText);
			reviewET.setHint(reviewJO.optString("content", "error"));
			final int id = reviewJO.optInt("id", 0);

			//显示评论卡片
			new AlertDialog.Builder(parent).setTitle(titleStr).setView(reviewDialog)
					.setPositiveButton("修改评论", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							int score = (int)pubScoreRB.getRating();
							EditText reviewED = (EditText)reviewDialog.findViewById(R.id.reviewEditText);
							final String review = reviewED.getText().toString();

							if (review.length() < 3)
								Toast.makeText(parent, "修改评论失败，字数过少", Toast.LENGTH_LONG).show();
								//改
							else
							{
								new Thread(new ReviewPoster(new BaseHandler(parent)
								{
									@Override
									public void doInHandMsgMethod(Activity activity, Message msg) {
										// TODO Auto-generated method stub
										doInReviewPostHandMessage(activity, msg, id,
												"修改评论失败", (int)pubScoreRB.getRating(),
												review, fileIO);
									}}, id, review, score)).start();
							}
						}
					}).setNeutralButton("删除评论", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					new Thread(new ReviewCutGeter(new BaseHandler(parent){
						@Override
						public void doInHandMsgMethod(
								Activity activity, Message msg) {
							// TODO Auto-generated method stub

							if (msg.what==1)
							{
								final JSONObject result = (JSONObject) msg.obj;
								if (result.optBoolean("success", false))
								{
									new Thread()
									{
										@Override
										public void run()
										{
											fileIO.deleteFile();
										}
									}.start();
									Toast.makeText(parent, "评论已删除",
											Toast.LENGTH_LONG).show();
								}
								else
									Toast.makeText(parent,new StringBuilder("删除失败：")
													.append(result.optString("msg", null)).toString(),
											Toast.LENGTH_LONG).show();
							}
						}
					}, id)).start();
				}
			}).setNegativeButton("取消", null).show();
		}
		//新增
		else
		{
			//设置星星
			final RatingBar pubScoreRB = (RatingBar)reviewDialog.findViewById(R.id.pubScoreRatingBar);
			pubScoreRB.setRating(0);
			//显示评论卡片
			new AlertDialog.Builder(parent).setTitle("我的评价：").setView(reviewDialog)
					.setPositiveButton("发表评论", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							EditText reviewED = (EditText)reviewDialog.findViewById(R.id.reviewEditText);
							final String review = reviewED.getText().toString();
							int score = (int)pubScoreRB.getRating();

							if (review.length() < 3)
							{
								Toast.makeText(parent, "发表评论失败，字数过少", Toast.LENGTH_LONG).show();
								return;
							}
							else
								new Thread(new ReviewPoster(new BaseHandler(parent)
								{
									@Override
									public void doInHandMsgMethod(Activity activity, Message msg) {
										// TODO Auto-generated method stub

										doInReviewPostHandMessage(activity, msg, 0,
												"发布评论失败", (int)pubScoreRB.getRating(),
												review, fileIO);
									}}, review, score)).start();
						}
					})
					.setNegativeButton("取消", null).show();
		}
	}
	private void doInReviewPostHandMessage(
			Activity activity, Message msg, final int id,
			String tipStr, final int score,
			final String review, final FileIO fileIO)
	{
		if (msg.what==1)
		{
			final JSONObject result = (JSONObject) msg.obj;
			if (result.optBoolean("success", false))
			{
				//写入文件
				new Thread()
				{
					@Override
					public void run() {
						// TODO Auto-generated method stub
						JSONObject jO = result.optJSONObject("result");
						if (jO==null)
						{
							jO = new JSONObject();
							try {
								jO.put("id", id);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						try {
							jO.put("score", score);
							jO.put("content", review);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							fileIO.writeFile(jO.toString().getBytes());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}.start();
				seeReviewCard();
			}
			else
				Toast.makeText(parent,new StringBuilder(tipStr)
								.append(result.optString("msg", null)).toString(),
						Toast.LENGTH_LONG).show();
		}
		else
			Toast.makeText(parent,
					"服务器连接失败", Toast.LENGTH_LONG).show();
	}
	private void seeReviewCard() {
		// TODO Auto-generated method stub

		new Thread(new ReviewGeter(new BaseHandler(parent){

			@Override
			public void doInHandMsgMethod(Activity activity, Message msg) {
				// TODO Auto-generated method stub
				if (msg.what==1)
				{
					new AlertDialog.Builder(parent).setTitle("查看评论：")
							.setItems(dataManager.getReview(), null)
							.setPositiveButton("去评论",  new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									pubReviewCard();
								}
							}).setNegativeButton("返回", null).show();
					dataManager.clearReview();
				}
			}})).start();
	}
}