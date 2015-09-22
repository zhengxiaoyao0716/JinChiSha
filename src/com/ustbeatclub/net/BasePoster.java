package com.ustbeatclub.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class BasePoster {
	public static JSONObject getJSONObject(String cmd, String content, int score) throws IOException
	{
		String jsonString = new String(sendPost(cmd, content, score));
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
	private static byte[] sendPost(String cmd, String content, int score) throws IOException
	{
		String urlStr = new StringBuilder("http://eatmanager.xuehan.me/app/v1/json/")
				.append(cmd).toString();

		HttpURLConnection connection = (HttpURLConnection)new URL(urlStr).openConnection();
		connection.setConnectTimeout(6000);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestMethod("POST");

		//设置请求体的类型是文本类型
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Charset", "UTF-8");
		connection.connect();

		//获得输出流，向服务器写入数据
		byte[] contentBytes = new StringBuilder("content=").append(content)
				.append("&rating=").append(score).toString().getBytes("UTF-8");
		OutputStream outputStream = connection.getOutputStream();
		outputStream.write(contentBytes);
		outputStream.close();

		if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK)
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
}