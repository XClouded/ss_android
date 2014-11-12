package com.myandb.singsong.activity;

import java.io.Serializable;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.FriendsAdapter;
import com.myandb.singsong.adapter.MusicAdapter;
import com.myandb.singsong.adapter.SimpleSongAdapter;
import com.myandb.singsong.net.UrlBuilder;

public class SearchActivity extends OldBaseActivity {
	
	public enum SearchType {
		USER, SONG_ROOT, SONG_LEAF, MUSIC
	}
	
	public static final String INTENT_SEARCH_TYPE = "_search_type_";
	
	private UrlBuilder urlBuilder;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Serializable serializable = getIntent().getSerializableExtra(INTENT_SEARCH_TYPE);
		
		if (serializable != null) {
			final SearchType type = (SearchType) serializable;
			final ImageView ivSearch = (ImageView) findViewById(R.id.iv_search);
			final ImageView ivClear = (ImageView) findViewById(R.id.iv_et_clear);
			final EditText etSearch = (EditText) findViewById(R.id.et_search);
			listView = (ListView) findViewById(R.id.lv_full_width);
			
			final ListAdapter adapter = null;
			listView.setAdapter(adapter);
			if (type.equals(SearchType.USER) || type.equals(SearchType.MUSIC)) {
				final ColorDrawable divider = new ColorDrawable(getResources().getColor(R.color.divider));
				listView.setDivider(divider);
				listView.setDividerHeight(1);
			}
			
			etSearch.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s.toString().length() > 0) {
						ivClear.setVisibility(View.VISIBLE);
					} else {
						ivClear.setVisibility(View.GONE);
						
//						performInitial(type, adapter);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
				
			});
			
			ivSearch.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String keyword = etSearch.getText().toString();
					
					if (keyword.isEmpty()) {
						Toast.makeText(SearchActivity.this, getString(R.string.t_keyword_empty), Toast.LENGTH_SHORT).show();
					} else {
						SearchActivity.this.closeEditText(etSearch, false);
						
						urlBuilder = getUrlBuilder(type);
						urlBuilder.keyword(keyword).p("order", "created_at");
						
//						adapter.resetRequest(urlBuilder);
//						listView.initializeScroll();
					}
				}
			});
			
			ivClear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					etSearch.setText("");
				}
			});
			
//			performInitial(type, adapter);
		} else {
			finish();
		}
	}
	
	/*
	private void performInitial(SearchType type, AutoLoadAdapter<?> adapter) {
		if (type.equals(SearchType.MUSIC)) {
			urlBuilder = getUrlBuilder(type);
			urlBuilder.p("order", "title").p("otype", "asc");
			
			adapter.resetRequest(urlBuilder);
			listView.initializeScroll();
		}
	}
	
	private AutoLoadAdapter<?> getAdapter(SearchType type) {
		switch (type) {
		case USER:
//			return new FriendsAdapter();
			
		case SONG_ROOT:
		case SONG_LEAF:
//			return new SimpleSongAdapter(this);
			
		case MUSIC:
			return null;new MusicBasicAdapter(this);

		default:
			break;
		}
		
		return null;
	}
	*/
	
	private UrlBuilder getUrlBuilder(SearchType type) {
		final UrlBuilder urlBuilder = new UrlBuilder();
		
		switch (type) {
		case USER:
			return urlBuilder.s("users").p("req[]", "profile");
			
		case SONG_ROOT:
			return urlBuilder.s("songs").s("root");
			
		case SONG_LEAF:
			return urlBuilder.s("songs").s("leaf");
			
		case MUSIC:
			return urlBuilder.s("musics");

		default:
			return null;
		}
	}

	@Override
	protected int getChildLayoutResourceId() {
		return R.layout.activity_search;
	}

	@Override
	protected boolean isRootActivity() {
		return false;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return false;
	}

}
