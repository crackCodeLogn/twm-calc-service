package com.vv.personal.twm.calc.core;

import com.vv.personal.twm.artifactory.generated.dates.DateRangeProto;
import com.vv.personal.twm.calc.util.LocalDateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;

import static com.vv.personal.twm.calc.core.DateRangeCalculator.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Vivek
 * @since 06/02/21
 */
@RunWith(JUnit4.class)
public class DateRangeCalculatorTest {

    @Test
    public void testGenerateIntegralDate() {
        int date = generateIntegralDate(6, 2, 2021);
        assertEquals(20210206, date);
    }

    @Test
    public void testGenerateFinancialYearEndDate() {
        LocalDate localDate = LocalDateUtil.generateLocalDateObject("20210206");
        int fyEnd = generateFinancialYearEndDate(localDate);
        assertEquals(20210401, fyEnd);

        localDate = LocalDateUtil.generateLocalDateObject("20210331");
        fyEnd = generateFinancialYearEndDate(localDate);
        assertEquals(20210401, fyEnd);

        localDate = LocalDateUtil.generateLocalDateObject("20210401");
        fyEnd = generateFinancialYearEndDate(localDate);
        assertEquals(20220401, fyEnd);

        localDate = LocalDateUtil.generateLocalDateObject("20211231");
        fyEnd = generateFinancialYearEndDate(localDate);
        assertEquals(20220401, fyEnd);
    }

    @Test
    public void testComputeDateRanges() {
        DateRangeProto.DateRangeList dateRangeList = computeDateRanges("20210206", "20210331");
        System.out.println(dateRangeList);
        assertEquals(1, dateRangeList.getDateRangesCount());
        assertEquals(53, dateRangeList.getDateRangesList().get(0).getDaysInBetween());

        dateRangeList = computeDateRanges("20210101", "20210331");
        System.out.println(dateRangeList);
        assertEquals(1, dateRangeList.getDateRangesCount());
        assertEquals(89, dateRangeList.getDateRangesList().get(0).getDaysInBetween());

        dateRangeList = computeDateRanges("20210101", "20210401");
        System.out.println(dateRangeList);
        assertEquals(2, dateRangeList.getDateRangesCount());
        assertEquals(90, dateRangeList.getDateRangesList().get(0).getDaysInBetween());
        assertEquals(0, dateRangeList.getDateRangesList().get(1).getDaysInBetween());

        System.out.println("xxxxxxxxxx");
        dateRangeList = computeDateRanges("20190921", "20210922");
        System.out.println(dateRangeList);
        assertEquals(3, dateRangeList.getDateRangesCount());
        assertEquals(193, dateRangeList.getDateRangesList().get(0).getDaysInBetween());
        assertEquals(365, dateRangeList.getDateRangesList().get(1).getDaysInBetween());
        assertEquals(174, dateRangeList.getDateRangesList().get(2).getDaysInBetween());

        System.out.println("yyyyyyyyyy");
        dateRangeList = computeDateRanges("20200829", "20250830");
        System.out.println(dateRangeList);
    }
}