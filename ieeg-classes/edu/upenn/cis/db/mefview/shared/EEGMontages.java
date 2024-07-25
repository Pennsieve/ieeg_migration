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

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.annotations.GwtCompatible;

@XmlRootElement(name = "montages")
@XmlAccessorType(XmlAccessType.NONE)
@GwtCompatible(serializable = true)
public final class EEGMontages {
	
	private static final class MontageByName implements Comparator<EEGMontage> {

		@Override
		public int compare(EEGMontage o1, EEGMontage o2) {
			final int byName = o1.getName().compareTo(o2.getName());
			if (byName != 0) {
				return byName;
			}
			return o1.getServerId().compareTo(o2.getServerId());
		}
		
	}

	private Set<EEGMontage> montages = newTreeSet(new MontageByName());

	@SuppressWarnings("unused")
	private EEGMontages() {}

	public EEGMontages(Collection<EEGMontage> montages) {
		this.montages.addAll(montages);
	}

	@XmlElement(name = "montage")
	public Set<EEGMontage> getMontages() {
		return montages;
	}

	@Override
	public String toString() {
		return "Montages [montages=" + montages + "]";
	}

}
