package com.db.interview;

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
 * average - O(logN)
 */
public class FxRateOrderedContainerImpl implements FxRateContainer {

    private final Map<String, List<RateValueHolder>> fxRates = new HashMap<>();
    private final Comparator<RateValueHolder> ratesComparator = Comparator.comparing(RateValueHolder::getTimestamp);

    @Override
    public void add(String ccyPair, double fxRate, long timestamp) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        final var rates = fxRates.computeIfAbsent(ccyPair, (key) -> new ArrayList<>());
        if (!rates.isEmpty() && timestamp <= rates.get(rates.size() - 1).getTimestamp()) {
            throw new IllegalArgumentException(String.format("timestamp is less than or equal to last stored timestamp, [timestamp, stored]: [%d, %d]",
                    timestamp, rates.get(rates.size() - 1).getTimestamp()));
        }

        double prevSum = rates.isEmpty() ? 0.0 : rates.get(rates.size() - 1).getCurrentSum();
        rates.add(new RateValueHolder(timestamp, fxRate, prevSum + fxRate));
    }

    @Override
    public double get(String ccyPair, long timestamp) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        final var rates = fxRates.get(ccyPair);
        if (rates == null) {
            return Double.NaN;
        }

        int index = Collections.binarySearch(rates, new RateValueHolder(timestamp, 0.0, 0.0), ratesComparator);
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

        return rates.get(index).getRate();
    }

    @Override
    public double average(String ccyPair, long start, long end) {
        Objects.requireNonNull(ccyPair, "ccyPair");

        if (start > end) {
            throw new IllegalArgumentException(String.format("start is grater than end, [start, end]: [%d, %d]", start, end));
        }

        final var rates = fxRates.get(ccyPair);
        if (rates == null || end < rates.get(0).getTimestamp() || start > rates.get(rates.size() - 1).getTimestamp()) {
            return Double.NaN;
        }

        int fromIndex = Collections.binarySearch(rates, new RateValueHolder(start, 0.0, 0.0), ratesComparator);
        // in case when specified timestamp (start) is not found in the list (fromIndex < 0)
        // the following logic gets minimum index of element with timestamp greater than specified;
        // in case when fromIndex >= 0 we already have index of element with timestamp equal to the specified one
        if (fromIndex < 0) {
            fromIndex = Math.abs(fromIndex) - 1;
        }

        int toIndex = Collections.binarySearch(rates, new RateValueHolder(end, 0.0, 0.0), ratesComparator);
        // in case when specified timestamp (end) is not found in the list (toIndex < 0)
        // the following logic gets maximum index of element with timestamp less than specified;
        // in case when toIndex >= 0 we already have index of element with timestamp equal to the specified one
        if (toIndex < 0) {
            toIndex = Math.abs(toIndex) - 2;
        }

        if (fromIndex == toIndex) {
            return rates.get(toIndex).getRate();
        }

        if (fromIndex > toIndex) {
            return Double.NaN;
        }

        final var toHolder = rates.get(toIndex);
        if (fromIndex == 0) {
            return toHolder.getCurrentSum() / (toIndex + 1);
        }

        fromIndex--;
        final var fromHolder = rates.get(fromIndex);
        return (toHolder.getCurrentSum() - fromHolder.getCurrentSum()) / (toIndex - fromIndex);
    }

    static class RateValueHolder {
        private final long timestamp;
        private final double rate;
        private final double currentSum;

        public RateValueHolder(long timestamp, double rate, double currentSum) {
            this.timestamp = timestamp;
            this.rate = rate;
            this.currentSum = currentSum;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public double getRate() {
            return rate;
        }

        public double getCurrentSum() {
            return currentSum;
        }
    }
}
