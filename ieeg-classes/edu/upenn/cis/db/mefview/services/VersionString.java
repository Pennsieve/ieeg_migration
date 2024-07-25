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
package edu.upenn.cis.db.mefview.services;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Splitter;
import com.google.common.collect.ComparisonChain;

@Immutable
public final class VersionString implements Comparable<VersionString> {
	private final Integer major;
	private final Integer minor;
	private final Integer revision;

	public VersionString(String versionString) {
		String versionStringStripped = versionString;
		int indexOfFirstDash = versionString.indexOf('-');
		if (indexOfFirstDash != -1) {
			versionStringStripped =
					versionString.substring(0, indexOfFirstDash);
		}

		List<String> clientParts = newArrayList(Splitter.on('.').split(
				versionStringStripped));
		checkArgument(clientParts.size() <= 3,
				"only versions n.n.n are supported");

		major = Integer.parseInt(clientParts.get(0));
		Integer thisMinor = null;
		Integer thisRevision = null;
		if (clientParts.size() >= 2) {
			thisMinor = Integer.valueOf(clientParts.get(1));
			if (clientParts.size() >= 3) {
				thisRevision = Integer.valueOf(clientParts.get(2));
			}
		}
		if (thisMinor == null) {
			minor = 0;
		} else {
			minor = thisMinor;
		}

		if (thisRevision == null) {
			revision = 0;
		} else {
			revision = thisRevision;
		}

	}

	@Override
	public int compareTo(VersionString that) {
		return ComparisonChain
				.start()
				.compare(this.major, that.major)
				.compare(this.minor, that.minor)
				.compare(this.revision, that.revision)
				.result();
	}

}
