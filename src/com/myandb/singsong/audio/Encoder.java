package com.myandb.singsong.audio;

import java.io.IOException;
import java.util.Arrays;

import android.os.AsyncTask;

import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.libogg.VorbisFileOutputStream;

public class Encoder extends AsyncTask<Track, Integer, Exception> {
	
	private static final int BUFFER_SIZE = 1024 * 16;
	
	private OnCompleteListener completeListener;
	private OnProgressListener progressListener;
	private String outputFileName;
	private VorbisFileOutputStream outputStream;
	private Track[] tracks;
	private boolean interrupted;
	
	@Override
	protected Exception doInBackground(Track... tracks) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 1);
		
		try {
			for (Track track : tracks) {
				track.startStream();
			}
			outputStream = new VorbisFileOutputStream(outputFileName);
			
			int read = 0;
			int totalRead = 0;
			int durationInMilli = (int) tracks[0].getDuration();
			int durationInSec = durationInMilli / 1000;
			int estimateLength = durationInSec * PcmPlayer.SAMPLERATE * PcmPlayer.CHANNELS;
			int estimateLengthInPercent = estimateLength / 100;
			int preProgressInPercent = 0;
			int currentProgressInPercent = 0;
			short[] pcm = new short[BUFFER_SIZE];
			
			while (!interrupted && read != -1) {
				Arrays.fill(pcm, (short) 0);
				
				for (Track track : tracks) {
					read = track.read(pcm);
					if (read == -1) {
						break;
					}
				}
				outputStream.write(pcm, 0, read);
				
				totalRead += read;
				currentProgressInPercent = Math.round(totalRead / estimateLengthInPercent);
				
				if (currentProgressInPercent > preProgressInPercent) {
					preProgressInPercent = currentProgressInPercent;
					publishProgress(currentProgressInPercent);
				}
			}
			
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
		releaseResources();
		
		if (!interrupted && completeListener != null) {
			completeListener.done(result);
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		releaseResources();
	}
	
	private void releaseResources() {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				outputStream = null;
			}
		}
		
		if (tracks != null) {
			for (Track track : tracks) {
				try {
					track.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void stop() {
		interrupted = true;
		cancel(true);
	}
	
	public void setOutputFileName(String fileName) {
		this.outputFileName = fileName;
	}
    
    public void setOnCompleteListener(OnCompleteListener completeListener) {
    	this.completeListener = completeListener;
    }
    
    public void setOnProgressListener(OnProgressListener progressListener) {
    	this.progressListener = progressListener;
    }

}
