package com.myandb.singsong.fragment;

import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;
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
				String imageUrl = urlBuilder.s("img").s("ic_launcher-web.png").toString();
				final KakaoLink kakaoLink = KakaoLink.getKakaoLink(getActivity());
				final KakaoTalkLinkMessageBuilder builder = kakaoLink.createKakaoTalkLinkMessageBuilder();
				final String message = builder
						.addText(getString(R.string.invitation_message_header) + "\n"
								+ getString(R.string.invitation_message_footer))
						.addImage(imageUrl, 150, 150)
						.addWebButton("둘러보기", new UrlBuilder().s("w").s("invitation").toString())
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
