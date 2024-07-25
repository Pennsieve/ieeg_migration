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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import edu.upenn.cis.db.mefview.shared.IHasName;

@Immutable
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class Layer implements IHasName {

	private Long id;
	private String name;
	private Integer annotationCount;

	/** For JAXB. */
	@SuppressWarnings("unused")
	private Layer() {}

	public Layer(String name) {
		this.name = checkNotNull(name);
	}

	public Layer(
			String name,
			long id) {
		this(name);
		this.id = id;
	}

	public Layer(
			String name,
			long id,
			int annotationCount) {
		this(name, id);
		this.annotationCount = annotationCount;
	}

	@Nullable
	@XmlAttribute
	public Integer getAnnotationCount() {
		return annotationCount;
	}

	@Nullable
	@XmlAttribute
	public Long getId() {
		return id;
	}

	@Override
	@XmlAttribute
	public String getName() {
		return name;
	}

	@SuppressWarnings("unused")
	private void setAnnotationCount(Integer annotationCount) {
		this.annotationCount = annotationCount;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Layer [id=" + id + ", name=" + name
				+ ", annotationCount=" + annotationCount + "]";
	}

}
