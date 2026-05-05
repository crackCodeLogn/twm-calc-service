package com.vv.personal.twm.calc.remote.controller.market;

import com.vv.personal.twm.calc.remote.controller.bank.BankController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Vivek
 * @since 2026-01-16
 */
@RestController("MarketCalcController")
@RequestMapping("/calc/market")
public class MarketCalcController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketCalcController.class);

}
