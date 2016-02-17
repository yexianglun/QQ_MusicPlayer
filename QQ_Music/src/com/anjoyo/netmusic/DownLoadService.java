package com.anjoyo.netmusic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.anjoyo.qq_music.R;
import com.anjoyo.qq_music.TabHostActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DownLoadService extends Service {
	// ֪ͨ��
		private NotificationManager updateNotificationManager = null;
		private Notification updateNotification = null;
		/*
		 * http://kongweile.iteye.com/blog/1133013
		 * http://blog.csdn.net/wdaming1986/article/details/7038402
		 * 
		 * RemoteView����һ��view,�����view��������һ��������ʾ�ġ�
		 * ��inflate��layout��Դ�ļ��������ṩ�˿����޸Ĺ�view���ݵ�һЩ�򵥻����Ĳ�����
		 */
		RemoteViews view = null;
		// ֪ͨ����תIntent
		private Intent updateIntent = null;
		private PendingIntent updatePendingIntent = null;

		MusicModel musicHs;

		@Override
		public IBinder onBind(Intent intent) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onCreate() {
			// TODO Auto-generated method stub
			super.onCreate();
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			musicHs = (MusicModel) intent.getSerializableExtra("musicInfo");
			
			// �õ����л�����
			// ���ϵͳ֪ͨ�������updateNotificationManager
			this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// ����һ��Notification
			this.updateNotification = new Notification();

			// �������ع����У����֪ͨ�����ص�������
			updateIntent = new Intent(this, TabHostActivity.class);
			// Activity������ģʽ��
			// http://www.open-open.com/lib/view/open1345173373350.html
			updateIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
					0);
			// ����֪ͨͼ��
			updateNotification.icon = R.drawable.ic_launcher;
			/*
			 * notification.contentIntent:һ��PendingIntent���󣬵��û������״̬���ϵ�ͼ��ʱ��
			 * ��Intent�ᱻ���� * notification.contentView:���ǿ��Բ���״̬����ͼ����Ƿ�һ��view *
			 * notification.deleteIntent ����ǰnotification���Ƴ�ʱִ�е�intent *
			 * notification.vibrate ���ֻ���ʱ������������
			 * 
			 * http://www.cnblogs.com/zenfly/archive/2012/02/09/2343923.html
			 */
			updateNotification.contentIntent = updatePendingIntent;

			view = new RemoteViews(getPackageName(), R.layout.dowanload_notification);
			view.setProgressBar(R.id.pb, 100, 0, false);
			view.setTextViewText(R.id.persent, "����0%");
			view.setTextViewText(R.id.mname, "");
			updateNotification.contentView = view;

			// ����֪ͨ����ʾ����
			updateNotification.tickerText = "�������أ�" + musicHs.getMp3Name();
			
			// ����֪ͨ
			updateNotificationManager.notify(0, updateNotification);

			new Thread(runn).start();// ���ص��߳̿���

			return super.onStartCommand(intent, flags, startId);
		}

		Handler downHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					if (msg.arg1 == 0) {
						updateNotificationManager.cancel(0);
						Toast.makeText(getApplicationContext(), "�������", 3000)
								.show();
						// ֹͣ����
						DownLoadService.this.stopSelf();
					} else if (msg.arg1 == 1) {
						updateNotificationManager.cancel(0);
						Toast.makeText(getApplicationContext(), "�ļ��Ѵ��ڣ���������", 3000)
								.show();
					}

					break;

				case 1:
					view.setProgressBar(R.id.pb, 100, msg.arg1, false);
					view.setTextViewText(R.id.persent, msg.arg1 + "%");

					// ��õ�ǰ�������ص��ļ���
					String name = musicHs.getMp3Name();
					view.setTextViewText(R.id.mname, name);
					updateNotification.contentIntent = updatePendingIntent;
					updateNotificationManager.notify(0, updateNotification);
					break;

				default:
					break;
				}

			};

		};

		Runnable runn = new Runnable() {

			@Override
			public void run() {
				/************************ ���ظ��� *****************************/
				String downPath = "http://192.168.133.211:9999/music/songs/"
						+ musicHs.getMp3FileName();

				System.out.println("·��----��" +"http://192.168.133.211:9999/music/songs/" + "����-----��"
						+ musicHs.getMp3FileName());
				// ����һ�����ļ�����������һ��ʵ���ļ��У�eg:"down"���д�����ظ���
				int reuslt = DownloadFile(downPath, "", musicHs.getMp3FileName());
				System.out.println("00dasd000---------" + musicHs);
				System.out.println("000000---------" + reuslt);
				Message msg = downHandler.obtainMessage();
				msg.what = 0;
				msg.arg1 = reuslt;
				msg.sendToTarget();

			}
		};

		boolean isstart = true;

		/**
		 * ע�⣺�����Ҫд��sd�����������������ļ�������Ȩ�� ���ز��� 1.��û�����ȷ�����ص���··��
		 * 2.����ֽ�����url��HttpURLConnection��inputStream�� 3.ȷ������·��������������Ҫ���ļ��У�
		 * 4.�������ļ������ļ�����ͷ���˵��ļ���ʽƥ�䣬��׺������һ���� 5.��Ҫ��һ��ѭ��ȥ��ȡ�ڶ�����õ��ֽ���
		 * ������ȡ������д����Ĳ������Ŀ��ļ���
		 * 
		 * @param fileName
		 * @return
		 */
		public int DownloadFile(String filel, String path, String fileName) {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			File file = null;

			try {
				// ʵ����SD��������
				FileToSdcard fileUtils = new FileToSdcard();
				// �ж��ļ��Ƿ����
				if (fileUtils.IsFileExists(path + "/" + fileName)) {

					return 1;
				}
				// 2���õ��ֽ�������
				URL url = new URL(filel);
				HttpURLConnection httpconn = (HttpURLConnection) url
						.openConnection();
				inputStream = httpconn.getInputStream();

				// 3����SD���ϴ����ļ���
				fileUtils.CreateSDDir(path);

				// 4����SD���ϴ����ļ�
				file = fileUtils.CreateSDFile(path + "/" + fileName);

				outputStream = new FileOutputStream(file);
				// 5����ȡ�������ö�ȡ����
				byte buffer[] = new byte[10 * 1024];
				int length = 0;
				int tol = 0;
				int totalSize = httpconn.getContentLength();
				while ((length = inputStream.read(buffer)) != -1) {
					tol += length;
					// д
					outputStream.write(buffer, 0, length);

					int downPer = tol * 100 / totalSize;

					if (downPer % 2 == 0 && isstart) {
						Message msg = downHandler.obtainMessage();
						msg.arg1 = downPer;
						msg.what = 1;
						msg.sendToTarget();
						isstart = false;
					}
					if (downPer % 2 == 1) {
						isstart = true;
					}

					Thread.sleep(300);

				}
				// �������������ݷ��ͳ�ȥ
				outputStream.flush();
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			} finally {
				try {
					// �ر�������
					inputStream.close();
					outputStream.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
			return 0;

		}


}
