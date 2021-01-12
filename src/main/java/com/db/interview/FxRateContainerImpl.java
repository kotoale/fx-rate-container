package com.db.interview;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 * This implementation supports any order of adding Fx Rates
 * <p>
 * Time Complexity (worse case):
 * add - O(logN)
 * get - O(logN)
 * average - O(logN)
 */
public class FxRateContainerImpl implements FxRateContainer {

    private final Map<String, NavigableMap<Long, RateValueHolder>> fxRates = new HashMap<>();

    @Override
    public void add(String ccyPair, double fxRate, long timestamp) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        final var rateByTime = fxRates.computeIfAbsent(ccyPair, (key) -> new TreeMap<>());

        double prevSum = rateByTime.isEmpty() ? 0.0 : rateByTime.lastEntry().getValue().getCurrentSum();
        rateByTime.put(timestamp, new RateValueHolder(fxRate, rateByTime.size() + 1, prevSum + fxRate));
    }

    @Override
    public double get(String ccyPair, long timestamp) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        final var rateByTime = fxRates.get(ccyPair);
        if (rateByTime == null) {
            return Double.NaN;
        }
        final var floorEntry = rateByTime.floorEntry(timestamp);
        return floorEntry == null ? Double.NaN : floorEntry.getValue().getRate();
    }

    @Override
    public double average(String ccyPair, long start, long end) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        if (start > end) {
            throw new IllegalArgumentException(String.format("start is grater than end, [start, end]: [%d, %d]", start, end));
        }

        final var rateByTime = fxRates.get(ccyPair);
        if (rateByTime == null || end < rateByTime.firstKey() || start > rateByTime.lastKey()) {
            return Double.NaN;
        }

        final var subMap = rateByTime.subMap(start, true, end, true);
        if (subMap.isEmpty()) {
            return Double.NaN;
        }

        if (subMap.size() == 1) {
            return subMap.firstEntry().getValue().getRate();
        }

        final var toHolder = subMap.lastEntry().getValue();

        if (subMap.firstEntry().getValue().getCurrentSize() == 1) {
            return toHolder.getCurrentSum() / toHolder.getCurrentSize();
        }

        RateValueHolder fromHolder = rateByTime.lowerEntry(subMap.firstKey()).getValue();
        return (toHolder.getCurrentSum() - fromHolder.getCurrentSum()) / (toHolder.getCurrentSize() - fromHolder.getCurrentSize());
    }

    static class RateValueHolder {
        private final double rate;
        private final long currentSize;
        private final double currentSum;

        public RateValueHolder(double rate, long currentSize, double currentSum) {
            this.rate = rate;
            this.currentSize = currentSize;
            this.currentSum = currentSum;
        }

        public double getRate() {
            return rate;
        }

        public long getCurrentSize() {
            return currentSize;
        }

        public double getCurrentSum() {
            return currentSum;
        }
    }
}
