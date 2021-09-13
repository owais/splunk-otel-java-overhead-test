/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.containers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RemotePostgresContainer {

  private static final Logger logger = LoggerFactory.getLogger(RemotePostgresContainer.class);
  private final String remoteHost;

  public RemotePostgresContainer(String remoteHost) {
    this.remoteHost = remoteHost;
  }

  public static RemotePostgresContainer build(String remoteHost) {
    return new RemotePostgresContainer(remoteHost);
  }

  public void start() throws Exception {
    logger.info("Running remote postgres via docker...");
    Process process = Runtime.getRuntime().exec("docker run -d --rm --name postgres -p 5432:5432 postgres", buildEnvp());
    int rc = process.waitFor();
    String errors = new String(process.getErrorStream().readAllBytes());
    String out = new String(process.getInputStream().readAllBytes());
    logger.info("  Exit code = " + rc);
  }

  public void stop() throws Exception {
    logger.info("Stopping remote postgres via docker...");
    Process process = Runtime.getRuntime().exec("docker stop postgres", buildEnvp());
    int rc = process.waitFor();
    logger.info("  Exit code = " + rc);
  }

  private String[] buildEnvp() {
    Map<String, String> env = new HashMap<>(System.getenv());
    env.put("DOCKER_HOST", remoteHost + ":2375");
    List<String> result = env.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.toList());
    return result.toArray(String[]::new);
  }

}
