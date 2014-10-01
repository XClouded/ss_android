package com.myandb.singsong.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;

import android.os.AsyncTask;

public class DownloadManager extends AsyncTask<File, Integer, Exception> {
	
	private String url;
	private OnCompleteListener completeListener;
	private OnProgressListener progressListener;
	private boolean isStop = false;
	
	@Override
	protected Exception doInBackground(File... params) {
		final int bufferSize = 16384;
			
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			InputStream inputStream = null;
			OutputStream outputStream = null;
			int dataTotalBytes = 0;
			
			conn.connect();
			inputStream = conn.getInputStream();
			outputStream = new FileOutputStream(params[0]);
			
			dataTotalBytes = conn.getContentLength();
			
			if (dataTotalBytes < 0) {
				inputStream.close();
				outputStream.close();
				
				throw new Exception("invalid content length");
			}
			
			byte[] buffer = new byte[bufferSize]; 
			int totalBytesRead = 0;
			int read = 0;
			int bytesInPercent = dataTotalBytes / 100;
			int prePercent = 0;
			int currentPercent = 0;
			
			while ((read = inputStream.read(buffer)) > 0 && !isStop) {
				outputStream.write(buffer, 0, read);
				
				totalBytesRead += read;
				currentPercent = totalBytesRead / bytesInPercent;
				if (currentPercent > prePercent && progressListener != null) {
					prePercent = currentPercent;
					publishProgress(Integer.valueOf(currentPercent));
				}
			}
			
			inputStream.close();
			outputStream.flush();
			outputStream.close();
			outputStream = null;
		} catch (Exception e) {
			return e;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
		if (progressListener != null) {
			progressListener.done(values[0]);
		}
	}
	
	@Override
	protected void onPostExecute(Exception result) {
		super.onPostExecute(result);
		
		if (completeListener != null) {
			completeListener.done(result);
		}
	}
	
	public void start(String url, File output, OnCompleteListener completeListener) {
		start(url, output, completeListener, null);
	}
	
	public void start(String url, File output, OnCompleteListener completeListener, OnProgressListener progressListener) {
		this.url = url;
		this.completeListener = completeListener;
		this.progressListener = progressListener;
		
		execute(output);
	}
	
	public void stop() {
		isStop = true;
		this.cancel(true);
	}

}
