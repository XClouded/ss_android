package com.myandb.singsong.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.model.Notice;
import com.myandb.singsong.util.Utility;

public class FrontNoticeDialog extends BaseDialog {
	
	public static final String EXTRA_NOTICE = "notice";
	
	private WebView webView;
	private Button btnClose;
	private Notice notice;
	
	@Override
	protected int getResourceId() {
		return R.layout.dialog_front_notice;
	}
	
	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		Gson gson = Utility.getGsonInstance();
		String noticeInJson = bundle.getString(EXTRA_NOTICE);
		notice = gson.fromJson(noticeInJson, Notice.class);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		webView = (WebView) view.findViewById(R.id.webview);
		btnClose = (Button) view.findViewById(R.id.btn_close);
	}

	@Override
	protected void initialize(Activity activity) {
		getDialog().setCanceledOnTouchOutside(false);
		getDialog().setCancelable(false);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void setupViews() {
		webView.getSettings().setJavaScriptEnabled(true); 
		webView.loadUrl(notice.getFrontImageUrl());
		webView.setWebViewClient(new WebViewClientClass());
		setWebViewHeight(webView, 0.8f);
		
		btnClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	private void setWebViewHeight(WebView webView, float ratio) {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) webView.getLayoutParams();
		lp.height = (int) (getScreenHeight() * ratio);
		webView.setLayoutParams(lp);
	}
	
	private int getScreenHeight() {
		return getResources().getDisplayMetrics().heightPixels;
	}
	
	private static class WebViewClientClass extends WebViewClient {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
