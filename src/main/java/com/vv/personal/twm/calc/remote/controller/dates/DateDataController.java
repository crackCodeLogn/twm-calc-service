package com.vv.personal.twm.calc.remote.controller.dates;

import com.google.protobuf.AbstractMessage;
import com.vv.personal.twm.artifactory.generated.dates.DateRangeProto;
import com.vv.personal.twm.calc.core.DateRangeCalculator;
import com.vv.personal.twm.calc.core.DaysCalculator;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.QueryParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vivek
 * @since 06/02/21
 */
@Path("/calc/dates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DateDataController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateDataController.class);

    @GET
    @Path("/days-in-between")
    public Long calculateDaysInBetween(@QueryParam("startDate") String startDate,
                                       @QueryParam("endDate") String endDate) {
        LOGGER.info("Will compute number of days in between {} -> {}", startDate, endDate);
        long numberOfDaysInBetween = DaysCalculator.numberOfDaysInBetween(startDate, endDate);
        LOGGER.info("Calculated numberOfDaysInBetween: {}", numberOfDaysInBetween);
        return numberOfDaysInBetween;
    }

    @Operation(summary = "compute dateRange between start and end date", hidden = true)
    @GET
    @Path("/dateRanges-in-between")
    public DateRangeProto.DateRangeList computeDateRanges(@QueryParam("startDate") String startDate,
                                                          @QueryParam("endDate") String endDate) {
        LOGGER.info("Will compute date range of days in between {} -> {}", startDate, endDate);
        DateRangeProto.DateRangeList dateRangeList = DateRangeCalculator.computeDateRanges(startDate, endDate);
        if (dateRangeList == null) {
            LOGGER.error("Date range list returned null...");
            return DateRangeProto.DateRangeList.newBuilder().build();
        }
        LOGGER.info("Calculated dateRangeList of entries {}", dateRangeList.getDateRangesCount());
        return dateRangeList;
    }

    @GET
    @Path("/manual/dateRanges-in-between")
    public List<String> computeDateRangesManually(@QueryParam("startDate") String startDate,
                                                  @QueryParam("endDate") String endDate) {
        return computeDateRanges(startDate, endDate).getDateRangesList()
                .stream().map(AbstractMessage::toString)
                .collect(Collectors.toList());
    }
}
