package io.opentelemetry.util;

// Holds helper classes that contain a count and a total (sum) and can be used to
// compute the average. This only exists because numeric generic type operations
// are impractical.
class AverageSupport {

    static class Long {
        final static Long EMPTY = new Long(0,0);
        final long count;
        final long total;

        Long(long count, long total) {
            this.count = count;
            this.total = total;
        }

        Long add(long value){
            return new Long(count+1, total + value);
        }

        long average(){
            if(count == 0) return -1;
            return total/count;
        }
    }

    static class Float {
        final static Float EMPTY = new Float(0,0);

        final long count;
        final float total;

        Float(long count, float total) {
            this.count = count;
            this.total = total;
        }

        Float add(float value){
            return new Float(count+1, total + value);
        }

        float average(){
            if(count == 0) return -1;
            return total/count;
        }
    }
}
