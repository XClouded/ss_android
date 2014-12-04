package com.myandb.singsong.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.myandb.singsong.App;
import com.myandb.singsong.event.OnCompleteListener;

public class UploadManager extends AsyncTask<File, Integer, Exception> {
	
	private OnCompleteListener onCompleteListener;
	private File file;
	private String contentType;
	private String tempUrl;

	@Override
	protected Exception doInBackground(File... params) {
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("android");
		HttpPut httpPut = new HttpPut(tempUrl);
		httpPut.setEntity(new FileEntity(params[0], contentType));
		
		try {
			httpClient.execute(httpPut);
		} catch (IOException e) {
			return e;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Exception result) {
		super.onPostExecute(result);
		
		if (onCompleteListener != null) {
			onCompleteListener.done(result);
		}
	}
	
	public void start(Context context, File file, String bucket, String fileName,
			String contentType, OnCompleteListener onCompleteListener) throws FileNotFoundException {
		
		if (file != null && file.exists()) {
			this.file = file;
			this.contentType = contentType;
			this.onCompleteListener = onCompleteListener;
			
			requestUploadUrl(context, bucket, fileName);
		} else {
			throw new FileNotFoundException();
		}
	}
	
	private void requestUploadUrl(Context context, String bucket, String fileName) {
		UrlBuilder urlBuilder = new UrlBuilder();
		String url = urlBuilder.s("uploads").s("url").s(bucket).s(fileName).toString();
		
		JSONObjectRequest request = new JSONObjectRequest(
				Method.GET, url, null,
				
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						try {
							tempUrl = response.getString("temp_url");
							
							execute(file);
						} catch (JSONException e) {
							if (onCompleteListener != null) {
								onCompleteListener.done(e);
							}
						}
					}
				},
				
				new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						if (onCompleteListener != null) {
							onCompleteListener.done(new Exception());
						}
					}
				}
		);
		
		RequestQueue queue = ((App) context.getApplicationContext()).getQueueInstance();
		queue.add(request);
	}
	
}
