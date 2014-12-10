package com.myandb.singsong.model;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;

import com.myandb.singsong.util.Utility;

public class Music extends Model {
	
	public static final int PART_INVALID = 0;
	public static final int PART_MALE = 1;
	public static final int PART_FEMALE = 2;
	
	private String singer;
	private String title;
	private String part_male;
	private String part_female;
	private int is_dynamic;
	private int sing_num;
	
	public String getSingerName() {
		return safeString(singer);
	}
	
	public Spannable getWorkedTitle() {
		final Spannable titleSpannable = new SpannableString(getTitle());
		Utility.getRelativeSizeSpan(titleSpannable, 1.3f);
		Utility.getStyleSpan(titleSpannable, Typeface.BOLD);
		
		return titleSpannable;
	}
	
	public String getTitle() {
		return safeString(title);
	}
	
	public String getAlbumPhotoUrl() {
		return STORAGE_HOST + STORAGE_ALBUM + getId() + SUFFIX_JPG;
	}
	
	public String getAudioUrl() {
		return STORAGE_HOST + STORAGE_MUSIC + getId() + SUFFIX_OGG;
	}
	
	public String getLrcUrl() {
		return STORAGE_HOST + STORAGE_LRC + getId() + SUFFIX_LRC;
	}
	
	public String getMalePart() {
		return safeString(part_male);
	}
	
	public String getFemalePart() {
		return safeString(part_female);
	}
	
	public String getWorkedSingNum() {
		return safeString(sing_num);
	}
	
	public int getSingNum() {
		return sing_num;
	}
	
	public boolean isLyricDynamic() {
		return is_dynamic == 1;
	}
	
}
