package com.vv.personal.twm.calc.core;

import com.vv.personal.twm.artifactory.generated.deposit.FixedDepositProto;
import com.vv.personal.twm.calc.util.LocalDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author Vivek
 * @since 03/02/21
 */
public class AmountInterestCalculator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmountInterestCalculator.class);


    //MONTH-DRIVEN CALC
    public static FixedDepositProto.FixedDeposit calcAmountAndInterest(double principal, double rateOfInterest, int months, int days, FixedDepositProto.AccountType accountType) {
        double amount, interest;
        double effectiveMonths = months + (days / 30.0); //division by 30.0 might be flaky, but won't have that much impact

        if (effectiveMonths <= 12) { // apply simple interest calculation
            LOGGER.info("Applying Simple Interest calculation as the effective months '{}' is <= 12", effectiveMonths);
            amount = principal + (principal * rateOfInterest * effectiveMonths) / (100.0 * 12);
        } else {
            int compoundingFactor = getCompoundingFactor(accountType);
            LOGGER.info("Applying Compound Interest calculation as the effective months '{}' is > 12, and compounding factor: {}", effectiveMonths, compoundingFactor);
            amount = calcAmount(principal, rateOfInterest, effectiveMonths, compoundingFactor);
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

    //DAYS-DRIVEN CALC
    public static FixedDepositProto.FixedDeposit calcAmountAndInterest(double principal, double rateOfInterest, long days) {
        double amount, interest;
        amount = calcAmount(principal, rateOfInterest, days, 4);
        interest = amount - principal;

        FixedDepositProto.FixedDeposit.Builder fdBuilder = FixedDepositProto.FixedDeposit.newBuilder();
        fdBuilder.setExpectedInterest(interest);
        fdBuilder.setExpectedAmount(amount);
        return fdBuilder.build();
    }

    private static double calcAmount(double principal, double rateOfInterest, long days, int compoundingFactor) {
        if (days == 0 || compoundingFactor == 0) return 0.0;
        LOGGER.info("Registered request for calculating amount for {} at {}% for {} days at compounding factor {}", principal, rateOfInterest, days, compoundingFactor);
        double amount = principal * Math.pow(1 + rateOfInterest / (compoundingFactor * 100.0), compoundingFactor * (days / 365.0));
        LOGGER.info("Subsequent amount calc: {}", amount);
        return amount;
    }

    public static void calcAmounts(double principal, double rateOfInterest, int startDate, int endDate, int compoundingFactor, Map<Integer, Double> dateAmountMap) {
        if (compoundingFactor == 0) return;
        int days = 1;
        System.out.println("Calculating amount for " + principal + ", " + rateOfInterest + ", " + startDate + ", " + endDate); // todo - comment post testing
        LocalDate dt = LocalDateUtil.generateLocalDateObject(startDate);
        LocalDate dtEnd = LocalDateUtil.generateLocalDateObject(endDate).plusDays(1);
        while (dt.isBefore(dtEnd)) {
            int date = LocalDateUtil.generateIntegralDate(dt);
            double amount;
            if (days <= 365) {
                amount = principal + (principal * rateOfInterest * days) / (100.0 * 365);
            } else {
                amount = principal * Math.pow(1 + rateOfInterest / (compoundingFactor * 100.0), compoundingFactor * (days / 365.0));
            }
            // System.out.println("date: " + date + ", amount: " + amount); // todo - comment post testing
            dateAmountMap.compute(date, (k, v) -> sanitizeDouble(v) + amount);
            days++;
            dt = dt.plusDays(1);
        }
    }

    public static int getCompoundingFactor(FixedDepositProto.AccountType accountType) {
        if (accountType == FixedDepositProto.AccountType.IND) return 4;
        else if (accountType == FixedDepositProto.AccountType.TFSA || accountType == FixedDepositProto.AccountType.NR || accountType == FixedDepositProto.AccountType.FHSA)
            return 1;
        return 1;
    }

    private static int computeHighestDividingTimePeriod(int months, int dividingFactor) {
        return ((int) Math.floor(months / (dividingFactor * 1.0))) * dividingFactor;
    }

    private static double sanitizeDouble(Double value) {
        return value == null ? 0.0 : value;
    }
}
