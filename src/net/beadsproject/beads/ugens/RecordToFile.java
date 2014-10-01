package net.beadsproject.beads.ugens;
/*
 * This file is part of Beads. See http://www.beadsproject.net for all information.
 */
import java.io.File;
import java.io.IOException;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.audiofile.AudioFile;

/**
 * RecordToFile records audio into a file.
 * 
 * You must {@link #kill() kill} this object when finished to finalise the writing of the file header.
 * 
 * IMPORTANT NOTE: At the moment only the WAVE (*.wav) type is supported.
 * 
 * @beads.category utilities
 * @author bp
 */
public class RecordToFile extends UGen {
	/**
	 * Instantiates a recorder for file recording.
	 * 
	 * @param context 
	 * 				The AudioContext 	
	 * @param numberOfChannels 
	 * 				The number of channels
	 * @param file
	 * 				The file to output to
	 * @param type
	 * 				The type of the file
	 * @throws IOException 
	 * 				if the audio format is not supported on this machine.
	 * 				
	 */
	public RecordToFile(AudioContext context, int numberOfChannels, File file, AudioFile.Type type) throws IOException {
		super(context,numberOfChannels,0);	
		//TODO needs to interact with separate implementation code		
	}
	
	/**
	 * Instantiates a recorder for file recording. Uses the .wav format.
	 * 
	 * @param context 
	 * 				The AudioContext 	
	 * @param numberOfChannels 
	 * 				The number of channels
	 * @param file
	 * 				The file to output to. Extension should be .wav.
	 * @throws IOException if the audio format is not supported on this machine.
	 * 				
	 */
	public RecordToFile(AudioContext context, int numberOfChannels, File file) throws IOException {
		this(context,numberOfChannels,file,AudioFile.Type.WAVE);
	}
	
	@Override
	public void calculateBuffer() {
		//TODO needs implementation code here
	}
	
	public void kill() {
		super.kill();
		//TODO needs implementation code here
	}
}
