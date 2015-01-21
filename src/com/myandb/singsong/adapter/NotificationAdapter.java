package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Activity;
import com.myandb.singsong.model.Notification;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;

public class NotificationAdapter extends HolderAdapter<Notification, NotificationAdapter.NotificationHolder> {
	
	private User currentUser;

	public NotificationAdapter() {
		super(Notification.class);
		
		currentUser = Authenticator.getUser();
	}

	@Override
	public NotificationHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_notification, parent, false);
		return new NotificationHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, NotificationHolder viewHolder, int position) {
		final Notification notification = getItem(position);
		final Activity activity = notification.getActivity();
		final User activityCreator = activity.getCreator();
		
		viewHolder.tvNotificationContent.setText("");
		for (CharSequence charSequence : notification.getContent(currentUser)) {
			viewHolder.tvNotificationContent.append(charSequence);
		}
		viewHolder.tvCreatedTime.setText(notification.getWorkedCreatedTime(getCurrentDate()));
		
		ImageHelper.displayPhoto(activityCreator, viewHolder.ivUserPhoto);
		
		viewHolder.view.setOnClickListener(Listeners.getSourceClickListener(context, notification));
	}
	
	public static final class NotificationHolder extends ViewHolder {
		
		public ImageView ivUserPhoto;
		public TextView tvCreatedTime;
		public TextView tvNotificationContent;
		
		public NotificationHolder(View view) {
			super(view);
			
			tvNotificationContent = (TextView) view.findViewById(R.id.tv_notification_content);
			tvCreatedTime = (TextView) view.findViewById(R.id.tv_created_time);
			ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		}
		
	}

}
