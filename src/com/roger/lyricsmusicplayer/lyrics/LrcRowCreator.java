package com.roger.lyricsmusicplayer.lyrics;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcRowCreator {

	private static final String TAG = LrcRowCreator.class.getSimpleName();

	public static ArrayList<LrcRow> createRows(String rawLine) {
		Pattern pt = Pattern.compile("\\d+:\\d+(\\.?\\d*)?");
		Matcher matcher = pt.matcher(rawLine);
		 ArrayList<LrcRow> rowsAll = new ArrayList<LrcRow>();
		try {
			while (matcher.find()) {
				String time = matcher.group();
				int last = rawLine.lastIndexOf(']');
				String content = rawLine.substring(last + 1);
				LrcRow row = new LrcRow(time, timeConvert(time), content);
				rowsAll.add(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rowsAll;
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
