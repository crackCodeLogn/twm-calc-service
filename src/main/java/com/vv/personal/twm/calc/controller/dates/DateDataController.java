package com.vv.personal.twm.calc.controller.dates;

import com.vv.personal.twm.calc.core.DaysCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Vivek
 * @since 06/02/21
 */
@RestController("DateDataController")
@RequestMapping("/calc/dates")
public class DateDataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateDataController.class);

    @GetMapping("/days-in-between")
    public Long calculateDaysInBetween(@RequestParam String startDate,
                                       @RequestParam String endDate) {
        LOGGER.info("Will compute number of days in between {} -> {}", startDate, endDate);
        long numberOfDaysInBetween = DaysCalculator.numberOfDaysInBetween(startDate, endDate);
        LOGGER.info("Calculated numberOfDaysInBetween: {}", numberOfDaysInBetween);
        return numberOfDaysInBetween;
    }
}
