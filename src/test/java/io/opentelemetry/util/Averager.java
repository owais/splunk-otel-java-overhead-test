package io.opentelemetry.util;

import jdk.jfr.consumer.RecordedEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Predicate;

// Finds the average value for a given property of a certain type of event across a jfr file.
class Averager {

    private final Path jfrFile;
    private String eventName;
    private String valueKey;
    private Predicate<RecordedEvent> predicate = x -> true;

    private Averager(Path jfrFile) {
        this.jfrFile = jfrFile;
    }

    static Averager forFile(Path jfrFile) {
        return new Averager(jfrFile);
    }

    Averager forEventsNamed(String eventName) {
        this.eventName = eventName;
        return this;
    }

    Averager usingValueFrom(String valueKey) {
        this.valueKey = valueKey;
        return this;
    }

    Averager predicatedBy(Predicate<RecordedEvent> predicate) {
        this.predicate = predicate;
        return this;
    }

    float computeFloat() throws IOException {
        return Reducer.<AverageFloatSupport, Float>forFile(jfrFile)
                .forEventsNamed(eventName)
                .usingValueFrom(valueKey)
                .predicatedBy(predicate)
                .withInitialValue(AverageFloatSupport.EMPTY)
                .reducedBy(AverageFloatSupport::add)
                .reduce()
                .average();
    }

    long computeLong() throws IOException {
        return Reducer.<AverageLongSupport, Long>forFile(jfrFile)
                .forEventsNamed(eventName)
                .usingValueFrom(valueKey)
                .predicatedBy(predicate)
                .withInitialValue(AverageLongSupport.EMPTY)
                .reducedBy(AverageLongSupport::add)
                .reduce()
                .average();
    }

    private static class AverageLongSupport {
        final static AverageLongSupport EMPTY = new AverageLongSupport(0,0);
        final long count;
        final long total;

        AverageLongSupport(long count, long total) {
            this.count = count;
            this.total = total;
        }

        AverageLongSupport add(long value){
            return new AverageLongSupport(count+1, total + value);
        }

        long average(){
            if(count == 0) return -1;
            return total/count;
        }
    }

    private static class AverageFloatSupport {
        final static AverageFloatSupport EMPTY = new AverageFloatSupport(0,0);

        final long count;
        final float total;

        AverageFloatSupport(long count, float total) {
            this.count = count;
            this.total = total;
        }

        AverageFloatSupport add(float value){
            return new AverageFloatSupport(count+1, total + value);
        }

        float average(){
            if(count == 0) return -1;
            return total/count;
        }
    }
}
