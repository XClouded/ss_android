/*
 * This file is part of Beads. See http://www.beadsproject.net for all information.
 */
package net.beadsproject.beads.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import net.beadsproject.beads.core.AudioIO;
import net.beadsproject.beads.core.AudioUtils;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.data.audiofile.AudioFile;
import net.beadsproject.beads.data.audiofile.UnsupportedAudioFileException;

/**
 * A Sample encapsulates audio data, either loaded from an audio file (such as
 * an MP3) or written by a Recorder. <br />
 * The typical use of a Sample is through
 * {@link net.beadsproject.beads.data.SampleManager}. For example, to load an
 * mp3, you would do the following. <br />
 * <br />
 * <code>
 * Sample wicked = SampleManager.sample("wickedTrack.mp3");	
 * </code> <br />
 * <br />
 * 
 * <p>
 * Samples are usually played with a
 * {@link net.beadsproject.beads.ugens.SamplePlayer}. Sample data can also be
 * accessed through the methods: {@link #getFrame(int, float[]) getFrame},
 * {@link #getFrameLinear(double, float[]) getFrameLinear}, and
 * {@link #getFrames(int, float[][]) getFrames}. Sample data can be written
 * with: {@link #putFrame(int, float[]) putFrame} or
 * {@link #putFrames(int, float[][]) putFrames}. <i>However</i> you can only
 * write into a sample if the sample {@link #isWriteable()}, which occurs if the
 * buffering regime is a {@link TotalRegime Total Regime} or has been created
 * with the {@link #Sample(IOAudioFormat, double) empty sample constructor}.
 * </p>
 * 
 * <p>
 * The {@link Regime buffering regime} of the Sample determines how the data is
 * stored and how it is buffered. The {@link Sample.TotalRegime TOTAL} regime is
 * the default. Under this regime, the sample loads all the data from the audio
 * file and stores it in Beads' native format. This is appropriate for most
 * small samples.For longer samples or compressed audio consider using a
 * {@link Sample.TimedRegime TimedRegime}.
 * </p>
 * 
 * <p>
 * A set of handy factory methods for handling common situations is also
 * provided. These are:
 * <ul>
 * <li>{@link Sample.Regime#newTotalRegime()}</li>
 * <li>{@link Sample.Regime#newTotalRegimeNative()}</li>
 * <li>{@link Sample.Regime#newStreamingRegime(long)}</li>
 * <li>{@link Sample.Regime#newStreamingRegimeWithAging(long,long)}</li>
 * </ul>
 * </p>
 * 
 * Current Issues:
 * <ul>
 * <li>Only 16-bit support at the moment.</li>
 * <li>Some issues with the boundary of regions with TimedRegime for MP3 files.</li>
 * </ul>
 * 
 * @beads.category data
 * @see SampleManager
 * @see net.beadsproject.beads.ugens.RecordToSample
 * @author Beads Team
 */
public class Sample implements Runnable {

	static public class Regime {
		/**
		 * Store the sample data in the native bit format.
		 * 
		 * If true then memory is conserved, but a conversion has to be done
		 * every time sample data is requested.
		 * 
		 * If false (the default) then the sample is stored in the internal
		 * format used by Beads. It uses more memory but is faster.
		 */
		public boolean storeInNativeBitDepth;

		private Regime() {
			this(false);
		}

		private Regime(boolean nbp) {
			storeInNativeBitDepth = nbp;
		}

		// Factory methods.
		/**
		 * Loads the entire file at initialisation. This is the default regime.
		 */
		static public TotalRegime newTotalRegime() {
			return new TotalRegime();
		}

		/**
		 * Loads the entire file and keeps it in its native bit depth.
		 */
		static public TotalRegime newTotalRegimeNative() {
			TotalRegime tr = new TotalRegime();
			tr.storeInNativeBitDepth = true;
			return tr;
		}

		/**
		 * Stream the file from disk -- storing the data in the sample. Useful
		 * for a forward sample player that possibly skips to different parts of
		 * the file. Once data is buffered it remains around forever.
		 * 
		 * @param regionSize
		 *            Buffer size in ms. Generally should be a small fraction of
		 *            the entire file.
		 */
		static public TimedRegime newStreamingRegime(long regionSize) {
			return new TimedRegime(regionSize, regionSize * 2, 0, -1,
					TimedRegime.Order.ORDERED);
		}

		/**
		 * Like {@link Sample.Regime#newStreamingRegime(long)} but each buffered
		 * segment is discarded after a specified amount of time. This is useful
		 * if the sample is very large to conserve memory.
		 * 
		 * @param regionSize
		 *            Buffer size in ms. Generally should be a small fraction of
		 *            the entire file.
		 * @param memory
		 *            Amount of time (ms) that an untouched region is allowed to
		 *            live for.
		 */
		static public TimedRegime newStreamingRegimeWithAging(long regionSize,
				long memory) {
			return new TimedRegime(regionSize, regionSize * 2, 0, memory,
					TimedRegime.Order.ORDERED);
		}
	};

	/**
	 * <p>
	 * A TimedRegime stores only some parts of the audio file in memory at a
	 * time. It is useful for very large audio files, for audio streaming (e.g.,
	 * playing an mp3), or for samples of which only a small part is used.
	 * </p>
	 * <p>
	 * A sample with a TimedRegime loads "regions" of sample data and buffers
	 * them according to various parameters. See the method documentation for
	 * more details, but briefly, the parameters are:
	 * <ul>
	 * <li>{@link #regionSize}: The size of the region in ms.</li>
	 * <li>{@link #lookAhead}, {@link #lookBack}: When a region is accessed the
	 * lookAhead and lookBack determine which surrounding regions should be
	 * queued and loaded.</li>
	 * <li>{@link #memory}: The amount of time an unaccessed region should be
	 * kept in memory.</li>
	 * <li>{@link #loadingOrder}: Affects the order that surrounding regions are
	 * queued. NEAREST is suitable if you are playing backwards and forwards
	 * around a sample position, while ORDERED is suitable for playing forwards.
	 * </li>
	 * </ul>
	 * </p>
	 */
	static public class TimedRegime extends Regime {
		public long lookAhead; // time lookahead, ms
		public long lookBack; // time lookback, ms
		public long memory; // age (ms) at which regions get removed
		public long regionSize; // size of each region (by default 10s)

		static public enum Order {
			NEAREST, ORDERED
		};

		public Order loadingOrder;

		public TimedRegime() {
			super();
			lookAhead = 100;
			lookBack = 0;
			memory = 1000;
			regionSize = 100;
			loadingOrder = Order.ORDERED;
		}

		public TimedRegime(long regionSize, long lookAhead, long lookBack,
				long memory, Order loadingOrder) {
			this(regionSize, lookAhead, lookBack, memory, loadingOrder, false);
		}

		public TimedRegime(long regionSize, long lookAhead, long lookBack,
				long memory, Order loadingOrder, boolean storeInNativeBitDepth) {
			super(storeInNativeBitDepth);
			this.regionSize = regionSize;
			this.lookAhead = lookAhead;
			this.lookBack = lookBack;
			this.memory = memory;
			this.loadingOrder = loadingOrder;
		}

		/**
		 * Set how many milliseconds from last loaded point to look ahead.
		 * 
		 * @param lookahead
		 *            time to look ahead in ms.
		 */
		public void setLookAhead(long lookahead) {
			this.lookAhead = lookahead;
		}

		/**
		 * Set how many milliseconds from last loaded point to look backwards.
		 * 
		 * @param lookback
		 *            time to look backwards in ms.
		 */
		public void setLookBack(long lookback) {
			this.lookBack = lookback;
		}

		/**
		 * If a part of an audio file has not been accessed for some amount of
		 * time it is discarded. The time that the part remains in memory is
		 * specified by setMemory(). Passing a value of -1 to this function will
		 * set the memory to the maximum value possible.
		 * 
		 * @param ms
		 *            Duration in milliseconds that unaccessed regions remain
		 *            loaded.
		 */
		public void setMemory(long ms) {
			this.memory = ms;
		}

		/**
		 * Specify the size of each buffered region.
		 * 
		 * @param ms
		 *            Size of the region (ms)
		 */
		public void setRegionSize(long ms) {
			this.regionSize = ms;
		}

		/**
		 * When a region is loaded, nearby regions are put on a queue to be
		 * loaded also. The loading regime affects the order in which the nearby
		 * regions (defined by lookback and lookahead) are loaded.
		 * 
		 * NEAREST (the default) will load regions nearest to the region first.
		 * ORDERED will load the regions from lowest to highest.
		 * 
		 * NEAREST makes sense if you are accessing the near regions first,
		 * e.g., playing a sample backwards or forwards. ORDERED makes sense if
		 * you are accessing random nearby regions. Loading regions in order is
		 * generally quicker.
		 * 
		 * @param lr
		 *            The order to load regions.
		 */
		public void setLoadingRegime(Order lr) {
			this.loadingOrder = lr;
		}
	};

	/**
	 * A sample with a TotalRegime reads and stores all the audio data upon
	 * initialisation. This provides faster access (than TimedRegime) at the
	 * cost of much more memory used.
	 */
	static public class TotalRegime extends Regime {
		public TotalRegime() {
			super();
		}

		public TotalRegime(boolean storeInNativeBitDepth) {
			super(storeInNativeBitDepth);
		}
	};
	
	// Sample stuff
	private Regime bufferingRegime;
	private AudioFile audioFile;
	private SampleAudioFormat audioFormat;
	private int nChannels;
	private long nFrames;
	private float length; // length in ms
	private boolean isBigEndian;

	// TimedRegime Only
	// / The region master controls the loading of queued regions.
	static public Executor regionMaster = null;

	private int r_regionSize; // region size in frames
	private int r_lookahead; // num region lookahead
	private int r_lookback; // num regions lookback
	private long r_memory;

	private int regionSizeInBytes; // the number of bytes per region (regionSize
									// * nChannels * bitconversionfactor)
	private int numberOfRegions; // total number of regions
	private int numberOfRegionsLoaded; // number of loaded regions

	private byte[][] regions; // the actual data
	private float[][][] f_regions; // uninterleaved data

	private long[] regionAge; // the age of each region (in ms)
	private long timeAtLastAgeUpdate; // the time at the last age updated
										// operation

	private boolean[] regionQueued; // true if a region is currently queued
	private ConcurrentLinkedQueue<Integer> regionQueue; // a queue of regions to be loaded
	// private Thread regionThread; // the thread that loads regions in the background
	private Lock[] regionLocks; // to support safe deletion/writing of regions

	private boolean isScheduled;

	// TotalRegime Only
	private byte[] sampleData;
	private float[][] f_sampleData; // f_sampleData[0] first channel,
									// f_sampleData[1] second channel, etc..

	private float[] current, next; // used as temp buffers whilst calculating
									// interpolation

	/**
	 * Instantiates a new writable sample with specified length and default
	 * audio format: 44.1KHz, 16 bit, stereo.
	 * 
	 * @param length
	 *            the length in ms.
	 */
	public Sample(double length) {
		this(new SampleAudioFormat(44100, 16, 2), length);
	}

	/**
	 * Instantiates a new writeable Sample with the specified audio format and
	 * length;
	 * 
	 * The sample isn't initialised, so may contain junk. Use {@link #clear()}
	 * to clear it.
	 * 
	 * @param audioFormat
	 *            the audio format.
	 * @param length
	 *            The length of the sample in ms.
	 */
	public Sample(SampleAudioFormat audioFormat, double length) {
		this();
		this.audioFormat = audioFormat;
		nChannels = audioFormat.channels;
		current = new float[nChannels];
		next = new float[nChannels];
		nFrames = (long) msToSamples(length);
		if (bufferingRegime.storeInNativeBitDepth) {
			sampleData = new byte[2 * nChannels * (int) nFrames]; // 16-bit
		} else {
			f_sampleData = new float[nChannels][(int) nFrames];
		}
		this.length = 1000f * nFrames / audioFormat.sampleRate;
	}

	public Sample(SampleAudioFormat audioFormat, double length, Regime br) {
		this();
		bufferingRegime = br;
		this.audioFormat = audioFormat;
		nChannels = audioFormat.channels;
		current = new float[nChannels];
		next = new float[nChannels];
		nFrames = (long) msToSamples(length);
		if (bufferingRegime.storeInNativeBitDepth) {
			sampleData = new byte[2 * nChannels * (int) nFrames]; // 16-bit
		} else {
			f_sampleData = new float[nChannels][(int) nFrames];
		}
		this.length = 1000f * nFrames / audioFormat.sampleRate;
	}

	/**
	 * Create a sample. Call setFile to initialise the sample.
	 * 
	 */
	private Sample() {
		bufferingRegime = Regime.newTotalRegime();
		isBigEndian = true;
		isScheduled = false;

	}

	/**
	 * Create a sample from an Audio File, using the default buffering scheme.
	 * 
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws AudioFileUnsupportedException
	 */
	public Sample(AudioFile af) throws IOException,
			UnsupportedAudioFileException {
		this();
		setFile(af);
	}

	/**
	 * Create a sample from an Audio File, using the buffering scheme suggested.
	 * 
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws AudioFileUnsupportedException
	 */
	public Sample(AudioFile af, Regime r) throws IOException,
			UnsupportedAudioFileException {
		this();
		setBufferingRegime(r);
		setFile(af);
	}


	/**
	 * Return a single frame.
	 * 
	 * If the data is not readily available this doesn't do anything to
	 * frameData.
	 * 
	 * @param frame
	 *            Must be in range, else framedata is unchanged.
	 * @param frameData
	 * 
	 */
	public void getFrame(int frame, float[] frameData) {
		if (frame < 0 || frame >= nFrames)
			return;

		if (isTotal()) {
			if (bufferingRegime.storeInNativeBitDepth) {
				int startIndex = frame * 2 * nChannels;
				AudioUtils.byteToFloat(frameData, sampleData, isBigEndian,
						startIndex, frameData.length);
			} else {
				for (int i = 0; i < nChannels; i++)
					frameData[i] = f_sampleData[i][frame];
			}
		} else // bufferingRegime==BufferingRegime.TIMED
		{
			int whichRegion = frame / r_regionSize;
			if (whichRegion > numberOfRegions) {
				Arrays.fill(frameData, 0.f);
				return;
			}

			// When someone requests a region, it may not be loaded yet.
			// Alternatively it may currently be being deleted, in which case we
			// have to wait.

			// lock access to region r, load it, and return it...
			// wait until it is free...
			/*
			 * try { while (!regionLocks[whichRegion].tryLock(0,
			 * TimeUnit.MILLISECONDS)) {} } catch (InterruptedException e) {
			 * e.printStackTrace(); }
			 */

			try {
				if (regionLocks[whichRegion].tryLock(0, TimeUnit.MILLISECONDS)) {
					try {

						if (bufferingRegime.storeInNativeBitDepth) {
							byte[] regionData = getRegion(whichRegion);
							if (regionData != null) {
								// convert it to the correct format,
								int startIndex = (frame % r_regionSize) * 2
										* nChannels;
								AudioUtils.byteToFloat(frameData, regionData,
										isBigEndian, startIndex,
										frameData.length);
							}
						} else {
							float[][] regionData = getRegionF(whichRegion);
							if (regionData != null) {
								int startIndex = frame % r_regionSize;
								for (int i = 0; i < nChannels; i++)
									frameData[i] = regionData[i][startIndex];
							}
						}
					} catch (Exception e) {
						Arrays.fill(frameData, 0.f);
						return;
					} finally {
						regionLocks[whichRegion].unlock();
					}
				}
			} catch (InterruptedException e) {
				Arrays.fill(frameData, 0.f);
				return;
			}
		}
	}

	/**
	 * Retrieves a frame of audio using no interpolation. If the frame is not in
	 * the sample range then zeros are returned.
	 * 
	 * @param posInMS
	 *            The frame to read -- will take the last frame before this one.
	 * @param result
	 *            The framedata to fill.
	 */
	public void getFrameNoInterp(double posInMS, float[] result) {
		double frame = msToSamples(posInMS);
		int frame_floor = (int) Math.floor(frame);
		getFrame(frame_floor, result);
	}

	/**
	 * Retrieves a frame of audio using linear interpolation. If the frame is
	 * not in the sample range then zeros are returned.
	 * 
	 * @param posInMS
	 *            The frame to read -- can be fractional (e.g., 4.4).
	 * @param result
	 *            The framedata to fill.
	 */
	public void getFrameLinear(double posInMS, float[] result) {
		double frame = msToSamples(posInMS);
		int frame_floor = (int) Math.floor(frame);
		if (frame_floor > 0 && frame_floor < nFrames) {
			double frame_frac = frame - frame_floor;
			if (frame_floor == nFrames - 1) {
				getFrame(frame_floor, result);
			} else // lerp
			{
				getFrame(frame_floor, current);
				getFrame(frame_floor + 1, next);
				for (int i = 0; i < nChannels; i++) {
					result[i] = (float) ((1 - frame_frac) * current[i] + frame_frac
							* next[i]);
				}
			}
		} else {
			for (int i = 0; i < nChannels; i++) {
				result[i] = 0.0f;
			}
		}
	}

	/**
	 * Retrieves a frame of audio using cubic interpolation. If the frame is not
	 * in the sample range then zeros are returned.
	 * 
	 * @param posInMS
	 *            The frame to read -- can be fractional (e.g., 4.4).
	 * @param result
	 *            The framedata to fill.
	 */
	public void getFrameCubic(double posInMS, float[] result) {
		double frame = msToSamples(posInMS);
		float a0, a1, a2, a3, mu2;
		float ym1, y0, y1, y2;
		for (int i = 0; i < nChannels; i++) {
			int realCurrentSample = (int) Math.floor(frame);
			float fractionOffset = (float) (frame - realCurrentSample);

			if (realCurrentSample >= 0 && realCurrentSample < (nFrames - 1)) {
				realCurrentSample--;
				if (realCurrentSample < 0) {
					getFrame(0, current);
					ym1 = current[i];
					realCurrentSample = 0;
				} else {
					getFrame(realCurrentSample++, current);
					ym1 = current[i];
				}
				getFrame(realCurrentSample++, current);
				y0 = current[i];
				if (realCurrentSample >= nFrames) {
					getFrame((int) nFrames - 1, current);
					y1 = current[i]; // ??
				} else {
					getFrame(realCurrentSample++, current);
					y1 = current[i];
				}
				if (realCurrentSample >= nFrames) {
					getFrame((int) nFrames - 1, current);
					y2 = current[i]; // ??
				} else {
					getFrame(realCurrentSample++, current);
					y2 = current[i];
				}
				mu2 = fractionOffset * fractionOffset;
				a0 = y2 - y1 - ym1 + y0;
				a1 = ym1 - y0 - a0;
				a2 = y1 - ym1;
				a3 = y0;
				result[i] = a0 * fractionOffset * mu2 + a1 * mu2 + a2
						* fractionOffset + a3;
			} else {
				result[i] = 0.0f;
			}
		}
	}

	/**
	 * Get a series of frames. FrameData will only be filled with the available
	 * frames. It is the caller's responsibility to count how many frames are
	 * valid. <code>min(nFrames - frame, frameData[0].length)</code> frames in
	 * frameData are valid.
	 * 
	 * If the data is not readily available this doesn't do anything.
	 * 
	 * @param frame
	 *            The frame number (NOTE: This parameter is in frames, not in
	 *            ms!)
	 * @param frameData
	 */
	public void getFrames(int frame, float[][] frameData) {
		if (frame >= nFrames)
			return;

		if (isTotal()) {
			int numFloats = Math.min(frameData[0].length,
					(int) (nFrames - frame));

			if (bufferingRegime.storeInNativeBitDepth) {
				int startIndex = frame * 2 * nChannels;
				float[] floatdata = new float[numFloats * nChannels];
				AudioUtils.byteToFloat(floatdata, sampleData, isBigEndian,
						startIndex, numFloats * nChannels);
				AudioUtils.deinterleave(floatdata, nChannels,
						frameData[0].length, frameData);
			} else {
				for (int i = 0; i < nChannels; i++)
					System.arraycopy(f_sampleData[i], frame, frameData[i], 0,
							numFloats);
			}
		} else // bufferingRegime==BufferingRegime.TIMED
		{
			int whichregion = frame / r_regionSize;
			if (whichregion > numberOfRegions) {
				for (int i = 0; i < nChannels; i++) {
					Arrays.fill(frameData[i], 0.f);
				}
				return;
			}

			int numFloats = Math.min(frameData[0].length,
					(int) (nFrames - frame));

			float[] floatdata = null;
			if (bufferingRegime.storeInNativeBitDepth)
				floatdata = new float[numFloats * nChannels];

			// fill floatdata with successive regions of byte data
			int floatdataindex = 0;
			int regionindex = frame % r_regionSize;
			int numfloatstocopy = Math.min(r_regionSize - regionindex,
					numFloats - floatdataindex);

			while (numfloatstocopy > 0) {
				// see getFrame() for explanation
				/*
				 * try { while (!regionLocks[whichregion].tryLock(0,
				 * TimeUnit.MILLISECONDS)){} } catch (InterruptedException e) {
				 * e.printStackTrace(); }
				 */

				try {
					if (regionLocks[whichregion].tryLock(0,
							TimeUnit.MILLISECONDS)) {
						try {

							if (bufferingRegime.storeInNativeBitDepth) {
								byte[] regionData = getRegion(whichregion);
								if (regionData != null)
									AudioUtils.byteToFloat(floatdata,
											regionData, isBigEndian,
											regionindex * 2 * nChannels,
											floatdataindex * nChannels,
											numfloatstocopy * nChannels);
								else {
									int start = floatdataindex * nChannels;
									for (int i = start; i < start
											+ numfloatstocopy * nChannels; i++)
										floatdata[i] = 0.f;
								}
							} else {
								float[][] regionData = getRegionF(whichregion);
								if (regionData != null) {
									// copy all channels...
									for (int i = 0; i < nChannels; i++) {
										System.arraycopy(regionData[i], 0,
												frameData[i], floatdataindex,
												numfloatstocopy);
									}
								} else {
									for (int i = 0; i < nChannels; i++) {
										for (int f = floatdataindex; f < floatdataindex
												+ numfloatstocopy; f++)
											frameData[i][f] = 0.f;
									}
								}
							}
						} catch (Exception e) {
							for (int i = 0; i < nChannels; i++) {
								Arrays.fill(frameData[i], 0.f);
							}
							return;
						} finally {
							regionLocks[whichregion].unlock();
						}
						floatdataindex += numfloatstocopy;
						regionindex = 0;
						numfloatstocopy = Math.min(r_regionSize, numFloats
								- floatdataindex);
						whichregion++;
					}
				} catch (InterruptedException e) {
					for (int i = 0; i < nChannels; i++) {
						Arrays.fill(frameData[i], 0.f);
					}
					return;
				}
			}

			if (bufferingRegime.storeInNativeBitDepth) {
				// deinterleave the whole thing
				AudioUtils.deinterleave(floatdata, nChannels,
						frameData[0].length, frameData);
			}
		}
	}

	/**
	 * Clears the (writeable) sample.
	 */
	public void clear() {
		if (bufferingRegime.storeInNativeBitDepth) {
			Arrays.fill(sampleData, (byte) 0);
		} else {
			for (int i = 0; i < nChannels; i++)
				Arrays.fill(f_sampleData[i], 0f);
		}

	}

	/**
	 * Write a single frame into this sample. Takes care of format conversion.
	 * 
	 * This only makes sense if this.isWriteable() returns true. If
	 * isWriteable() is false, the behaviour is undefined/unstable.
	 * 
	 * @param frame
	 *            The frame to write into. Must be >=0 and <numFrames.
	 * @param frameData
	 *            The frame data to write.
	 */
	public void putFrame(int frame, float[] frameData) {
		if (bufferingRegime.storeInNativeBitDepth) {
			int startIndex = frame * 2 * nChannels;
			AudioUtils.floatToByte(sampleData, startIndex, frameData, 0,
					frameData.length, isBigEndian);
		} else {
			for (int i = 0; i < nChannels; i++)
				f_sampleData[i][frame] = frameData[i];
		}
	}

	/**
	 * Write multiple frames into the sample.
	 * 
	 * This only makes sense if this.isWriteable() returns true. If
	 * isWriteable() is false, the behaviour is undefined/unstable.
	 * 
	 * @param frame
	 *            The frame to write into.
	 * @param frameData
	 *            The frames to write.
	 */
	public void putFrames(int frame, float[][] frameData) {
		int numFrames = Math.min(frameData[0].length, (int) (nFrames - frame));

		if (bufferingRegime.storeInNativeBitDepth) {
			int startIndex = frame * 2 * nChannels;
			int numFloats = numFrames * nChannels;

			float[] floatdata = new float[numFloats];
			AudioUtils.interleave(frameData, nChannels, frameData[0].length,
					floatdata);
			AudioUtils.floatToByte(sampleData, startIndex, floatdata, 0,
					floatdata.length, isBigEndian);
		} else {
			// FIXME in loop record this falls over
			for (int i = 0; i < nChannels; i++) {
				System.arraycopy(frameData[i], 0, f_sampleData[i], frame,
						numFrames);
			}
		}
	}

	/**
	 * Write multiple frames into the sample.
	 * 
	 * This only makes sense if this.isWriteable() returns true. If
	 * isWriteable() is false, the behaviour is undefined/unstable.
	 * 
	 * @param frame
	 *            The frame to write into.
	 * @param frameData
	 *            The frames to write.
	 * @param offset
	 *            The offset into frameData
	 * @param numFrames
	 *            The number of frames from frameData to write
	 */
	public void putFrames(int frame, float[][] frameData, int offset,
			int numFrames) {
		if (numFrames <= 0)
			return;

		// clip numFrames
		numFrames = Math.min(numFrames, (int) (nFrames - frame));

		if (bufferingRegime.storeInNativeBitDepth) {
			int startIndex = frame * 2 * nChannels;
			int numFloats = numFrames * nChannels;

			float[] floatdata = new float[numFloats];
			AudioUtils.interleave(frameData, nChannels, offset,
					frameData[0].length, floatdata);
			AudioUtils.floatToByte(sampleData, startIndex, floatdata, 0,
					floatdata.length, isBigEndian);
		} else {
			for (int i = 0; i < nChannels; i++) {
				System.arraycopy(frameData[i], offset, f_sampleData[i], frame,
						numFrames);
			}
		}
	}
	
	
	

	/**
	 * This saves the sample to a file with the specified
	 * AudioFileFormat.Type. It is BLOCKING.
	 * 
	 * @param fn
	 *            The filename.
	 * @param type
	 *            The type (AIFF, WAVE, etc.)
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void write(AudioIO audioIO, String fn, AudioFile.Type type) throws IOException {
		//TODO removed (OB Android rework 20110629) should be done in implementation
		//Needs to connect with the right AudioFile, therefore needs info from AudioIO
	}

	/**
	 * Set everything up, ready to use.
	 * 
	 * @throws IOException
	 */
	private void init() throws IOException {
		//TODO removed (OB Android rework 20110629) should be done in implementation
	}
	
	
	
	
	/**
	 * <b>Advanced</b>
	 * 
	 * Change the number of frames in the (writeable) sample. This is slow and
	 * so should be used sparingly.
	 * 
	 * The new frames may contain garbage, but see
	 * {@link #resizeWithZeros(long)}.
	 * 
	 * @param frames
	 *            The total number of frames the sample should have.
	 * @throws Exception
	 *             Thrown if the sample isn't writeable.
	 */
	public void resize(long frames) throws Exception {
		if (!isWriteable()) {
			throw (new Exception(
					"Sample.resize only works on writeable samples."));
		} else if (isTotal()) {
			int framesToCopy = (int) Math.min(frames, nFrames);

			if (bufferingRegime.storeInNativeBitDepth) {
				byte[] olddata = sampleData;
				sampleData = new byte[(int) frames];
				System.arraycopy(olddata, 0, sampleData, 0, framesToCopy);
			} else {
				float[][] olddata = f_sampleData;
				f_sampleData = new float[nChannels][(int) frames];
				for (int i = 0; i < nChannels; i++)
					System.arraycopy(olddata[i], 0, f_sampleData[i], 0,
							framesToCopy);
			}

			nFrames = frames;
			length = (float) samplesToMs(nFrames);
		}
	}

	/**
	 * Just like {@link #resize(long)} but initialises the new frames with
	 * zeros.
	 * 
	 * @param frames
	 *            The total number of frames the sample should have.
	 * @throws Exception
	 *             Thrown if the sample isn't writeable.
	 */
	public void resizeWithZeros(long frames) throws Exception {
		if (!isWriteable()) {
			throw (new Exception(
					"Sample.resize only works on writeable samples."));
		} else if (isTotal()) {
			int framesToCopy = (int) Math.min(frames, nFrames);

			if (bufferingRegime.storeInNativeBitDepth) {
				byte[] olddata = sampleData;
				sampleData = new byte[(int) frames];
				System.arraycopy(olddata, 0, sampleData, 0, framesToCopy);
				Arrays.fill(sampleData, framesToCopy, (int) frames, (byte) 0);
			} else {
				float[][] olddata = f_sampleData;
				f_sampleData = new float[nChannels][(int) frames];
				for (int i = 0; i < nChannels; i++) {
					System.arraycopy(olddata[i], 0, f_sampleData[i], 0,
							framesToCopy);
					Arrays.fill(f_sampleData[i], framesToCopy, (int) frames, 0f);
				}
			}

			nFrames = frames;
			length = (float) samplesToMs(nFrames);
		}
	}

	/**
	 * Prints audio format info to System.out.
	 */
	public void printAudioFormatInfo() {
		System.out.println("Sample Rate: " + audioFormat.sampleRate);
		System.out.println("Channels: " + nChannels);
		System.out.println("Big Endian: " + audioFormat.bigEndian);
		System.out.println("Signed: " + audioFormat.signed);
	}

	/**
	 * Converts from milliseconds to samples based on the sample rate specified
	 * by {@link #audioFormat}.
	 * 
	 * @param msTime
	 *            the time in milliseconds.
	 * 
	 * @return the time in samples.
	 */
	public double msToSamples(double msTime) {
		return msTime * audioFormat.sampleRate / 1000.0f;
	}

	/**
	 * Converts from samples to milliseconds based on the sample rate specified
	 * by {@link #audioFormat}.
	 * 
	 * @param sampleTime
	 *            the time in samples.
	 * 
	 * @return the time in milliseconds.
	 */
	public double samplesToMs(double sampleTime) {
		return sampleTime / audioFormat.sampleRate * 1000.0f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getFileName();
	}

	/**
	 * A Sample needs to be writeable in order to be recorded into. Currently
	 * buffered samples are not writeable, but TOTAL (file or empty) samples
	 * are.
	 */
	public boolean isWriteable() {
		return isTotal();
	}

	/**
	 * Gets the full file path.
	 * 
	 * @return the file path.
	 */
	public String getFileName() {
		if (audioFile == null)
			return null;
		return audioFile.getName();
	}

	/**
	 * Gets the simple file name.
	 * 
	 * @return the file name.
	 */
	public String getSimpleFileName() {
		String fileName = getFileName();
		if (fileName == null)
			return null;
		String[] nameParts = fileName.split("/");
		return nameParts[nameParts.length - 1];
	}

	public AudioFile getAudioFile() {
		return audioFile;
	}

	public SampleAudioFormat getAudioFormat() {
		return audioFormat;
	}

	public int getNumChannels() {
		return nChannels;
	}

	public long getNumFrames() {
		return nFrames;
	}

	/**
	 * @return The number of bytes this sample uses to store each sample. May be
	 *         different than audioFile.audioFormat.
	 */
	public int getBytesPerSample() {
		if (bufferingRegime.storeInNativeBitDepth) {
			return 2;
		} else {
			return Float.SIZE / 8;
		}
	}

	public float getLength() {
		return length;
	}

	public float getSampleRate() {
		return audioFormat.sampleRate;
	}

	/**
	 * @return The number of regions. Only valid if bufferingRegime is
	 *         region-based.
	 */
	public int getNumberOfRegions() {
		return numberOfRegions;
	}

	/**
	 * @return The number of regions currently loaded. Only valid if
	 *         bufferingRegime is region-based.
	 */
	public int getNumberOfRegionsLoaded() {
		return numberOfRegionsLoaded;
	}

	// Region loading, handling, queuing, removing, etc...

	// / are we using the total regime?
	private boolean isTotal() {
		return (bufferingRegime instanceof TotalRegime);
	}

	/**
	 * The buffering regime affects how the sample accesses the audio data.
	 * 
	 * 
	 * @param r
	 *            The buffering regime to use.
	 */
	private void setBufferingRegime(Regime r) {
		bufferingRegime = r;
	}

	/**
	 * Specify an explicit AudioFile that the Sample reads from. NOTE: Only one
	 * sample should reference a particular AudioFile.
	 * 
	 * If BufferedRegime is TOTAL, this will block until the sample is loaded.
	 * 
	 * @throws AudioFileUnsupportedException
	 * 
	 */
	private void setFile(AudioFile af) throws IOException,
			UnsupportedAudioFileException {
		audioFile = af;
		audioFile.open();
		audioFormat = audioFile.getFormat();
		nFrames = audioFile.getNumFrames();
		nChannels = audioFile.getNumChannels();
		current = new float[nChannels];
		next = new float[nChannels];
		length = audioFile.getLength();
		isBigEndian = audioFile.getFormat().bigEndian;
		init();
	}


	// / Region handling, loading, etc...
	private byte[] getRegion(int r) {
		if (!isRegionAvailable(r)) {
			queueRegionForLoading(r);
			queueRegions(r);
			return null;
		} else {
			queueRegions(r);
			touchRegion(r);
			return regions[r];
		}
	}

	// / Region handling, loading, etc...
	private float[][] getRegionF(int r) {
		if (!isRegionAvailable(r)) {
			queueRegionForLoading(r);
			queueRegions(r);
			// System.out.println("null");
			return null;
		} else {
			queueRegions(r);
			touchRegion(r);
			return f_regions[r];
		}
	}

	private void queueRegions(int r) {
		if (((TimedRegime) bufferingRegime).loadingOrder == TimedRegime.Order.ORDERED) {
			// queue the regions from back to front
			for (int i = Math.max(0, r - r_lookback); i <= Math.min(r
					+ r_lookahead, numberOfRegions - 1); i++) {
				if (i != r) {
					queueRegionForLoading(i);
					touchRegion(i);
				}
			}
		} else // loadingOrder==LoadingRegime.NEAREST
		{
			// queue the regions from nearest to furthest, back to front...
			int br = Math.min(r, r_lookback); // number of back regions
			int fr = Math.min(r_lookahead, numberOfRegions - 1 - r); // number
																		// of
																		// ahead
																		// regions

			// have two pointers, one going backwards the other going forwards
			int bp = 1;
			int fp = 1;
			boolean backwards = (bp <= br); // start backwards (if there are
											// backward regions)
			while (bp <= br || fp <= fr) {
				if (backwards) {
					queueRegionForLoading(r - bp);
					touchRegion(r - bp);
					bp++;
					if (fp <= fr)
						backwards = false;
				} else // if forwards
				{
					queueRegionForLoading(r + fp);
					touchRegion(r + fp);
					fp++;
					if (bp <= br)
						backwards = true;
				}
			}
		}
	}

	private void touchRegion(int r) {
		// touch the region, make it new
		// synchronized (regionAge)
		// { regionAge[r] = 0; }
		// regionAge[r] = 0;
	}

	public boolean isRegionAvailable(int r) {
		if (bufferingRegime.storeInNativeBitDepth)
			return regions[r] != null;
		else
			return f_regions[r] != null;
	}

	public boolean isRegionQueued(int r) {
		return regionQueued[r];
	}

	// / loads the region IMMEDIATELY, blocks until it is loaded
	// this is called by the regionloader as it loads,
	// but also by the main thread when it needs a region RIGHT AWAY
	//
	// FIXME: Need a way to kill the regionMaster
	synchronized private void loadRegion(int r) {
		// for now, just seek to the correct position
		try {
			if (bufferingRegime.storeInNativeBitDepth) {
				regions[r] = new byte[regionSizeInBytes];
				numberOfRegionsLoaded++;
				audioFile.seek(r_regionSize * r);
				int bytesRead = audioFile.read(regions[r]);
				if (bytesRead <= 0)
					regions[r] = null;
			} else // store in float[][] format
			{
				// load the bytes and convert them on the spot
				byte[] region = new byte[regionSizeInBytes];
				numberOfRegionsLoaded++;
				audioFile.seek(r_regionSize * r);
				int bytesRead = audioFile.read(region);
				if (bytesRead <= 0) {
					f_regions[r] = new float[nChannels][r_regionSize];
					for (int i = 0; i < nChannels; i++)
						Arrays.fill(f_regions[r][i], 0.f);
				} else {
					// now convert
					f_regions[r] = new float[nChannels][r_regionSize];
					float[] interleaved = new float[nChannels * r_regionSize];
					AudioUtils.byteToFloat(interleaved, region, isBigEndian);
					AudioUtils.deinterleave(interleaved, nChannels,
							r_regionSize, f_regions[r]);
				}
			}
			synchronized (regionAge) {
				regionAge[r] = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// / load the region r when you can, non-blocking
	private void queueRegionForLoading(int r) {
		if (!isRegionAvailable(r) && !isRegionQueued(r)) {
			regionQueued[r] = true;
			regionQueue.add(r);

			// if not scheduled then schedule
			rescheduleSelf();
		}
	}

	private void unloadRegion(int r) {
		if (bufferingRegime.storeInNativeBitDepth)
			regions[r] = null;
		else
			f_regions[r] = null;
	}

	/**
	 * <i>Internal:</i> This method is called internally by the region master.
	 * It loads the next queued region.
	 */
	public synchronized void run() {
		boolean hasMoreQueuedRegions = loadQueuedRegion();

		ageAllRegions();

		if (hasMoreQueuedRegions) {
			isScheduled = false;
			rescheduleSelf();
		} else
			isScheduled = false;
	}

	/*
	 * old run operation public void run() { while (true) { boolean
	 * hasMoreQueuedRegions = loadQueuedRegion();
	 * 
	 * // age all the loaded regions // remove the oldest ones if we exceed the
	 * memory limit // FIXME: don't need to age things all the time, this should
	 * be based on some tunable param if (timeAtLastAgeUpdate==0)
	 * timeAtLastAgeUpdate = System.currentTimeMillis(); long dt =
	 * System.currentTimeMillis() - timeAtLastAgeUpdate;
	 * //System.out.println(dt); //System.out.println();
	 * 
	 * //int numRegionsToRemove = numberOfRegionsLoaded -
	 * maxRegionsLoadedAtOnce; //SortedSet sortedByAge = new
	 * TreeSet<Integer>(new Comparator(){}); //if (numRegionsToRemove>0) for
	 * (int i=0;i<numberOfRegions;i++) { if (!isRegionAvailable(i)) {
	 * synchronized(regionAge) { regionAge[i] += dt; } if
	 * (regionAge[i]>r_memory) { // if it is unlocked, then remove it... if
	 * (regionLocks[i].tryLock()) { try { unloadRegion(i);
	 * numberOfRegionsLoaded--; } finally { regionLocks[i].unlock(); } } //
	 * else, ignore and try again next time... } } } timeAtLastAgeUpdate += dt;
	 * 
	 * if (!hasMoreQueuedRegions) { try { // Thread.currentThread()
	 * Thread.sleep(1000000); } catch (InterruptedException ignore) {
	 * //System.out.println("Wake up!"); } } } }
	 */

	/*
	 * Loads the next queued region. Returns true if there are more regions
	 * queued.
	 */
	private boolean loadQueuedRegion() {
		// First check if the queue has anything to load.
		if (!regionQueue.isEmpty()) {
			// Load the next region
			int r = regionQueue.poll();
			if (regionLocks[r].tryLock()) {
				try {
					if (!isRegionAvailable(r))
						loadRegion(r);

					synchronized (regionQueued) {
						regionQueued[r] = false;
					}
				} finally {
					regionLocks[r].unlock();
				}
			}
		}

		return !regionQueue.isEmpty();
	}

	/*
	 * ages all the regions remove the oldest ones if we exceed the memory limit
	 * FIXME: don't need to age things all the time, this should be based on some
	 * tunable param
	 */
	private void ageAllRegions() {
		if (timeAtLastAgeUpdate == 0)
			timeAtLastAgeUpdate = System.currentTimeMillis();
		long dt = System.currentTimeMillis() - timeAtLastAgeUpdate;

		// we only update ages every multiple of m_memory
		if (dt > r_memory) {
			// int numRegionsToRemove = numberOfRegionsLoaded -
			// maxRegionsLoadedAtOnce;
			// SortedSet sortedByAge = new TreeSet<Integer>(new Comparator(){});
			// if (numRegionsToRemove>0)
			for (int i = 0; i < numberOfRegions; i++) {
				if (isRegionAvailable(i)) {
					synchronized (regionAge) {
						regionAge[i] += dt;
					}

					if (regionAge[i] > r_memory) {
						System.out.printf(
								"deleting a region %dms old (r_memory=%d)\n",
								regionAge[i], r_memory);

						// if it is unlocked, then remove it...
						if (regionLocks[i].tryLock()) {
							try {
								unloadRegion(i);
								numberOfRegionsLoaded--;
							} finally {
								regionLocks[i].unlock();
							}
						}
						// else, ignore and try again next time...
						// else
						// System.out.println("oops, I can't...");
					}
				}
			}

			timeAtLastAgeUpdate += dt;
		}
	}

	// / notifies the region master that this sample has more stuff to do
	private synchronized void rescheduleSelf() {
		// make sure that only one instance of self is scheduled at once
		// otherwise we will get asynchronous access of the same audiofile,
		// which we can't have
		if (!isScheduled) {
			isScheduled = true;
			regionMaster.execute(this);
		}
	}

	// a helper function, loads the entire sample into sampleData
	private void loadEntireSample() throws IOException {
		final int BUFFERSIZE = 4096;
		byte[] audioBytes = new byte[BUFFERSIZE];

		int sampleBufferSize = 4096;
		byte[] data = new byte[sampleBufferSize];

		int bytesRead;
		int totalBytesRead = 0;

		int numberOfFrames = 0;

		while ((bytesRead = audioFile.read(audioBytes)) != -1) {

			int numFramesJustRead = bytesRead / (2 * nChannels);

			// resize buf if necessary
			if (bytesRead > (sampleBufferSize - totalBytesRead)) {
				sampleBufferSize = Math.max(sampleBufferSize * 2,
						sampleBufferSize + bytesRead);
				// System.out.printf("Adjusted samplebuffersize to %d\n",sampleBufferSize);

				// resize buffer
				byte[] newBuf = new byte[sampleBufferSize];
				System.arraycopy(data, 0, newBuf, 0, data.length);
				data = newBuf;
			}

			System.arraycopy(audioBytes, 0, data, totalBytesRead, bytesRead);

			numberOfFrames += numFramesJustRead;
			totalBytesRead += bytesRead;
		}

		// resize buf to proper length
		// resize buf if necessary
		if (sampleBufferSize > totalBytesRead) {
			sampleBufferSize = totalBytesRead;

			// resize buffer
			byte[] newBuf = new byte[sampleBufferSize];
			System.arraycopy(data, 0, newBuf, 0, sampleBufferSize);
			data = newBuf;
		}

		this.nFrames = sampleBufferSize / (2 * nChannels);
		this.length = 1000f * nFrames / audioFormat.sampleRate;

		if (!bufferingRegime.storeInNativeBitDepth) {
			// copy and deinterleave entire data
			f_sampleData = new float[nChannels][(int) nFrames];
			float[] interleaved = new float[(int) (nChannels * nFrames)];
			AudioUtils.byteToFloat(interleaved, data, isBigEndian);
			AudioUtils.deinterleave(interleaved, nChannels, (int) nFrames,
					f_sampleData);
		} else {
			// store the data in this sample in native format
			sampleData = data;
		}

		audioFile.close();
	}

}
