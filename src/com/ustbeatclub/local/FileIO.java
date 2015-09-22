package com.ustbeatclub.local;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

public class FileIO {
	private File file;
	private boolean sdcardState;
	private FileIO(String pathName, String fileName)
	{
		sdcardState = Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);
		if (!sdcardState) return;

		File path = new File(Environment.getExternalStorageDirectory(),
				new StringBuilder("/Android/data/com.ustbeatclub.jinchisha/files/")
						.append(pathName).toString());
		path.mkdirs();
		file = new File(path, fileName);
	}
	public static FileIO imageFileIO(String cmd)
	{
		String fileName = cmd.replace("?", " ").replace("/", " ");
		return new FileIO("images",
				new StringBuilder(fileName).append(".ewt").toString());
	}
	public static FileIO errorFileIO(String fileName)
	{
		return new FileIO("errors",
				new StringBuilder(fileName).append(".log").toString());
	}
	//不安全
	public static FileIO reviewFileIO(String fileName)
	{
		return new FileIO("reviews",
				new StringBuilder(fileName).append(".txt").toString());
	}
	public void writeFile(byte[] value) throws IOException
	{
		if (!sdcardState) return;
		FileOutputStream fOS = null;
		fOS = new FileOutputStream(file);
		fOS.write(value);
		fOS.flush();
		fOS.close();
	}
	public byte[] readFile() throws IOException
	{
		if (!sdcardState) return null ;
		if(!file.exists()) return null;
		FileInputStream fIS = new FileInputStream(file);
		InputStream inputStream
				= new BufferedInputStream(fIS);
		byte[] data = new byte[inputStream.available()];
		inputStream.read(data);
		if( inputStream != null ) inputStream.close();
		fIS.close();
		return data;
	}
	public void deleteFile()
	{
		if (!sdcardState) return;
		if(!file.exists()) return;
		file.delete();
	}
}