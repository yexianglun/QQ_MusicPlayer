package com.anjoyo.netmusic;

import java.util.ArrayList;

import com.anjoyo.qq_music.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MKAdapter extends BaseAdapter {

	Context myContent;

	ArrayList<MusicModel> mylistData;
	String imgpath;

	public MKAdapter(Context context, ArrayList<MusicModel> listData) {
		this.myContent = context;
		this.mylistData = listData;

	}

	@Override
	public int getCount() {
		return mylistData.size();

	}

	@Override
	public Object getItem(int position) {
		return mylistData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	int clickposition = -1;
	MusicFileDown md = new MusicFileDown();

	public View getView(final int position, View convertView, ViewGroup parent) {
		MyHolder myHolder = null;

		if (convertView == null) {

			myHolder = new MyHolder();
			convertView = LayoutInflater.from(myContent).inflate(
					R.layout.netmusiclistitem, null);
			convertView.setTag(myHolder);
			myHolder.txtmp3Name = (TextView) convertView
					.findViewById(R.id.netmusic_title);
			myHolder.txtmp3Artist = (TextView) convertView
					.findViewById(R.id.netmusic_artist);

			myHolder.mp3_album_image = (ImageView) convertView
					.findViewById(R.id.netmusic_album_image);
			myHolder.imgDown = (TextView) convertView
					.findViewById(R.id.netmusic_download);
		} else {
			myHolder = (MyHolder) convertView.getTag();
		}
		myHolder.txtmp3Name.setText(mylistData.get(position).getMp3Name());
		myHolder.txtmp3Artist.setText(mylistData.get(position).getMp3Artist());

		// ���������¼�����
		myHolder.imgDown.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickposition = position;
				Intent in = new Intent(myContent, DownLoadService.class);
				in.putExtra("musicInfo", mylistData.get(clickposition));
				myContent.startService(in);
				System.out.println("in-------" + in + clickposition);
			}
		});

		// ƴ��ͼƬ·��(NetMusicFinals.MUSIC_IP)��ͼƬ����
		String imgHead ="http://192.168.133.211:9999/music/songs/"
				+ mylistData.get(position).getMp3Image();

		// ��dTask
		myHolder.dTask = new DownLoadTask(myHolder.mp3_album_image);
		// AsyncTask����ִ��execute�����������첽�̵߳�ִ��
		myHolder.dTask.execute(imgHead);
		return convertView;
	}

	private final class MyHolder {
		TextView txtmp3Name;
		TextView txtmp3Artist;
		ImageView mp3_album_image;
		TextView imgDown;
		DownLoadTask dTask;
	}

	// �첽������
	class DownLoadTask extends AsyncTask<String, Integer, Bitmap> {
		Bitmap bm;
		ImageView imageView;

		DownLoadTask(ImageView v) {
			this.imageView = v;
		}

		// doInBackground���������ź�ʱ����
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			MusicFileDown md = new MusicFileDown();
			// ������������ͷ��ؼ�����
			bm = md.GetImgToIntent(params[0]);

			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				imageView.setBackgroundResource(0);
				imageView.setImageBitmap(bm);
			} else {

			}

		}

	}
}