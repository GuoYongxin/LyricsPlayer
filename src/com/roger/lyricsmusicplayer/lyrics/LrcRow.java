package com.roger.lyricsmusicplayer.lyrics;

import com.roger.lyricsmusicplayer.lyrics.iterfa.Range;

import android.util.Log;

public class LrcRow implements Comparable<LrcRow> {
	public final static String TAG = "LrcRow";

	public long time;
	public String content;
	public String strTime;
	private Range range;
	public LrcRow() {
		
	}

	public LrcRow(String strTime, long time, String content) {
		this.strTime = strTime;
		this.time = time;
		this.content = content;
		Log.d(TAG, "strTime:" + strTime + " time:" + time + " content:" + content);
	}


	public int compareTo(LrcRow another) {
		return (int) (this.time - another.time);
	}

	@Override
	public String toString() {
		return strTime + ":" + time + ":" + content;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

}