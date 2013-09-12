package com.roger.lyricsmusicplayer.activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

import com.roger.lyricsmusicplayer.lyrics.DefaultLrcBuilder;
import com.roger.lyricsmusicplayer.lyrics.LrcDownloader;
import com.roger.lyricsmusicplayer.lyrics.LrcDownloader.LrcDownloaderHook;
import com.roger.lyricsmusicplayer.lyrics.LrcRow;
import com.roger.lyricsmusicplayer.lyrics.LrcView;
import com.roger.lyricsmusicplayer.lyrics.iterfa.ILrcBuilder;
import com.sony.lyricsmusicplayer.R;

public class MainActivity extends Activity
		implements
			MediaPlayerControl,
			OnPreparedListener {
	public static final String TAG = "MainActivity";
	// public static final String FILE = "High Voltage";
	public static final String FILE = "Music";
	public static final int TIME_MSG = 1;
	MediaPlayer mPlayer;
	private MediaController mController;
	private TextView mDisplayText;
	private TextView mDurationText;
	private TextView mCurrentTimeText;
	private LrcView mLrcView;
	private TimerThread mThread;
	Handler handler = new Handler() {
		LrcRow currentRow = null;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case TIME_MSG :
					int currentTime = msg.arg1;
					mCurrentTimeText.setText("CurrentTime: " + currentTime
							+ " " + stringForTime(currentTime));
					mLrcView.seekToTime(currentTime);
					break;
				default :

			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDisplayText = (TextView) findViewById(R.id.main_text);
		mDurationText = (TextView) findViewById(R.id.main_duration);
		mCurrentTimeText = (TextView) findViewById(R.id.main_current);
		mLrcView = (LrcView) findViewById(R.id.main_lrcview);
		initMediaPlayer();
		initLyrics();
		initThread();
	}

	private void initThread() {
		// mThread = new TimerThread();
	}

	private void initMediaPlayer() {
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setOnPreparedListener(this);
		mController = new MediaController(this);
		try {
			String path = "http://mod.cri.cn/eng/features/pik/2013/08/0807pik.mp3";
			mPlayer.setDataSource(path.trim());

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mPlayer.prepare();
			String duration = "Duration: " + mPlayer.getDuration() + "  "
					+ stringForTime(mPlayer.getDuration());
			mDurationText.setText(duration);
			Log.v(TAG, duration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initLyrics() {
		String url = "http://music.baidu.com/data2/lrc/72084890/72084890.lrc";
		final LrcDownloader dl = new LrcDownloader(url,
				new LrcDownloaderHook() {

					@Override
					public void onSuccess(String content) {
						ILrcBuilder builder = new DefaultLrcBuilder();
						ArrayList<LrcRow> rows = builder.getLrcRows(content);
						mLrcView.setLrcAll(rows);
						mLrcView.invalidate();
					}

					@Override
					public void onFail(Exception e) {

					}
				}, handler);

		new Thread() {

			@Override
			public void run() {
				dl.startDownLoad();
			}

		}.start();

	}

	private String getFromAssets(String fileName) {
		try {
			InputStreamReader inputReader = new InputStreamReader(
					getResources().getAssets().open(fileName));
			BufferedReader bufReader = new BufferedReader(inputReader);
			String line = "";
			String Result = "";
			while ((line = bufReader.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				Result += line + "\r\n";
			}
			return Result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private String stringForTime(int timeMs) {
		StringBuilder mFormatBuilder = new StringBuilder();
		Formatter mFormatter = new Formatter(mFormatBuilder,
				Locale.getDefault());
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// the MediaController will hide after 3 seconds - tap the screen to
		// make it appear again
		mController.show();
		return false;
	}

	// MediaControl
	@Override
	public void start() {
		mPlayer.start();
		// if (mThread == null)
		mThread = new TimerThread();
		mThread.start();
	}

	@Override
	public void pause() {
		mThread = null;
		mPlayer.pause();
	}

	@Override
	public int getDuration() {
		// System.out.println(mPlayer.getDuration());
		return mPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mPlayer.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		System.out.println("seekTo:" + pos);
		sendTimeMessage(pos);
		mPlayer.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return ((mPlayer.getCurrentPosition() + 20) * 100)
				/ mPlayer.getDuration();
	}

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return true;
	}

	// onPrepare
	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d(TAG, "onPrepared");
		mController.setMediaPlayer(this);
		mController.setAnchorView(findViewById(R.id.main_root));
		handler.post(new Runnable() {
			public void run() {
				mController.setEnabled(true);
				mController.show();
			}
		});
	}

	private void sendTimeMessage(int time) {
		Message msg = new Message();
		msg.what = TIME_MSG;
		msg.arg1 = time;
		handler.sendMessage(msg);
	}

	private class TimerThread extends Thread {

		private static final int SLEEP_UNIT = 100;

		@Override
		public void run() {
			System.out.println("Thread start");
			while (mPlayer != null && mPlayer.isPlaying()) {
				sendTimeMessage(mPlayer.getCurrentPosition());
				try {
					Thread.sleep(SLEEP_UNIT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Thread end");
		}

	}
}
