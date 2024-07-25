package edu.upenn.cis.db.mefview.shared;

/**
 * Generically, a dataset
 * 
 * @author Zack Ives
 *
 */
public interface IDataset extends SerializableMetadata {
	public String getFriendlyName();
	
	public String getId();
}
