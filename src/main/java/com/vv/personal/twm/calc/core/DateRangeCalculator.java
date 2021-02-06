package com.vv.personal.twm.calc.core;

import com.vv.personal.twm.artifactory.generated.dates.DateRangeProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static com.vv.personal.twm.calc.util.LocalDateUtil.*;

/**
 * @author Vivek
 * @since 06/02/21
 */
public class DateRangeCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateRangeCalculator.class);

    public static DateRangeProto.DateRangeList computeDateRanges(String startDateStr, String endDateStr) {
        DateRangeProto.DateRangeList.Builder dateRanges = DateRangeProto.DateRangeList.newBuilder();
        LocalDate startDate, endDate = generateLocalDateObject(endDateStr);
        Integer endDateInt = generateIntegralDate(endDate);

        while (true) {
            startDate = generateLocalDateObject(startDateStr);
            if (startDate == null) {
                LOGGER.error("Got null start date for {}!", startDateStr);
                return null;
            }
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

}
