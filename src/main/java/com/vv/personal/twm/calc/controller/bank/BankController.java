package com.vv.personal.twm.calc.controller.bank;

import com.vv.personal.twm.artifactory.generated.dates.DateRangeProto;
import com.vv.personal.twm.artifactory.generated.deposit.FixedDepositProto;
import com.vv.personal.twm.calc.controller.dates.DateDataController;
import com.vv.personal.twm.calc.core.AmountInterestCalculator;
import com.vv.personal.twm.calc.core.DaysCalculator;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.vv.personal.twm.calc.util.LocalDateUtil.generateFinancialYearStartDate;

/**
 * @author Vivek
 * @since 03/02/21
 */
@RestController("BankController")
@RequestMapping("/calc/bank")
public class BankController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankController.class);

    @Autowired
    private DateDataController dateDataController;

    @GetMapping("/fd/amount-interest")
    @ApiOperation(value = "calc FD amount and interest", hidden = true)
    public FixedDepositProto.FixedDeposit calcAmountAndInterest(@RequestParam double depositAmount,
                                                                @RequestParam double rateOfInterest,
                                                                @RequestParam int months,
                                                                @RequestParam int days) {
        LOGGER.info("Will compute amount and interest for FD with principal: {}, ROI: {}%, months: {}, days: {}", depositAmount, rateOfInterest, months, days);
        FixedDepositProto.FixedDeposit fixedDeposit = AmountInterestCalculator.calcAmountAndInterest(depositAmount, rateOfInterest, months, days);
        LOGGER.info("Computed interest: {} and final amount: {}", fixedDeposit.getExpectedInterest(), fixedDeposit.getExpectedAmount());
        return fixedDeposit;
    }

    @GetMapping("/manual/fd/amount-interest")
    public String calcAmountAndInterestForSwagger(@RequestParam double depositAmount,
                                                  @RequestParam double rateOfInterest,
                                                  @RequestParam int months,
                                                  @RequestParam(defaultValue = "0", required = false) int days) {
        return calcAmountAndInterest(depositAmount, rateOfInterest, months, days).toString();
    }

    @GetMapping("/fd/end-date")
    public String calcEndDate(@RequestParam String startDate,
                              @RequestParam Integer months,
                              @RequestParam(defaultValue = "0", required = false) Integer days) {
        LOGGER.info("Will compute end-date for FD with start-date: {}, months: {} & days: {}", startDate, months, days);
        String endDate = DaysCalculator.calcEndDate(startDate, months, days);
        LOGGER.info("Computed end-date: {}", endDate);
        return endDate;
    }

    @GetMapping("/fd/amount-interest/annual")
    @ApiOperation(value = "calc annual FD amount and interest", hidden = true)
    public FixedDepositProto.AnnualBreakdownList calcAnnualAmountAndInterest(@RequestParam double depositAmount,
                                                                             @RequestParam double rateOfInterest,
                                                                             @RequestParam String startDate,
                                                                             @RequestParam String endDate) {
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

    @GetMapping("/manual/fd/amount-interest/annual")
    public String calcAnnualAmountAndInterestManually(@RequestParam double depositAmount,
                                                      @RequestParam double rateOfInterest,
                                                      @RequestParam String startDate,
                                                      @RequestParam String endDate) {
        return calcAnnualAmountAndInterest(depositAmount, rateOfInterest, startDate, endDate).toString();
    }
}
