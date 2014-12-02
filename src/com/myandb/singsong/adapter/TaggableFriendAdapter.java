package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.TaggableFriend;
import com.myandb.singsong.util.Logger;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TaggableFriendAdapter extends HolderAdapter<TaggableFriend, TaggableFriendAdapter.TaggableFriendHolder> {

	public TaggableFriendAdapter() {
		super(TaggableFriend.class);
	}
	
	@Override
	public TaggableFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_taggable, null);
		return new TaggableFriendHolder(view);
	}
	
	@Override
	public void onBindViewHolder(TaggableFriendHolder viewHolder, int position) {
		final TaggableFriend taggable = getItem(position);
		final String photoUrl = taggable.getPicture().getData().getUrl();
		
		ImageHelper.displayPhoto(photoUrl, viewHolder.ivTaggablePhoto);
		viewHolder.tvTaggableName.setText(taggable.getName());
		viewHolder.btnInvite.setTag(taggable.getId());
		viewHolder.btnInvite.setOnClickListener(inviteClickListener);
	}
	
	private OnClickListener inviteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String id = (String) v.getTag();
			Logger.log(id);
		}
	};

	public static final class TaggableFriendHolder extends ViewHolder {
		
		public ImageView ivTaggablePhoto;
		public TextView tvTaggableName;
		public Button btnInvite;

		public TaggableFriendHolder(View view) {
			super(view);
			ivTaggablePhoto = (ImageView) view.findViewById(R.id.iv_taggable_photo);
			tvTaggableName = (TextView) view.findViewById(R.id.tv_taggable_name);
			btnInvite = (Button) view.findViewById(R.id.btn_invite);
		}
		
	}
	
}
