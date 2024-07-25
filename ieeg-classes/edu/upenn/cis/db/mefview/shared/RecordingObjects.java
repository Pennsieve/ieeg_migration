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

import static com.google.common.collect.Sets.newTreeSet;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.annotations.GwtCompatible;

import edu.upenn.cis.db.mefview.shared.IHasName;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@GwtCompatible(serializable = true)
public final class RecordingObjects implements Serializable {

	private static final long serialVersionUID = 1L;
	private Set<RecordingObject> recordingObjects = newTreeSet(new IHasName.NameComparator());

	@SuppressWarnings("unused")
	private RecordingObjects() {}

	public RecordingObjects(Set<RecordingObject> recordingObjects) {
		this.recordingObjects.addAll(recordingObjects);
	}

	/**
	 * Will be in alphabetical order by name unless you mess with the returned
	 * set here.
	 */
	@XmlElementWrapper
	@XmlElement(name = "recordingObject")
	public Set<RecordingObject> getRecordingObjects() {
		return recordingObjects;
	}

	@Override
	public String toString() {
		return "RecordingObjects [recordingObjects=" + recordingObjects + "]";
	}

}
