package com.anjoyo.qq_music;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anjoyo.netmusic.DownMusicSax;
import com.anjoyo.netmusic.MKAdapter;
import com.anjoyo.netmusic.MusicFileDown;
import com.anjoyo.netmusic.MusicModel;
import com.anjoyo.netmusic.MusicSax;
import com.anjoyo.resource.GetMusicData;
import com.anjoyo.resource.MusicUtils;
import com.anjoyo.service.MediaPlayService;



import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings.Global;
import android.text.method.ScrollingMovementMethod;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.ToggleButton;

public class TabHostActivity extends Activity implements OnItemClickListener{
	RadioGroup radioGroup;
	List<Map<String, Object>> data1;
	View view1, view2, view3, view4, view5, viewminiplay, mini_play_btn;
	public static TextView song_num, song_nameView, singer_nameView;
	public static Button playButton;
	MediaPlayService mediaPlayService;
	int play_state, pesition;
	URL album;
	long musicAlbum_ID;// 歌曲专辑ID
	long musicID;// 歌曲ID
	Bitmap bm;
	public static ImageView mini_play_image;

	// net
	ArrayList<MusicModel> listData;

	MusicSax ms = new MusicSax();// sax解析类
	MKAdapter musicAdapter;
	MusicModel m;
	ListView listView;
	View oldview;	
	ImageView imgDown;
	public static int musicindex = -1;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_tabhost);
		setView();
		
		
		// 文本滚动
		song_nameView.setMovementMethod(ScrollingMovementMethod.getInstance());
		//net
		setview();
		new MusicThread().start();		
		
		//
		final TabHost tabHost = (TabHost) findViewById(R.id.main_tabhost);
		tabHost.setup();

		TabSpec tabSpec1 = tabHost.newTabSpec("Tab1");
		tabSpec1.setIndicator(createTabIndication(tabHost, "我的音乐",
				R.drawable.my_music_self_normal));
		tabSpec1.setContent(R.id.tab1);
		tabHost.addTab(tabSpec1);

		TabSpec tabSpec2 = tabHost.newTabSpec("Tab2");
		tabSpec2.setIndicator(createTabIndication(tabHost, "音乐馆",
				R.drawable.my_music_libry_normal));
		tabSpec2.setContent(R.id.tab2);
		tabHost.addTab(tabSpec2);

		TabSpec tabSpec3 = tabHost.newTabSpec("Tab3");
		tabSpec3.setIndicator(createTabIndication(tabHost, "乐库",
				R.drawable.my_music_box_normal));
		tabSpec3.setContent(R.id.tab3);
		tabHost.addTab(tabSpec3);

		TabSpec tabSpec4 = tabHost.newTabSpec("Tab4");
		tabSpec4.setIndicator(createTabIndication(tabHost, "更多",
				R.drawable.my_music_setting_normal));
		tabSpec4.setContent(R.id.tab4);
		tabHost.addTab(tabSpec4);
		tabHost.setCurrentTab(0);
		tabHost.getTabWidget().getChildAt(0)
				.setBackgroundResource(R.drawable.topbar_bg_clicked);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("Tab1")) {

					tabHost.getTabWidget()
							.getChildAt(0)
							.setBackgroundResource(R.drawable.topbar_bg_clicked);
					tabHost.getTabWidget().getChildAt(1)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
					tabHost.getTabWidget().getChildAt(2)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
					tabHost.getTabWidget().getChildAt(3)
							.setBackgroundResource(R.drawable.main_top_bar_bg);

				} else if (tabId.equals("Tab2")) {

					tabHost.getTabWidget().getChildAt(0)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
					tabHost.getTabWidget()
							.getChildAt(1)
							.setBackgroundResource(R.drawable.topbar_bg_clicked);
					tabHost.getTabWidget().getChildAt(2)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
					tabHost.getTabWidget().getChildAt(3)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
				} else if (tabId.equals("Tab3")) {

					tabHost.getTabWidget().getChildAt(0)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
					tabHost.getTabWidget().getChildAt(1)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
					tabHost.getTabWidget()
							.getChildAt(2)
							.setBackgroundResource(R.drawable.topbar_bg_clicked);
					tabHost.getTabWidget().getChildAt(3)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
				} else if (tabId.equals("Tab4")) {

					tabHost.getTabWidget().getChildAt(0)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
					tabHost.getTabWidget().getChildAt(1)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
					tabHost.getTabWidget().getChildAt(2)
							.setBackgroundResource(R.drawable.main_top_bar_bg);
					tabHost.getTabWidget()
							.getChildAt(3)
							.setBackgroundResource(R.drawable.topbar_bg_clicked);
				}
			}
		});

		view1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(TabHostActivity.this,
						SongActivity.class);
				startActivity(intent);
			}
		});
		view2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		view3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		view4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		view5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		song_num.setText("共有" + GetMusicData.GetMusicData(this).size() + "首歌");

		mini_play_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (play_state == 1) {
					Intent intent = new Intent(TabHostActivity.this,
							MediaPlayService.class);
					intent.putExtra("what", "pause");
					intent.putExtra("pesition", pesition);
					startService(intent);
					play_state = 0;
					playButton
							.setBackgroundResource(R.drawable.mini_pause_button);
				} else if (play_state == 0) {
					Intent intent = new Intent(TabHostActivity.this,
							MediaPlayService.class);
					intent.putExtra("what", "restart");
					intent.putExtra("pesition", pesition);
					startService(intent);
					play_state = 1;
					playButton
							.setBackgroundResource(R.drawable.mini_play_button_pressed);
				}

			}
		});
		viewminiplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TabHostActivity.this,
						MainPlaying.class);
				startActivity(intent);
			}
		});
		// 发送广播给服务在服务中跟新该页UI
		Intent intent = new Intent();
		intent.setAction("UI");
		intent.putExtra("UISTATE", "1");
		sendBroadcast(intent);

		IntentFilter filter = new IntentFilter();
		filter.addAction("isplay");
		filter.addAction("info");
		registerReceiver(receiver, filter);

	};

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("isplay")) {
				play_state = intent.getIntExtra("state", 0);
				if (play_state == 1) {
					playButton
							.setBackgroundResource(R.drawable.mini_play_button);
					
				} else {
					playButton
							.setBackgroundResource(R.drawable.mini_pause_button_pressed);
				}
			}
			if (intent.getAction().equals("info")) {
				// 第一次根据歌曲索引跟新UI
				pesition = intent.getIntExtra("pesition", -1);
				song_nameView.setText(data1.get(pesition).get("TITLE")
						.toString());
				singer_nameView.setText(data1.get(pesition).get("SINGER")
						.toString());

				musicID = Integer.parseInt(data1.get(pesition).get("ID")
						.toString());
				musicAlbum_ID = Integer.parseInt(data1.get(pesition)
						.get("ALBUMID").toString());

				bm = MusicUtils.getArtWork(TabHostActivity.this, musicID,
						musicAlbum_ID, true);
				mini_play_image.setImageBitmap(bm);
			}
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	public View createTabIndication(TabHost tabHost, String title, int icon) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.activity_main_tabitem, null, false);
		ImageButton imageView = (ImageButton) view
				.findViewById(R.id.maintab_icon);
		TextView textView = (TextView) view.findViewById(R.id.maintab_text);
		imageView.setBackgroundResource(icon);
		textView.setText(title);
		return view;
	}

	public void setView() {

		data1 = GetMusicData.GetMusicData(this);
		view1 = findViewById(R.id.all_music_view);
		view2 = findViewById(R.id.download_music_view);
		view3 = findViewById(R.id.folder_music_view);
		view4 = findViewById(R.id.favourite_music_view);
		view5 = findViewById(R.id.create_music_view);
		song_num = (TextView) findViewById(R.id.allmusic_text2);
		playButton = (Button) findViewById(R.id.play_or_pause);
		song_nameView = (TextView) findViewById(R.id.song_name);
		singer_nameView = (TextView) findViewById(R.id.singer_name);
		viewminiplay = findViewById(R.id.click_to_playing);
		mini_play_image = (ImageView) findViewById(R.id.mini_image);
		mini_play_btn = findViewById(R.id.mini_play_btn);		

	}
	public void setview() {
		listView = (ListView) findViewById(R.id.netmusic_downloadlist);
		listView.setOnItemClickListener(this);
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_settings2:
			NotificationManager manager=(NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
			manager.cancel(10);
			Intent intent = new Intent(TabHostActivity.this,
					MediaPlayService.class);
			stopService(intent);
			
			System.exit(0);  

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			musicAdapter = new MKAdapter(TabHostActivity.this, listData);
			listView.setAdapter(musicAdapter);

		};
	};
	
	class MusicThread extends Thread {
		@Override
		public void run() {

			// 获得网络连接
			MusicFileDown f = new MusicFileDown();

			// 拿到路径
			String resultVal = f.DownLoad("http://192.168.133.211:9999/music/songs.xml");

			// 开始解析
			DownMusicSax dm = new DownMusicSax();
			dm.Sax(resultVal, ms);

			// 填充集合
			listData = MusicSax.getListData();

			// 发送消息
			Message mg = handler.obtainMessage();
			mg.sendToTarget();

		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}
