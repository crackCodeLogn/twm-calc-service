package com.vv.personal.twm.calc.core;

import com.vv.personal.twm.calc.util.LocalDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static com.vv.personal.twm.calc.constants.Constants.*;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * @author Vivek
 * @since 03/02/21
 */
public class DaysCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DaysCalculator.class);

    public static String calcEndDate(String startDate, int months, int extendedDays) {
        LocalDate date = LocalDateUtil.generateLocalDateObject(startDate);
        if (date == null) return EMPTY_STR;

        date = date.plusMonths(months);
        date = date.plusDays(extendedDays);
        return date.format(DTF_YYYYMMDD);
    }

    public static Long numberOfDaysInBetween(String startDate, String endDate) {
        LocalDate startDateObject = LocalDateUtil.generateLocalDateObject(startDate);
        if (startDateObject == null) return DEFAULT_LONG_ZERO;
        LocalDate endDateObject = LocalDateUtil.generateLocalDateObject(endDate);
        if (endDateObject == null) return DEFAULT_LONG_ZERO;

        return DAYS.between(startDateObject, endDateObject);
    }

}
