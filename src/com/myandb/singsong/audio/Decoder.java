package com.myandb.singsong.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.os.AsyncTask;

import com.myandb.singsong.audio.AudioConfig.ConfigMode;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.libogg.VorbisFileInputStream;
import com.myandb.singsong.util.StringFormatter;

public class Decoder extends AsyncTask<File, Integer, Exception> {
	
	private boolean interrupted;
	private OnCompleteListener completeListener;
	private OnProgressListener progressListener;

	@Override
	protected Exception doInBackground(File... params) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 1);
		
		try {
			File inputFile = params[0];
			File outputFile = params[1];
			VorbisFileInputStream inputStream = new VorbisFileInputStream(inputFile.getAbsolutePath());
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			AudioConfig audioConfig = new AudioConfig(ConfigMode.PLAY);
			
			int read = 0;
			int totalRead = 0;
			int durationInMilli = StringFormatter.getDuration(inputFile);
			int durationInSec = durationInMilli / 1000;
			int estimateLength = durationInSec * AudioConfig.SAMPLERATE * audioConfig.getChannels();
			int estimateLengthInPercent = estimateLength / 100;
			int preProgressInPercent = 0;
			int currentProgressInPercent = 0;
			short[] data = new short[1024 * 16];
			byte[] buffer = new byte[data.length * 2];
			
			while ( !interrupted && (read = inputStream.read(data)) != -1 ) {
				ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(data);
				outputStream.write(buffer, 0, read * 2);
				
				totalRead += read;
				currentProgressInPercent = Math.round(totalRead / estimateLengthInPercent);
				
				if (currentProgressInPercent > preProgressInPercent) {
					preProgressInPercent = currentProgressInPercent;
					publishProgress(currentProgressInPercent);
				}
			}
			
			inputStream.close();
			outputStream.close();
			
			return null;
		} catch (Exception e) {
			return e;
		}
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		
		if (!interrupted && progressListener != null) {
			progressListener.done(values[0]);
		}
	}
	
	@Override
	protected void onPostExecute(Exception result) {
		super.onPostExecute(result);
		
		if (!interrupted && completeListener != null) {
			completeListener.done(result);
		}
	}
	
	public void setOnCompleteListener(OnCompleteListener completeListener) {
		this.completeListener = completeListener;
	}
	
	public void setOnProgressListener(OnProgressListener progressListener) {
		this.progressListener = progressListener;
	}
	
	public void start(File inputFile, File outputFile) {
		execute(inputFile, outputFile);
	}
	
	public void stop() {
		interrupted = true;
		cancel(true);
	}

}
