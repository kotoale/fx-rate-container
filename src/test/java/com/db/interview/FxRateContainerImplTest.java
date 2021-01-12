package com.db.interview;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FxRateContainerImplTest {

    FxRateContainer fxRateContainer = new FxRateContainerImpl();

    @Test
    void add_should_throw_NullPointerException() {
        assertThrows(NullPointerException.class, () -> fxRateContainer.add(null, 90.1208, 1579598352));
    }

    @Test
    void add_should_add_correctly() {
        fxRateContainer.add("EURRUB", 90.1208, 1579598352);
        fxRateContainer.add("EURRUB", 90.1207, 1579598352 - 1);
        fxRateContainer.add("EURRUB", 90.1209, 1579598352 + 1);

        assertThat(fxRateContainer.get("EURRUB", 1579598352)).isEqualTo(90.1208);
    }

    @Test
    void get_should_throw_NullPointerException() {
        assertThrows(NullPointerException.class, () -> fxRateContainer.get(null, 1579598352));
    }

    @ParameterizedTest
    @MethodSource("provideDifferentValuesForTestGet")
    void get_should_return_expected_value(String ccyPair, long timestamp, double expected) {
        fxRateContainer.add("EURRUB", 90.110, 1579598310);
        fxRateContainer.add("EURRUB", 90.120, 1579598320);
        fxRateContainer.add("EURRUB", 90.130, 1579598330);
        fxRateContainer.add("EURRUB", 90.140, 1579598340);

        assertThat(fxRateContainer.get(ccyPair, timestamp)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideDifferentValuesForTestGet() {
        return Stream.of(
                Arguments.of("EURUSD", 1579598310, Double.NaN),
                Arguments.of("EURRUB", 1579598350, 90.140),
                Arguments.of("EURRUB", 1579598305, Double.NaN),
                Arguments.of("EURRUB", 1579598310, 90.110),
                Arguments.of("EURRUB", 1579598311, 90.110),
                Arguments.of("EURRUB", 1579598315, 90.110),
                Arguments.of("EURRUB", 1579598317, 90.110),
                Arguments.of("EURRUB", 1579598320, 90.120),
                Arguments.of("EURRUB", 1579598324, 90.120),
                Arguments.of("EURRUB", 1579598330, 90.130),
                Arguments.of("EURRUB", 1579598336, 90.130),
                Arguments.of("EURRUB", 1579598340, 90.140),
                Arguments.of("EURRUB", 1579598341, 90.140),
                Arguments.of("EURRUB", 1579598345, 90.140),
                Arguments.of("EURRUB", 1579598309, Double.NaN)
        );
    }

    @Test
    void average_should_throw_NullPointerException() {
        assertThrows(NullPointerException.class, () -> fxRateContainer.average(null, 1579598352, 1579598352));
    }

    @Test
    void average_should_throw_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> fxRateContainer.average("EURRUB", 1579598352 + 1, 1579598352));
    }

    @ParameterizedTest
    @MethodSource("provideDifferentValuesForTestAverage")
    void average_should_return_expected_value(String ccyPair, long start, long end, double average) {
        fxRateContainer.add("EURRUB", 90.110, 1579598310);
        fxRateContainer.add("EURRUB", 90.120, 1579598320);
        fxRateContainer.add("EURRUB", 90.130, 1579598330);
        fxRateContainer.add("EURRUB", 90.140, 1579598340);

        assertThat(fxRateContainer.average(ccyPair, start, end)).isEqualTo(average);
    }

    private static Stream<Arguments> provideDifferentValuesForTestAverage() {
        return Stream.of(
                Arguments.of("EURUSD", 1579598310, 1579598310, Double.NaN),
                Arguments.of("EURRUB", 1579598350, 1579598360, Double.NaN),
                Arguments.of("EURRUB", 1579598300, 1579598305, Double.NaN),
                Arguments.of("EURRUB", 1579598310, 1579598310, 90.110),
                Arguments.of("EURRUB", 1579598315, 1579598315, Double.NaN),
                Arguments.of("EURRUB", 1579598320, 1579598320, 90.120),
                Arguments.of("EURRUB", 1579598320, 1579598325, 90.120),
                Arguments.of("EURRUB", 1579598340, 1579598340, 90.140),
                Arguments.of("EURRUB", 1579598310, 1579598320, (90.110 + 90.120) / 2),
                Arguments.of("EURRUB", 1579598310, 1579598315, 90.110),
                Arguments.of("EURRUB", 1579598315, 1579598320, 90.120),
                Arguments.of("EURRUB", 1579598315, 1579598325, 90.120),
                Arguments.of("EURRUB", 1579598315, 1579598330, (90.120 + 90.130) / 2),
                Arguments.of("EURRUB", 1579598315, 1579598335, (90.120 + 90.130) / 2),
                Arguments.of("EURRUB", 1579598310, 1579598340, (90.110 + 90.120 + 90.130 + 90.140) / 4),
                Arguments.of("EURRUB", 1579598310, 1579598345, (90.110 + 90.120 + 90.130 + 90.140) / 4),
                Arguments.of("EURRUB", 1579598305, 1579598340, (90.110 + 90.120 + 90.130 + 90.140) / 4),
                Arguments.of("EURRUB", 1579598305, 1579598345, (90.110 + 90.120 + 90.130 + 90.140) / 4),
                Arguments.of("EURRUB", 1579598321, 1579598329, Double.NaN)
        );
    }
}