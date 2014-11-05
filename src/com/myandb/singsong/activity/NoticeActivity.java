package com.myandb.singsong.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.myandb.singsong.R;
import com.myandb.singsong.net.UrlBuilder;

public class NoticeActivity extends OldBaseActivity {
	
	private WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		webView = (WebView) findViewById(R.id.webview);
		
		UrlBuilder urlBuilder = new UrlBuilder();
		webView.getSettings().setJavaScriptEnabled(true); 
		webView.loadUrl(urlBuilder.s("w").s("notices").toString());
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
		return R.layout.activity_notice;
	}

	@Override
	protected boolean isRootActivity() {
		return false;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return true;
	}

}
