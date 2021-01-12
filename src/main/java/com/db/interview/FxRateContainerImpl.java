package com.db.interview;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 * This implementation supports any order of adding Fx Rates
 */
public class FxRateContainerImpl implements FxRateContainer {

    private final Map<String, NavigableMap<Long, Double>> fxRates = new HashMap<>();

    @Override
    public void add(String ccyPair, double fxRate, long timestamp) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        final var rateByTime = fxRates.computeIfAbsent(ccyPair, (key) -> new TreeMap<>());
        rateByTime.put(timestamp, fxRate);
    }

    @Override
    public double get(String ccyPair, long timestamp) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        final var rateByTime = fxRates.get(ccyPair);
        if (rateByTime == null) {
            return Double.NaN;
        }
        final var floorEntry = rateByTime.floorEntry(timestamp);
        return floorEntry == null ? Double.NaN : floorEntry.getValue();
    }

    @Override
    public double average(String ccyPair, long start, long end) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        if (start > end) {
            throw new IllegalArgumentException(String.format("start is grater than end, [start, end]: [%d, %d]", start, end));
        }

        final var rateByTime = fxRates.get(ccyPair);
        return rateByTime == null
                ? Double.NaN
                : rateByTime.subMap(start, true, end, true)
                .values()
                .stream()
                .mapToDouble(e -> e)
                .average()
                .orElse(Double.NaN);
    }
}
