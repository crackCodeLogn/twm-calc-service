package com.vv.personal.twm.calc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static com.vv.personal.twm.calc.constants.Constants.DATE_TIME_FORMATTER_YYYY_M_MDD;
import static com.vv.personal.twm.calc.constants.Constants.EMPTY_STR;

/**
 * @author Vivek
 * @since 03/02/21
 */
public class DaysCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DaysCalculator.class);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_YYYY_M_MDD);

    public static String calcEndDate(String startDate, int months, int extendedDays) {
        LocalDate date;
        try {
            date = LocalDate.parse(startDate, DTF);
        } catch (DateTimeParseException e) {
            LOGGER.error("Unable to parse input startDate '{}'. ", startDate, e);
            return EMPTY_STR;
        }
        date = date.plusMonths(months);
        date = date.plusDays(extendedDays);
        return date.format(DTF);
    }
}
