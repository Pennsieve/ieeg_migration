/*
 * Copyright 2013 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.db.mefview.shared;

import java.beans.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable = true)
public interface GeneralMetadata extends Serializable {
	public static enum VALUE_TYPE {INT, DOUBLE, STRING, META, 
		STRING_SET, META_SET};
		
	public static enum BASIC_CATEGORIES {
		Metadata, Snapshot, EEG, Timeseries, Images, Documents, Annotations, 
		Collections, Configurations, Downloads, Projects, Links, Users,
		ImageStack, Genes};
		
	public String getMetadataCategory();
	
	public String getLabel();
	
	public String getId();
	
	public void setId(String id);
	
	@Transient
	@JsonIgnore
	public Set<String> getKeys();
	
	public String getStringValue(String key);
	
	public VALUE_TYPE getValueType(String key);
	
	public Double getDoubleValue(String key);
	
	public Integer getIntegerValue(String key);
	
	public GeneralMetadata getMetadataValue(String key);

	public List<String> getStringSetValue(String key);

	public List<? extends GeneralMetadata> getMetadataSetValue(String key);

	public GeneralMetadata getParent();
	
	public void setParent(GeneralMetadata p);
}
