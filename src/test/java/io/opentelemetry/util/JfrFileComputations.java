/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.util;

import io.opentelemetry.results.AppPerfResults.MinMax;

import java.io.IOException;
import java.nio.file.Path;

public class JfrFileComputations {

    private final JfrFileReduceOps reduceOps;

    public JfrFileComputations(Path jfrFile) {
        this(new JfrFileReduceOps(jfrFile));
    }

    public JfrFileComputations(JfrFileReduceOps reduceOps) {
        this.reduceOps = reduceOps;
    }

    public float computeAverageJvmUserCpu(Path jfrFile) throws IOException {
        return reduceOps.computeAverageFloat( "jdk.CPULoad", "jvmUser");
    }

    public float computeMaxJvmUserCpu(Path jfrFile) throws IOException {
        return reduceOps.findMaxFloat( "jdk.CPULoad", "jvmUser");
    }

    public float computeAverageJvmSystemCpu(Path jfrFile) throws IOException {
        return reduceOps.computeAverageFloat( "jdk.CPULoad", "jvmSystem");
    }

    public float computeMaxJvmSystemCpu(Path jfrFile) throws IOException {
        return reduceOps.findMaxFloat("jdk.CPULoad", "jvmSystem");
    }

    public float computeAverageMachineCpuTotal(Path jfrFile) throws IOException {
        return reduceOps.computeAverageFloat("jdk.CPULoad", "machineTotal");
    }

    public long computeAverageNetworkRead(Path jfrFile) throws IOException {
        return reduceOps.findAverageLong("jdk.NetworkUtilization", "readRate");

    }

    public long computeAverageNetworkWrite(Path jfrFile) throws IOException {
        return reduceOps.findAverageLong("jdk.NetworkUtilization", "writeRate");
    }

    public long computeTotalGcPauseNanos(Path jfrFile) throws IOException {
        return reduceOps.sumLongEventValues("jdk.GCPhasePause", "duration");
    }

    public long readPeakThreadCount(Path jfrFile) throws IOException {
        MinMax minMax = reduceOps.findMinMax("jdk.JavaThreadStatistics", "peakCount");
        return minMax.max;
    }

    public long readTotalGCTime(Path jfrFile) throws IOException {
        return reduceOps.sumLongEventValues("jdk.G1GarbageCollection", "duration");
    }

    public long readTotalAllocated(Path jfrFile) throws IOException {
        return reduceOps.sumLongEventValues("jdk.ThreadAllocationStatistics", "allocated");
    }

    public MinMax readHeapUsed(Path jfrFile) throws IOException {
        return reduceOps.findMinMax("jdk.GCHeapSummary", "heapUsed");
    }

    public float readMaxThreadContextSwitchRate(Path jfrFile) throws IOException {
        return reduceOps.findMaxFloat("jdk.ThreadContextSwitchRate", "switchRate");
    }

}
