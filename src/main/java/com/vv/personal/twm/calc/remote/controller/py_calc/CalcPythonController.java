package com.vv.personal.twm.calc.remote.controller.py_calc;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.vv.personal.twm.artifactory.generated.equitiesMarket.MarketDataProto;
import com.vv.personal.twm.calc.remote.feign.CalcPythonEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Vivek
 * @since 2026-01-16
 */
@RestController("CalcPythonController")
@RequestMapping("/calc/engine/py")
public class CalcPythonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalcPythonController.class);

    @Autowired
    private CalcPythonEngine calcPythonEngine;

    @GetMapping("/test")
    public String test() {
        double vix = 28.0;
        double maxWeight = .35;
        double minYield = .03;
        double targetBeta;
        double maxVol;
        double maxPe;
        String riskMode;

        if (vix > 25) {
            riskMode = "OPPORTUNISTIC (BUYING THE DIP)";
            targetBeta = 1.15;
            maxVol = 0.18;
            maxPe = 18.0;
        } else {
            riskMode = "CONSERVATIVE (HARVESTING PnL)";
            targetBeta = 0.90;
            maxVol = 0.10;
            maxPe = 22.0;
        }

        MarketDataProto.Portfolio requestPortfolio = MarketDataProto.Portfolio.newBuilder()
                .addInstruments(
                        MarketDataProto.Instrument.newBuilder()
                                .putMetaData("vix", String.valueOf(vix))
                                .putMetaData("risk_mode", riskMode)
                                .putMetaData("target_beta", String.valueOf(targetBeta))
                                .putMetaData("max_vol", String.valueOf(maxVol))
                                .putMetaData("max_pe", String.valueOf(maxPe))
                                .putMetaData("max_weight", String.valueOf(maxWeight))
                                .putMetaData("min_yield", String.valueOf(minYield))
                                .build()
                )
                .addInstruments(generateImnt("Tech Growth", 20000, 1.5, .005, .18, .28, 45))
                .addInstruments(generateImnt("Blue Chip", 20000, 1.1, .025, .11, .18, 18))
                .addInstruments(generateImnt("Utility Co", 20000, .55, .045, .06, .12, 14))
                .addInstruments(generateImnt("Consumer Staple", 20000, .45, .035, .07, .10, 21))
                .addInstruments(generateImnt("Bank Stock", 20000, .90, .050, .09, .15, 10))
                .setCorrelationMatrix(getCorrelationMatrix(generateTestCorrelationMatrix()))
                .build();

        System.out.println(requestPortfolio);
        return calcPythonEngine.calcPortfolioOptimizer(requestPortfolio);

    }

    private MarketDataProto.Instrument generateImnt(String symbol, double capital,
                                                    double beta, double imntYield, double imntReturn,
                                                    double stdDev, double peRatio) {
        return MarketDataProto.Instrument.newBuilder()
                .setTicker(
                        MarketDataProto.Ticker.newBuilder()
                                .setSymbol(symbol)
                                .addData(MarketDataProto.Value.newBuilder().setPrice(capital).build())
                                .build()
                )
                .setBeta(beta)
                .setDividendYield(imntYield)
                .putMetaData("return", String.valueOf(imntReturn))
                .putMetaData("std_dev", String.valueOf(stdDev))
                .putMetaData("pe_ratio", String.valueOf(peRatio))
                .build();
    }

    private Optional<Table<String, String, Double>> generateTestCorrelationMatrix() {
        Table<String, String, Double> testCorrelationMatrix = HashBasedTable.create();
        String tg = "Tech Growth";
        String bc = "Blue Chip";
        String uc = "Utility Co";
        String cs = "Consumer Staple";
        String bs = "Bank Stock";

        populate(testCorrelationMatrix, tg, tg, 1.0);
        populate(testCorrelationMatrix, bc, bc, 1.0);
        populate(testCorrelationMatrix, uc, uc, 1.0);
        populate(testCorrelationMatrix, cs, cs, 1.0);
        populate(testCorrelationMatrix, bs, bs, 1.0);

        populate(testCorrelationMatrix, tg, bc, .7);
        populate(testCorrelationMatrix, tg, uc, .1);
        populate(testCorrelationMatrix, tg, cs, .2);
        populate(testCorrelationMatrix, tg, bs, .4);

        populate(testCorrelationMatrix, bc, uc, .2);
        populate(testCorrelationMatrix, bc, cs, .3);
        populate(testCorrelationMatrix, bc, bs, .5);

        populate(testCorrelationMatrix, uc, cs, .6);
        populate(testCorrelationMatrix, uc, bs, .1);

        populate(testCorrelationMatrix, cs, bs, .2);
        return Optional.of(testCorrelationMatrix);
    }

    private void populate(Table<String, String, Double> matrix, String key1, String key2, double val) {
        matrix.put(key1, key2, val);
        matrix.put(key2, key1, val);
    }

    private MarketDataProto.CorrelationMatrix getCorrelationMatrix(
            Optional<Table<String, String, Double>> optionalCorrelationMatrix) {
        if (optionalCorrelationMatrix.isEmpty()) {
            LOGGER.warn("Supplied optional correlation matrix is empty");
            return MarketDataProto.CorrelationMatrix.newBuilder().build();
        }
        Table<String, String, Double> correlationMatrix = optionalCorrelationMatrix.get();

        List<MarketDataProto.CorrelationCell> correlationCells =
                new ArrayList<>(correlationMatrix.size());
        correlationMatrix
                .rowMap()
                .forEach(
                        (imntRow, imntColValMap) ->
                                imntColValMap.forEach(
                                        (imntCol, imntValMap) ->
                                                correlationCells.add(
                                                        generateCorrelationCell(imntRow, imntCol, imntValMap))));
        return MarketDataProto.CorrelationMatrix.newBuilder().addAllEntries(correlationCells).build();
    }

    private MarketDataProto.CorrelationCell generateCorrelationCell(
            String rowKey, String columnKey, Double value) {
        return MarketDataProto.CorrelationCell.newBuilder()
                .setImntRow(rowKey)
                .setImntCol(columnKey)
                .setValue(value)
                .build();
    }
}
