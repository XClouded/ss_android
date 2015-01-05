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
import android.widget.TextView;

public class EtcFragment extends BaseFragment {
	
	private TextView tvLinkNotice;
	private TextView tvLinkFaq;
	private TextView tvLinkFree;
	private TextView tvLinkRequestMr;
	private TextView tvLinkFacebook;
	private TextView tvLinkPlayStore;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_etc;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		tvLinkNotice = (TextView) view.findViewById(R.id.tv_link_notice);
		tvLinkFaq = (TextView) view.findViewById(R.id.tv_link_faq);
		tvLinkFree = (TextView) view.findViewById(R.id.tv_link_free);
		tvLinkRequestMr = (TextView) view.findViewById(R.id.tv_link_request_mr);
		tvLinkFacebook = (TextView) view.findViewById(R.id.tv_link_facebook);
		tvLinkPlayStore = (TextView) view.findViewById(R.id.tv_link_playstore);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		tvLinkNotice.setOnClickListener(webLinkClickListener);
		tvLinkFaq.setOnClickListener(webLinkClickListener);
		tvLinkFree.setOnClickListener(webLinkClickListener);
		tvLinkRequestMr.setOnClickListener(webLinkClickListener);
		tvLinkFacebook.setOnClickListener(facebookClickListener);
		tvLinkPlayStore.setOnClickListener(storeClickListener);
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
			case R.id.tv_link_notice:
				builder.s("1");
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "공지사항");
				break;
				
			case R.id.tv_link_faq:
				builder.s("2");
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "FAQ");
				break;
				
			case R.id.tv_link_free:
				builder.s("3");
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "콜라보 게시판");
				break;
				
			case R.id.tv_link_request_mr:
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
