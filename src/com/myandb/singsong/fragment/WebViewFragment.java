package com.myandb.singsong.fragment;

import java.util.Map;

import com.myandb.singsong.R;
import com.myandb.singsong.Router;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.net.HttpHeaderScheme;
import com.myandb.singsong.net.MelonHttpHeaderScheme;
import com.myandb.singsong.net.SingSongHttpHeaderScheme;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
	
	public enum WebViewType {
		
		IA,
		
		PA,
		
		OA
		
	}
	
	public static final String EXTRA_TYPE = "type";
	public static final String EXTRA_URL = "url";
	
	private WebView webView;
	private String url;
	private WebViewType type = WebViewType.IA;
	private Map<String, String> headers;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_webview;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		url = bundle.getString(EXTRA_URL);
		String typeInString = bundle.getString(EXTRA_TYPE);
		if (typeInString != null) {
			try {
				type = WebViewType.valueOf(bundle.getString(EXTRA_TYPE));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		webView = (WebView) view.findViewById(R.id.webview);
	}

	@Override
	protected void initialize(Activity activity) {
		CookieSyncManager.createInstance(activity);
		CookieManager.getInstance().removeAllCookie();
		
		HttpHeaderScheme melonHttpHeaderScheme = new MelonHttpHeaderScheme();
		headers = melonHttpHeaderScheme.getHeaders();
		HttpHeaderScheme singSongHttpHeaderScheme = new SingSongHttpHeaderScheme();
		headers = singSongHttpHeaderScheme.getHeaders(headers);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void setupViews(Bundle savedInstanceState) {
		switch (type) {
		default:
		case PA:
		case IA:
			webView.getSettings().setJavaScriptEnabled(true); 
			webView.setWebViewClient(new WebViewClientClass());
			webView.loadUrl(url, headers);
			
			break;
			
		case OA:
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			getActivity().finish();
			break;
		}
	}

	@Override
	protected void onDataChanged() {}

	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) { 
			webView.goBack(); 
        } else {
        	super.onBackPressed();
        }
	}
	
	private class WebViewClientClass extends WebViewClient {
		
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
					view.loadUrl(url, headers);
					return true;
				}
			}
			
			return false;
		}
	}

}
