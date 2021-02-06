package com.vv.personal.twm.calc.core;

import com.vv.personal.twm.artifactory.generated.dates.DateRangeProto;

import java.time.LocalDate;

import static com.vv.personal.twm.calc.util.LocalDateUtil.generateLocalDateObject;

/**
 * @author Vivek
 * @since 06/02/21
 */
public class DateRangeCalculator {
    public static final Integer FY_ROLL_DATE_MONTH = 4;
    public static final Integer FY_ROLL_DATE_DAY = 1;

    public static DateRangeProto.DateRangeList computeDateRanges(String startDateStr, String endDateStr) {
        DateRangeProto.DateRangeList.Builder dateRanges = DateRangeProto.DateRangeList.newBuilder();
        LocalDate startDate, endDate = generateLocalDateObject(endDateStr);
        Integer endDateInt = generateIntegralDate(endDate);

        while (true) {
            startDate = generateLocalDateObject(startDateStr);
            Integer fyEndDateInt = generateFinancialYearEndDate(startDate);
            if (endDateInt < fyEndDateInt) {
                return dateRanges.addDateRanges(generateDateRange(startDateStr, endDateStr)).build();
            }
            dateRanges.addDateRanges(generateDateRange(startDateStr, String.valueOf(fyEndDateInt)));
            startDateStr = String.valueOf(fyEndDateInt);
        }
    }

    private static DateRangeProto.DateRange generateDateRange(String startDate, String endDate) {
        return DateRangeProto.DateRange.newBuilder()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setDaysInBetween(DaysCalculator.numberOfDaysInBetween(startDate, endDate))
                .build();
    }

    public static Integer generateIntegralDate(LocalDate localDate) {
        return generateIntegralDate(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());
    }

    public static Integer generateIntegralDate(int day, int month, int year) {
        return Integer.valueOf(String.format("%04d%02d%02d", year, month, day));
    }

    public static Integer generateFinancialYearEndDate(LocalDate dateObject) {
        int nextYear = dateObject.getYear() + 1;
        if (dateObject.getMonthValue() <= 3) nextYear--;
        return generateIntegralDate(FY_ROLL_DATE_DAY, FY_ROLL_DATE_MONTH, nextYear);
    }

}
