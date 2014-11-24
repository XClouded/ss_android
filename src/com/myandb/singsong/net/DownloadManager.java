package com.myandb.singsong.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
	private InputStream inputStream;
	private OutputStream outputStream;
	private HttpURLConnection connection;
	private boolean interrupt = false;
	
	@Override
	protected Exception doInBackground(File... params) {
		final int bufferSize = 16384;
			
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			int dataTotalBytes = 0;
			
			connection.connect();
			inputStream = connection.getInputStream();
			outputStream = new FileOutputStream(params[0]);
			
			dataTotalBytes = connection.getContentLength();
			
			if (dataTotalBytes < 0) {
				throw new Exception("invalid content length");
			}
			
			byte[] buffer = new byte[bufferSize]; 
			int totalBytesRead = 0;
			int read = 0;
			int bytesInPercent = dataTotalBytes / 100;
			int prePercent = 0;
			int currentPercent = 0;
			
			while ((read = inputStream.read(buffer)) > 0 && !interrupt) {
				outputStream.write(buffer, 0, read);
				
				totalBytesRead += read;
				currentPercent = totalBytesRead / bytesInPercent;
				if (currentPercent > prePercent && progressListener != null) {
					prePercent = currentPercent;
					publishProgress(Integer.valueOf(currentPercent));
				}
			}

			connection.disconnect();
			
			return null;
		} catch (Exception e) {
			return e;
		}
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
		if (progressListener != null) {
			progressListener.done(values[0]);
		}
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		releaseResources();
	}

	@Override
	protected void onPostExecute(Exception result) {
		super.onPostExecute(result);
		releaseResources();
		
		if (completeListener != null) {
			completeListener.done(result);
		}
	}
	
	private void releaseResources() {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			inputStream = null;
		}
		
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outputStream = null;
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
		interrupt = true;
		this.cancel(true);
	}

}
