package com.myandb.singsong.fragment;

import com.myandb.singsong.GoogleStore;
import com.myandb.singsong.R;
import com.myandb.singsong.Store;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.net.UrlBuilder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class CommunityFragment extends BaseFragment {
	
	private View vLinkNotice;
	private View vLinkFaq;
	private View vLinkFree;
	private View vLinkMr;
	private View vLinkFacebook;
	private View vLinkReview;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_community;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		vLinkNotice = view.findViewById(R.id.rl_link_notice);
		vLinkFaq = view.findViewById(R.id.rl_link_faq);
		vLinkFree = view.findViewById(R.id.rl_link_free);
		vLinkMr = view.findViewById(R.id.rl_link_mr);
		vLinkFacebook = view.findViewById(R.id.rl_link_facebook);
		vLinkReview = view.findViewById(R.id.rl_link_review);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		vLinkNotice.setOnClickListener(webLinkClickListener);
		vLinkFaq.setOnClickListener(webLinkClickListener);
		vLinkFree.setOnClickListener(webLinkClickListener);
		vLinkMr.setOnClickListener(webLinkClickListener);
		vLinkFacebook.setOnClickListener(facebookClickListener);
		vLinkReview.setOnClickListener(storeClickListener);
	}
	
	private OnClickListener webLinkClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, WebViewFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			UrlBuilder builder = new UrlBuilder().s("w").s("board").s("list");
			
			switch (v.getId()) {
			case R.id.rl_link_notice:
				builder.s("1");
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "공지사항");
				break;
				
			case R.id.rl_link_faq:
				builder.s("2");
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "FAQ");
				break;
				
			case R.id.rl_link_free:
				builder.s("3");
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "콜라보 게시판");
				break;
				
			case R.id.rl_link_mr:
				builder.s("4");
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "MR 요청 게시판");
				break;

			default:
				return;
			}
			
			bundle.putString(WebViewFragment.EXTRA_WEBVIEW_URL, builder.toString());
			startFragment(intent);
		}
	};
	
	private OnClickListener facebookClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String url = "https://www.facebook.com/collabokaraoke";
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}
	};
	
	private OnClickListener storeClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Store store = new GoogleStore(getActivity().getPackageName());
			store.move(getActivity());
		}
	};

	@Override
	protected void onDataChanged() {}

}
