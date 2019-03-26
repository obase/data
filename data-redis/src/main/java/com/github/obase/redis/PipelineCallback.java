package com.github.obase.redis;

import java.util.List;

import redis.clients.jedis.Pipeline;

@FunctionalInterface
public interface PipelineCallback {

	List<Object> pipeline(Pipeline pipeline);
}
