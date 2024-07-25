package edu.upenn.cis.db.mefview.shared;

public interface ISerializableDetection extends IDetection {
	public IDetection createAnother();
	
	public byte[] getSignature();
	
	byte[] serializeSummary();
	
	byte[] serializeFull();
	
	ISerializableDetection deserializeSummary(byte[] main);

	ISerializableDetection deserializeFull(byte[] aux);
}
