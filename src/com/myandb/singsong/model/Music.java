package com.myandb.singsong.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;

import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.event.ActivateOnlyClickListener;
import com.myandb.singsong.fragment.KaraokeFragment;
import com.myandb.singsong.fragment.SelectRecordModeFragment;
import com.myandb.singsong.util.Utility;

public class Music extends Model {
	
	public static final int PART_INVALID = 0;
	public static final int PART_MALE = 1;
	public static final int PART_FEMALE = 2;
	
	private String singer;
	private String title;
	private String part_male;
	private String part_female;
	private List<Song> songs;
	private int is_dynamic;
	private int sing_num;
	private int is_nxing_api;
	
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
	
	public String getMuseOnlineLrcUrl() {
		return STORAGE_HOST + STORAGE_MUSEONLINE_LRC + getId() + SUFFIX_LRC;
	}
	
	public String getNxingLrcUrl() {
		return STORAGE_HOST + STORAGE_NXING_LRC + getId() + SUFFIX_LRC;
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
	
	public List<Song> getSongs() {
		return songs != null ? songs : new ArrayList<Song>();
	}
	
	public boolean isLyricDynamic() {
		return is_dynamic == 1;
	}
	
	public boolean isNxing() {
		return is_nxing_api == 1;
	}
	
	public OnClickListener getMusicClickListener() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(SelectRecordModeFragment.EXTRA_MUSIC, Music.this.toString());
				Intent intent = new Intent(v.getContext(), UpActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SelectRecordModeFragment.class.getName());
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				intent.putExtra(UpActivity.EXTRA_FULL_SCREEN, true);
				((BaseActivity) v.getContext()).changePage(intent);
			}
		};
	}
	
	public OnClickListener getMaleRecordClickListener() {
		return new RecordClickListener(PART_MALE);
	}
	
	public OnClickListener getFemaleRecordClickListener() {
		return new RecordClickListener(PART_FEMALE);
	}
	
	private class RecordClickListener extends ActivateOnlyClickListener {
		
		private int part;
		
		public RecordClickListener(int part) {
			this.part = part;
		}

		@Override
		public void onActivated(View v, User user) {
			Bundle bundle = new Bundle();
			bundle.putString(KaraokeFragment.EXTRA_MUSIC, Music.this.toString());
			bundle.putInt(KaraokeFragment.EXTRA_PART, part);
			Intent intent = new Intent(v.getContext(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, KaraokeFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			intent.putExtra(UpActivity.EXTRA_FULL_SCREEN, true);
			intent.putExtra(UpActivity.EXTRA_SHOULD_STOP, true);
			v.getContext().startActivity(intent);
		}
		
	}
	
}
