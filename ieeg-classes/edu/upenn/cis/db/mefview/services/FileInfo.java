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
package edu.upenn.cis.db.mefview.services;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Immutable
public final class FileInfo {

	@Immutable
	static final class FileNameComparator
			implements Comparator<FileInfo> {
		@Override
		public int compare(FileInfo mc1, FileInfo mc2) {
			return mc1.getFileName().compareTo(mc2.getFileName());
		}
	}

	private String id;
	private String eTag;
	private String fileName;
	private String serverFileKey;
	private Long sizeBytes;

	@SuppressWarnings("unused")
	private FileInfo() {}

	public FileInfo(
			String id,
			String eTag,
			String serverFileKey,
			String fileName,
			long sizeBytes) {
		this.id = checkNotNull(id);
		this.eTag = checkNotNull(eTag);
		this.serverFileKey = checkNotNull(serverFileKey);
		this.fileName = checkNotNull(fileName);
		this.sizeBytes = sizeBytes;
	}

	@XmlAttribute
	public String getId() {
		return id;
	}

	@XmlAttribute(name = "eTag")
	public String getETag() {
		return eTag;
	}

	@XmlAttribute
	public String getFileName() {
		return fileName;
	}

	@XmlAttribute
	public Long getSizeBytes() {
		return sizeBytes;
	}

	@XmlAttribute
	public String getServerFileKey() {
		return serverFileKey;
	}

	@SuppressWarnings("unused")
	private void setId(String timeSeriesId) {
		this.id = timeSeriesId;
	}

	@SuppressWarnings("unused")
	private void setETag(String eTag) {
		this.eTag = eTag;
	}

	@SuppressWarnings("unused")
	private void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@SuppressWarnings("unused")
	private void setSizeBytes(Long sizeBytes) {
		this.sizeBytes = sizeBytes;
	}

	@SuppressWarnings("unused")
	private void setServerFileKey(String serverFileKey) {
		this.serverFileKey = serverFileKey;
	}

}
