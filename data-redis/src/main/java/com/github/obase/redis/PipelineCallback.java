package com.github.obase.redis;

import redis.clients.jedis.Pipeline;

@FunctionalInterface
public interface PipelineCallback {

	boolean pipeline(Pipeline pipeline);
}
