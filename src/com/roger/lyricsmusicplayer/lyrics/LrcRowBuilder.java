package com.roger.lyricsmusicplayer.lyrics;

import java.util.ArrayList;

import android.util.Log;

public class LrcRowBuilder {

	private static final String TAG = LrcRowBuilder.class.getSimpleName();

	public static ArrayList<LrcRow> createRows(String rawLine) {
		try {
			Log.d(TAG, "lrc raw line:" + rawLine);
			if (rawLine.indexOf("[") != 0
					|| rawLine.indexOf("]") != 9) {
				return null;
			}
			int lastIndexOfRightBracket = rawLine.lastIndexOf("]");
			String content = rawLine.substring(
					lastIndexOfRightBracket + 1, rawLine.length());

			String times = rawLine
					.substring(0, lastIndexOfRightBracket + 1)
					.replace("[", "-").replace("]", "-");
			String arrTimes[] = times.split("-");
			ArrayList<LrcRow> listTimes = new ArrayList<LrcRow>();
			for (String temp : arrTimes) {
				if (temp.trim().length() == 0) {
					continue;
				}
				LrcRow lrcRow = new LrcRow(temp, timeConvert(temp), content);
				listTimes.add(lrcRow);
			}
			return listTimes;
		} catch (Exception e) {
			Log.e(TAG, "createRows exception:" + e.getMessage());
			return null;
		}
	}

	private static long timeConvert(String timeString) {
		timeString = timeString.replace('.', ':');
		String[] times = timeString.split(":");
		return Integer.valueOf(times[0]) * 60 * 1000
				+ Integer.valueOf(times[1]) * 1000 + Integer.valueOf(times[2]);
	}
}
