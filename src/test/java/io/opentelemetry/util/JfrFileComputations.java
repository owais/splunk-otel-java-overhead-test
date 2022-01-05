/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.util;

import io.opentelemetry.results.AppPerfResults.MinMax;
import jdk.jfr.consumer.RecordedEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

public class JfrFileComputations {

    private final static Predicate<RecordedEvent> EXCLUDE_LOCALHOST = event -> {
        String networkInterface = event.getValue("networkInterface");
        return !networkInterface.startsWith("lo");
    };

    private final JfrFileReduceOps reduceOps;

    public JfrFileComputations(Path jfrFile) {
        this(new JfrFileReduceOps(jfrFile));
    }

    public JfrFileComputations(JfrFileReduceOps reduceOps) {
        this.reduceOps = reduceOps;
    }

    public float computeAverageJvmUserCpu() throws IOException {
        return reduceOps.computeAverageFloat("jdk.CPULoad", "jvmUser");
    }

    public float computeMaxJvmUserCpu() throws IOException {
        return reduceOps.findMaxFloat("jdk.CPULoad", "jvmUser");
    }

    public float computeAverageJvmSystemCpu() throws IOException {
        return reduceOps.computeAverageFloat("jdk.CPULoad", "jvmSystem");
    }

    public float computeMaxJvmSystemCpu() throws IOException {
        return reduceOps.findMaxFloat("jdk.CPULoad", "jvmSystem");
    }

    public float computeAverageMachineCpuTotal() throws IOException {
        return reduceOps.computeAverageFloat("jdk.CPULoad", "machineTotal");
    }

    public long computeAverageNetworkRead() throws IOException {
        return reduceOps.findAverageLong("jdk.NetworkUtilization", "readRate", EXCLUDE_LOCALHOST);
    }

    public long computeAverageNetworkWrite() throws IOException {
        return reduceOps.findAverageLong("jdk.NetworkUtilization", "writeRate", EXCLUDE_LOCALHOST);
    }

    public long computeTotalGcPauseNanos() throws IOException {
        return reduceOps.sumLongEventValues("jdk.GCPhasePause", "duration");
    }

    public long readPeakThreadCount() throws IOException {
        MinMax minMax = reduceOps.findMinMax("jdk.JavaThreadStatistics", "peakCount");
        return minMax.max;
    }

    public long readTotalGCTime() throws IOException {
        return reduceOps.sumLongEventValues("jdk.G1GarbageCollection", "duration");
    }

    public long readTotalAllocated() throws IOException {
        return reduceOps.sumLongEventValues("jdk.ThreadAllocationStatistics", "allocated");
    }

    public MinMax readHeapUsed() throws IOException {
        return reduceOps.findMinMax("jdk.GCHeapSummary", "heapUsed");
    }

    public float readMaxThreadContextSwitchRate() throws IOException {
        return reduceOps.findMaxFloat("jdk.ThreadContextSwitchRate", "switchRate");
    }

}
