package io.opentelemetry.agents;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Agent {

  final static String OTEL_LATEST = "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent-all.jar";

  public final static Agent NONE = new Agent("none", "no agent at all");
  public final static Agent LATEST_UPSTREAM_RELEASE = new Agent("latest", "latest mainstream release", OTEL_LATEST);
  public final static Agent LATEST_UPSTREAM_SNAPSHOT = new Agent("snapshot", "latest available snapshot version from main");

  private final static String SPLUNK_AGENT_URL = "https://repo1.maven.org/maven2/com/splunk/splunk-otel-javaagent/1.3.1/splunk-otel-javaagent-1.3.1-all.jar";

  public final static Agent SPLUNK_OTEL = new Agent("splunk-otel", "splunk-otel-java 1.3.1", SPLUNK_AGENT_URL);
  public final static Agent SPLUNK_PROFILER = new Agent("profiler", "splunk-otel-java 1.3.1",
          SPLUNK_AGENT_URL,
          List.of("-Dsplunk.profiler.enabled=true"));
  public final static Agent SPLUNK_PROFILER_W_TLAB = new Agent("profiler-tlab", "splunk-otel-java 1.3.1",
          SPLUNK_AGENT_URL,
          List.of("-Dsplunk.profiler.enabled=true", "-Dsplunk.profiler.tlab.enabled=true"));

  private final String name;
  private final String description;
  private final URL url;
  private final List<String> additionalJvmArgs;

  public Agent(String name, String description) {
    this(name, description, null);
  }

  public Agent(String name, String description, String url) {
      this(name, description, url, Collections.emptyList());
  }

  public Agent(String name, String description, String url, List<String> additionalJvmArgs) {
    this.name = name;
    this.description = description;
    this.url = makeUrl(url);
    this.additionalJvmArgs = new ArrayList<>(additionalJvmArgs);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public boolean hasUrl(){
    return url != null;
  }

  public URL getUrl() {
    return url;
  }

  public List<String> getAdditionalJvmArgs() {
    return Collections.unmodifiableList(additionalJvmArgs);
  }

  private static URL makeUrl(String url) {
    try {
      if(url == null) return null;
      return URI.create(url).toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error parsing url", e);
    }
  }
}
