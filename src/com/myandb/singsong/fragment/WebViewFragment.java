package com.myandb.singsong.fragment;

import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.myandb.singsong.R;
import com.myandb.singsong.Router;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.net.HttpScheme;
import com.myandb.singsong.net.MelonHttpScheme;
import com.myandb.singsong.net.SingSongHttpScheme;
import com.myandb.singsong.net.StringRequest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
		
		HttpScheme melonHttpScheme = new MelonHttpScheme();
		headers = melonHttpScheme.getHeaders();
		HttpScheme singSongHttpScheme = new SingSongHttpScheme();
		headers = singSongHttpScheme.getHeaders(headers);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void setupViews(Bundle savedInstanceState) {
		switch (type) {
		case OA:
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			getActivity().finish();
			return;
			
		default:
		case PA:
		case IA:
			if (Build.VERSION.SDK_INT >= 19) {
		        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		    }
			webView.getSettings().setJavaScriptEnabled(true); 
			webView.setWebViewClient(new WebViewClientClass());
			loadUrl(url);
			break;
		}
	}
	
	private void loadUrl(String url) {
		if (isMelonUrl(url)) {
			webView.loadUrl(url, headers);
		} else {
			webView.loadUrl(url, headers);
		}
	}
	
	private boolean isMelonUrl(String url) {
		return url.startsWith("https://m.melon.com:4554");
	}
	
	@SuppressWarnings("unused")
	private void postUrl(String url) {
		StringRequest request = new StringRequest(
				Request.Method.POST, url, 
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						webView.loadDataWithBaseURL("https://m.melon.com:4554", response, "text/html; charset=utf-8", "utf-8", null);
					}
				},
				new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						error.printStackTrace();
						Toast.makeText(getActivity(), "웹뷰 로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
					}
				});
		
		addRequest(request);
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
					loadUrl(url);
					return true;
				}
			}
			
			return false;
		}
	}

}
