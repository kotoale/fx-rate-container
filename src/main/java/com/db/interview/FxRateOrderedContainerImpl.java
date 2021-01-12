package com.db.interview;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This implementation assumes correct order of adding Fx Rates.
 * <p>
 * Time Complexity (worse case):
 * add - O(1)
 * get - O(logN)
 * average - O(N)
 */
public class FxRateOrderedContainerImpl implements FxRateContainer {

    private final Map<String, List<ImmutablePair<Long, Double>>> fxRates = new HashMap<>();
    private final Comparator<ImmutablePair<Long, Double>> ratesComparator = Comparator.comparing(ImmutablePair::getLeft);

    @Override
    public void add(String ccyPair, double fxRate, long timestamp) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        final var rates = fxRates.computeIfAbsent(ccyPair, (key) -> new ArrayList<>());
        if (!rates.isEmpty() && timestamp <= rates.get(rates.size() - 1).getLeft()) {
            throw new IllegalArgumentException(String.format("timestamp is less than or equal to last stored timestamp, [timestamp, stored]: [%d, %d]",
                    timestamp, rates.get(rates.size() - 1).getLeft()));
        }

        rates.add(new ImmutablePair<>(timestamp, fxRate));
    }

    @Override
    public double get(String ccyPair, long timestamp) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        final var rates = fxRates.get(ccyPair);
        if (rates == null) {
            return Double.NaN;
        }

        int index = Collections.binarySearch(rates, new ImmutablePair<>(timestamp, 0.0), ratesComparator);
        // all stored timestamps are greater than specified one
        if (index == -1) {
            return Double.NaN;
        }

        // in case when specified timestamp is not found in the list (index < 0)
        // the following logic gets maximum index of element with timestamp less than specified;
        // in case when index >= 0 we already have index of element with timestamp equal to the specified one
        if (index < 0) {
            index = Math.abs(index) - 2;
        }

        return rates.get(index).getRight();
    }

    @Override
    public double average(String ccyPair, long start, long end) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        if (start > end) {
            throw new IllegalArgumentException(String.format("start is grater than end, [start, end]: [%d, %d]", start, end));
        }

        final var rates = fxRates.get(ccyPair);
        if (rates == null) {
            return Double.NaN;
        }

        int startIndex = Collections.binarySearch(rates, new ImmutablePair<>(start, 0.0), ratesComparator);
        // in case when specified timestamp (start) is not found in the list (startIndex < 0)
        // the following logic gets minimum index of element with timestamp greater than specified;
        // in case when startIndex >= 0 we already have index of element with timestamp equal to the specified one
        if (startIndex < 0) {
            startIndex = Math.abs(startIndex) - 1;
        }

        int toIndex = Collections.binarySearch(rates, new ImmutablePair<>(end, 0.0), ratesComparator);
        // in case when specified timestamp (end) is not found in the list (toIndex < 0)
        // the following logic gets maximum index of element with timestamp less than specified;
        // in case when toIndex >= 0 we already have index of element with timestamp equal to the specified one
        if (toIndex < 0) {
            toIndex = Math.abs(toIndex) - 2;
        }

        return rates.subList(startIndex, ++toIndex)
                .stream()
                .mapToDouble(ImmutablePair::getRight)
                .average()
                .orElse(Double.NaN);
    }
}
