package com.roger.lyricsmusicplayer.lyrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.roger.lyricsmusicplayer.lyrics.iterfa.ILrcBuilder;

import android.util.Log;

public class DefaultLrcBuilder implements ILrcBuilder {
	static final String TAG = "DefaultLrcBuilder";
	public ArrayList<LrcRow> getLrcRows(String rawLrc) {
		Log.d(TAG, "getLrcRows by rawString");
		if (rawLrc == null || rawLrc.length() == 0) {
			Log.e(TAG, "getLrcRows rawLrc null or empty");
			return null;
		}
		StringReader reader = new StringReader(rawLrc);
		BufferedReader br = new BufferedReader(reader);
		String line = null;
		ArrayList<LrcRow> rows = new ArrayList<LrcRow>();
		try {
			do {
				line = br.readLine();
				if (line != null && line.length() > 0) {
					List<LrcRow> lrcRows = LrcRowBuilder.createRows(line);
					if (lrcRows != null && lrcRows.size() > 0) {
						for (LrcRow row : lrcRows) {
							rows.add(row);
						}
					}
				}

			} while (line != null);
			if (rows.size() > 0) {
				Collections.sort(rows);
			}

		} catch (Exception e) {
			Log.e(TAG, "parse exceptioned:" + e.getMessage());
			return null;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			reader.close();
		}
		return rows;
	}
}
