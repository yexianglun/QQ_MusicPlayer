package com.anjoyo.netmusic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class MusicFileDown {
	/**
	 * �����ļ�����ȡ�ļ����ݣ�ֻ�������ı��ļ���
	 * */
	public String DownLoad(String path) {
		try {
			String line;
			StringBuilder sb = new StringBuilder();
			URL url = new URL(path); // ������Ҫ���ʵĵ�ַ
			HttpURLConnection httpconn = (HttpURLConnection) url
					.openConnection(); // ͨ��HttpURLConnection������
			InputStream inputS = httpconn.getInputStream(); // �õ�һ������������
			// �����������뵽�����������õ�һ��������
			BufferedReader b = new BufferedReader(new InputStreamReader(inputS,
					"gb2312"));
			while ((line = b.readLine()) != null) { // ѭ����ȡÿһ�У������ǰ�������ݣ���׷�ӵ��ַ�����
				sb.append(line);
			}
			System.out.println(sb.toString()
					+ "1111111111111111111111-------------");
			return sb.toString();
		} catch (IOException e) {
			Log.i("DownLoad", "--------error--------------");
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * ��ȡ����ͼƬ
	 */
	public Bitmap GetImgToIntent(String path) {
		HttpURLConnection httpconn = null;
		InputStream inputS = null;
		BufferedReader b = null;
		try {
			URL url = new URL(path); // ������Ҫ���ʵĵ�ַ
			// 1 ��������
			httpconn = (HttpURLConnection) url.openConnection(); // ͨ��HttpURLConnection������
			// 2 ��������е����ݻ�ȡ��buffer��������
			inputS = httpconn.getInputStream(); // �õ�һ������������
			BufferedInputStream buff = new BufferedInputStream(inputS);
			return BitmapFactory.decodeStream(buff);
		} catch (IOException e) {
			System.out.println("img-------error----" + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

}
