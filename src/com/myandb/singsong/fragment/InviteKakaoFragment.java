package com.myandb.singsong.fragment;

import com.kakao.AppActionBuilder;
import com.kakao.AppActionInfoBuilder;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
import com.kakao.internal.Action;
import com.kakao.internal.AppActionInfo;
import com.myandb.singsong.R;
import com.myandb.singsong.net.UrlBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class InviteKakaoFragment extends BaseFragment {
	
	private View vInviteKakao;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_invite_kakao;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		vInviteKakao = view.findViewById(R.id.ll_invite_kakaotalk);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		vInviteKakao.setOnClickListener(inviteKakaoClickListener);
	}
	
	private OnClickListener inviteKakaoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				UrlBuilder urlBuilder = new UrlBuilder();
				String imageUrl = urlBuilder.s("img").s("playstore.png").toString();
				final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
				final KakaoTalkLinkMessageBuilder builder = kakaoLink.createKakaoTalkLinkMessageBuilder();
				final AppActionInfo appActionInfo = AppActionInfoBuilder.createAndroidActionInfoBuilder().build();
				final Action action = new AppActionBuilder().addActionInfo(appActionInfo).build();
				final String message = builder
						.addText("같이 불러요. 콜라보 노래방")
						.addImage(imageUrl, 300, 100)
						.addAppButton("앱으로 이동", action)
						.build();
				kakaoLink.sendMessage(message, getActivity());
			} catch (KakaoParameterException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void onDataChanged() {}

}
