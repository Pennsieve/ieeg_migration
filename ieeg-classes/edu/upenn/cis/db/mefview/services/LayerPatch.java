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

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Set;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class LayerPatch {

	private long id;
	private String name;
	private Set<MontagedChAnnotation> createAnnotations = newLinkedHashSet();

	/**
	 * For JAXB.
	 */
	@SuppressWarnings("unused")
	private LayerPatch() {}

	public LayerPatch(
			long id,
			@Nullable String name,
			@Nullable Set<? extends MontagedChAnnotation> createAnnotations) {
		this.id = id;
		this.name = name;
		if (createAnnotations != null) {
			this.createAnnotations.addAll(createAnnotations);
		}
	}

	@Nullable
	@XmlElementWrapper
	@XmlElement(name = "annotation")
	public Set<MontagedChAnnotation> getCreateAnnotations() {
		return createAnnotations;
	}

	@XmlAttribute
	public long getId() {
		return id;
	}

	@Nullable
	@XmlAttribute
	public String getName() {
		return name;
	}

	@SuppressWarnings("unused")
	private void setId(long id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private void setName(String name) {
		this.name = name;
	}
}
