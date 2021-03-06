package com.roger.lyricsmusicplayer.lyrics;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.roger.lyricsmusicplayer.lyrics.iterfa.ILrcDisplay;
import com.roger.lyricsmusicplayer.lyrics.iterfa.ISeekalble;
import com.roger.lyricsmusicplayer.lyrics.iterfa.Range;

public class LrcView extends View implements ISeekalble, ILrcDisplay {

	private static final String TAG = "LrcView";
	private ArrayList<LrcRow> mLrcAll;
	private int mCurrentRow = 0;
	private Paint mPaint;
	private int mFontSize = 26;
	private int mPaddingY = 10;
	private int mHighLightColor = Color.YELLOW;
	private int mNormalColor = Color.BLACK;
	private int maxCharPerRow;
	private final String NO_LRC_TIPS = "No Lrc right row";
	private final String DOWNLOAD_FAILED_TIPS = "Lrc download failed";
	private String mDisplay_Tips;
	private int mHeight;
	private int mWidth;
	private int rowX;
	public LrcView(Context context, AttributeSet attr) {
		super(context, attr);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(mFontSize);
		mDisplay_Tips = NO_LRC_TIPS;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		maxCharPerRow = getParentWidth() / getFontSize();
		Log.v(TAG, "MAX CHARS:" + maxCharPerRow);

		mHeight = getHeight();
		mWidth = getWidth();

		rowX = mWidth / 2;
		int rowY;
		int row;
		if (mLrcAll == null || mLrcAll.size() == 0) {
			Log.v(TAG, "noLrc");
			displayTips(canvas);
			return;

		}

		// draw highlight row
		int highlightRowY = mHeight / 2 - mFontSize;
		LrcRow currentRow;
		currentRow = mLrcAll.get(mCurrentRow);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setColor(mHighLightColor);
		displayLrcRow(currentRow, canvas, mPaint, highlightRowY, Direction.DOWN);

		// draw before
		row = mCurrentRow - 1;
		rowY = currentRow.getRange().upBound;
		mPaint.setColor(mNormalColor);
		while (row >= 0 && rowY > -3 * mFontSize) {
			LrcRow rowTodraw = mLrcAll.get(row);
			displayLrcRow(rowTodraw, canvas, mPaint, rowY, Direction.UP);
			row--;
			rowY = rowTodraw.getRange().upBound;
		}

		// draw after
		row = mCurrentRow + 1;
		rowY = currentRow.getRange().lowBound;
		while (row < mLrcAll.size() && rowY < 3 * mHeight) {
			LrcRow rowTodraw = mLrcAll.get(row);
			displayLrcRow(rowTodraw, canvas, mPaint, rowY, Direction.DOWN);
			row++;
			rowY = rowTodraw.getRange().lowBound;
		}
		super.onDraw(canvas);
	}

	private void displayTips(Canvas canvas) {
		int y = mHeight / 2 - mFontSize;
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setColor(mHighLightColor);
		canvas.drawText(mDisplay_Tips, rowX, y, mPaint);
	}

	private void seekToRow(int pos) {
		if (pos == mCurrentRow)
			return;
		mCurrentRow = pos;
		Log.v(TAG, "Seek To Row:" + pos);
		// invalidate();
		startAnimation();
	}

	@Override
	public void seekToTime(long milliSec) {
		if (mLrcAll == null || mLrcAll.size() == 0)
			return;
		int low = 0;
		int high = mLrcAll.size() - 1;
		int mid;
		while (low != high) {
			mid = (low + high) / 2;
			if (mLrcAll.get(mid).time < milliSec) {
				low = mid + 1;
			} else {
				high = mid;
			}
		}
		if (mLrcAll.get(low).time != milliSec && low != 0) {
			low = low - 1;
		}
		seekToRow(low);
	}

	private void startAnimation() {

		LrcRow row = mLrcAll.get(mCurrentRow);
		long currentTime = row.time;
		TranslateAnimation ta = new TranslateAnimation(0, 0, 0, 0
				- row.getRange().lines * (mPaddingY + mFontSize));
		int nextRow = mCurrentRow == mLrcAll.size() - 1
				? mCurrentRow
				: mCurrentRow + 1;

		long nextTime = mLrcAll.get(nextRow).time;
		ta.setDuration(nextTime - currentTime);
		ta.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				invalidate();
			}
		});
		this.startAnimation(ta);
	}

	public ArrayList<LrcRow> getLrcAll() {
		return mLrcAll;
	}

	public void setLrcAll(ArrayList<LrcRow> mLrcAll) {
		this.mLrcAll = mLrcAll;
	}

	@Override
	public int getFontSize() {
		return mFontSize;
	}

	@Override
	public int getParentWidth() {
		return getWidth();
	}

	@Override
	public void displayLrcRow(LrcRow row, Canvas canvas, Paint paint,
			int yCurrent, Direction direction) {
		if (row == null || row.content == null)
			return;
		Log.v(TAG, "Begine draw row");
		float enCharWidth = paint.getTextSize() / 2;
		int width = getWidth();
		String content = row.content;
		int length = 0;
		int size = 0;
		int indexFrom = 0;
		List<String> strings = new ArrayList<String>();
		for (int i = 0; i < content.length(); i++) {
			char ch = content.charAt(i);
			size = isChinese(ch) ? 2 : 1;
			if (length + enCharWidth * 4 > width) {
				String str = content.substring(indexFrom, i);
				indexFrom = i;
				length = 0;
				strings.add(str);
			}
			length += size * enCharWidth;
			if (i == content.length() - 1) {
				String str = content.substring(indexFrom);
				strings.add(str);
			}
		}

		Log.v(TAG, "lines:" + strings.size());
		int lines = strings.size();
		int y = yCurrent;
		Range range = new Range();
		range.lines = lines;
		if (direction == Direction.UP) {
			y -= lines * (getFontSize() + mPaddingY);
			range.upBound = y;
			range.lowBound = yCurrent;
		} else {
			y += lines * (getFontSize() + mPaddingY);
			range.upBound = yCurrent;
			range.lowBound = y;
		}
		if (direction == Direction.DOWN) {
			y = yCurrent;
		}

		for (int i = 0; i < lines; i++) {
			canvas.drawText(strings.get(i), rowX, y, mPaint);
			y += getFontSize() + mPaddingY;

		}
		row.setRange(range);

		return;
	}
	public boolean isChinese(char a) {
		int v = (int) a;
		return (v >= 19968 && v <= 171941);
	}

}
