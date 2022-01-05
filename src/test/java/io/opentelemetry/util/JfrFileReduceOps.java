package io.opentelemetry.util;

import io.opentelemetry.results.AppPerfResults;
import jdk.jfr.consumer.RecordedEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

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

    public long computeAverageLong(String eventName, String valueKey) throws IOException {
        return computeAverageLong(eventName, valueKey, x -> true);
    }

    public long computeAverageLong(String eventName, String valueKey, Predicate<RecordedEvent> predicate) throws IOException {
        return Reducer.newLongAverager(jfrFile)
                .forEventsNamed(eventName)
                .usingValueFrom(valueKey)
                .predicatedBy(predicate)
                .reduce()
                .average();
    }

    public float computeAverageFloat(String eventName, String valueKey) throws IOException {
        return Reducer.newFloatAverager(jfrFile)
                .forEventsNamed(eventName)
                .usingValueFrom(valueKey)
                .reduce()
                .average();
    }


}
