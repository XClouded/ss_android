package com.myandb.singsong.net;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.myandb.singsong.event.OnErrorListener;
import com.myandb.singsong.event.OnProgressListener;

import android.os.AsyncTask;

public class DownloadManager extends AsyncTask<String, Integer, Exception> {
	
	private OnDownloadListener listener;
	private InputStream inputStream;
	private OutputStream outputStream;
	private HttpURLConnection connection;
	private File file;
	private boolean interrupt = false;
	
	@Override
	protected Exception doInBackground(String... urls) {
		final int bufferSize = 16384;
		Exception exception = null;
			
		try {
			final String url = urls[0];
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.connect();
			inputStream = connection.getInputStream();
			
			if (file == null) {
				final String filePrefix = String.valueOf(url.hashCode()) + ".download";
				file = File.createTempFile(filePrefix, ".tmp");
			}
			outputStream = new FileOutputStream(file);
			
			int dataTotalBytes = connection.getContentLength();
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
				if (currentPercent > prePercent) {
					prePercent = currentPercent;
					publishProgress(Integer.valueOf(currentPercent));
				}
			}
		} catch (Exception e) {
			exception = e;
		}
		
		closeStream(inputStream);
		closeStream(outputStream);
		connection.disconnect();
		
		return exception;
	}
	
	private void closeStream(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
		if (listener != null) {
			listener.onProgress(values[0]);
		}
	}

	@Override
	protected void onPostExecute(Exception exception) {
		super.onPostExecute(exception);
		
		if (listener == null) {
			return;
		}
		
		if (exception == null) {
			listener.onComplete(file);
		} else {
			listener.onError(exception);
		}
	}
	
	public void start(String url, OnDownloadListener listener) {
		start(url, null, listener);
	}
	
	public void start(String url, File file, OnDownloadListener listener) {
		this.file = file;
		this.listener = listener;
		execute(url);
	}
	
	public void stop() {
		interrupt = true;
	}
	
	public static abstract class OnDownloadListener implements OnErrorListener, OnProgressListener {
		
		public void onComplete(File file) {
		}

		@Override
		public void onProgress(Integer progress) {
		}

		@Override
		public void onError(Exception exception) {
		}
	}

}
