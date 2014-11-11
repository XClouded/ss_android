package com.myandb.singsong.fragment;

import java.util.HashMap;
import java.util.Map;

import com.myandb.singsong.R;
import com.myandb.singsong.secure.Authenticator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment extends BaseFragment {
	
	public static final String EXTRA_WEBVIEW_URL = "webview_url";
	
	private WebView webView;
	private String url;
	private Map<String, String> header;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_webview;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		url = bundle.getString(EXTRA_WEBVIEW_URL);
	}
	
	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		webView = (WebView) view.findViewById(R.id.webview);
	}

	@Override
	protected void initialize(Activity activity) {
		CookieSyncManager.createInstance(activity);
		CookieManager.getInstance().removeAllCookie();
		
		String tokenHeaderKey = "oauth-token";
		header = new HashMap<String, String>();
		header.put(tokenHeaderKey, Authenticator.getAccessToken());
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void setupViews() {
		webView.getSettings().setJavaScriptEnabled(true); 
		webView.loadUrl(url, header);
		webView.setWebViewClient(new WebViewClientClass());
	}

	@Override
	protected void onDataChanged() {
		// Nothing to run
	}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) { 
			webView.goBack(); 
        } else {
        	super.onBackPressed();
        }
	}
	
	private static class WebViewClientClass extends WebViewClient {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
