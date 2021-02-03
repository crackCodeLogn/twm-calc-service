package com.vv.personal.twm.calc.core;

import com.vv.personal.twm.artifactory.generated.deposit.FixedDepositProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vivek
 * @since 03/02/21
 */
public class AmountInterestCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmountInterestCalculator.class);

    public static FixedDepositProto.FixedDeposit calcAmountAndInterest(double principal, double rateOfInterest, int months, int days) {
        double amount, interest;
        double effectiveMonths = months + (days / 30.0); //division by 30.0 might be flaky, but won't have that much impact

        if (effectiveMonths <= 6) {
            amount = (principal * rateOfInterest * effectiveMonths) / (100.0 * 12);
        } else {
            //computing interest quarterly ->
            amount = calcAmount(principal, rateOfInterest, effectiveMonths, 4);
        }
        interest = amount - principal;

        FixedDepositProto.FixedDeposit.Builder fdBuilder = FixedDepositProto.FixedDeposit.newBuilder();
        fdBuilder.setExpectedInterest(interest);
        fdBuilder.setExpectedAmount(amount);
        return fdBuilder.build();
    }

    private static double calcAmount(double principal, double rateOfInterest, double monthsTimePeriod, int compoundingFactor) {
        if (monthsTimePeriod == 0 || compoundingFactor == 0) return 0.0;
        LOGGER.info("Registered request for calculating amount for {} at {}% for {} months at compounding factor {}", principal, rateOfInterest, monthsTimePeriod, compoundingFactor);
        double amount = principal * Math.pow(1 + rateOfInterest / (compoundingFactor * 100.0), compoundingFactor * (monthsTimePeriod / 12.0));
        LOGGER.info("Subsequent amount calc: {}", amount);
        return amount;
    }

    private static int computeHighestDividingTimePeriod(int months, int dividingFactor) {
        return ((int) Math.floor(months / (dividingFactor * 1.0))) * dividingFactor;
    }

}
