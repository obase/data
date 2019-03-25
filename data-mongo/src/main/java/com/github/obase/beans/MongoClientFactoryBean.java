package com.github.obase.beans;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.FactoryBean;

import com.github.obase.SystemException;
import com.github.obase.base.ConfBase;
import com.github.obase.base.ObjectBase;
import com.github.obase.base.StringBase;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.ReadConcernLevel;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoClientFactoryBean implements FactoryBean<MongoClient> {

	final MongoConfig config;

	public MongoClientFactoryBean(MongoConfig config) {
		this.config = config;
	}

	@SuppressWarnings("deprecation")
	@Override
	public MongoClient getObject() throws Exception {
		MongoClientSettings.Builder bd = MongoClientSettings.builder();
		bd.applyConnectionString(makeConnectionString(config));
		bd.applyToConnectionPoolSettings(p -> {
			p.minSize(config.minPoolSize);
			p.maxSize(config.maxPoolSize);
			p.maxConnectionIdleTime(config.maxPoolIdleTimeMS, TimeUnit.MILLISECONDS);
			p.maxWaitTime(config.maxPoolWaitTimeMS, TimeUnit.MICROSECONDS);
		});
		bd.applyToSocketSettings(s -> {
			s.readTimeout(config.readTimeout, TimeUnit.SECONDS);
			s.connectTimeout(config.connectTimeout, TimeUnit.SECONDS);
		});
		if (ObjectBase.isEmpty(config.safe)) {
			int w = 0;
			String wmode = null;
			String rmode = null;
			int wtimeout = 0;
			boolean fsync = false;
			boolean j = false;
			for (Map.Entry<String, Object> entry : config.safe.entrySet()) {
				String key = entry.getKey();
				if ("w".equalsIgnoreCase(entry.getKey())) {
					w = ConfBase.toInteger(entry.getValue(), 0);
				} else if ("wmode".equalsIgnoreCase(key)) {
					wmode = ConfBase.toString(entry.getValue(), null);
				} else if ("wtimeout".equalsIgnoreCase(key)) {
					wtimeout = ConfBase.toInteger(entry.getValue(), 0);
				} else if ("fsync".equalsIgnoreCase(key)) {
					fsync = ConfBase.toBoolean(entry.getValue(), false);
				} else if ("j".equalsIgnoreCase(key)) {
					j = ConfBase.toBoolean(entry.getValue(), false);
				}
			}
			if (StringBase.isNotEmpty(wmode)) {
				bd.writeConcern(new WriteConcern(wmode, wtimeout, fsync, j));
			} else if (w > 0) {
				bd.writeConcern(new WriteConcern(w, wtimeout, fsync, j));
			}

			if (StringBase.isNotEmpty(rmode)) {
				bd.readConcern(new ReadConcern(ReadConcernLevel.valueOf(rmode)));
			}

		}

		if (StringBase.isNotEmpty(config.mode)) {
			if ("primary".equalsIgnoreCase(config.mode)) {
				bd.readPreference(ReadPreference.primary());
			} else if ("primaryPreferred".equalsIgnoreCase(config.mode)) {
				bd.readPreference(ReadPreference.primaryPreferred());
			} else if ("secondary".equalsIgnoreCase(config.mode)) {
				bd.readPreference(ReadPreference.secondary());
			} else if ("secondaryPreferred".equalsIgnoreCase(config.mode)) {
				bd.readPreference(ReadPreference.secondaryPreferred());
			} else if ("nearest".equalsIgnoreCase(config.mode)) {
				bd.readPreference(ReadPreference.nearest());
			} else if ("eventual".equalsIgnoreCase(config.mode)) {
				throw new SystemException("unsport eventual");
			} else if ("monotonic".equalsIgnoreCase(config.mode)) {
				throw new SystemException("unsport monotonic");
			} else if ("strong".equalsIgnoreCase(config.mode)) {
				bd.readPreference(ReadPreference.primary());
			}
		}

		return MongoClients.create(bd.build());
	}

	@Override
	public Class<?> getObjectType() {
		return MongoClient.class;
	}

	@SuppressWarnings("deprecation")
	private ConnectionString makeConnectionString(MongoConfig config) {
		StringBuilder sb = new StringBuilder(512);
		sb.append("mongodb://");
		if (StringBase.isNotEmpty(config.username)) {
			sb.append(config.username);
			if (StringBase.isNotEmpty(config.password)) {
				sb.append(":").append(config.password);
			}
			sb.append("@");
		}
		sb.append(config.address);
		sb.append("/");

		String database = null;
		if (StringBase.isNotEmpty(config.authDatabase)) {
			sb.append(database = config.authDatabase);
		} else if (StringBase.isNotEmpty(config.database)) {
			sb.append(database = config.database);
		} else {
			sb.append(database = "admin");
		}

		sb.append("?authSource=");
		if (StringBase.isNotEmpty(config.authSource)) {
			sb.append(config.authSource);
		} else if (StringBase.isNotEmpty(config.source)) {
			sb.append(config.source);
		} else {
			sb.append(database);
		}
		return new ConnectionString(sb.toString());
	}
}
