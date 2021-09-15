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

//  RELEASE(TestConfig.builder()
//      .name("devguy")
//      .description("just for troubleshooting")
//      .withAgents(Agent.SPLUNK_OTEL)
//      .totalIterations(15)
//      .build()),
  RELEASE(TestConfig.builder()
      .name("release_1ss_5vu_500iter")
      .description("multiple agent configurations compared")
      .withAgents(Agent.NONE, Agent.SPLUNK_OTEL, Agent.SPLUNK_PROFILER, Agent.SPLUNK_PROFILER_W_TLAB_1SS, Agent.SPLUNK_PROFILER_W_TLAB_10SS, Agent.SPLUNK_PROFILER_W_TLAB_100SS)
      .totalIterations(500)
      .warmupSeconds(60)
      .maxRequestRate(50)
//      .concurrentConnections(2)
      .build()),

//  RELEASEx1(TestConfig.builder()
//      .name("release_10ss_5vu_500iter")
//      .description("multiple agent configurations compared")
//      .withAgents(Agent.NONE, Agent.SPLUNK_OTEL, Agent.SPLUNK_PROFILER, Agent.SPLUNK_PROFILER_W_TLAB_10SS)
//      .totalIterations(500)
//      .warmupSeconds(60)
//      .build()),
//
//  RELEASEx2(TestConfig.builder()
//      .name("release_100ss_5vu_500iter")
//      .description("multiple agent configurations compared")
//      .withAgents(Agent.NONE, Agent.SPLUNK_OTEL, Agent.SPLUNK_PROFILER, Agent.SPLUNK_PROFILER_W_TLAB_100SS)
//      .totalIterations(500)
//      .warmupSeconds(60)
//      .build()),
  ;

  public final TestConfig config;

  public static Stream<TestConfig> all(){
    return Arrays.stream(Configs.values()).map(x -> x.config);
  }

  Configs(TestConfig config) {
    this.config = config;
  }
}
