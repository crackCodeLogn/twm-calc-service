package com.vv.personal.twm.calc.core;

import com.vv.personal.twm.artifactory.generated.deposit.FixedDepositProto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.vv.personal.twm.calc.constants.Constants.DELTA_PRECISION_6;
import static com.vv.personal.twm.calc.core.AmountInterestCalculator.calcAmountAndInterest;
import static org.junit.Assert.assertEquals;

/**
 * @author Vivek
 * @since 03/02/21
 */
@RunWith(JUnit4.class)
public class AmountInterestCalculatorTest {

    @Test
    public void testCalcAmountAndInterest() {
        FixedDepositProto.FixedDeposit fixedDeposit = calcAmountAndInterest(100000, 7, 35, 1);
        System.out.println(fixedDeposit);
        assertEquals(22457.463041, fixedDeposit.getExpectedInterest(), DELTA_PRECISION_6);
        assertEquals(122457.463041, fixedDeposit.getExpectedAmount(), DELTA_PRECISION_6);
    }
}