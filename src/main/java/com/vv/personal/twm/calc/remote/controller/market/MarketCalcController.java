package com.vv.personal.twm.calc.remote.controller.market;

import com.vv.personal.twm.calc.remote.controller.bank.BankController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Vivek
 * @since 2026-01-16
 */
@Path("/calc/market")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MarketCalcController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketCalcController.class);

}
