# Test Plan

This test plan briefly describes what we will be testing and how we will 
gather and present the results.

# Background

We intend to begin quantifying the amount of "overhead" by using the agent in various configurations.
Overhead can vary wildly in different configurations and deployment environments, so this experiment 
fixes the target application to [spring-petclinic-rest](https://github.com/spring-petclinic/spring-petclinic-rest) 
running in a continuous integration (CI/GitHub Actions) environment.

# Agents

Each test pass will use 4 agent configurations:

* No agent
* Latest [splunk-otel-java](https://github.com/signalfx/splunk-otel-java), no profiler
* Latest [splunk-otel-java](https://github.com/signalfx/splunk-otel-java), with profiler (TLABs disabled)
* Latest [splunk-otel-java](https://github.com/signalfx/splunk-otel-java), with profiler (TLABs enabled)

# Variables

We explore the above 4 configurations across 3 variables:

* sampling rate - the rate at which the JFR `jdk.ThreadDump` is configured to take snapshots of every thread 
* concurrency - the number of concurrent requests into the service
* test length - number of passes through the test script

Throughput (requests per unit time) is generally a factor of app design, hardware environment, and concurrency.
As we do not have control over hardware in a CI environment, we will not constrain the throughput nor will
we attempt to push it to its maximum. If we discover (during testing) that we have reached a throughput
limit, we should try and reduce operating parameters to avoid this.

Again, the purpose is to quantify overhead in the normal operational range, not at maximum capacity.

# Process

We are primarily concerned with comparison of different agents (eg. how agent configuration A compares to config B),
not consistency between runs. 

We will run each agent in turn on the same EC2 instance. The postgres database and collector
will be run on a separate instance (via docker). The postgres database is restarted between each run to avoid optimizations
and biased caching. A 60s JVM warm up phase will be used to exclude most jit compilations.

For the purposes of this experiment, we _assume_ that [noisy neighbor](https://searchcloudcomputing.techtarget.com/definition/noisy-neighbor-cloud-computing-performance)
interference is minimal and do not take it into consideration.

Given:
* s = sampling rate (in samples per second)
* c = concurrency (number of virtual users)
* p = passes (through the test script)

We will perform 10 independent test passes for each set of {s,c} variables:

* s = {1, 10, 100} 
* c = {5, 10, 50}
* p = {500, 5000}

In each test, we will measure the metrics listed [in the table here](https://github.com/breedx-splk/opentelemetry-java-instrumentation/tree/main/benchmark-overhead#what-do-we-measure).

# Results

In this permutation space, we will then have 3 * 3 * 2 = 18 results. Each result will
be a result of 10 test passes.

{s, c, p} => 
* {1, 5, 500}
* {1, 5, 5000}
* {1, 10, 50}
* {1, 10, 5000}
* {1, 100, 50}
* {1, 100, 5000}
* {10, 5, 500}
* {10, 5, 5000}
* {10, 10, 50}
* {10, 10, 5000}
* {10, 100, 50}
* {10, 100, 5000}
* {100, 5, 500}
* {100, 5, 5000}
* {100, 10, 50}
* {100, 10, 5000}
* {100, 100, 50}
* {100, 100, 5000}

The test framework will persist 10 test passes in a single csv file.
The test passes should be averaged and the results presented as 
absolute units (percentages should be avoided).

This csv file will be used to create a bar chart, 1 bar for 
each agent configuration, 1 chart per permutation line above.

The data should be managed with a shared google sheet.
We will look at the results to rule out anomalies and 
summarize the results with another document (shared google doc).