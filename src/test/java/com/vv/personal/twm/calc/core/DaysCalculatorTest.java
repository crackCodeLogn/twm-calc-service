package com.vv.personal.twm.calc.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.vv.personal.twm.calc.core.DaysCalculator.calcEndDate;
import static com.vv.personal.twm.calc.core.DaysCalculator.numberOfDaysInBetween;
import static org.junit.Assert.assertEquals;

/**
 * @author Vivek
 * @since 03/02/21
 */
@RunWith(JUnit4.class)
public class DaysCalculatorTest {

    @Test
    public void testCalcEndDate() {
        String endDate = calcEndDate("20210203", 1, 0);
        assertEquals("20210303", endDate);

        endDate = calcEndDate("20210101", 11, 20);
        assertEquals("20211221", endDate);

        endDate = calcEndDate("20210101", 12, 20);
        assertEquals("20220121", endDate);
    }

    @Test
    public void testNumberOfDaysInBetween() {
        long numberOfDaysInBetween = numberOfDaysInBetween("20210401", "20220331");
        assertEquals(364, numberOfDaysInBetween);

        numberOfDaysInBetween = numberOfDaysInBetween("20210401", "20210402");
        assertEquals(1, numberOfDaysInBetween);
    }
}