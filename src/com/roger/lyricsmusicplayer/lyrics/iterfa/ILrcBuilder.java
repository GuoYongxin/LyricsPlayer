package com.roger.lyricsmusicplayer.lyrics.iterfa;

import java.util.ArrayList;

import com.roger.lyricsmusicplayer.lyrics.LrcRow;

public interface ILrcBuilder {
	ArrayList<LrcRow> getLrcRows(String rawLrc);
}
