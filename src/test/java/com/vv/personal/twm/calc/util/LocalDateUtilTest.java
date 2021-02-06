package com.vv.personal.twm.calc.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.LocalDate;

import static com.vv.personal.twm.calc.util.LocalDateUtil.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Vivek
 * @since 06/02/21
 */
@RunWith(JUnit4.class)
public class LocalDateUtilTest {

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
    public void testGenerateFinancialYearStartDate() {
        LocalDate localDate = LocalDateUtil.generateLocalDateObject("20210206");
        int fyEnd = generateFinancialYearStartDate(localDate);
        assertEquals(20200401, fyEnd);

        localDate = LocalDateUtil.generateLocalDateObject("20210331");
        fyEnd = generateFinancialYearStartDate(localDate);
        assertEquals(20200401, fyEnd);

        localDate = LocalDateUtil.generateLocalDateObject("20210401");
        fyEnd = generateFinancialYearStartDate(localDate);
        assertEquals(20210401, fyEnd);

        localDate = LocalDateUtil.generateLocalDateObject("20211231");
        fyEnd = generateFinancialYearStartDate(localDate);
        assertEquals(20210401, fyEnd);
    }

}