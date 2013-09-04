package com.roger.lyricsmusicplayer.lyrics.iterfa;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.roger.lyricsmusicplayer.lyrics.LrcRow;

public interface ILrcDisplay {
	public int getParentWidth();
	public int getFontSize();
	public void displayLrcRow(LrcRow row, Canvas canvas, Paint paint,
			int yCurrent, Direction direction);
	public enum Direction {
		UP, DOWN
	}
}
