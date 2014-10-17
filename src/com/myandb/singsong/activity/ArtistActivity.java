package com.myandb.singsong.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.myandb.singsong.R;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Auth;

public class ArtistActivity extends BaseActivity {
	
	private WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		webView = (WebView) findViewById(R.id.webview);
		
		Map<String, String> header = new HashMap<String, String>();
		header.put("oauth-token", Auth.getAccessToken());
		
		UrlBuilder urlBuilder = UrlBuilder.getInstance();
		webView.getSettings().setJavaScriptEnabled(true); 
		webView.loadUrl(urlBuilder.l("w").l("apply-candidate").build(), header);
		webView.setWebViewClient(new WebViewClientClass());  
	}

	private class WebViewClientClass extends WebViewClient {
		
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	view.loadUrl(url);
        	return true;
        }
    }

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) { 
			webView.goBack(); 
        } else {
        	super.onBackPressed();
        }
	}

	@Override
	protected int getChildLayoutResourceId() {
		return R.layout.activity_artist;
	}

	@Override
	protected boolean isRootActivity() {
		return false;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return false;
	}
	
}
