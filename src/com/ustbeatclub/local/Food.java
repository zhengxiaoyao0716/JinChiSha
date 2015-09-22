package com.ustbeatclub.local;

import android.graphics.Bitmap;

public class Food {
	private int id;
	private int[] pos;    //rest, floor, window
	private String name;
	//int mark;
	private int time;   //1248boolean
	private float price;
	private String[] priceStrs;
	//private float hot, health;
	private int hot, health;
	private Bitmap image;
	private String md5;

	public Food(int id, int[] pos, String name, /*int mark,*/
				int time, float price, String[] priceStrs, int hot, int health, String md5)
	{
		this.id = id;
		this.pos = pos;
		this.name = name;
		//this.mark = mark;
		this.time = time;
		this.price = price;
		this.priceStrs = priceStrs;
		this.hot = hot;
		this.health = health;
		this.md5 = md5;
		image = null;
	}
	//set
	protected void setImage(Bitmap image)                { this.image = image; }
	//get
	protected int getId()								{ return id; }
	protected int[] getPos()							{ return pos; }
	protected String getName()							{ return name; }
	//public int getMark()							{ return mark; }
	protected int getTime()							{ return time; }
	protected float getPrice()							{ return price; }
	protected String[] getPriceStrs()					{ return priceStrs; }
	protected int getHot()							{ return hot; }
	protected int getHealth()						{ return health; }
	protected Bitmap getImage()						{ return image; }
	protected String getMd5()							{ return md5; }
	/*
	 * 
	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof Food)) return false;
		Food mObject = (Food)object;
		return id==mObject.getId();
	}
	@Override
	public int hashCode()
	{
		int hashCode = 1;
		hashCode = 31 * hashCode + id;
		return hashCode;
	}
	 */
}