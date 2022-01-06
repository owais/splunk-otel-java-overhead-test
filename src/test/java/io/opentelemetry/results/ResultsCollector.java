/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.results;

import com.jayway.jsonpath.JsonPath;
import io.opentelemetry.agents.Agent;
import io.opentelemetry.config.TestConfig;
import io.opentelemetry.util.JfrFileComputations;
import io.opentelemetry.util.NamingConvention;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResultsCollector {

  private final NamingConvention namingConvention;
  private final Map<String, Long> runDurations;

  public ResultsCollector(NamingConvention namingConvention, Map<String, Long> runDurations) {
    this.namingConvention = namingConvention;
    this.runDurations = runDurations;
  }

  public List<AppPerfResults> collect(TestConfig config) {
    return config.getAgents().stream()
        .map(a -> readAgentResults(a, config))
        .collect(Collectors.toList());
  }

  private AppPerfResults readAgentResults(Agent agent, TestConfig config) {
    try {
      AppPerfResults.Builder builder = AppPerfResults.builder()
          .agent(agent)
          .runDurationMs(runDurations.get(agent.getName()))
          .config(config);

      builder = addStartupTime(builder, agent);
      builder = addK6Results(builder, agent);
      builder = addJfrResults(builder, agent);

      return builder.build();
    } catch (IOException e) {
      throw new RuntimeException("Error reading results", e);
    }
  }

  private AppPerfResults.Builder addStartupTime(
      AppPerfResults.Builder builder, Agent agent) throws IOException {
    Path file = namingConvention.startupDurationFile(agent);
    long startupDuration = Long.parseLong(new String(Files.readAllBytes(file)).trim());
    return builder.startupDurationMs(startupDuration);
  }

  private AppPerfResults.Builder addK6Results(
      AppPerfResults.Builder builder, Agent agent)
      throws IOException {
    Path k6File = namingConvention.k6Results(agent);
    String json = new String(Files.readAllBytes(k6File));
    double iterationAvg = JsonPath.read(json, "$.metrics.iteration_duration.avg");
    double iterationP95 = JsonPath.read(json, "$.metrics.iteration_duration['p(95)']");
    double requestAvg = JsonPath.read(json, "$.metrics.http_req_duration.avg");
    double requestP95 = JsonPath.read(json, "$.metrics.http_req_duration['p(95)']");
    double throughputRequestsPerSecond = JsonPath.read(json, "$.metrics.http_reqs['rate']");
    return builder
        .iterationAvg(iterationAvg)
        .iterationP95(iterationP95)
        .requestAvg(requestAvg)
        .requestP95(requestP95)
        .throughputRequestsPerSecond(throughputRequestsPerSecond);
  }

  private AppPerfResults.Builder addJfrResults(
      AppPerfResults.Builder builder, Agent agent) throws IOException {
    Path jfrFile = namingConvention.jfrFile(agent);
    JfrFileComputations compute = new JfrFileComputations(jfrFile);
    return builder
        .totalGCTime(compute.readTotalGCTime())
        .totalAllocated(compute.readTotalAllocated())
        .heapUsed(compute.readHeapUsed())
        .maxThreadContextSwitchRate(compute.readMaxThreadContextSwitchRate())
        .peakThreadCount(compute.readPeakThreadCount())
        .averageNetworkRead(compute.computeAverageNetworkRead())
        .averageNetworkWrite(compute.computeAverageNetworkWrite())
        .averageJvmUserCpu(compute.computeAverageJvmUserCpu())
        .maxJvmUserCpu(compute.computeMaxJvmUserCpu())
        .averageJvmSystemCpu(compute.computeAverageJvmSystemCpu())
        .maxJvmSystemCpu(compute.computeMaxJvmSystemCpu())
        .averageMachineCpuTotal(compute.computeAverageMachineCpuTotal())
        .totalGcPauseNanos(compute.computeTotalGcPauseNanos());
  }

}
