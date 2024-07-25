package edu.upenn.cis.db.mefview.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Basic interface representing a time series channel
 * 
 * @author Zack Ives
 *
 */
public interface INamedTimeSegment extends SerializableMetadata {
	public static final String EEG = "eeg";
	
	public static final String EMG = "emg";

	public static final String EKG = "ekg";

	public static final String UNIT = "unit";

	public static final String BLOOD_PRESSURE = "bp";


	/**
	 * Channel ID
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * The parent dataset
	 * 
	 * @return
	 */
	@JsonIgnore
	public IDataset getParent();

	/**
	 * Duration in microseconds
	 * 
	 * @return
	 */
	public double getDuration();
	
	/**
	 * Sampling rate (frequency in Hz)
	 * 
	 * @return
	 */
	public double getSampleRate();
	
	/**
	 * Start time (uUTC)
	 * @return
	 */
	public long getStartTime();

	/**
	 * Channel label
	 * 
	 * @return
	 */
	public String getLabel();

	/**
	 * Channel type / modality
	 * 
	 * @return
	 */
	public String getDataModality();

	/**
	 * Channel units
	 * 
	 * @return
	 */
	public String getUnits();

	/**
	 * Channel level baseline for relative units
	 * 
	 * @return
	 */
	public double getBaseline();

	/**
	 * Channel time baseline (esp. for event/trial-based relative offsets)
	 *  
	 * @return
	 */
	public long getTimeShift();
	
	/**
	 * Multiplier on the channel units (e.g., .001 = mV)
	 * @return
	 */
	public double getScale();

	
	/**
	 * Any comments / misc
	 * 
	 * @return
	 */
	public String getChannelComments();
	
	/**
	 * Is it a sequence of events (i.e., raster plot mode) or amplitudes?
	 * @return
	 */
	public boolean isEventBased();

	public void setTimeShift(long timeOffset);
}
