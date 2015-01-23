package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Artist;
import com.myandb.singsong.model.User;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ArtistAdapter extends HolderAdapter<Artist, ArtistAdapter.ArtistHolder> {
	
	public enum LayoutType {
		
		NORMAL(R.layout.row_artist, false, true),
		
		SIMPLE(R.layout.row_artist_simple, true, false);
		
		private int layoutResId;
		private boolean showNum;
		private boolean showIntroduction;
		
		LayoutType(int layoutResId, boolean showNum, boolean showIntroduction) {
			this.layoutResId = layoutResId;
			this.showNum = showNum;
			this.showIntroduction = showIntroduction;
		}
		
		public int getLayoutResId() {
			return layoutResId;
		}
		
		public boolean isShowNum() {
			return showNum;
		}
		
		public boolean isShowIntroduction() {
			return showIntroduction;
		}
		
	}
	
	private LayoutType layoutType;
	
	public ArtistAdapter() {
		this(LayoutType.NORMAL);
	}
	
	public ArtistAdapter(LayoutType layoutType) {
		super(Artist.class);
		this.layoutType = layoutType;
	}
	
	@Override
	public ArtistHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(layoutType.getLayoutResId(), parent, false);
		return new ArtistHolder(view);
	}
	
	@Override
	public void onBindViewHolder(Context context, ArtistHolder viewHolder, Artist artist, int position) {
		final User user = artist.getUser();
		if (user == null) {
			return;
		}
		
		viewHolder.tvUserNickname.setText(user.getNickname());
		if (user.getProfile() != null) {
			viewHolder.tvFollowersNum.setText(String.valueOf(user.getProfile().getFollowersNum()));
		}
		viewHolder.view.setOnClickListener(artist.getArtistClickListener());
		
		if (layoutType.isShowNum() && position < 99) {
			viewHolder.tvArtistNum.setText(String.valueOf(artist.getId() + 1));
		}
		
		if (layoutType.isShowIntroduction()) {
			CharSequence content = viewHolder.tvInterviewTitle.getText();
			SpannableString underlined = new SpannableString(content);
			underlined.setSpan(new UnderlineSpan(), 0, underlined.length(), 0);
			viewHolder.tvInterviewTitle.setText(underlined);
			viewHolder.tvArtistIntroduction.setText(artist.getIntroduction());
		}
		
		ImageHelper.displayPhoto(user, viewHolder.ivArtistPhoto);
	}

	public static final class ArtistHolder extends ViewHolder {
		
		public TextView tvUserNickname;
		public TextView tvFollowersNum;
		public TextView tvArtistNum;
		public TextView tvArtistIntroduction;
		public TextView tvInterviewTitle;
		public ImageView ivArtistPhoto;

		public ArtistHolder(View view) {
			super(view);
			
			tvUserNickname = (TextView) view.findViewById(R.id.tv_artist_user_nickname);
			tvFollowersNum = (TextView) view.findViewById(R.id.tv_artist_followers_num);
			tvArtistNum = (TextView) view.findViewById(R.id.tv_artist_num);
			tvArtistIntroduction = (TextView) view.findViewById(R.id.tv_artist_introduction);
			tvInterviewTitle = (TextView) view.findViewById(R.id.tv_interview_title);
			ivArtistPhoto = (ImageView) view.findViewById(R.id.iv_artist_photo);
		}
		
	}
	
}
