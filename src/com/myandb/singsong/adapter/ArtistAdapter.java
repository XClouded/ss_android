package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Artist;
import com.myandb.singsong.model.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ArtistAdapter extends HolderAdapter<Artist, ArtistAdapter.ArtistHolder> {
	
	public ArtistAdapter() {
		super(Artist.class);
	}
	
	@Override
	public ArtistHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_artist, parent, false);
		return new ArtistHolder(view);
	}
	
	@Override
	public void onBindViewHolder(Context context, ArtistHolder viewHolder, int position) {
		final Artist artist = getItem(position);
		final User user = artist.getUser();
		
		viewHolder.tvUserNickname.setText(user.getNickname());
		viewHolder.tvArtistNickname.setText(artist.getNickname());
//		viewHolder.tvFollowersNum.setText(user.getProfile().getFollowersNum());
		viewHolder.tvArtistNum.setText(String.valueOf(artist.getId()));
		viewHolder.tvArtistIntroduction.setText(artist.getIntroduction());
		viewHolder.view.setOnClickListener(artist.getArtistClickListener());
		
		ImageHelper.displayPhoto(user, viewHolder.ivArtistPhoto);
	}

	public static final class ArtistHolder extends ViewHolder {
		
		public TextView tvUserNickname;
		public TextView tvArtistNickname;
		public TextView tvFollowersNum;
		public TextView tvArtistNum;
		public TextView tvArtistIntroduction;
		public ImageView ivArtistPhoto;

		public ArtistHolder(View view) {
			super(view);
			
			tvUserNickname = (TextView) view.findViewById(R.id.tv_artist_user_nickname);
			tvArtistNickname = (TextView) view.findViewById(R.id.tv_artist_nickname);
			tvFollowersNum = (TextView) view.findViewById(R.id.tv_artist_followers_num);
			tvArtistNum = (TextView) view.findViewById(R.id.tv_artist_num);
			tvArtistIntroduction = (TextView) view.findViewById(R.id.tv_artist_introduction);
			ivArtistPhoto = (ImageView) view.findViewById(R.id.iv_artist_photo);
		}
		
	}
	
}
