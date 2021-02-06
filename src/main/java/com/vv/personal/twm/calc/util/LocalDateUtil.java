package com.vv.personal.twm.calc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static com.vv.personal.twm.calc.constants.Constants.DTF_YYYYMMDD;

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
}
