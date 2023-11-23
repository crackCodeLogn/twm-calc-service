package com.vv.personal.twm.calc.controller.dates;

import com.google.protobuf.AbstractMessage;
import com.vv.personal.twm.artifactory.generated.dates.DateRangeProto;
import com.vv.personal.twm.calc.core.DateRangeCalculator;
import com.vv.personal.twm.calc.core.DaysCalculator;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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

    @Operation(summary = "compute dateRange between start and end date", hidden = true)
    @GetMapping("/dateRanges-in-between")
    public DateRangeProto.DateRangeList computeDateRanges(@RequestParam String startDate,
                                                          @RequestParam String endDate) {
        LOGGER.info("Will compute date range of days in between {} -> {}", startDate, endDate);
        DateRangeProto.DateRangeList dateRangeList = DateRangeCalculator.computeDateRanges(startDate, endDate);
        if (dateRangeList == null) {
            LOGGER.error("Date range list returned null...");
            return DateRangeProto.DateRangeList.newBuilder().build();
        }
        LOGGER.info("Calculated dateRangeList of entries {}", dateRangeList.getDateRangesCount());
        return dateRangeList;
    }

    @GetMapping("/manual/dateRanges-in-between")
    public List<String> computeDateRangesManually(@RequestParam String startDate,
                                                  @RequestParam String endDate) {
        return computeDateRanges(startDate, endDate).getDateRangesList()
                .stream().map(AbstractMessage::toString)
                .collect(Collectors.toList());
    }
}
