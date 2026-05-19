package com.vv.personal.twm.calc.remote.feign;

import com.vv.personal.twm.artifactory.generated.equitiesMarket.MarketDataProto;
import com.vv.personal.twm.ping.remote.feign.PingFeign;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Vivek
 * @since 2026-01-16
 */
@RegisterRestClient(configKey = "twm-calc-py-engine")
public interface CalcPythonEngine extends PingFeign {

    @POST
    @Path("/calc/portfolio/optimizer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    String calcPortfolioOptimizer(MarketDataProto.Portfolio requestPortfolio);
}
