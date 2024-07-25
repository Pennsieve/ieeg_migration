/*******************************************************************************
 * Copyright 2010 Trustees of the University of Pennsylvania
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package edu.upenn.cis.db.mefview.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newTreeSet;
import static edu.upenn.cis.IeegUtil.diffNowThenSeconds;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nullable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

import edu.upenn.cis.db.mefview.shared.EEGMontage;
import edu.upenn.cis.db.mefview.shared.EEGMontages;
import edu.upenn.cis.eeg.edf.EDFHeader;
import edu.upenn.cis.thirdparty.RED;
import edu.upenn.cis.thirdparty.RED.PageAndBytesRead;

/**
 * Main interface class to the Time Series Web service. Instantiate a proxy
 * object of this type, with user ID & password, to enable REST calls to the Web
 * service.
 * 
 * @author zives
 */
public class TimeSeriesInterface extends ServerInterface {
	private ITimeSeriesResource timeSeriesResource;
	private IDatasetResource datasetResource;
	private final IeegWsErrorHandler errorHandler = new IeegWsErrorHandler();

	private static final Logger timeLogger =
			LoggerFactory.getLogger("time."
					+ TimeSeriesInterface.class.getName());

	private static final Logger logger =
			LoggerFactory.getLogger(TimeSeriesInterface.class);

	private final RED red = new RED();

	private Optional<DataGetter> dataGetter = Optional.absent();

	public static TimeSeriesInterface newInstance(
			String toolRevId,
			String url,
			String username,
			String password,
			String tsiPropsDir) throws UnknownHostException {
		TimeSeriesInterface ret = newInstance(
				url,
				username,
				password,
				tsiPropsDir);
		return ret;
	}

	public static TimeSeriesInterface newInstance(
			String url,
			String username,
			String password,
			String tsiPropsDir) throws UnknownHostException {
		final String m = "newInstance(...)";
		final File tsiPropsFile = new File(tsiPropsDir, "tsi.properties");
		if (tsiPropsFile.exists()) {
			logger.debug("{}: reading properties file: {}",
					m,
					tsiPropsFile.getAbsolutePath());
			TsiProps props = new TsiProps(tsiPropsFile);
			Optional<String> mongoDbHostname = props.getMongoDbHostname();
			Optional<Integer> mongoDbPort = props.getMongoDbPort();
			if (mongoDbHostname.isPresent() && mongoDbPort.isPresent()) {

				final Optional<String> mongoDbUsername = props
						.getMongoDbUsername();
				if (mongoDbUsername.isPresent()) {
					return new TimeSeriesInterface(
							url,
							username,
							password,
							mongoDbHostname.get(),
							mongoDbPort.get(),
							mongoDbUsername.get(),
							props.getMongoDbPassword());
				} else {
					return new TimeSeriesInterface(
							url,
							username,
							password,
							mongoDbHostname.get(),
							mongoDbPort.get(),
							null,
							null);
				}
			} else if (mongoDbHostname.isPresent() && !mongoDbPort.isPresent()) {
				final Optional<String> mongoDbUsername = props
						.getMongoDbUsername();
				if (mongoDbUsername.isPresent()) {
					return new TimeSeriesInterface(
							url,
							username,
							password,
							mongoDbHostname.get(),
							null,
							mongoDbUsername.get(),
							props.getMongoDbPassword());
				} else {
					return new TimeSeriesInterface(
							url,
							username,
							password,
							mongoDbHostname.get(),
							null,
							null,
							null);
				}
			} else {
				return new TimeSeriesInterface(url, username, password, false);
			}
		} else {
			logger.debug(
					"{}: no properties file found",
					m);
			return new TimeSeriesInterface(url, username, password, false);
		}
	}

	public TimeSeriesInterface(
			String url,
			String username,
			String password,
			boolean useMongoDb) throws UnknownHostException {
		this(url, username, password);
		if (useMongoDb) {
			dataGetter = Optional.of(new DataGetter());
		} else {
			dataGetter = Optional.absent();
		}
	}

	public TimeSeriesInterface(
			String url,
			String username,
			String password,
			String hostname) throws UnknownHostException {
		this(url, username, password);
		dataGetter = Optional.of(new DataGetter(hostname));
	}

	public TimeSeriesInterface(
			String url,
			String username,
			String password,
			String hostname,
			Integer port) throws UnknownHostException {
		this(url, username, password);
		dataGetter = Optional.of(new DataGetter(hostname, port));
	}

	public TimeSeriesInterface(
			String url,
			String username,
			String password,
			@Nullable String hostname,
			@Nullable Integer port,
			@Nullable String mongoDbUsername,
			@Nullable String mongoDbPwd) throws UnknownHostException {
		this(url, username, password);
		dataGetter = Optional.of(
				new DataGetter(hostname, port, mongoDbUsername, mongoDbPwd));
	}

	public TimeSeriesInterface(
			String url,
			String username,
			String password) {
		super(
				url,
				username,
				password);
		ResteasyWebTarget rtarget = getResteasyWebTarget();
		timeSeriesResource = rtarget.proxy(ITimeSeriesResource.class);
		datasetResource = rtarget.proxy(IDatasetResource.class);
	}

	public Set<String> getOwnedDataSnapshotNames(String creator) {
		final String M = "getOwnedDataSnapshotNames(...)";
		final long start = System.nanoTime();
		try {
			setUserAndPassword();
			String result = timeSeriesResource
					.getOwnedDataSnapshotNames(creator);

			Set<String> results = new HashSet<String>();

			if (!result.isEmpty()) {
				String[] names = result.split(",");
				for (String nam : names)
					results.add(nam);
			}

			return results;
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		} finally {
			timeLogger.info("{}: {} seconds", M, diffNowThenSeconds(start));
		}
	}

	public Set<String> getDataSnapshotNames() {
		final String M = "getDataSnapshotNames(...)";
		final long start = System.nanoTime();
		try {
			setUserAndPassword();
			String result = timeSeriesResource
					.getOwnedDataSnapshotNames("any");

			Set<String> results = new HashSet<String>();

			if (!result.isEmpty()) {
				String[] names = result.split(",");
				for (String nam : names)
					results.add(nam);
			}

			return results;
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		} finally {
			timeLogger.info("{}: {} seconds", M, diffNowThenSeconds(start));
		}
	}

	/**
	 * Returns the header information for the time series traces associated with
	 * the dataSnapshot
	 * 
	 * @param dataSnapshot
	 * @return list of time series, sorted by name
	 */
	public List<TimeSeriesDetails> getDataSnapshotTimeSeriesDetails(
			final String dataSnapshot) {
		final String M = "getDataSnapshotTimeSeriesDetails(...)";
		final long start = System.nanoTime();
		try {
			setUserAndPassword();
			return (timeSeriesResource
					.getDataSnapshotTimeSeriesDetails(
					dataSnapshot)).getList();
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		} finally {
			timeLogger.info("{}: {} seconds", M, diffNowThenSeconds(start));
		}
	}

	public List<TimeSeriesAnnotation> getTsAnnotationsLtStartTime(
			String dataSnapshotId,
			long startTimeUutc,
			String layer,
			int firstResult,
			int maxResults) {
		setUserAndPassword();
		try {
			return timeSeriesResource.getTsAnnotations(
					dataSnapshotId,
					startTimeUutc,
					layer,
					true,
					firstResult,
					maxResults).getList();
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	/**
	 * Returns at most {@code maxResults} annotations from the given data
	 * snapshot that annotated {@code annotated} and which have start times
	 * greater {@code >= startTimeUutc} and are in the given layer. Ordered
	 * first by start time, then by id.
	 * 
	 * @param dataSnapshotId the data snapshot that contains {@code tsAnn}
	 * @param startTimeUutc the start time we should start at
	 * @param layer the layer we want
	 * @param annotated only return annotations that annotated these time series
	 * @param maxCount the max number of annotations to return
	 * 
	 * @return at most {@code maxCount} annotations from the given data snapshot
	 *         which have start times greater {@code >= startTimeUutc} and are
	 *         in the given layer. Ordered first by start time, then by id.
	 */
	public List<TimeSeriesAnnotation> getTsAnnotations(
			String dataSnapshotId,
			long startTimeUutc,
			String layer,
			int firstResult,
			int maxResults) {
		setUserAndPassword();
		try {
			return timeSeriesResource.getTsAnnotations(
					dataSnapshotId,
					startTimeUutc,
					layer,
					null,
					firstResult,
					maxResults).getList();
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	public Response getTsAnnotations(
			String datasetId) {
		setUserAndPassword();
		Response response = datasetResource.getTsAnnotations(
				datasetId);
		errorHandler.handleErrorClientResponse(response);
		return response;
	}

	public void setDataSnapshotName(
			String dataSnapshotId,
			String originalName,
			String newName) {
		setUserAndPassword();
		try {
			timeSeriesResource.setDataSnapshotName(
					dataSnapshotId,
					originalName,
					newName);
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	public int moveTsAnnotations(
			String datasetId,
			String fromLayerName,
			String toLayerName) {
		setUserAndPassword();
		try {
			return timeSeriesResource.moveTsAnnotations(
					datasetId,
					fromLayerName,
					toLayerName).getMoved();
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	public UnscaledTimeSeriesSegment[][] getUnscaledTimeSeriesSet(
			final String dataSnapshot,
			final String[] seriesNames,
			final FilterSpec[] filters,
			final double start,
			final double duration,
			final double frequency) {
		setUserAndPassword();
		return getUnscaledTimeSeriesSet(
				dataSnapshot,
				seriesNames,
				filters,
				start,
				duration,
				frequency,
				null);
	}

	/**
	 * Get multiple time series, each as a series of segments, of which each in
	 * turn has its own voltage conversion factor.
	 * 
	 * @param dataSnapshot
	 * @param seriesNames
	 * @param start
	 * @param duration [MAXIMUM OF 999 SEC]
	 * @param frequency
	 * @param filterList
	 * @return
	 */
	public UnscaledTimeSeriesSegment[][] getUnscaledTimeSeriesSet(
			final String dataSnapshot,
			final String[] seriesNames,
			final FilterSpec[] filters,
			final double start,
			final double duration,
			final double frequency,
			final String processing) {
		final String m = "getUnscaledTimeSeriesSet(...)";
		final long startMethod = System.nanoTime();
		try {
			setUserAndPassword();
			final TimeSeriesRequestList reqList = new TimeSeriesRequestList(
					seriesNames, filters);
			final Response response = timeSeriesResource
					.getUnscaledTimeSeriesSetBinary(
							dataSnapshot,
							reqList,
							start,
							duration,
							frequency,
							processing);
			errorHandler.handleErrorClientResponse(response);
			final String samplesPerRowHdr = (String) response.getHeaders()
					.getFirst(
							ITimeSeriesResource.SAMPLES_PER_ROW);
			logger.debug("{}: {} value: {}", new Object[] {
					m,
					ITimeSeriesResource.SAMPLES_PER_ROW,
					samplesPerRowHdr });
			final Iterator<String> samplesPerRowItr = Splitter.on(",")
					.split(samplesPerRowHdr).iterator();
			final String voltageConvFactorsMvHdr = (String) response
					.getHeaders()
					.getFirst(ITimeSeriesResource.VOLTAGE_CONVERSION_FACTORS_MV);
			logger.debug("{}: {} value: {}", new Object[] {
					m,
					ITimeSeriesResource.VOLTAGE_CONVERSION_FACTORS_MV,
					voltageConvFactorsMvHdr });
			final Iterator<String> voltageConvFactorsMvItr = Splitter.on(",")
					.split(voltageConvFactorsMvHdr).iterator();
			final byte[] entity = response.readEntity(byte[].class);
			logger.info("{}: byte array size: {}", m, entity.length);
			final DataInputStream is = new DataInputStream(
					new ByteArrayInputStream(entity));
			UnscaledTimeSeriesSegment[][] ret = new UnscaledTimeSeriesSegment[seriesNames
					.length][1];
			for (int i = 0; i < ret.length; i++) {
				final int noSamples = Integer.parseInt(samplesPerRowItr.next());
				final double voltageConvFactorMv = Double
						.parseDouble(voltageConvFactorsMvItr.next());

				int[] values = new int[noSamples];
				for (int j = 0; j < noSamples; j++) {
					values[j] = is.readInt();
				}
				logger.trace("{}: Adding segment of length {}", m,
						values.length);
				ret[i][0] = new UnscaledTimeSeriesSegment(voltageConvFactorMv,
						values);
			}
			is.close();
			return ret;
		} catch (Exception e) {
			logger.error("caught exception", e);
			Throwables.propagateIfInstanceOf(e, IeegWsRemoteAppException.class);
			throw Throwables.propagate(e);
		} finally {
			timeLogger.info("{}: {} seconds", m,
					diffNowThenSeconds(startMethod));
		}

	}

	/**
	 * Get multiple time series, each as a series of segments, of which each in
	 * turn has its own voltage conversion factor.
	 * 
	 * @param datasetId
	 * @param timeSeriesIds
	 * @param startOffsetUsecs
	 * @param durationUsecs
	 * @param frequency
	 * @param filterList
	 * @return
	 */
	public UnscaledTimeSeriesSegment[][] getUnscaledTimeSeriesSetRaw(
			String datasetId,
			List<TimeSeriesIdAndDCheck> timeSeriesIdAndDVers,
			double startOffsetUsecs,
			double durationUsecs,
			int downsampleFactor) {
		String m = "getUnscaledTimeSeriesSetRaw(...)";
		long startMethod = System.nanoTime();
		logger.debug("{}: startOffsetUSecs: {} durationUSecs: {}",
				new Object[] {
						m,
						startOffsetUsecs,
						durationUsecs });
		try {
			setUserAndPassword();
			List<TimeSeriesIdAndDCheck> timeSeriesWeNeed = newArrayList();

			Map<TimeSeriesIdAndDCheck, UnscaledTimeSeriesSegment> timeSeriesSegments = newHashMap();

			if (!dataGetter.isPresent()) {
				timeSeriesWeNeed = newArrayList(timeSeriesIdAndDVers);
			} else {
				long in = 0;
				for (TimeSeriesIdAndDCheck timeSeriesId : timeSeriesIdAndDVers) {
					long in2 = System.nanoTime();
					long startOffsetUsecsLong = (long) startOffsetUsecs;
					long durationUsecsLong = (long) durationUsecs;
					logger.debug(
							"{}: starOffsetUsecsLong: {} durationUsecsLong: {}",
							new Object[] {
									m,
									startOffsetUsecsLong,
									durationUsecsLong });
					Optional<SortedSet<MEFBlock>> mefBlocks =
							dataGetter
									.get()
									.getMEFBlocks(
											timeSeriesId,
											startOffsetUsecsLong,
											durationUsecsLong);
					long in3 = System.nanoTime() - in2;
					in += in3;
					if (mefBlocks.isPresent()) {
						logger.debug("{}: found in mongodb", m);
						long timeSeriesStartUutc = -1;
						double samplingRate = -1;
						double voltageConvFactorMicroV = -1;
						final List<TimeSeriesPage> pages = newArrayList();
						for (MEFBlock mefBlock : mefBlocks.get()) {
							timeSeriesStartUutc = mefBlock
									.getStartUutc();
							samplingRate = mefBlock.getSamplingRateHz();
							voltageConvFactorMicroV = mefBlock
									.getVoltageConvFactorMicroV();
							PageAndBytesRead pageAndBytesRead = red.decodePage(
									mefBlock.getData(),
									0,
									1E6 / mefBlock.getSamplingRateHz());
							logger.trace("{}: Decoded page from cache: {}", m,
									pageAndBytesRead);
							pages.add(pageAndBytesRead.page);
						}
						final double startTimeAbs = startOffsetUsecs
								+ timeSeriesStartUutc;
						final double endTimeAbs = startTimeAbs + durationUsecs;
						final Downsample downsample = new Downsample(
								samplingRate,
								downsampleFactor);
						logger.debug("{}: startTimeAbs {} endTimeAbs {}"
								, new Object[] { m, startTimeAbs, endTimeAbs });
						ArrayList<TimeSeriesData> tsd = downsample
								.process(
										startTimeAbs,
										endTimeAbs,
										samplingRate,
										(long) startOffsetUsecs,
										voltageConvFactorMicroV,
										pages.toArray(
												new TimeSeriesPage[pages.size()]),
										null,// not used
										false,// not used
										-1,// not used
										-1);// not used
						TimeSeriesData timeSeriesData = getOnlyElement(tsd);
						timeSeriesSegments.put(
								timeSeriesId,
								timeSeriesData.getSegment());
					} else {
						logger.debug("{}: not found in mongodb", m);
						timeSeriesWeNeed.add(timeSeriesId);
					}
				}
				timeLogger.debug("{}: out of mongodb {} seconds", m,
						in / 1E9);
			}
			UnscaledTimeSeriesSegment[][] ret = new UnscaledTimeSeriesSegment[timeSeriesIdAndDVers
					.size()][1];
			if (timeSeriesWeNeed.size() > 0) {
				logger.debug("{}: going over the network", m);
				TimeSeriesIdAndDChecks reqList =
						new TimeSeriesIdAndDChecks(timeSeriesWeNeed);

				long network = System.nanoTime();

				final Response response = timeSeriesResource
						.getUnscaledTimeSeriesSetRawRed(
								datasetId,
								reqList,
								startOffsetUsecs,
								durationUsecs);

				errorHandler.handleErrorClientResponse(response);

				final byte[] entity = response.readEntity(byte[].class);
				logger.info("{}: byte array size: {}", m, entity.length);

				timeLogger.debug("{}: out of network {} seconds", m,
						(System.nanoTime() - network) / 1E9);

				final String rowLengthsStr =
						(String) response
								.getHeaders()
								.getFirst(ITimeSeriesResource.ROW_LENGTHS);
				logger.debug("{}: rowLengths value: {}", m, rowLengthsStr);
				final Iterable<String> rowLengths =
						Splitter.on(",").split(rowLengthsStr);
				final String sampleRatesStr = (String) response.getHeaders()
						.getFirst(ITimeSeriesResource.SAMPLE_RATES);
				logger.debug("{}: sampleRates value: {}", m, sampleRatesStr);
				Iterable<String> sampleRates =
						Splitter.on(",")
								.split(sampleRatesStr);
				Iterator<String> sampleRatesItr = sampleRates.iterator();

				String voltageConvFactorsMicroVStr = (String) response
						.getHeaders()
						.getFirst(
								ITimeSeriesResource.VOLTAGE_CONVERSION_FACTORS_MV);
				logger.debug("{}: voltageConversionFactorsMv value: {}", m,
						voltageConvFactorsMicroVStr);
				Iterable<String> voltageConvFactorsMicroV = Splitter.on(
						",")
						.split(voltageConvFactorsMicroVStr);

				Iterator<String> voltageConvFactorsMicroVItr =
						voltageConvFactorsMicroV.iterator();

				String mefBlockNosHeaderValue = (String) response
						.getHeaders()
						.getFirst(ITimeSeriesResource.MEF_BLOCK_NOS);

				Iterable<String> pageNos =
						Splitter.on(",")
								.split(mefBlockNosHeaderValue);
				Iterator<String> pageNosItr = pageNos.iterator();
				final String startTimesUutcStr = (String) response.getHeaders()
						.getFirst(ITimeSeriesResource.START_TIMES_UUTC);
				logger.debug("{}: startTimesUutc value: {}", m,
						startTimesUutcStr);
				Iterable<String> startTimesUutc =
						Splitter.on(",")
								.split(startTimesUutcStr);
				Iterator<String> startTimesUutcItr = startTimesUutc.iterator();

				int pos = 0;
				int rowNo = -1;
				for (String rowLengthStr : rowLengths) {
					rowNo++;
					Long lowBlockNo = Long.valueOf(pageNosItr.next());
					Long highBlockNo = Long.valueOf(pageNosItr.next());
					Range<Long> blockNosRange =
							Range.closed(lowBlockNo, highBlockNo);
					ContiguousSet<Long> blockNos =
							ContiguousSet.create(
									blockNosRange,
									DiscreteDomain.longs());
					Iterator<Long> blockNosItr = blockNos.iterator();
					final int rowLength = Integer.parseInt(rowLengthStr);
					double sampleRate =
							Double.parseDouble(sampleRatesItr.next());
					double voltageConvFactorMicroV =
							Double.parseDouble(
									voltageConvFactorsMicroVItr
											.next());
					final long startUutc =
							Long.parseLong(startTimesUutcItr.next());
					logger.debug(
							"{}: row {}: length: {}, sample rate: {}, voltage conversion factor: {}, start time UUTC: {}",
							new Object[] {
									m,
									rowNo,
									rowLength,
									sampleRate,
									voltageConvFactorMicroV,
									startUutc
							});
					double samplePeriod = 1e6 / sampleRate;
					int thisRow = 0;
					final List<TimeSeriesPage> pages = newArrayList();
					while (thisRow < rowLength) {
						PageAndBytesRead pageAndBytesRead =
								red.decodePage(entity, pos, samplePeriod);
						logger.trace("{}: Decoded page from network: {}", m,
								pageAndBytesRead);
						pages.add(pageAndBytesRead.page);
						pos += pageAndBytesRead.noBytesRead;
						thisRow += pageAndBytesRead.noBytesRead;
						if (dataGetter.isPresent()) {
							dataGetter
									.get()
									.putMEFBlock(
											new MEFBlock(
													timeSeriesWeNeed
															.get(rowNo)
															.getId(),
													pageAndBytesRead.page.timeStart
															- startUutc,
													pageAndBytesRead.page.timeEnd
															- startUutc,
													sampleRate,
													voltageConvFactorMicroV,
													startUutc,
													blockNosItr.next(),
													datasetId,
													timeSeriesWeNeed
															.get(rowNo)
															.getDataCheck(),
													pageAndBytesRead.bytesRead));
						}
					}

					final double startTimeAbs = startOffsetUsecs
							+ startUutc;
					final double endTimeAbs = startTimeAbs + durationUsecs;
					final Downsample downsample = new Downsample(sampleRate,
							downsampleFactor);

					ArrayList<TimeSeriesData> tsd = downsample.process(
							startTimeAbs,
							endTimeAbs,
							sampleRate,
							(long) startOffsetUsecs,
							voltageConvFactorMicroV,
							pages.toArray(new TimeSeriesPage[pages.size()]),
							null,// not used
							false,// not used
							-1,// not used
							-1);// not used
					TimeSeriesData data = getOnlyElement(tsd);
					timeSeriesSegments.put(
							timeSeriesWeNeed.get(rowNo),
							data.getSegment());
				}
			}
			int timeSeriesIdsIdx = -1;
			for (TimeSeriesIdAndDCheck timeSeriesId : timeSeriesIdAndDVers) {
				timeSeriesIdsIdx++;
				ret[timeSeriesIdsIdx][0] =
						timeSeriesSegments.get(timeSeriesId);
			}
			return ret;
		} catch (Exception e) {
			logger.error("caught exception", e);
			Throwables.propagateIfInstanceOf(e,
					IeegWsRemoteAppException.class);
			throw Throwables.propagate(e);
		} finally {
			timeLogger.info("{}: {} seconds", m,
					diffNowThenSeconds(startMethod));
		}
	}

	public EDFHeader getEdfHeaderRaw(
			String datasetId) {
		String m = "getEdfHeaderRaw(...)";
		long startMethod = System.nanoTime();
		try {
			setUserAndPassword();
			checkNotNull(datasetId);

			final Response response = timeSeriesResource
					.getEdfHeaderRaw(datasetId);
			errorHandler.handleErrorClientResponse(response);

			InputStream entity = null;
			try {
				entity = response
						.readEntity(InputStream.class);
				final EDFHeader header = EDFHeader.createFromStream(entity);
				return header;
			} finally {
				if (entity != null) {
					entity.close();
				}
			}
		} catch (Exception e) {
			logger.error("caught exception", e);
			Throwables.propagateIfInstanceOf(e,
					IeegWsRemoteAppException.class);
			throw Throwables.propagate(e);
		} finally {
			timeLogger.info("{}: {} seconds", m,
					diffNowThenSeconds(startMethod));
		}
	}
	
	public List<EEGMontage> getMontages(String datasetId) {
			try {
				setUserAndPassword();
				checkNotNull(datasetId);

				final EEGMontages response = datasetResource
						.getMontages(datasetId);
				return newArrayList(response.getMontages());
			} catch (WebApplicationException e) {
				throw errorHandler.handleWebApplicationException(e);
			}
	}

	public int removeTsAnnotationsByLayer(
			String datasetId,
			String layer) {
		setUserAndPassword();
		try {
			return timeSeriesResource.removeTsAnnotationsByLayer(
					datasetId,
					layer).getNoDeleted();
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	/**
	 * Create a new, empty data snapshot "container"
	 * 
	 * @param friendlyName
	 * @param previousRevId Provenance tracking rev ID showing what we were
	 *            derived from
	 * @param toolName the name of the tool whose results will be stored in the
	 *            data snapshot
	 * @return
	 */
	public String deriveEmptyDataSnapshot(
			final String friendlyName,
			final String previousRevId,
			final String toolName) {
		setUserAndPassword();
		try {
			return timeSeriesResource.deriveEmptyDataSnapshot(
					friendlyName,
					previousRevId,
					toolName);
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	/**
	 * Create a new data snapshot "container" with the given time series.
	 * 
	 * @param friendlyName
	 * @param includedTimeSeriesIds IDs of the time series to include
	 * @param sourceId Provenance tracking ID showing what we were derived from
	 * @param toolName the name of the tool whose results will be stored in the
	 *            data snapshot
	 * @return
	 */
	public String deriveDataSnapshot(
			final String friendlyName,
			final String[] includedTimeSeriesIds,
			final String sourceId,
			String toolName) {
		final RevisionIdList revIdList = new RevisionIdList(
				Arrays.asList(includedTimeSeriesIds));
		setUserAndPassword();
		try {
			return timeSeriesResource.deriveDataSnapshot(
					friendlyName,
					revIdList,
					sourceId,
					toolName);
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	/**
	 * Derive a new data snapshot "container" that inherits the channels from
	 * its predecessor
	 * 
	 * @param friendlyName
	 * @param sourceId Provenance tracking ID showing what we were derived from
	 * @param toolName the name of the tool through which the derivation took
	 *            place
	 * @return
	 */
	public String deriveDataSnapshot(
			final String friendlyName,
			final String sourceId, String toolName) {
		setUserAndPassword();
		try {
			return timeSeriesResource.deriveDataSnapshot(
					friendlyName,
					sourceId,
					toolName);
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	/**
	 * Update a dataSnapshot by adding a set of annotations
	 */
	public String addAnnotationsToDataSnapshot(
			String dataSnapshot,
			List<TimeSeriesAnnotation> annotations) {
		setUserAndPassword();
		TimeSeriesAnnotationList annotationList = new TimeSeriesAnnotationList(
				annotations);
		try {
			String ret = timeSeriesResource.addAnnotationsToDataSnapshot(
					dataSnapshot,
					annotationList);
			return ret;
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}

	}

	public String getDataSnapshotIdByName(String datasetName) {
		setUserAndPassword();
		try {
			return timeSeriesResource.getDataSnapshotIdByName(
					datasetName);
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	public CountsByLayer getCountsByLayer(String datasetId) {
		setUserAndPassword();
		try {
			return timeSeriesResource.getCountsByLayer(
					datasetId);
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	public boolean isVersionOkay(String matlabClientVersion) {
		try {
			VersionOkay versionOkay =
					timeSeriesResource.isVersionOkay(matlabClientVersion);
			return versionOkay.isOkay();
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	public String test(String str) {
		try {
			return timeSeriesResource.test(str);
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	/**
	 * Utility fn for MATLAB: create a blank annotation list
	 */
	public ArrayList<TimeSeriesAnnotation> createAnnotationList() {
		setUserAndPassword();
		try {
			return new ArrayList<TimeSeriesAnnotation>();
		} catch (WebApplicationException e) {
			throw errorHandler.handleWebApplicationException(e);
		}
	}

	/**
	 * Utility fn for MATLAB: create a blank {@code TimeSeries} set.
	 */
	public SortedSet<TimeSeries> createTimeSeriesSet() {
		setUserAndPassword();
		return newTreeSet();
	}

//	public Montage createMontage(
//			String datasetId,
//			Montage montage) {
//		Response resp = null;
//		try {
//			setUserAndPassword();
//			resp =
//					datasetResource
//							.createMontage(
//									datasetId,
//									montage);
//			errorHandler.handleErrorClientResponse(resp);
//			Montage montageRet = resp.readEntity(Montage.class);
//			return montageRet;
//		} finally {
//			if (resp != null) {
//				resp.close();
//			}
//		}
//	}
//
//	public MontageInfos getMontageInfos(String datasetId) {
//		setUserAndPassword();
//		try {
//			return datasetResource.getMontageInfos(datasetId);
//		} catch (WebApplicationException e) {
//			throw errorHandler.handleWebApplicationException(e);
//		}
//	}
//
//	public Montage getMontage(Long montageId) {
//		setUserAndPassword();
//		Response response =
//				montageResource.getMontage(montageId);
//		errorHandler.handleErrorClientResponse(response);
//		Montage montage = (Montage) response.readEntity(Montage.class);
//		return montage;
//	}

//	/**
//	 * Delete the montage. It must contain no layers. If you want to check for
//	 * concurrent modifications, pass in an ETag, otherwise give it null.
//	 * 
//	 * @param montageId the id of the montage you want to delete.
//	 * @param eTag if null, no ETag check
//	 */
//	public void deleteMontage(
//			long montageId, @Nullable String eTag) {
//		setUserAndPassword();
//		try {
//			montageResource.deleteMontage(eTag, montageId);
//		} catch (WebApplicationException e) {
//			throw errorHandler.handleWebApplicationException(e);
//		}
//	}

//	public Layer patchLayer(LayerPatch patchLayer) {
//		setUserAndPassword();
//		try {
//			Layer layer = layerResource.patchLayer(
//					patchLayer.getId(),
//					patchLayer);
//			return layer;
//		} catch (WebApplicationException e) {
//			throw errorHandler.handleWebApplicationException(e);
//		}
//	}

//	public Set<MontagedChAnnotation> getMontagedChAnnotations(
//			long layerId,
//			long startOffsetUsecs,
//			int firstResult,
//			int maxResults) {
//		setUserAndPassword();
//		try {
//			Set<MontagedChAnnotation> annotations = layerResource
//					.getAnnotations(
//							layerId,
//							startOffsetUsecs,
//							null,
//							firstResult,
//							maxResults).getAnnotations();
//			return annotations;
//		} catch (WebApplicationException e) {
//			throw errorHandler.handleWebApplicationException(e);
//		}
//	}

//	public Layer createLayer(Long montageId, String layerName) {
//		Response resp = null;
//		try {
//			setUserAndPassword();
//			Layer layer = new Layer(layerName);
//			resp =
//					montageResource.createLayer(
//							montageId,
//							layer);
//			errorHandler.handleErrorClientResponse(resp);
//			Layer layerRet =
//					resp.readEntity(Layer.class);
//			return layerRet;
//		} finally {
//			if (resp != null) {
//				resp.close();
//			}
//		}
//	}
//
//	public Set<Layer> getLayers(Long montageId) {
//		setUserAndPassword();
//		try {
//			return montageResource
//					.getLayerInfos(montageId)
//					.getLayers();
//		} catch (WebApplicationException e) {
//			throw errorHandler.handleWebApplicationException(e);
//		}
//	}
//
//	public void deleteLayer(Layer layer) {
//		setUserAndPassword();
//		try {
//			layerResource
//					.deleteLayer(layer.getId());
//		} catch (WebApplicationException e) {
//			throw errorHandler.handleWebApplicationException(e);
//		}
//	}
}
