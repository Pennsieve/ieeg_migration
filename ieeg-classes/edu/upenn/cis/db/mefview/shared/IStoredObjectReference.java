/*
 * Copyright 2015 Trustees of the University of Pennsylvania
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



/**
 * Handle to an object
 * 
 * @author zives
 *
 */
public interface IStoredObjectReference extends JsonTyped, Comparable<IStoredObjectReference> {
	/**
	 * The overall filesystem, bucket, etc.
	 * 
	 * @return
	 */
	IStoredObjectContainer getDirBucketContainer();
	
	void setDirBucketContainer(IStoredObjectContainer cont);
	
	/**
	 * If relevant, the key of the object
	 * 
	 * @return
	 */
	String getObjectKey();
	
	void setObjectKey(String key);
	
	/**
	 * The pathname of the object
	 * 
	 * @return
	 */
	String getFilePath();
	
	/**
	 * Is the objecdt a container, e.g., a directory?
	 * 
	 * @return
	 */
	public boolean isContainer();
}
