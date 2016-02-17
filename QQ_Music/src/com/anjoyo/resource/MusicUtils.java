package com.anjoyo.resource;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.anjoyo.qq_music.R;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public class MusicUtils {
	static Uri sArtworkUri = Uri
			.parse("content://media/external/audio/albumart");
	static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
	static Bitmap mCacheBit;
	/**
	 * �Ӳ�ѯ���ĸ����л�ȡר��ͼƬ
	 * 
	 * @param context
	 *            �������Ķ���
	 * @param song_id
	 *            ������id
	 * @param album_id
	 *            ��ר��id
	 * @param allowdefault
	 *            :boolean ����
	 * @return
	 */
	public static Bitmap getArtWork(Context context, long song_id,
			long album_id, boolean allowdefault) {

		if (album_id < 0) {
			if (song_id >= 0) {
				Bitmap bm = getArtworkFromFile(context, song_id, -1);
				if (bm != null) {
					return bm;
				}

			}
			if (allowdefault) {
				return getDefaultArtWork(context);
			}
			return null;
		}
		ContentResolver cResolver = context.getContentResolver();
		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
		if (uri != null) {
			InputStream in = null;
			try {
				in = cResolver.openInputStream(uri);
				return BitmapFactory.decodeStream(in, null, sBitmapOptions);
			} catch (Exception e) {
				Bitmap bm = getArtworkFromFile(context, song_id, album_id);
				if (bm != null) {
					if (bm.getConfig() == null) {
						bm = bm.copy(Bitmap.Config.RGB_565, false);
						if (bm == null && allowdefault) {
							return getDefaultArtWork(context);
						}
					} else if (allowdefault) {
						bm = getDefaultArtWork(context);
					}
					return bm;
				}

			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}

			}
		}
		return null;
	}

	/**
	 * ����Ĭ��ͼƬ
	 * 
	 * @param context
	 *            ����ǰ�����Ķ���
	 * @return
	 */
	private static Bitmap getDefaultArtWork(Context context) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;

		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(R.drawable.default_play_activity_bg1), null,
				opts);
	}

	/**
	 * ���ôӸ����еõ���ר��ͼƬ
	 * 
	 * @param context
	 *            ����ǰ�����Ķ���
	 * @param song_id
	 *            ������id(musicID)
	 * @param album_id
	 *            :ר��id(musicAlbum_ID)
	 * @return
	 */
	private static Bitmap getArtworkFromFile(Context context, long song_id,
			long album_id) {
		Bitmap bm = null;
		if (album_id < 0 && song_id < 0) {
			throw new IllegalArgumentException("��Ҫһ������id��ר��id");
		}
		try {
			if (album_id < 0) {
				Uri uri = Uri.parse("content://media/external/audio/media/"
						+ song_id + "/albumart");
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");
				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}

			} else {
				Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
				ParcelFileDescriptor pfd = context.getContentResolver()
						.openFileDescriptor(uri, "r");

				if (pfd != null) {
					FileDescriptor fd = pfd.getFileDescriptor();
					bm = BitmapFactory.decodeFileDescriptor(fd);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (bm != null) {
			mCacheBit = bm;
		}
		return bm;
	}
}
