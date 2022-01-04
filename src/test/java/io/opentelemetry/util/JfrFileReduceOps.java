package io.opentelemetry.util;

import io.opentelemetry.results.AppPerfResults;

import java.io.IOException;
import java.nio.file.Path;

public class JfrFileReduceOps {

    private final Path jfrFile;

    public JfrFileReduceOps(Path jfrFile) {
        this.jfrFile = jfrFile;
    }

    public long sumLongEventValues(String eventName, String valueKey) throws IOException {
        return Reducer.<Long, Long>forFile(jfrFile)
                .forEventsNamed(eventName)
                .usingValueFrom(valueKey)
                .withInitialValue(0L)
                .reducedBy(Long::sum)
                .reduce();
    }

    public float findMaxFloat(String eventName, String valueKey) throws IOException {
        return Reducer.<Float, Float>forFile(jfrFile)
                .forEventsNamed(eventName)
                .usingValueFrom(valueKey)
                .withInitialValue(0.0f)
                .reducedBy(Math::max)
                .reduce();
    }

    public AppPerfResults.MinMax findMinMax(String eventName, String valueKey) throws IOException {
        return Reducer.<AppPerfResults.MinMax, Long>forFile(jfrFile)
                .forEventsNamed(eventName)
                .usingValueFrom(valueKey)
                .withInitialValue(new AppPerfResults.MinMax())
                .reducedBy((AppPerfResults.MinMax acc, Long value) -> {
                    if (value > acc.max) {
                        acc = acc.withMax(value);
                    }
                    if (value < acc.min) {
                        acc = acc.withMin(value);
                    }
                    return acc;
                })
                .reduce();
    }

    public long findAverageLong(String eventName, String valueKey) throws IOException {
        return Averager.forFile(jfrFile)
                .forEventsNamed(eventName)
                .usingValueFrom(valueKey)
                .computeLong();
    }

    public float computeAverageFloat(String eventName, String valueKey) throws IOException {
        return Averager.forFile(jfrFile)
                .forEventsNamed(eventName)
                .usingValueFrom(valueKey)
                .computeFloat();
    }


}
