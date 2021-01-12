package com.db.interview;

public interface FxRateContainer {
    /**
     * Adds Fx Rate for specified {@code ccyPair} and {@code timestamp} to the container;
     *
     * @throws NullPointerException  if {@code ccyPair} is null;
     * @throws IllegalStateException in case of particular container implementation assumes the correct order
     *                               of adding Fx Rates (for the same {@code ccyPair} {@code timestamp} values should be
     *                               added in increasing order), but the order is broken
     */
    void add(String ccyPair, double fxRate, long timestamp);

    /**
     * @return Fx Rate for {@code ccyPair} at the moment of time that nearest less or equal to {@code timestamp}
     * or Double.NaN when there is no Fx Rates for {@code ccyPair} at all
     * or when there is no Fx Rates for {@code ccyPair} with timestamp less or equal to {@code timestamp};
     * @throws NullPointerException if {@code ccyPair} is null;
     */
    double get(String ccyPair, long timestamp);

    /**
     * @return average Fx Rate for the period from {@code start} to {@code end} inclusively
     * or Double.NaN when there is no Fx Rates for {@code ccyPair} at all
     * or when there is no Fx Rates for {@code ccyPair}  within the specified range;
     * @throws NullPointerException     if {@code ccyPair} is null;
     * @throws IllegalArgumentException if {@code start} is greater than {@code end};
     */
    double average(String ccyPair, long start, long end);
}
