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
import static com.google.common.collect.Sets.newTreeSet;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.SortedSet;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

import edu.upenn.cis.db.mefview.services.MEFBlock.BlockNoComparator;

public final class DataGetter {

	private static final Integer MEF_BLOCK_VERSION = 2;

	private static Logger logger = LoggerFactory.getLogger(DataGetter.class);

	private DBCollection coll;

	public DataGetter() throws UnknownHostException {
		this(null, null);
	}

	public DataGetter(String hostname) throws UnknownHostException {
		this(hostname, null);
	}

	public DataGetter(
			@Nullable String hostname,
			@Nullable Integer port)
			throws UnknownHostException {
		this(hostname, port, null, null);
	}

	public DataGetter(
			@Nullable String hostname,
			@Nullable Integer port,
			@Nullable String mongoDbUsername,
			@Nullable String mongoDbPwd) throws UnknownHostException {
		String m = "DataGetter(...)";
		ServerAddress serverAddress;
		if (hostname == null) {
			checkArgument(port == null);
			serverAddress = new ServerAddress(hostname);
		} else {
			if (port == null) {
				serverAddress = new ServerAddress(hostname);
			} else {
				serverAddress = new ServerAddress(hostname, port);
			}
		}
		final Builder optionsBuilder = MongoClientOptions.builder();
		optionsBuilder.writeConcern(WriteConcern.UNACKNOWLEDGED);
		MongoClient mongoClient;
		if (mongoDbUsername == null && mongoDbPwd == null) {
			// No authentication
			mongoClient = new MongoClient(serverAddress, optionsBuilder.build());
		} else if (mongoDbUsername != null && mongoDbPwd != null) {
			final MongoCredential credential = MongoCredential
					.createCredential(mongoDbUsername,
							"ieeg", mongoDbPwd.toCharArray());
			mongoClient = new MongoClient(serverAddress, credential,
					optionsBuilder.build());
		} else {
			throw new IllegalArgumentException(
					"mongoDbUsername and mongoDbPwd must either be both null or both non-null");
		}

		DB db = mongoClient.getDB("ieeg");

		coll = db.getCollection(MEFBlock.MONGO_COLLECTION_NAME);

		try {
			coll.dropIndex(
					new BasicDBObject(
							MEFBlock.TIME_SERIES_ID, 1)
							.append(MEFBlock.START_OFFSET_MICROS, 1)
					);
			logger.debug("{}: dropping old index", m);
		} catch (MongoException e) {
			logger.debug("{}: old index already dropped", m);
		}

		coll.createIndex(
				new BasicDBObject(
						MEFBlock.TIME_SERIES_ID, 1)
						.append(MEFBlock.VERSION_FIELD, 1)
						.append(MEFBlock.START_OFFSET_MICROS, 1)
				);

		try {
			coll.dropIndex(
					new BasicDBObject(
							MEFBlock.TIME_SERIES_ID, 1)
							.append(MEFBlock.BLOCK_NO, 1));
			logger.debug("{}: dropping old unique index", m);
		} catch (MongoException e) {
			// those were version null indices
			// it's not there any more - that's good
			logger.debug("{}: old unique index already dropped", m);
		}

		final BasicDBObject keys = new BasicDBObject(
				MEFBlock.TIME_SERIES_ID, 1)
				.append(MEFBlock.BLOCK_NO, 1)
				.append(MEFBlock.VERSION_FIELD, 1);
		final Map<String, ?> indexOptions = ImmutableMap.of(
				"unique", true,
				"name", genIndexName(keys),
				"ns", coll.getFullName());
		coll.createIndex(
				keys,
				new BasicDBObject(indexOptions));
	}

	/**
	 * Returns an index name based on keys. Taken from old Mongo java driver
	 * where this was the default name when none was provided.
	 * 
	 * @param keys
	 * @return
	 */
	private static String genIndexName(DBObject keys) {
		StringBuilder name = new StringBuilder();
		for (String s : keys.keySet()) {
			if (name.length() > 0)
				name.append('_');
			name.append(s).append('_');
			Object val = keys.get(s);
			if (val instanceof Number || val instanceof String)
				name.append(val.toString().replace(' ', '_'));
		}
		return name.toString();
	}

	public Optional<SortedSet<MEFBlock>> getMEFBlocks(
			TimeSeriesIdAndDCheck tsIdAndDCheck,
			long startOffsetUsecs,
			long durationUsecs) {
		String m = "getMEFBlocks(...)";
		BasicDBObject timeSeriesIdClause =
				new BasicDBObject(
						MEFBlock.TIME_SERIES_ID,
						tsIdAndDCheck.getId())
						.append(MEFBlock.VERSION_FIELD, MEF_BLOCK_VERSION);

		BasicDBObject clause2 =
				new BasicDBObject(
						MEFBlock.START_OFFSET_MICROS,
						new BasicDBObject("$gte", startOffsetUsecs)
								.append("$lt",
										startOffsetUsecs + durationUsecs));

		DBObject query =
				QueryBuilder
						.start()
						.and(
								timeSeriesIdClause,
								clause2)
						.get();

		DBCursor cursor = coll.find(query);
		// System.out.println(cursor.explain());
		SortedSet<MEFBlock> mefBlocks = newTreeSet(new BlockNoComparator());
		try {
			while (cursor.hasNext()) {
				DBObject dbObject = cursor.next();
				mefBlocks.add(new MEFBlock(dbObject));
			}
		} finally {
			cursor.close();
		}

		if (mefBlocks.size() > 0) {
			MEFBlock firstBlock = mefBlocks.first();
			if (firstBlock.getStartOffsetMicros() > startOffsetUsecs) {
				BasicDBObject ltBlockNo =
						new BasicDBObject(
								MEFBlock.BLOCK_NO,
								new BasicDBObject(
										"$lt",
										firstBlock.getBlockNo()));
				DBObject query2 =
						QueryBuilder
								.start()
								.and(
										timeSeriesIdClause,
										ltBlockNo
								)
								.get();

				DBCursor cursor2 = coll
						.find(query2)
						.sort(new BasicDBObject(MEFBlock.BLOCK_NO, -1))
						.limit(1);
				try {
					while (cursor2.hasNext()) {
						DBObject dbObject = cursor2.next();
						MEFBlock mefBlock = new MEFBlock(dbObject);
						if (mefBlock.getEndOffsetMicros() >= startOffsetUsecs) {
							mefBlocks.add(mefBlock);
						}
					}
				} finally {
					cursor2.close();
				}
			}
		}

		boolean lookBefore = false, lookAfter = false;

		if (mefBlocks.size() == 0) {
			lookBefore = lookAfter = true;
		} else if (mefBlocks.first().getStartOffsetMicros() > startOffsetUsecs) {
			lookBefore = true;
			lookAfter = false;
		} else if (mefBlocks.last().getEndOffsetMicros() < (startOffsetUsecs + durationUsecs)) {
			lookBefore = false;
			lookAfter = true;
		}

		if (lookBefore) {
			logger.debug("{}: lookBefore", m);
			BasicDBObject firstLtStart =
					new BasicDBObject(
							MEFBlock.START_OFFSET_MICROS,
							new BasicDBObject("$lt",
									startOffsetUsecs));

			DBObject query3 =
					QueryBuilder
							.start()
							.and(
									timeSeriesIdClause,
									firstLtStart)
							.get();

			DBCursor cursor2 =
					coll.find(query3)
							.sort(new BasicDBObject(
									MEFBlock.START_OFFSET_MICROS, -1))
							.limit(1);

			// System.out.println(cursor2.explain());

			while (cursor2.hasNext()) {
				MEFBlock mefBlock = new MEFBlock(cursor2.next());
				mefBlocks.add(mefBlock);
				// System.out.println("found before gap");
			}
		}
		if (mefBlocks.size() > 0
				&& mefBlocks.first().getStartOffsetMicros() <= startOffsetUsecs
				&& mefBlocks.last().getEndOffsetMicros() >= (startOffsetUsecs + durationUsecs)) {
			// we found it
			lookAfter = false;
		}
		if (lookAfter) {
			logger.debug("{}: lookAfter", m);
			BasicDBObject firstGtStart =
					new BasicDBObject(
							MEFBlock.START_OFFSET_MICROS,
							new BasicDBObject("$gte",
									startOffsetUsecs + durationUsecs));

			DBObject query5 =
					QueryBuilder
							.start()
							.and(
									timeSeriesIdClause,
									firstGtStart)
							.get();

			DBCursor cursor3 =
					coll.find(query5)
							.sort(new BasicDBObject(
									MEFBlock.START_OFFSET_MICROS, 1))
							.limit(1);

			// System.out.println(cursor3.explain());

			while (cursor3.hasNext()) {
				MEFBlock mefBlock = new MEFBlock(cursor3.next());
				mefBlocks.add(mefBlock);
				// System.out.println("found after gap");
			}
		}

		if (mefBlocks.size() > 0
				&& mefBlocks.first().getStartOffsetMicros() <= startOffsetUsecs
				&& mefBlocks.last().getEndOffsetMicros() >= (startOffsetUsecs + durationUsecs)) {
			boolean contiguousBlocks = true;
			long lastBlockNo = -1;
			for (MEFBlock mefBlock : mefBlocks) {
				if (!mefBlock.getTimeSeriesId().equals(tsIdAndDCheck.getId())) {
					logger.error(
							m
									+ ": mongodb query was incorrect, wrong time series id");
					throw new AssertionError(
							"mongodb query was incorrect, wrong time series id");
				}
				if (!mefBlock.getVersion().equals(MEF_BLOCK_VERSION)) {
					logger.error(
							m
									+ ": mongodb query was incorrect, wrong mef block version found "
									+ mefBlock.getVersion() + " wanted "
									+ MEF_BLOCK_VERSION);
					throw new AssertionError(
							"mongodb query was incorrect, wrong mef block version found "
									+ mefBlock.getVersion() + " wanted "
									+ MEF_BLOCK_VERSION);
				}
				if (lastBlockNo == -1
						|| mefBlock.getBlockNo() == lastBlockNo + 1) {
					lastBlockNo = mefBlock.getBlockNo();
				} else {
					contiguousBlocks = false;
					break;
				}
			}
			if (contiguousBlocks) {
				return Optional.of(mefBlocks);
			}
		}
		return Optional.absent();
	}

	public void putMEFBlock(MEFBlock mefBlock) {
		DBObject mefBlockDbObject = mefBlock.toDBObject();
		coll.insert(mefBlockDbObject);
	}
}
