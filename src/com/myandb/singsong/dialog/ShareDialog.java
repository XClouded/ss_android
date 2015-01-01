package com.myandb.singsong.dialog;

import com.facebook.Session;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.google.gson.Gson;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.myandb.singsong.R;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ShareDialog extends BaseDialog {
	
	public static final String EXTRA_SONG = "song";
	
	private View vShareFacebook;
	private View vShareKakaotalk;
	private TextView tvShareEtc;
	private String webUrl;
	private String photoUrl;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().setWindowAnimations(R.style.DialogNoAnimation);
		return dialog;
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_share;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		Gson gson = Utility.getGsonInstance();
		String songInJson = bundle.getString(EXTRA_SONG);
		Song song = gson.fromJson(songInJson, Song.class);
		webUrl = new UrlBuilder().s("w").s("player").s(song.getId()).toString();
		photoUrl = song.getMusic().getAlbumPhotoUrl();
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		vShareFacebook = view.findViewById(R.id.ll_share_facebook);
		vShareKakaotalk = view.findViewById(R.id.ll_share_kakaotalk);
		tvShareEtc = (TextView) view.findViewById(R.id.tv_share_etc);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews() {
		vShareFacebook.setOnClickListener(shareClickListener);
		vShareKakaotalk.setOnClickListener(shareClickListener);
		tvShareEtc.setOnClickListener(shareClickListener);
	}
	
	private OnClickListener shareClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_share_facebook:
				shareOnFacebook();
				break;
				
			case R.id.ll_share_kakaotalk:
				shareOnKakaotalk();
				break;
				
			case R.id.tv_share_etc:
				shareOnBasicIntent();
				break;

			default:
				break;
			}
		}
	};
	
	private void shareOnFacebook() {
		if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
		    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
		            .setLink(webUrl)
		            .build();
		    shareDialog.present();
		} else {
		    publishFeedDialog();
		}
	}
	
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", "Facebook SDK for Android");
	    params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	    params.putString("link", webUrl);
	    params.putString("picture", photoUrl);

	    WebDialog feedDialog = (
	            new WebDialog.FeedDialogBuilder(getActivity(),
	                    Session.getActiveSession(),
	                    params)).build();
	    feedDialog.show();
	}
	
	private void shareOnKakaotalk() {
		try {
			final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
			final KakaoTalkLinkMessageBuilder builder = kakaoLink.createKakaoTalkLinkMessageBuilder();
			final String message = builder
					.addText("노래를 들어보세요!")
					.addImage(photoUrl, 300, 100)
					.addWebButton("노래 들어보기", webUrl)
					.build();
			kakaoLink.sendMessage(message, getActivity());
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}
	
	private void shareOnBasicIntent() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, webUrl);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}
}
