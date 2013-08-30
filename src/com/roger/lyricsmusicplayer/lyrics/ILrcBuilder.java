package com.roger.lyricsmusicplayer.lyrics;

import java.util.ArrayList;

public interface ILrcBuilder {
	ArrayList<LrcRow> getLrcRows(String rawLrc);
}
