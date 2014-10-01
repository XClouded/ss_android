package com.myandb.singsong.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.model.Activity;
import com.myandb.singsong.model.Notification;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Auth;
import com.myandb.singsong.util.ImageHelper;
import com.myandb.singsong.util.Utility;

public class NotificationAdapter extends AutoLoadAdapter<Notification> {
	
	private User currentUser;

	public NotificationAdapter(Context context) {
		super(context, Notification.class, true);
		
		currentUser = Auth.getUser();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final NotificationHolder notificationHolder;
		final Notification notification = (Notification) getItem(position);
		final Activity activity = notification.getActivity();
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_notification, null);
			
			notificationHolder = new NotificationHolder();
			
			notificationHolder.tvNotificationContent = (TextView) view.findViewById(R.id.tv_notification_content);
			notificationHolder.tvCreatedTime = (TextView) view.findViewById(R.id.tv_created_time);
			notificationHolder.ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
			
			view.setTag(notificationHolder);
		} else {
			notificationHolder = (NotificationHolder) view.getTag();
		}
		
		if (activity != null) {
			final User activityCreator = activity.getCreator();
			
			Spannable nicknameSpan = new SpannableString(activityCreator.getNickname());
			Utility.getStyleSpan(nicknameSpan, Typeface.BOLD);
			
			notificationHolder.tvNotificationContent.setText(nicknameSpan);
			notificationHolder.tvNotificationContent.append("´ÔÀÌ ");
			notificationHolder.tvNotificationContent.append(notification.getContent(currentUser));
			
			notificationHolder.tvCreatedTime.setText(notification.getWorkedCreatedTime(getCurrentDate()));
			
			ImageHelper.displayPhoto(activityCreator, notificationHolder.ivUserPhoto);
			
			view.setOnClickListener(Listeners.getSourceClickListener(getContext(), notification));
		}
		
		return view;
	}
	
	public static class NotificationHolder {
		
		public ImageView ivUserPhoto;
		public TextView tvCreatedTime;
		public TextView tvNotificationContent;
		
	}

}
