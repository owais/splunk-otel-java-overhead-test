/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.config;

import io.opentelemetry.agents.Agent;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Defines all test configurations
 */
public enum Configs {

  RELEASE(TestConfig.builder()
      .name("release_1ss_5vu_500iter")
      .description("multiple agent configurations compared")
//      .withAgents(Agent.NONE, Agent.SPLUNK_OTEL, Agent.SPLUNK_PROFILER, Agent.SPLUNK_PROFILER_W_TLAB_1SS, Agent.SPLUNK_PROFILER_W_TLAB_10SS, Agent.SPLUNK_PROFILER_W_TLAB_100SS)
      .withAgents(Agent.SPLUNK_PROFILER)
      .totalIterations(500)
      .warmupSeconds(60)
      .maxRequestRate(50)
//      .concurrentConnections(2)
      .build()),
  ;

  public final TestConfig config;

  public static Stream<TestConfig> all(){
    return Arrays.stream(Configs.values()).map(x -> x.config);
  }

  Configs(TestConfig config) {
    this.config = config;
  }
}
