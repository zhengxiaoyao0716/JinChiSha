package com.ustbeatclub.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ustbeatclub.local.FileIO;

public class BaseGeter {
	public static JSONArray getJSONArrayResult(String cmd)
			throws IOException
	{
		JSONArray result = null;
		try {
			result = getJSONObject(cmd).optJSONArray("result");
		} catch (NullPointerException e) {
			throw new IOException();
		}
		return result;
	}
	public static JSONObject getJSONObjectResult(String cmd)
			throws IOException
	{
		return getJSONObject(cmd).optJSONObject("result");
	}
	public static Bitmap getBitmapResult(String cmd, String md5)
			throws IOException
	{
		FileIO fileIO = FileIO.imageFileIO(
				new StringBuilder(cmd).append("_extral").toString());
		byte[] md5Bytes = fileIO.readFile();
		if (md5Bytes!=null)
			if (!md5.equals(new String(md5Bytes)))
			{
				fileIO.deleteFile();
				FileIO.imageFileIO(cmd).deleteFile();
			}
		//首先从SD卡抓取数据
		byte[] data = FileIO.imageFileIO(cmd).readFile();
		//如果null则从网络
		if (data==null)
		{
			data = getBytes(cmd);
			FileIO.imageFileIO(cmd).writeFile(data);
		}
		Bitmap image;
		if (data==null) image = null;
		else
		{
			int length = data.length;
			image = BitmapFactory.decodeByteArray(data, 0, length);
		}
		return image;
	}
	public static JSONObject getJSONObject(String cmd)
			throws IOException
	{
		String jsonString = new String(getBytes(cmd));
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	private static byte[] getBytes(String cmd) throws IOException
	{
		String urlStr
				= new StringBuilder("http://eatmanager.xuehan.me/app/v1/json/")
				.append(cmd).toString();

		HttpURLConnection connection
				= (HttpURLConnection)new URL(urlStr).openConnection();
		connection.setConnectTimeout(6000);
		connection.setReadTimeout(12000);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode()!=HttpURLConnection.HTTP_OK)
			return null;

		ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
		byte[] bytePer = new byte[1024];
		int len = 0;
		while((len = connection.getInputStream().read(bytePer))!=-1)
		{
			outPutStream.write(bytePer, 0, len);
		}
		return outPutStream.toByteArray();
	}
	/*
	 * 
	private static String getMd5(byte[] data)
	{
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md5.update(data);
		byte[] value = md5.digest();
		StringBuffer strBuffer = new StringBuffer();
		  for (int i = 0; i < value.length; i++) {
		   strBuffer.append(Integer.toHexString(0xff & value[i]));
		  }
		  return strBuffer.toString();
	}
	 */
}