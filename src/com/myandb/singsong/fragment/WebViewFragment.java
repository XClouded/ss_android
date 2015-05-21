package com.myandb.singsong.fragment;

import java.util.HashMap;
import java.util.Map;

import com.myandb.singsong.R;
import com.myandb.singsong.Router;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.secure.Authenticator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressWarnings("deprecation")
public class WebViewFragment extends BaseFragment {
	
	public static final String EXTRA_TYPE = "type";
	public static final String EXTRA_URL = "url";
	
	private WebView webView;
	private String url;
	private Map<String, String> header;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_webview;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		url = bundle.getString(EXTRA_URL);
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
	protected void setupViews(Bundle savedInstanceState) {
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
			if (url != null) {
				if (url.startsWith("singsong://")) {
					BaseActivity context = (BaseActivity) view.getContext();
					if (context != null) {
						Router router = new Router(context, Uri.parse(url));
						context.changePage(router.getIntent());
						return true;
					}
				} else {
					view.loadUrl(url);
					return true;
				}
			}
			
			return false;
		}
	}

}
