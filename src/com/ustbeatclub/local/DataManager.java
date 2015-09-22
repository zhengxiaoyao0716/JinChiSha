package com.ustbeatclub.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;

import com.umeng.analytics.MobclickAgent;
import com.ustbeatclub.net.SortImgTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

public enum DataManager {
	//singleton
	INSTANCE();

	private static final String[] restStr = {"全馆", "万秀", "鸿博"};
	private static final String[] floorStr = {"全层", "一层", "二层", "三层"};
	private int rest, floor, sortPos, rank, foodPos;
	//List
	private ArrayList<Sort> sorts;
	private ArrayList<Food> foods;
	private ArrayList<int[]> prefers;
	private LinkedHashSet<SortImgTask> sortImgTasks;
	class Review
	{
		int id;
		String content;
		int time;
		Review(int id, String content, int time) {
			// TODO Auto-generated constructor stub
			this.id = id;
			this.content = content;
			this.time = time;
		}
		public int getId()
		{
			return id;
		}
		public String getContent()
		{
			return content;
		}
		int getTime()
		{
			return time;
		}
	}
	private ArrayList<Review> reviews;

	DataManager()
	{
		//*load
		sorts = new ArrayList<Sort>();
		foods = new ArrayList<Food>();
		prefers = new ArrayList<int[]>();
		sortImgTasks = new LinkedHashSet<SortImgTask>();
		reviews = new ArrayList<Review>();
		//rest = floor = sort = rank = food = 0;
	}
	/*
	 * set
	 */
	public DataManager setRest(int rest)
	{
		this.rest = rest;
		return this;
	}
	public DataManager setFloor(int floor)
	{
		this.floor = floor;
		return this;
	}
	public DataManager setSortPos(int sortPos)
	{
		this.sortPos = sortPos;
		return this;
	}
	public DataManager setRank(int rank)
	{
		this.rank = rank;
		return this;
	}
	public DataManager setFoodPos(int foodPos)
	{
		this.foodPos = foodPos;
		return this;
	}
	public DataManager setSorts(ArrayList<Sort> sorts)
	{
		this.sorts = sorts;
		return this;
	}
	public DataManager setFoods(ArrayList<Food> foods)
	{
		this.foods = foods;
		return this;
	}
	public DataManager setPrefers(ArrayList<int[]> prefers)
	{
		this.prefers = prefers;
		return this;
	}
	public DataManager setFoodImage(Bitmap image)
	{
		foods.get(foodPos).setImage(image);
		return this;
	}
	/*
	 * change
	 */
	public void changeRest(int value)
	{
		rest+=value;
		if (rest < 0) rest = 2;
		else if (rest >2) rest = 0;
	}
	public void changeFloor(int value)
	{
		floor+=value;
		if (floor < 0) floor = 3;
		else if (floor > 3) floor = 0;
	}
	public void changeCollect(Context context)
	{
		Food thisFood = foods.get(foodPos);
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("food_id", String.valueOf(thisFood.getId()));
		map.put("food_name", thisFood.getName());

		int i;
		for (i = 0; i < prefers.size(); i++)
		{
			if (prefers.get(i)[0]==thisFood.getId())
			{
				prefers.remove(i);
				MobclickAgent.onEvent(context, "cancel_collect", map);
			}
		}
		if (i==prefers.size())
		{
			prefers.add(new int[]{thisFood.getId(),
					thisFood.getPos()[0], thisFood.getPos()[1]});
			MobclickAgent.onEvent(context, "collect", map);
		}
	}
	public void addSortImgTask(SortImgTask sortImgTask)
	{
		synchronized (sortImgTasks)
		{
			sortImgTasks.add(sortImgTask);
			Log.v("sortImgTasks.size()", String.valueOf(sortImgTasks.size()));
		}
	}
	public void addSortImgTask(int sortId, int sortPos, String md5)
	{
		synchronized (sortImgTasks)
		{
			sortImgTasks.add(
					new SortImgTask(sortId, sortPos, md5));
		}
	}
	public void clearSortImgTasks()
	{
		synchronized (sortImgTasks)
		{
			sortImgTasks.clear();
			sortImgTasks = new LinkedHashSet<SortImgTask>();
		}
	}
	public void addReview(int id, String content, int time)
	{
		reviews.add(new Review(id, content, time));
	}
	public void clearReview()
	{
		reviews.clear();
		reviews = null;
		reviews = new ArrayList<Review>();
	}
	/*
	 * get
	 */
	public int getRest()
	{
		return rest;
	}
	public int getFloor()
	{
		return floor;
	}
	public String getFloorStr()
	{
		return floorStr[floor];
	}
	public int getSortPos()
	{
		return sortPos;
	}
	public String getSortStr()
	{
		if (sortPos==-1) return "收藏的美食";
		if (sorts==null||sorts.size() < 1) return "出错了";
		return sorts.get(sortPos).getName();
	}
	public int getRank()
	{
		return rank;
	}
	public int getFoodId()
	{
		return foods.get(foodPos).getId();
	}
	public int getFood()
	{
		return foodPos;
	}
	public String getFoodStr()
	{
		return foods.get(foodPos).getName();
	}
	public boolean isCollect()
	{
		Food thisFood = foods.get(foodPos);
		for (int[] prefer: prefers)
			if (prefer[0]==thisFood.getId())
				return true;
		return false;
	}
	public CharSequence getPositionStr()
	{
		int[] pos = foods.get(foodPos).getPos();
		return new StringBuilder("地点：").append(restStr[pos[0]])
				.append(floorStr[pos[1]])
				.append(pos[2]).append("号窗口");
	}
	public int getTime()
	{
		return foods.get(foodPos).getTime();
	}
	//获得价格带样式字符串
	public CharSequence getPriceSpanStr()
	{
		String[] priceStrs = foods.get(foodPos).getPriceStrs();

		if (priceStrs.length==1)
			return new StringBuilder("￥").append(priceStrs[0]);
		else if (priceStrs.length==2)
			return new StringBuilder("￥").append(priceStrs[0])
					.append("\n").append("￥").append(priceStrs[1]);
		return null;
	}
	public int getHot()
	{
		return foods.get(foodPos).getHot();
	}
	public int getHealth()
	{
		return foods.get(foodPos).getHealth();
	}
	//获得推荐信息字符串
	private CharSequence getSuggest(int sort)
	{
		SpannableStringBuilder suggestSpanStr = new SpannableStringBuilder();
		if (rest==0)
		{
			suggestSpanStr.append("供应餐厅：");
			if (sorts.get(sort).isInPos(1, 0)) suggestSpanStr.append("  鸿博园");
			if (sorts.get(sort).isInPos(2, 0)) suggestSpanStr.append("  万秀园");
		}
		else if (floor==0)
		{
			suggestSpanStr.append("供应楼层：");
			if (sorts.get(sort).isInPos(0, 1)) suggestSpanStr.append("  一层");
			if (sorts.get(sort).isInPos(0, 2)) suggestSpanStr.append("  二层");
			if (sorts.get(sort).isInPos(0, 3)) suggestSpanStr.append("  三层");
			if (sorts.get(sort).isInPos(0, 4)) suggestSpanStr.append("  四层");
		}
		else
		{
			suggestSpanStr.append("供应窗口：");
			for (Integer window : sorts.get(sort).getWindows())
				suggestSpanStr.append("  ").append(String.valueOf(window)).append("号窗口");
			/*
			for (int i = 0; i < allFoods.size(); i++)
			{
				if (allFoods.get(i).isInSort(rest, floor, 0, sort) && allFoods.get(i).getMark()>=0)
					suggestSpanStr.append("  ").append(getFoodSpanStr(context, allFoods.get(i)));
			}*/
		}
		suggestSpanStr.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 5,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		return suggestSpanStr;
	}
	//获得分类数据
	public ArrayList<Sort> getSorts()
	{
		return sorts;
	}
	//获得分类列表并完善信息
	public ArrayList<Sort> getSortListData()
	{
		for (int i = 0; i < sorts.size(); i++)
			sorts.get(i).setSuggest(getSuggest(i));
		return sorts;
	}
	//获取食物数据
	public ArrayList<Food> getFoods()
	{
		return foods;
	}
	//获得食物名称列表
	public ArrayList<String> getFoodListData()
	{
		if (foods==null||foods.size() < 1) return null;
		foodPos = 0;
		//排序
		Collections.sort(foods, new Comparator<Food>()
		{

			@Override
			public int compare(Food lhs, Food rhs) {
				// TODO Auto-generated method stub
				switch (rank)
				{
					case 0:
						return lhs.getPos()[2] - rhs.getPos()[2];
					case 1:
						return (int) (lhs.getPrice() - rhs.getPrice());
					case 2:
						return lhs.getHot() - rhs.getHot();
					case 3:
						return lhs.getTime() - rhs.getTime();
				}
				return 0;
			}

		});

		ArrayList<String> foodNameList = new ArrayList<String>();
		for (int i = 0; i < foods.size(); i++)
			foodNameList.add(foods.get(i).getName());
		return foodNameList;
	}
	public ArrayList<int[]> getPrefers()
	{
		return prefers;
	}
	public SortImgTask getSortImgTask()
	{
		synchronized (sortImgTasks)
		{
			SortImgTask sortImgTask = null;
			try {
				Iterator<SortImgTask> iterator = sortImgTasks.iterator();
				sortImgTask =  iterator.next();
				iterator.remove();
			} catch (NoSuchElementException e){
				return null;
			} catch (NullPointerException e) {
				return null;
			}
			return sortImgTask;
		}
	}
	public Bitmap getFoodImage() {
		// TODO Auto-generated method stub
		return foods.get(foodPos).getImage();
	}
	public String getFoodMd5()
	{
		return foods.get(foodPos).getMd5();
	}
	public String[] getReview()
	{
		ArrayList<String> reviewStrs = new ArrayList<String>();
		for (Review review: reviews)
		{
			reviewStrs.add(review.getContent());
		}
		String[] strs = new String[reviewStrs.size()];
		return reviewStrs.toArray(strs);
	}
}