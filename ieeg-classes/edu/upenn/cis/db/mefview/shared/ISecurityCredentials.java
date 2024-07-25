package edu.upenn.cis.db.mefview.shared;

import java.util.Set;

public interface ISecurityCredentials {
	public Long getIdentifier();
	
	public String getClassification();
	
	public String getName();

//	public Set<ISecurityCredentials> getMembers();
}
