package com.myandb.singsong;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.fragment.HomeFragment;
import com.myandb.singsong.fragment.WebViewFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class Router {
	
	private Intent intent;
	
	public Router(Context context, Uri uri) {
		intent = new Intent();
		try {
			intent.setClass(context, getTargetClass(uri.getHost()));
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, getTargetFragmentName(uri.getPath()));
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, isRootFragment(uri.getPath()));
			if (uri.getQuery() != null) {
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, getBundle(uri.getQuery()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private Class<?> getTargetClass(String host) {
		if (host.equals("root")) {
			return RootActivity.class;
		} else if (host.equals("up")) {
			return UpActivity.class;
		}
		return null;
	}
	
	private String getTargetFragmentName(String path) {
		if (path.equals("/home")) {
			return HomeFragment.class.getName();
		} else if (path.equals("/web")) {
			return WebViewFragment.class.getName();
		}
		return null;
	}
	
	private boolean isRootFragment(String path) {
		if (path.equals("/home")) {
			return true;
		}
		return false;
	}
	
	private Bundle getBundle(String query) throws UnsupportedEncodingException {
		final String charset = "UTF-8";
		
		Bundle bundle = new Bundle();
		
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			final int index = pair.indexOf("=");
			final String key = URLDecoder.decode(pair.substring(0, index), charset);
			final String value = URLDecoder.decode(pair.substring(index + 1), charset);
			bundle.putString(key, value);
		}
		return bundle;
	}
	
	public Intent getIntent() {
		return intent;
	}

}
