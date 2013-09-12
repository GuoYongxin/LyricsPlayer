package com.roger.lyricsmusicplayer.lyrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class LrcRowCreator {

	private static final String TAG = LrcRowCreator.class.getSimpleName();

	public static LrcRow createRows(String rawLine) {
		Pattern pt = Pattern.compile("\\d{2}:\\d{2}.\\d{2}");
		Matcher matcher = pt.matcher(rawLine);
		try {
			if (matcher.find()) {
				String time = matcher.group();
				int end = matcher.end();
				String content = rawLine.substring(end + 1);
				LrcRow row = new LrcRow(time, timeConvert(time), content);
				return row;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static long timeConvert(String timeString) {
		if (timeString == null) {
			return 0l;
		}
		timeString = timeString.replace('.', ':');
		String[] times = timeString.split(":");
		return Integer.valueOf(times[0]) * 60 * 1000
				+ Integer.valueOf(times[1]) * 1000 + Integer.valueOf(times[2]);
	}
}
