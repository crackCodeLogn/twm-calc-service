package com.vv.personal.twm.calc.remote.controller.bank;

import com.vv.personal.twm.artifactory.generated.data.DataPacketProto;
import com.vv.personal.twm.artifactory.generated.dates.DateRangeProto;
import com.vv.personal.twm.artifactory.generated.deposit.FixedDepositProto;
import com.vv.personal.twm.calc.core.AmountInterestCalculator;
import com.vv.personal.twm.calc.core.DaysCalculator;
import com.vv.personal.twm.calc.remote.controller.dates.DateDataController;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.QueryParam;

import java.util.HashMap;
import java.util.Map;

import static com.vv.personal.twm.calc.util.LocalDateUtil.generateFinancialYearStartDate;
import static com.vv.personal.twm.calc.util.LocalDateUtil.generateIntegralDate;

/**
 * @author Vivek
 * @since 03/02/21
 */
@Path("/calc/bank")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BankController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankController.class);

    @Inject
    private DateDataController dateDataController;

    @GET
    @Path("/fd/amount-interest")
    @Operation(summary = "calc FD amount and interest", hidden = true)
    public FixedDepositProto.FixedDeposit calcAmountAndInterest(@QueryParam("depositAmount") double depositAmount,
                                                                @QueryParam("rateOfInterest") double rateOfInterest,
                                                                @QueryParam("months") int months,
                                                                @QueryParam("days") int days,
                                                                @QueryParam("accountType") int accountType) {
        FixedDepositProto.AccountType accType = FixedDepositProto.AccountType.forNumber(accountType);
        if (accType == null) {
            LOGGER.warn("Cannot process FD compute request due to unknown account type: {}", accountType);
            return FixedDepositProto.FixedDeposit.newBuilder().build(); // empty
        }
        LOGGER.info("Will compute amount and interest for FD with principal: {}, ROI: {}%, months: {}, days: {}, " +
                "account type: {}", depositAmount, rateOfInterest, months, days, accType);
        FixedDepositProto.FixedDeposit fixedDeposit = AmountInterestCalculator.calcAmountAndInterest(depositAmount,
                rateOfInterest, months, days, accType);
        LOGGER.info("Computed interest: {} and final amount: {}", fixedDeposit.getExpectedInterest(), fixedDeposit.getExpectedAmount());
        return fixedDeposit;
    }

    @GET
    @Path("/manual/fd/amount-interest")
    @Produces(MediaType.TEXT_PLAIN)
    public String calcAmountAndInterestForSwagger(@QueryParam("depositAmount") double depositAmount,
                                                  @QueryParam("rateOfInterest") double rateOfInterest,
                                                  @QueryParam("months") int months,
                                                  @QueryParam("days") int days,
                                                  @QueryParam("accountType") int accountType) {
        return calcAmountAndInterest(depositAmount, rateOfInterest, months, days, accountType).toString();
    }

    @GET
    @Path("/fd/end-date")
    @Produces(MediaType.TEXT_PLAIN)
    public String calcEndDate(@QueryParam("startDate") String startDate,
                              @QueryParam("months") Integer months,
                              @QueryParam("days") Integer days) {
        if (days == null) days = 0;
        LOGGER.info("Will compute end-date for FD with start-date: {}, months: {} & days: {}", startDate, months, days);
        String endDate = DaysCalculator.calcEndDate(startDate, months, days);
        LOGGER.info("Computed end-date: {}", endDate);
        return endDate;
    }

    @GET
    @Path("/fd/amount-interest/annual")
    @Operation(summary = "calc annual FD amount and interest", hidden = true)
    public FixedDepositProto.AnnualBreakdownList calcAnnualAmountAndInterest(@QueryParam("depositAmount") double depositAmount,
                                                                             @QueryParam("rateOfInterest") double rateOfInterest,
                                                                             @QueryParam("startDate") String startDate,
                                                                             @QueryParam("endDate") String endDate) {
        LOGGER.info("Will compute annual amount and interest for FD with principal: {}, ROI: {}%, start-date: {}, end-date: {}", depositAmount, rateOfInterest, startDate, endDate);
        DateRangeProto.DateRangeList dateRangeList = dateDataController.computeDateRanges(startDate, endDate);

        FixedDepositProto.AnnualBreakdownList.Builder annualBreakdownListBuilder = FixedDepositProto.AnnualBreakdownList.newBuilder();
        double currentPrincipal = depositAmount;
        for (DateRangeProto.DateRange dateRange : dateRangeList.getDateRangesList()) {
            FixedDepositProto.FixedDeposit computedDixedDepositDetail = AmountInterestCalculator.calcAmountAndInterest(currentPrincipal, rateOfInterest, dateRange.getDaysInBetween());
            FixedDepositProto.AnnualBreakdown.Builder builder = FixedDepositProto.AnnualBreakdown.newBuilder();
            builder.setExpectedInterestGained(computedDixedDepositDetail.getExpectedInterest());
            builder.setExpectedAmountAccumulated(computedDixedDepositDetail.getExpectedAmount());
            builder.setStartDate(dateRange.getStartDate());
            builder.setEndDate(dateRange.getEndDate());
            builder.setDaysInBetween(dateRange.getDaysInBetween());
            builder.setFinancialYear(generateFinancialYearStartDate(dateRange.getStartDate()));
            annualBreakdownListBuilder.addAnnualBreakdown(builder.build());

            currentPrincipal = computedDixedDepositDetail.getExpectedAmount();
        }
        FixedDepositProto.AnnualBreakdownList annualBreakdownList = annualBreakdownListBuilder.build();

        LOGGER.info("Computed {} breakdowns. Final amount: {} and interest: {}", annualBreakdownList.getAnnualBreakdownCount(),
                annualBreakdownListBuilder.getAnnualBreakdown(annualBreakdownList.getAnnualBreakdownCount() - 1).getExpectedAmountAccumulated(),
                annualBreakdownList.getAnnualBreakdownList().stream().mapToDouble(FixedDepositProto.AnnualBreakdown::getExpectedInterestGained).sum());
        return annualBreakdownList;
    }

    @GET
    @Path("/manual/fd/amount-interest/annual")
    @Produces(MediaType.TEXT_PLAIN)
    public String calcAnnualAmountAndInterestManually(@QueryParam("depositAmount") double depositAmount,
                                                      @QueryParam("rateOfInterest") double rateOfInterest,
                                                      @QueryParam("startDate") String startDate,
                                                      @QueryParam("endDate") String endDate) {
        return calcAnnualAmountAndInterest(depositAmount, rateOfInterest, startDate, endDate).toString();
    }

    @POST
    @Path("/fd/amount")
    public DataPacketProto.DataPacket calcAnnualAmounts(FixedDepositProto.FixedDeposit fixedDeposit) {
        Map<Integer, Double> dateAmountMap = new HashMap<>();
        calcAmounts(fixedDeposit, dateAmountMap);
        return DataPacketProto.DataPacket.newBuilder()
                .putAllIntDoubleMap(dateAmountMap)
                .build();
    }

    @POST
    @Path("/fd/amounts")
    public DataPacketProto.DataPacket calcAnnualAmounts(FixedDepositProto.FixedDepositList fixedDepositList) {
        Map<Integer, Double> dateAmountMap = new HashMap<>();
        fixedDepositList.getFixedDepositList().forEach(fixedDeposit -> calcAmounts(fixedDeposit, dateAmountMap));
        return DataPacketProto.DataPacket.newBuilder()
                .putAllIntDoubleMap(dateAmountMap)
                .build();
    }

    private void calcAmounts(FixedDepositProto.FixedDeposit fixedDeposit, Map<Integer, Double> dateAmountMap) {
        int compoundingFactor = AmountInterestCalculator.getCompoundingFactor(fixedDeposit.getAccountType());
        int startDate = generateIntegralDate(fixedDeposit.getStartDate());
        int endDate = generateIntegralDate(fixedDeposit.getEndDate());

        AmountInterestCalculator.calcAmounts(
                fixedDeposit.getDepositAmount(),
                fixedDeposit.getRateOfInterest(),
                startDate,
                endDate,
                compoundingFactor,
                dateAmountMap
        );
    }
}
