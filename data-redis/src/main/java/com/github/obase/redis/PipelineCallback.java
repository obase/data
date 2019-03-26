package com.github.obase.redis;

import redis.clients.jedis.Pipeline;

@FunctionalInterface
public interface PipelineCallback {

	void pipeline(Pipeline pipeline);
}
