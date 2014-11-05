package com.myandb.singsong.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.AutoLoadAdapter;
import com.myandb.singsong.adapter.FriendsAdapter;
import com.myandb.singsong.adapter.MyCommentAdapter;
import com.myandb.singsong.adapter.MyLikeSongAdapter;
import com.myandb.singsong.adapter.MySongAdapter;
import com.myandb.singsong.adapter.NotificationAdapter;
import com.myandb.singsong.file.Storage;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.ImageHelper;
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.widget.AutoLoadListView;

public class SimpleListActivity extends OldBaseActivity {
	
	public enum SimpleListType {
		FOLLOWINGS, FOLLOWERS, LIKINGS, COMMENTS, TRASHED, NOTIFICATION
	}
	
	public static final String INTENT_USER = "_user_";
	public static final String INTENT_LIST_TYPE = "_list_type_";
	
	private AutoLoadListView listView;
	private ImageView ivHeaderIcon;
	private TextView tvHeader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		String userJson = getIntent().getStringExtra(INTENT_USER);
		User user = Utility.getGsonInstance().fromJson(userJson, User.class);
		SimpleListType listType = (SimpleListType) getIntent().getSerializableExtra(INTENT_LIST_TYPE);
		
		tvHeader = (TextView) findViewById(R.id.tv_header);
		ivHeaderIcon = (ImageView) findViewById(R.id.iv_header_icon);
		listView = (AutoLoadListView) findViewById(R.id.lv_full_width);
		
		decorateHeader(listType, user);
		
		AutoLoadAdapter<?> adapter = getAdapter(listType);
		listView.setAdapter(adapter);
		
		if (listType.equals(SimpleListType.LIKINGS) || listType.equals(SimpleListType.COMMENTS)) {
			int paddingTop = listView.getPaddingTop();
			int paddingBottom = getResources().getDimensionPixelSize(R.dimen.margin_small);
			
			listView.setDividerHeight(0);
			listView.setPadding(0, paddingTop, 0, paddingBottom);
		}
		
		if (listType.equals(SimpleListType.NOTIFICATION)) {
			Storage storage = new Storage();
			storage.readAllNoti();
		}
		
		UrlBuilder urlBuilder = getUrlBuilder(listType, user);
		adapter.resetRequest(urlBuilder);
	}
	
	private void decorateHeader(SimpleListType type, User user) {
		ivHeaderIcon.setVisibility(View.VISIBLE);
		ImageHelper.displayPhoto(user, ivHeaderIcon);
		
		tvHeader.setText(user.getNickname());
		tvHeader.append("´ÔÀÇ ");
		tvHeader.append(getHeaderText(type));
	}
	
	private String getHeaderText(SimpleListType type) {
		switch (type) {
		case FOLLOWINGS:
			return "ÆÈ·ÎÀ×";
			
		case FOLLOWERS:
			return "ÆÈ·Î¿ö";
			
		case LIKINGS:
			return "°ü½É ³ë·¡";
			
		case COMMENTS:
			return "´ñ±Û";
			
		case TRASHED:
			return "ÈÞÁöÅë";
			
		case NOTIFICATION:
			return "¾Ë¸²";
			
		default:
			return null;
		}
	}
	
	private AutoLoadAdapter<?> getAdapter(SimpleListType type) {
		switch (type) {
		case FOLLOWINGS:
		case FOLLOWERS:
			return new FriendsAdapter(this);

		case LIKINGS:
			return new MyLikeSongAdapter(this);
			
		case COMMENTS:
			return new MyCommentAdapter(this);
			
		case TRASHED:
			return new MySongAdapter(this, true, true);
			
		case NOTIFICATION:
			return new NotificationAdapter(this);
			
		default:
			return null;
		}
	}
	
	private UrlBuilder getUrlBuilder(SimpleListType type, User user) {
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.s("users").s(user.getId());
		
		switch (type) {
		case FOLLOWINGS:
			return urlBuilder.s("followings").p("req[]", "profile").p("order", "friendships.created_at");
			
		case FOLLOWERS:
			return urlBuilder.s("followers").p("req[]", "profile").p("order", "friendships.created_at");
			
		case LIKINGS:
			return urlBuilder.s("songs").s("likings").p("order", "created_at");
			
		case COMMENTS:
			return urlBuilder.s("songs").s("comments").p("order", "created_at");
			
		case TRASHED:
			return urlBuilder.s("songs").s("trash").p("order", "deleted_at");
			
		case NOTIFICATION:
			return urlBuilder.s("notifications").p("order", "updated_at");
			
		default:
			return null;
		}
	}

	@Override
	protected int getChildLayoutResourceId() {
		return R.layout.activity_simple_list;
	}

	@Override
	protected boolean isRootActivity() {
		return false;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return true;
	}

}
