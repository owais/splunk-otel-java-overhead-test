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
      .name("release")
      .description("compares the latest stable release to no agent")
      .withAgents(Agent.NONE, Agent.SPLUNK_OTEL, Agent.SPLUNK_PROFILER, Agent.SPLUNK_PROFILER_W_TLAB)
      .warmupSeconds(60)
      .build()
  ),
  ;

  public final TestConfig config;

  public static Stream<TestConfig> all(){
    return Arrays.stream(Configs.values()).map(x -> x.config);
  }

  Configs(TestConfig config) {
    this.config = config;
  }
}
