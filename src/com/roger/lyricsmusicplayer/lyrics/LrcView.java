package com.roger.lyricsmusicplayer.lyrics;

import java.util.ArrayList;

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

public class LrcView extends View {

	private static final String TAG = "LrcView";
	private ArrayList<LrcRow> mLrcAll;
	private int mCurrentRow = 0;
	private Paint mPaint;
	private int mFontSize = 26;
	private int mPaddingY = 10;
	// private int mPaddingX = 0;
	private int mHighLightColor = Color.YELLOW;
	private int mNormalColor = Color.BLACK;

	public LrcView(Context context, AttributeSet attr) {
		super(context, attr);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(mFontSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mLrcAll == null || mLrcAll.size() == 0) {
			Log.v(TAG, "noLrc");
			return;
		}
		int height = getHeight();
		int width = getWidth();

		final int rowX = width / 2;
		int rowY;
		int row;

		// draw highlight row
		int highlightRowY = height / 2 - mFontSize;
		String highlightText = mLrcAll.get(mCurrentRow).content;
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setColor(mHighLightColor);
		canvas.drawText(highlightText, rowX, highlightRowY, mPaint);

		// draw before
		row = mCurrentRow - 1;
		rowY = highlightRowY - mPaddingY - mFontSize;
		mPaint.setColor(mNormalColor);
		while (row >= 0 && rowY > -mFontSize) {
			String text = mLrcAll.get(row).content;
			canvas.drawText(text, rowX, rowY, mPaint);
			row--;
			rowY -= (mPaddingY + mFontSize);
		}

		// draw after
		row = mCurrentRow + 1;
		rowY = highlightRowY + mPaddingY + mFontSize;
		while (row < mLrcAll.size() && rowY < height) {
			String text = mLrcAll.get(row).content;
			canvas.drawText(text, rowX, rowY, mPaint);
			row++;
			rowY += (mPaddingY + mFontSize);
		}
		super.onDraw(canvas);
	}

	public void seekToRow(int pos) {
		if (pos == mCurrentRow)
			return;
		mCurrentRow = pos;
		Log.v(TAG, "Seek To Row:" + pos);
		// invalidate();
		startAnimation();
	}

	public void seekToTime(long milliSec) {
		if (mLrcAll == null)
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
		// Log.v(TAG, "Seek To time:" + milliSec);
		seekToRow(low);
	}

	private void startAnimation() {
		TranslateAnimation ta = new TranslateAnimation(0, 0, 0, 0 - mPaddingY
				- mFontSize);
		long currentTime = mLrcAll.get(mCurrentRow).time;
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
}
