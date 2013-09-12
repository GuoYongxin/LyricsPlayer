package com.roger.lyricsmusicplayer.lyrics;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.methods.HttpGet;

import android.os.Handler;
import android.util.Log;

public class LrcDownloader {

	private String requestURL;
	private LrcDownloaderHook hook;
	private int TIME_OUT = 3000;
	private Handler mHandler;
	public LrcDownloader(String url, LrcDownloaderHook hook, Handler handler) {
		this.requestURL = url;
		this.hook = hook;
		this.mHandler = handler;
	}

	public void startDownLoad() {
		HttpURLConnection connection = null;
		StringBuilder builder = new StringBuilder();
		try {
			URL url = new URL(this.requestURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(HttpGet.METHOD_NAME);
			connection.setRequestProperty("User-Agent", "Roger_Anroid_Device");
			connection.setConnectTimeout(TIME_OUT);
			connection.setReadTimeout(TIME_OUT);
			connection.connect();
			InputStream is = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(reader);
			String line;
			while ((line = br.readLine()) != null) {
				builder.append(line + "\n");
			}
			final String content = builder.toString();
			Log.v(requestURL, content);
			if (content != null && content.length() > 0) {
				if (mHandler != null) {
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							hook.onSuccess(content);
						}
					});
				} else {
					hook.onSuccess(content);
				}
			}
			br.close();
			connection.disconnect();

		} catch (final Exception e) {
			hook.onFail(e);
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						hook.onFail(e);
					}
				});
			} else {
				hook.onFail(e);
			}
		}
	}

	public interface LrcDownloaderHook {
		public void onSuccess(String content);

		public void onFail(Exception e);
	}
}
