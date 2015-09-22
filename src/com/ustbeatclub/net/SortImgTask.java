package com.ustbeatclub.net;

public class SortImgTask {
	private int id;
	private int pos;
	private String md5;
	public SortImgTask(int id, int pos, String md5)
	{
		this.id = id;
		this.pos = pos;
		this.md5 = md5;
	}
	public int getId()				{ return id; }
	public int getPos()				{ return pos; }
	public String getMd5()			{ return md5; }
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SortImgTask)) return false;
		SortImgTask sortImgTask = (SortImgTask) obj;
		return id==sortImgTask.getId();
	}
	@Override
	public int hashCode() {
		return 16 * 31 + id;
	}
}