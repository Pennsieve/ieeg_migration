package edu.upenn.cis.db.mefview.shared;

import java.beans.Transient;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable=true)
public class Detection implements Serializable, ISerializableDetection, Comparable<Detection> {
	
	byte[] buf = new byte[20];
	ByteBuffer b = ByteBuffer.wrap(buf);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	long timeUsec;
	int event;
	int value;
	String misc;
	DetectionType detection;
	Map<Integer,Map<Integer,String>> valueMap = null;
	
	public Detection() {}

	public long getTimeUsec() {
		return timeUsec;
	}

	public void setTimeUsec(long timeUsec) {
		this.timeUsec = timeUsec;
	}
	
	public int getEvent() {
		return event;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	@JsonIgnore
	@Transient
	@Override
	public double getStart() {
		return timeUsec;
	}

	public String getMisc() {
		return misc;
	}

	public void setMisc(String misc) {
		this.misc = misc;
	}

	public Detection(long timeUsec, DetectionType type, int event, int value, String misc) {
		super();
		this.timeUsec = timeUsec;
		this.event = event;
		this.value = value;
		this.detection = type;
		this.misc = misc;
	}

	public Detection(long timeUsec, DetectionType type,int event, int value, String misc, 
			Map<Integer, Map<Integer,String>> valueMap) {
		this(timeUsec, type, event, value, misc);
		this.valueMap = valueMap;
	}

	@Override
	@JsonIgnore
	public double getDuration() {
		return 0;
	}

	@Override
	@JsonIgnore
	public int getNumericValue() {
		return getValue();
	}

	@Override
	@JsonIgnore
	public JsonTyped getNameOrAuxiliaryInfo() {
		return new JsonString(getMisc());
	}

	@Override
	public String toString() {
		return "(" + timeUsec + "," + event + "," + value + "," + ((misc != null) ? misc : "") + "," + detection.name() + ")";
	}
	
	public static Detection fromString(String str) throws ParseException {
		if (!(str.startsWith("(") && str.endsWith(")")))
				throw new ParseException("Non parseable " + str, 0);
		
		String[] items = str.substring(1, str.length() - 2).split(",");
		
		if (items.length != 3)
			throw new ParseException("Could not find 3 separated items: " + str, 0);
		
		return new Detection(
				Long.valueOf(items[0]), 
				DetectionType.values()[Integer.valueOf(items[4])], 
				Integer.valueOf(items[1]), 
				Integer.valueOf(items[2]),
				items[3]);
	}

	@Override
	public DetectionType getDetectionType() {
		return detection;
	}

	@Override
	public String getStringValue() {
		if (valueMap == null || !valueMap.containsKey(event) || !valueMap.get(event).containsKey(value))
			return "Event " + event;
		else
			return valueMap.get(event).get(value);
	}

	@Override
	public IDetection createAnother() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getSignature() {
		String sig = "Detection.0"; 
		return sig.getBytes();
	}

	@Override
	public Detection deserializeFull(byte[] aux) {
		ByteBuffer b = ByteBuffer.wrap(aux);

		return new Detection(b.getLong(), DetectionType.values()[b.getInt()], b.getInt(), b.getInt(), getString(b));
	}
	
	String getString(ByteBuffer b) {
		int length = b.getInt();
		byte[] buf = new byte[length];
		b.get(buf);
		return new String(buf);
	}

	@Override
	public byte[] serializeFull() {
		byte[] auxBuf = new byte[misc.getBytes().length + 8];
		ByteBuffer b = ByteBuffer.wrap(auxBuf);
		b.putLong(timeUsec);
		b.putInt(detection.ordinal());
		b.putInt(event);
		b.putInt(value);
		b.putInt(misc.length());
		b.put(misc.getBytes());
		
		return auxBuf;
	}

	@Override
	public Detection deserializeSummary(byte[] main) {
		return deserialize(main);
	}

	@Override
	public byte[] serializeSummary() {
		// 8B
		b.putLong(timeUsec);
		// 4B
		b.putInt(detection.ordinal());
		// 4B
		b.putInt(event);
		// 4B
		b.putInt(value);

		return buf;
	}

	public static Detection deserialize(byte[] main) {
		ByteBuffer b = ByteBuffer.wrap(main);

		return new Detection(b.getLong(), 
				DetectionType.values()[b.getInt()], 
				b.getInt(), 
				b.getInt(), 
				"");
	}

	@Override
	public int compareTo(Detection o) {
		if (timeUsec != o.getTimeUsec())
			return Long.valueOf(timeUsec).compareTo(o.getTimeUsec());
		else if (detection != o.getDetectionType())
			return Integer.valueOf(detection.ordinal()).compareTo(o.getDetectionType().ordinal());
		else if (event != o.event)
			return Integer.valueOf(event).compareTo(o.event);
		else
			return Integer.valueOf(value).compareTo(o.value);
	}
	
	/**
	 * Symmetric equals
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Detection))
			return false;
		else return equals2((Detection)o) && ((Detection)o).equals2(this);
	}

	boolean equals2(Detection d) {
		return timeUsec == d.timeUsec && detection == d.detection && event == d.event &&
				value == d.value && misc.equals(d.misc);
	}

	@Override
	public int hashCode() {
		return (int)timeUsec % event; 
	}
}
