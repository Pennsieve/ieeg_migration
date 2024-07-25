/*
 * Copyright 2012 Trustees of the University of Pennsylvania
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
package edu.upenn.cis.db.mefview.testhelper;

import java.util.Random;
import java.util.SortedSet;
import java.util.UUID;

import edu.upenn.cis.db.mefview.services.TimeSeries;
import edu.upenn.cis.db.mefview.services.TimeSeriesAnnotation;
import edu.upenn.cis.db.mefview.services.TimeSeriesTask;
import edu.upenn.cis.db.mefview.services.TimeSeriesTask.STATUS;
import edu.upenn.cis.db.mefview.shared.EEGMontage;
import edu.upenn.cis.db.mefview.shared.EEGMontagePair;
import edu.upenn.cis.db.mefview.shared.RecordingObject;

/**
 * Create object for tests. Under main instead of test so that dependent modules
 * can use it easily.
 * 
 * @author John Frommeyer
 * 
 */
public class ClientTstObjectFactory {
	private final Random random = new Random();

	public String newUuid() {
		return UUID.randomUUID().toString();
	}

	public int randomNonnegInt() {
		// These are frequently added together to get a new non-negative int, so
		// divide by three to make sure there is no overflow.
		return random.nextInt(Integer.MAX_VALUE / 3);
	}

	public <T extends Enum<T>> T randomEnum(final Class<T> enumType) {
		final T[] enumConstants = enumType.getEnumConstants();
		return enumConstants[random.nextInt(enumConstants.length)];
	}

	public EEGMontage newEEGMontage() {
		return new EEGMontage(newUuid());
	}

	public EEGMontagePair newEEGMontagePair() {
		return new EEGMontagePair(newUuid(), newUuid());
	}

	public RecordingObject newRecordingObject(String datasetId) {
		return new RecordingObject(
				newUuid(),
				newUuid().replaceAll("-", ""),
				randomNonnegInt(),
				newUuid(),
				newUuid(),
				datasetId);
	}

	public TimeSeries newTimeSeries() {
		return new TimeSeries(newUuid(), newUuid());
	}

	public TimeSeriesAnnotation newTimeSeriesAnnotation(
			SortedSet<TimeSeries> annotated) {
		final long startPrim = randomNonnegInt();
		final Long start = Long.valueOf(startPrim);
		final Long end = Long.valueOf(startPrim
				+ randomNonnegInt());

		return new TimeSeriesAnnotation(annotated, newUuid(), start, end,
				newUuid(), newUuid(), newUuid());
	}

	public TimeSeriesTask newTimeSeriesTask() {
		final TimeSeriesTask task = newTimeSeriesTaskUnsceduled();
		task.setStatus(randomEnum(STATUS.class));
		task.setStartedRunning(randomNonnegInt(), newUuid());
		return task;
	}

	public TimeSeriesTask newTimeSeriesTaskUnsceduled() {
		final long startTime = randomNonnegInt();
		final long endTime = startTime + randomNonnegInt();
		final String[] channels = new String[random.nextInt(100) + 1];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = newUuid();
		}
		final TimeSeriesTask task = new TimeSeriesTask(newUuid(), channels,
				startTime, endTime);
		return task;
	}

}
