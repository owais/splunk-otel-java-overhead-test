package io.opentelemetry.util;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Predicate;

// Given a jfr file, reduces all values of a field from a certain type of jfr event
// to a single value. Is used in aggregating reductions like finding min/max, averaging,
// summing, counting, etc.
class Reducer<T, V> {
    private final Path jfrFile;
    private String eventName;
    private String valueKey;
    private Predicate<RecordedEvent> predicate = x -> true;
    private T initialValue;
    private BiFunction<T, V, T> reducer;

    public static Reducer<AverageSupport.Long, Long> newLongAverager(Path jfrFile){
        return Reducer.<AverageSupport.Long, Long>forFile(jfrFile)
                .withInitialValue(AverageSupport.Long.EMPTY)
                .reducedBy(AverageSupport.Long::add);
    }

    public static Reducer<AverageSupport.Float, Float> newFloatAverager(Path jfrFile){
        return Reducer.<AverageSupport.Float, Float>forFile(jfrFile)
                .withInitialValue(AverageSupport.Float.EMPTY)
                .reducedBy(AverageSupport.Float::add);
    }

    private Reducer(Path jfrFile) {
        this.jfrFile = jfrFile;
    }

    static <T, V> Reducer<T, V> forFile(Path jfrFile) {
        return new Reducer<>(jfrFile);
    }

    Reducer<T, V> forEventsNamed(String eventName) {
        this.eventName = eventName;
        return this;
    }

    Reducer<T, V> usingValueFrom(String valueKey) {
        this.valueKey = valueKey;
        return this;
    }

    Reducer<T, V> predicatedBy(Predicate<RecordedEvent> predicate) {
        this.predicate = predicate;
        return this;
    }

    Reducer<T, V> withInitialValue(T initialValue) {
        this.initialValue = initialValue;
        return this;
    }

    Reducer<T, V> reducedBy(BiFunction<T, V, T> reducer) {
        this.reducer = reducer;
        return this;
    }

    T reduce() throws IOException {
        RecordingFile recordingFile = new RecordingFile(jfrFile);
        T result = initialValue;
        while (recordingFile.hasMoreEvents()) {
            RecordedEvent recordedEvent = recordingFile.readEvent();
            if (eventName.equals(recordedEvent.getEventType().getName())) {
                if(predicate.test(recordedEvent)){
                    V value = recordedEvent.getValue(valueKey);
                    result = reducer.apply(result, value);
                }
            }
        }
        return result;
    }
}
