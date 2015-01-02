package com.myandb.singsong.fragment;

import com.facebook.widget.FacebookDialog.MessageDialogBuilder;
import com.myandb.singsong.GoogleStore;
import com.myandb.singsong.R;
import com.myandb.singsong.Store;
import com.myandb.singsong.net.UrlBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class InviteFacebookFragment extends BaseFragment {
	
	private View vInviteFacebook;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_invite_facebook;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		vInviteFacebook = view.findViewById(R.id.ll_invite_facebook);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		vInviteFacebook.setOnClickListener(inviteFacebookClickListener);
	}
	
	private OnClickListener inviteFacebookClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MessageDialogBuilder builder = new MessageDialogBuilder(getActivity());
			if (builder.canPresent()) {
				showFacebookMessageDialog(builder);
			} else {
				moveToFacebookMessengerOnGoogleStore();
			}
		}
	};
	
	private void showFacebookMessageDialog(MessageDialogBuilder builder) {
		builder.setLink(new UrlBuilder().s("w").s("invitation").toString())
			.setName(getString(R.string.app_name))
			.setCaption(getString(R.string.app_name))
			.setPicture(new UrlBuilder().s("img").s("actionbar_logo.png").toString())
			.setDescription("노래도 부르고 짝도 찾는 콜라보 노래방과 함께 해보세요.")
			.setFragment(this)
			.build()
			.present();
	}
	
	private void moveToFacebookMessengerOnGoogleStore() {
		Store store = new GoogleStore("com.facebook.orca");
		store.move(getActivity());
	}

	@Override
	protected void onDataChanged() {}

}
