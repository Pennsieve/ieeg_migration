package edu.upenn.cis.db.mefview.shared;

/**
 * A basic event detection
 * 
 * @author Zack Ives
 *
 */
public interface IDetection {
	
	public enum DetectionType {Spike, SpikeEnd, ExternalEvent, StrobeEvent, Annotation};
	
	/**
	 * Detection event start time, usec from start of data
	 * @return
	 */
	public double getStart();
	
	/**
	 * How long in usec, 0 for instantaneous
	 * 
	 * @return
	 */
	public double getDuration();
	
	/**
	 * The numeric value of the detection, e.g., magnitude
	 * or cluster ID
	 * 
	 * @return
	 */
	public int getNumericValue();
	
	/**
	 * Stringified code for the numeric value (requires lookup)
	 * 
	 * @return
	 */
	public String getStringValue();
	
	/**
	 * Any other information like detection name or type
	 * 
	 * @return Name, aux info, or null
	 */
	public JsonTyped getNameOrAuxiliaryInfo();
	
	public DetectionType getDetectionType();
}
