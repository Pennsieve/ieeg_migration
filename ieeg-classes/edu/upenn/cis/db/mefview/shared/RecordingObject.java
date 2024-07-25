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

import java.util.Date;
import java.util.Map;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;

import edu.upenn.cis.db.mefview.services.IsoDateAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@GwtCompatible(serializable = true)
public final class RecordingObject implements IHasName {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String eTag;
	private String md5Hash;
	private Long sizeBytes;
	private String creator;
	private Date createTime;
	private String internetMediaType;
	private String datasetId;
	private String description;
	
	private Map<String,String> header;

	@SuppressWarnings("unused")
	private RecordingObject() {}

	public RecordingObject(
			String name,
			String md5Hash,
			long sizeBytes,
			String creator,
			String internetMediaType,
			String datasetId) {
		this.name = checkNotNull(name);
		this.md5Hash = checkNotNull(md5Hash);
		this.sizeBytes = sizeBytes;
		this.creator = checkNotNull(creator);
		this.internetMediaType = checkNotNull(internetMediaType);
		this.datasetId = checkNotNull(datasetId);

	}

	public RecordingObject(
			String name,
			String md5Hash,
			long sizeBytes,
			String creator,
			String internetMediaType,
			String datasetId,
			long id,
			String eTag,
			Date createTime) {
		this(
				name,
				md5Hash,
				sizeBytes,
				creator,
				internetMediaType,
				datasetId);
		this.id = id;
		this.eTag = checkNotNull(eTag);
		this.createTime = checkNotNull(createTime);
	}

	public RecordingObject(
			String name,
			String md5Hash,
			long sizeBytes,
			String creator,
			String internetMediaType,
			String datasetId,
			long id,
			String eTag,
			Date createTime,
			String description) {
		this(
				name,
				md5Hash,
				sizeBytes,
				creator,
				internetMediaType,
				datasetId,
				id,
				eTag,
				createTime);
		this.description = checkNotNull(description);
	}

	@Nullable
	@XmlAttribute
	public Long getId() {
		return id;
	}

	@Nullable
	@XmlAttribute
	@XmlJavaTypeAdapter(IsoDateAdapter.class)
	public Date getCreateTime() {
		return createTime;
	}

	@XmlAttribute
	public String getCreator() {
		return creator;
	}

	@XmlAttribute
	public String getDatasetId() {
		return datasetId;
	}

	@XmlAttribute
	@Nullable
	public String getDescription() {
		return description;
	}

	@Nullable
	@XmlAttribute(name = "eTag")
	public String getETag() {
		return eTag;
	}

	@XmlAttribute
	public String getInternetMediaType() {
		return internetMediaType;
	}

	@XmlAttribute
	public String getMd5Hash() {
		return md5Hash;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	@XmlAttribute
	public Long getSizeBytes() {
		return sizeBytes;
	}

	public void setId(Long id) {
		this.id = checkNotNull(id);
	}

	public void setCreateTime(Date createTime) {
		this.createTime = checkNotNull(createTime);
	}

	public void setCreator(String creator) {
		this.creator = checkNotNull(creator);
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = checkNotNull(datasetId);
	}

	public void setDescription(String description) {
		this.description = checkNotNull(description);
	}

	public void setETag(String eTag) {
		this.eTag = checkNotNull(eTag);
	}

	public void setInternetMediaType(String internetMediaType) {
		this.internetMediaType = checkNotNull(internetMediaType);
	}

	public void setMd5Hash(String md5Hash) {
		this.md5Hash = checkNotNull(md5Hash);
	}

	public void setName(String name) {
		this.name = checkNotNull(name);
	}

	public void setSizeBytes(Long sizeBytes) {
		this.sizeBytes = checkNotNull(sizeBytes);
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> metadata) {
		this.header = metadata;
	}
	
	@Override
	public String toString() {
		return "RecordingObject [id=" + id + ", name=" + name + ", eTag="
				+ eTag + ", md5Hash=" + md5Hash + ", sizeBytes=" + sizeBytes
				+ ", creator=" + creator + ", createTime=" + createTime
				+ ", internetMediaType=" + internetMediaType + ", datasetId="
				+ datasetId + ", description=" + description + "]";
	}
}
