package com.vv.personal.twm.calc.constants;

import java.time.format.DateTimeFormatter;

/**
 * @author Vivek
 * @since 23/01/21
 */
public class Constants {

    public static final String EMPTY_STR = "";

    public static final String DATE_TIME_FORMATTER_YYYYMMDD = "yyyyMMdd";
    public static final DateTimeFormatter DTF_YYYYMMDD = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_YYYYMMDD);

    public static final double DELTA_PRECISION_6 = 1E-6;

    public static final Integer FY_ROLL_DATE_MONTH = 4;
    public static final Integer FY_ROLL_DATE_DAY = 1;

    public static final Integer DEFAULT_INT_ZERO = 0;
    public static final Long DEFAULT_LONG_ZERO = 0L;

    //URL - FORMATTERS
    public static final String HEROKU_SWAGGER_UI_URL = "https://%s/swagger-ui/index.html";
    public static final String SWAGGER_UI_URL = "http://%s:%s/swagger-ui/index.html";
    public static final String HEROKU_HOST_URL = "https://%s";
    public static final String HOST_URL = "http://%s:%s";

    public static final String LOCALHOST = "localhost";
    public static final String LOCAL_SPRING_HOST = "local.server.host";
    public static final String LOCAL_SPRING_PORT = "local.server.port";
    public static final String SPRING_APPLICATION_HEROKU = "spring.application.heroku";

}
