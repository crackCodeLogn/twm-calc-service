package com.vv.personal.twm.calc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import static com.vv.personal.twm.calc.constants.Constants.*;

/**
 * @author Vivek
 * @since 06/02/21
 */
public class LocalDateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateUtil.class);

    public static LocalDate generateLocalDateObject(String date) {
        try {
            return LocalDate.parse(date, DTF_YYYYMMDD);
        } catch (DateTimeParseException e) {
            LOGGER.error("Unable to parse input startDate '{}'. ", date, e);
        }
        return null;
    }

    public static LocalDate generateLocalDateObject(int date) {
        int day = date % 100;
        date /= 100;
        int month = date % 100;
        date /= 100;
        return LocalDate.of(date, month, day);
    }

    public static Integer generateIntegralDate(String date) {
        return generateIntegralDate(Objects.requireNonNull(generateLocalDateObject(date)));
    }

    public static Integer generateIntegralDate(LocalDate localDate) {
        return generateIntegralDate(localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());
    }

    public static Integer generateIntegralDate(int day, int month, int year) {
        return Integer.valueOf(String.format("%04d%02d%02d", year, month, day));
    }

    public static String generateFinancialYearStartDate(String date) {
        return String.valueOf(generateFinancialYearStartDate(generateLocalDateObject(date)));
    }

    public static Integer generateFinancialYearStartDate(LocalDate dateObject) {
        if (dateObject == null) return DEFAULT_INT_ZERO;

        int currentYear = dateObject.getYear();
        if (dateObject.getMonthValue() <= 3) currentYear--;
        return generateIntegralDate(FY_ROLL_DATE_DAY, FY_ROLL_DATE_MONTH, currentYear);
    }

    public static String generateFinancialYearEndDate(String date) {
        return String.valueOf(generateFinancialYearEndDate(generateLocalDateObject(date)));
    }

    public static Integer generateFinancialYearEndDate(LocalDate dateObject) {
        if (dateObject == null) return DEFAULT_INT_ZERO;

        int nextYear = dateObject.getYear() + 1;
        if (dateObject.getMonthValue() <= 3) nextYear--;
        return generateIntegralDate(FY_ROLL_DATE_DAY, FY_ROLL_DATE_MONTH, nextYear);
    }
}
