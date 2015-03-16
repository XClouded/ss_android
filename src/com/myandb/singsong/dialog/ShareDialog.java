package com.myandb.singsong.dialog;

import com.facebook.Session;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.google.gson.Gson;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.myandb.singsong.R;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.Utility;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.listeners.OnLoginListener;

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
	private Song song;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().setWindowAnimations(R.style.S2_Animation_Dialog);
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
		song = gson.fromJson(songInJson, Song.class);
	}
	
	private String getShareContent() {
		String content = "";
		if (song.isRoot()) {
			final User creator = song.getCreator();
			content += creator.getNickname();
			content += "님이 부른 ";
		} else {
			final User creator = song.getCreator();
			final User partner = song.getParentUser();
			content += creator.getNickname();
			content += "님과 ";
			content += partner.getNickname();
			content += "님이 함께 콜라보 한 ";
		}
		
		final Music music = song.getMusic();
		content += music.getSingerName();
		content += "의 ";
		content += music.getTitle();
		content += "\n";
		content += "지금 바로 들어보세요!";
		
		return content;
	}
	
	private String getShareUrl() {
		return new UrlBuilder().s("w").s("p").s(song.getId()).toString();
	}
	
	private String getPhotoUrl() {
		return song.getMusic().getAlbumPhotoUrl();
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
		    		.setName(getString(R.string.app_name))
		    		.setDescription(getShareContent())
		    		.setPicture(getPhotoUrl())
		            .setLink(getShareUrl())
		            .build();
		    shareDialog.present();
		} else {
		    publishFeedDialog();
		}
	}
	
	private void publishFeedDialog() {
		getSimpleFacebook().login(new OnLoginListener() {
			
			@Override
			public void onFail(String reason) {}
			
			@Override
			public void onException(Throwable throwable) {}
			
			@Override
			public void onThinking() {}
			
			@Override
			public void onNotAcceptingPermissions(Type type) {}
			
			@Override
			public void onLogin() {
				Bundle params = new Bundle();
				params.putString("name", getString(R.string.app_name));
				params.putString("description", getShareContent());
				params.putString("link", getShareUrl());
				params.putString("picture", getPhotoUrl());
				
				WebDialog feedDialog = (
						new WebDialog.FeedDialogBuilder(getActivity(),
								Session.getActiveSession(),
								params)).build();
				feedDialog.show();
			}
		});
	}
	
	private void shareOnKakaotalk() {
		try {
			final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
			final KakaoTalkLinkMessageBuilder builder = kakaoLink.createKakaoTalkLinkMessageBuilder();
			final String message = builder
					.addText(getShareContent())
					.addImage(getPhotoUrl(), 300, 300)
					.addWebButton("노래 들어보기", getShareUrl())
					.build();
			kakaoLink.sendMessage(message, getActivity());
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}
	
	private void shareOnBasicIntent() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, getShareContent() + "\n" + getShareUrl());
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}
	
}
