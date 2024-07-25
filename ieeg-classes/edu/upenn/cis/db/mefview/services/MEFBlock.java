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
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.bson.BSONObject;

import com.google.common.base.Objects;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

// Not @Immutable because of byte[] data
public final class MEFBlock {

	@Immutable
	public static final class BlockNoComparator implements Comparator<MEFBlock> {
		@Override
		public int compare(final MEFBlock o1, final MEFBlock o2) {
			return o1.getBlockNo().compareTo(o2.getBlockNo());
		}
	}

	public static final String MONGO_COLLECTION_NAME = "mefBlocks";

	public static final String VERSION_FIELD = "version";
	public static final String TIME_SERIES_ID = "timeSeriesId";
	public static final String START_OFFSET_MICROS = "startOffsetMicros";
	public static final String END_OFFSET_MICROS = "endOffsetMicros";
	public static final String SAMPLING_RATE_HZ = "samplingRateHz";
	public static final String VOLTAGE_CONV_FACTOR_MICRO_V = "voltageConvFactorMicroV";
	public static final String START_UUTC = "startUutc";
	public static final String BLOCK_NO = "blockNo";
	public static final String FILE_CHECK = "fileCheck";
	public static final String DATASET_ID = "datasetId";
	public static final String DATA = "data";

	private final Integer version;
	private final String timeSeriesId;
	private final Long startOffsetMicros;
	private final Long endOffsetMicros;
	private final Double samplingRateHz;
	private final Double voltageConvFactorMicroV;
	private final Long startUutc;
	private final Long blockNo;
	private final String fileCheck;
	private final String datasetId;
	private final byte[] data;

	public MEFBlock(BSONObject mefBlockBsonObj) {
		this((Integer) mefBlockBsonObj
				.get(MEFBlock.VERSION_FIELD),
				(String) mefBlockBsonObj
						.get(MEFBlock.TIME_SERIES_ID),
				(Long) mefBlockBsonObj
						.get(MEFBlock.START_OFFSET_MICROS),
				(Long) mefBlockBsonObj
						.get(MEFBlock.END_OFFSET_MICROS),
				(Double) mefBlockBsonObj
						.get(MEFBlock.SAMPLING_RATE_HZ),
				(Double) mefBlockBsonObj
						.get(MEFBlock.VOLTAGE_CONV_FACTOR_MICRO_V),
				(Long) mefBlockBsonObj
						.get(MEFBlock.START_UUTC),
				(Long) mefBlockBsonObj
						.get(MEFBlock.BLOCK_NO),
				(String) mefBlockBsonObj
						.get(MEFBlock.FILE_CHECK),
				(String) mefBlockBsonObj
						.get(MEFBlock.DATASET_ID),
				(byte[]) mefBlockBsonObj
						.get(MEFBlock.DATA));
	}

	public MEFBlock(
			Integer version,
			String timeSeriesId,
			Long startOffsetMicros,
			Long endOffsetMicros,
			Double samplingRateHz,
			Double voltageConvFactorMicroV,
			Long startUutc,
			Long blockNo,
			String fileCheck,
			String datasetId,
			byte[] data) {
		this.version = checkNotNull(version);
		this.timeSeriesId = checkNotNull(timeSeriesId);

		this.startOffsetMicros = checkNotNull(startOffsetMicros);
		checkArgument(startOffsetMicros.longValue() >= 0);

		this.endOffsetMicros = checkNotNull(endOffsetMicros);
		checkArgument(endOffsetMicros.longValue() >= 0);

		this.samplingRateHz = checkNotNull(samplingRateHz);
		this.voltageConvFactorMicroV = checkNotNull(voltageConvFactorMicroV);
		this.startUutc = checkNotNull(startUutc);
		this.blockNo = checkNotNull(blockNo);
		this.fileCheck = checkNotNull(fileCheck);
		this.datasetId = checkNotNull(datasetId);
		this.data = checkNotNull(data);
	}

	public MEFBlock(
			String timeSeriesId,
			Long startOffsetMicros,
			Long endOffsetMicros,
			Double samplingRateHz,
			Double voltageConvFactorMicroV,
			Long startUutc,
			Long blockNo,
			String fileCheck,
			String datasetId,
			byte[] data) {
		this(
				2,
				timeSeriesId,
				startOffsetMicros,
				endOffsetMicros,
				samplingRateHz,
				voltageConvFactorMicroV,
				startUutc,
				blockNo,
				fileCheck,
				datasetId,
				data);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MEFBlock)) {
			return false;
		}
		final MEFBlock other = (MEFBlock) obj;
		if (!Objects.equal(blockNo, other.blockNo)) {
			return false;
		}
		if (!Objects.equal(timeSeriesId, other.timeSeriesId)) {
			return false;
		}
		if (!Objects.equal(fileCheck, other.fileCheck)) {
			return false;
		}
		if (!Objects.equal(version, other.version)) {
			return false;
		}
		return true;
	}

	public Long getBlockNo() {
		return blockNo;
	}

	public byte[] getData() {
		return data;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public Long getEndOffsetMicros() {
		return endOffsetMicros;
	}

	public String getFileCheck() {
		return fileCheck;
	}

	public Double getSamplingRateHz() {
		return samplingRateHz;
	}

	public Long getStartOffsetMicros() {
		return startOffsetMicros;
	}

	public Long getStartUutc() {
		return startUutc;
	}

	public String getTimeSeriesId() {
		return timeSeriesId;
	}

	public Integer getVersion() {
		return version;
	}

	public Double getVoltageConvFactorMicroV() {
		return voltageConvFactorMicroV;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blockNo == null) ? 0 : blockNo.hashCode());
		result = prime * result
				+ ((timeSeriesId == null) ? 0 : timeSeriesId.hashCode());
		result = prime * result
				+ ((fileCheck == null) ? 0 : fileCheck.hashCode());
		result = prime * result
				+ ((samplingRateHz == null) ? 0 : samplingRateHz.hashCode());
		result = prime * result
				+ ((version == null) ? 0 : version.hashCode());
		return result;
	}

	public DBObject toDBObject() {
		final DBObject mefBlockDbObject =
				new BasicDBObject(
						MEFBlock.VERSION_FIELD, version)
						.append(MEFBlock.TIME_SERIES_ID,
								getTimeSeriesId())
						.append(MEFBlock.START_OFFSET_MICROS,
								startOffsetMicros)
						.append(MEFBlock.END_OFFSET_MICROS,
								endOffsetMicros)
						.append(MEFBlock.SAMPLING_RATE_HZ,
								samplingRateHz)
						.append(MEFBlock.VOLTAGE_CONV_FACTOR_MICRO_V,
								voltageConvFactorMicroV)
						.append(MEFBlock.START_UUTC,
								startUutc)
						.append(MEFBlock.BLOCK_NO,
								blockNo)
						.append(MEFBlock.FILE_CHECK,
								fileCheck)
						.append(MEFBlock.DATASET_ID,
								datasetId)
						.append(MEFBlock.DATA, getData());
		return mefBlockDbObject;
	}

	@Override
	public String toString() {
		return "MEFBlock [version=" + version + ", timeSeriesId="
				+ timeSeriesId + ", startOffsetMicros=" + startOffsetMicros
				+ ", endOffsetMicros=" + endOffsetMicros + ", samplingRateHz="
				+ samplingRateHz + ", voltageConvFactorMicroV="
				+ voltageConvFactorMicroV + ", startUutc=" + startUutc
				+ ", blockNo=" + blockNo + ", fileCheck=" + fileCheck
				+ ", datasetId=" + datasetId + "]";
	}

}