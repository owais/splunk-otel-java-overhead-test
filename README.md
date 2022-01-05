# Performance overhead tests for the Splunk OTel Java Agent

This repository contains documentation, tools, and results concerning
the impact on performance of the [Splunk distribution](https://github.com/signalfx/splunk-otel-java) of the 
[OpenTelemetry Java Instrumentation Agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation).

# Background

Java instrumentation agents are bits of software that run inside the same JVM as 
user applications. This software, just like any other software, requires resources
like CPU, memory, and network in order to function. This use of resources is 
often referred to as "overhead" or "agent overhead", and it often means different things 
to different people.

In an ideal scenario, the overhead of an instrumentation agent would be zero 
(no overhead); however, this is unrealistic. In reality, we find that instrumentation 
agents are considerably low overhead and that the rich insights gained by observing 
the application behavior is much more valuable than the resources consumed by 
the instrumentation.

Due to the extreme complexity of modern software systems and the broad diversity in 
the deployment landscape, it is effectively impossible to come up with a single "overhead" number.
The actual overhead depends on a massive number of variables. When attempting to measure or
even discuss overhead, one must take many variables into consideration.

Some variables that can impact overhead are environmental, such as physical machine architecture, 
CPU frequency, amount and speed of memory, temperature, and generalized resource contention.
Environmental factors also include virtualization and containerization, and the multitude
of configurations thereof. Other aspects of overhead include the choice of operating system and 
its libraries, the JVM version (and vendor), and the algorithmic design of the software being 
monitored, and the dependency graph of all software libraries. Furthermore, the JVM runtime
configuration like memory settings and garbage collector type can have a major impact on agent
overhead.

Your results will be different from the results contained here, but they are expected to
be somewhat similar. The only guaranteed way to know the actual overhead of an instrumentation
agent in a given deployment is to conduct experiments and perform measurements.

# Testing approach

Given the above, we have designed and built a test suite that quantifies the agent
overhead for a typical style of microservice under fixed conditions.

The test environment consists of 4 primary components, all running within docker on EC2:
* The application under test ([spring-petclinic-rest](https://github.com/spring-petclinic/spring-petclinic-rest) microservice)
* An application database (postgres)
* An [OpenTelemetry collector](https://github.com/open-telemetry/opentelemetry-collector-contrib)
* The [k6](https://k6.io/) test runner

The microservice application (spring-petclinic-rest) is representative of a real-world microservice.

In order to help minimize the impact that "[noisy neighbors](https://en.wikipedia.org/wiki/Cloud_computing_issues#Performance_interference_and_noisy_neighbors)"
(external components) may have on the agent, the database and 
collector were placed on their own separate EC2 instance called "externals".

## What we measure

Different users may care about different aspects of overhead. Most users are primarily 
concerned with service latency, but others with computationally intense workloads 
may care more about CPU overhead. Many users are concerned with memory consumption 
and its impact on garbage collection characteristics (which can impact CPU and increase 
latency). Some users deploy frequently (often due to elastic/spiky workloads) and they 
care about startup time. 

We distill these down into these categories of measurement:

* Startup time
* CPU
  * average (user)
  * peak (user)
  * average (machine)
  * GC pause time
* Memory
  * max heap used
  * total allocated
* Service latency
  * single REST call (avg, p95)
  * test script (avg, p95)
  * throughput (requests per second)
* Network
  * read throughput (avg)
  * write throughput (avg)

These metrics are captured and aggregated across all test executions,
and the results are compared between run configurations.

## How we measure

We define 3 run configurations:
* No instrumentation
* Splunk OpenTelemetry Java Agent
* Splunk OpenTelemetry Java Agent with AlwaysOn Profiling enabled

Each test _run_ executes every configuration of the microservice in a fresh JVM.

This sequence is executed 10 times:
* for each configuration: 
  * start postgres and wait for healthy
  * start petclinic and wait for healthy
  * record application start time
  * conduct warmup phase:
    * start warmup JFR recording
    * until 60 seconds has passed:
      * run k6 with 5 users, 25 iterations (generate traffic)
    * stop JFR
  * record test start time
  * start real JFR recording
  * run k6 script
    * 8500 passes, 30 concurrent users, 900RPS max  
  * collect results and write to csv 
  
The k6 test script contains 12 REST operations, so after all 10 test
runs are complete, each agent has seen more than 1 million REST calls.

Measurements are derived from k6 and from JFR data and aggregated across all 10 runs.
The results are saved to a CSV file and the final run is summarized in a txt file.

## Configuration

`externals` instance: 
* m4.large
* 2 vCPU
* 8 GiB memory

`testbox` instance: 
* m4.xlarge
* 4 vCPU
* 16 GiB memory

Both machines are running 64-bit Debian 9 with kernel 4.9 and the latest version of
docker-ce.

The petclinic application runs with OpenJDK version 11.0.11 and no additional JVM arguments 
except “-javaagent” (no heap limits were specified). The G1 Garbage Collector is the default 
for java 11 and is used across all tests. 

## Additional Details

* Most software behaves unexpectedly when resource starved. In order to produce
  meaningful and consistent results, we have taken care to ensure that the microservice
  is not starved for resources and has satisfactory CPU and memory headroom to operate
  normally.

# Conclusions

TBD

# Performing your own tests

YMMV. This will eventually contain guidance.

# License

The Splunk OpenTelemetry Java Overhead Benchmark tests are released under the terms of the Apache Software License
version 2.0. For more details, see [the license file](./LICENSE).

> Copyright 2021 Splunk Inc.
>
> Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
>
> http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
