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

import java.io.Serializable;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable=true)
public class DirectoryBucketContainer implements IStoredObjectContainer, JsonTyped, Serializable {
	String bucket;
	String fileDirectory;
	String source = "*";
	
	
	public DirectoryBucketContainer() {}
	
	public DirectoryBucketContainer(String bucket, String path) {
		this.bucket = bucket;
		fileDirectory = path;
	}

	@Override
	public String getBucket() {
		return bucket;
	}

	@Override
	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setBucket(String s3Bucket) {
		this.bucket = s3Bucket;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	@Override
	public String toString() {
		return "{" + bucket + ":" + fileDirectory + "}";
	}

	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source; 
	}

}
