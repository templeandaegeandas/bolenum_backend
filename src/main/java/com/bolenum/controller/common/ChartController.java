package com.bolenum.controller.common;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.model.Currency;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.common.chart.ChartService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
@Api(value = "Chart Controller")
public class ChartController {

    private static final Logger logger = LoggerFactory.getLogger(ChartController.class);
    @Autowired
    private ChartService chartService;

    @Autowired
    private CurrencyService currencyService;

    /**
     * to configure the chart
     */
    @RequestMapping(value = UrlConstant.CONFIG, method = RequestMethod.GET)
    public ResponseEntity<Object> configChart() {
        Map<String, Object> map = chartService.getChartConfig();
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
  
   /**
     * This method is use for symbol information of chart.
     * @param marketId
     */
    @RequestMapping(value = UrlConstant.SYMBOLE, method = RequestMethod.GET)
    public ResponseEntity<Object> chartSymboleInfo(@RequestParam("symbol") String symbol) {
        logger.debug("symbole: {}", symbol);
        Map<String, Object> map;
        if (symbol.contains("BE")) {
            String[] ids = symbol.split("BE");
            long marketId = Long.parseLong(ids[0]);
            long pairId = Long.parseLong(ids[1]);
            Currency marketCurrency = currencyService.findCurrencyById(marketId);
            Currency pairCurrency = currencyService.findCurrencyById(pairId);
            map = chartService.getSymbolInfo(marketCurrency, pairCurrency);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        return new ResponseEntity<>(Optional.empty(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

     /**
     * This method is use for history information of chart.
     * @param marketId
     * @param fromDate
     * @param toDate
     * @param resolution
     */
    @RequestMapping(value = UrlConstant.HISTORY, method = RequestMethod.GET)
    public ResponseEntity<Object> chartHistoryInfo(@RequestParam("symbol") String symbol
            , @RequestParam("from") String fromDate, @RequestParam("to") String toDate, @RequestParam(name = "resolution", required = false, defaultValue = "60") String resolution) {
        Map<String, Object> map;
        logger.debug("history req, symbol: {}, from: {}, to: {}, resolution: {}", symbol, fromDate, toDate, resolution);
        if (symbol.contains("BE")) {
            String[] ids = symbol.split("BE");
            long marketId = Long.parseLong(ids[0]);
            long pairId = Long.parseLong(ids[1]);
            Currency marketCurrency = currencyService.findCurrencyById(marketId);
            Currency pairCurrency = currencyService.findCurrencyById(pairId);
            if (marketCurrency != null && pairCurrency != null) {
                map = chartService.getHistroyInfo(marketId, pairId, fromDate, toDate, resolution);
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                logger.error("market currency: {} , pair currency: {}", marketCurrency, pairCurrency);
            }
        }
        return new ResponseEntity<>(Optional.empty(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}