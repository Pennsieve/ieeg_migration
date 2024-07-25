/*
 * Copyright (C) 2014 Trustees of the University of Pennsylvania
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.annotations.GwtCompatible;

import edu.upenn.cis.db.mefview.services.IsoDateAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@GwtCompatible(serializable = true)
public final class ControlFileRegistration implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String eTag;

	private String bucket;
	private String fileKey;
	private String creator;
	private Date createTime;

	private String status;
	private String metadata;

	public ControlFileRegistration(
			String bucket,
			String fileKey) {
		this.bucket = checkNotNull(bucket);
		this.fileKey = checkNotNull(fileKey);

	}

	public ControlFileRegistration(
			String bucket,
			String fileKey,
			String creator,
			Date createTime,
			String status,
			Long id,
			String eTag,
			@Nullable String metadata) {
		this(
				bucket,
				fileKey);
		this.creator = checkNotNull(creator);
		this.createTime = checkNotNull(createTime);
		this.status = checkNotNull(status);
		this.id = checkNotNull(id);
		this.eTag = checkNotNull(eTag);
		this.metadata = metadata;
	}

	@SuppressWarnings("unused")
	private ControlFileRegistration() {}

	@XmlAttribute
	public String getBucket() {
		return bucket;
	}

	@XmlAttribute
	public String getFileKey() {
		return fileKey;
	}

	@XmlAttribute
	public String getCreator() {
		return creator;
	}

	@Nullable
	@XmlAttribute
	@XmlJavaTypeAdapter(IsoDateAdapter.class)
	public Date getCreateTime() {
		return createTime;
	}

	@Nullable
	@XmlAttribute
	public String getStatus() {
		return status;
	}

	@Nullable
	@XmlAttribute
	public Long getId() {
		return id;
	}

	@Nullable
	@XmlAttribute(name = "eTag")
	public String getETag() {
		return eTag;
	}
	
	@Nullable
	@XmlAttribute
	public String getMetadata() {
		return metadata;
	}

	public void setBucket(String bucket) {
		this.bucket = checkNotNull(bucket);
	}

	public void setFileKey(String fileKey) {
		this.fileKey = checkNotNull(fileKey);
	}

	public void setCreator(String creator) {
		this.creator = checkNotNull(creator);
	}

	public void setCreateTime(Date createTime) {
		this.createTime = checkNotNull(createTime);
	}

	public void setStatus(String status) {
		this.status = checkNotNull(status);
	}

	public void setId(Long id) {
		this.id = checkNotNull(id);
	}

	public void setETag(String eTag) {
		this.eTag = checkNotNull(eTag);
	}
	
	public void setMetadata(String metadata) {
		this.metadata = checkNotNull(metadata);
	}
}
