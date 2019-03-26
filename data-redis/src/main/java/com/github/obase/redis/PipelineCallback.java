package com.github.obase.redis;

import redis.clients.jedis.Pipeline;

public interface PipelineCallback {

	void doInPipeline(Pipeline pipeline, Object... args);
}
