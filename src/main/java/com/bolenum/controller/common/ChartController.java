package com.bolenum.controller.common;

import com.bolenum.constant.UrlConstant;
import com.bolenum.services.common.chart.ChartService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = UrlConstant.BASE_USER_URI_V1)
@Api(value = "Chart Controller")
public class ChartController {

    @Autowired
    private ChartService chartService;

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
    @RequestMapping(value = UrlConstant.SYMBOLE_INFO, method = RequestMethod.GET)
    public ResponseEntity<Object> chartSymboleInfo(@RequestParam("symbol") String marketId) {
        Map<String, Object> map = new HashMap<>();
//        if (marketId == null || pairId == null) {
//            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
//        }
        map = chartService.getSymbolInfo(1l,2l);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

   // @RequestMapping(value = UrlConstant.HISTORY_INFO, method = RequestMethod.GET)
    /**
     * This method is use for history information of chart.
     * @param marketId
     * @param fromDate
     * @param toDate
     * @param resolution
     */
    public ResponseEntity<Object> chartHistoryInfo(@RequestParam("symbol") String marketId
                                                   , @RequestParam("from") String fromDate, @RequestParam("to") String toDate, @RequestParam(name = "resolution", required = false, defaultValue = "60") String resolution) {
        Map<String, Object> map = new HashMap<>();
//        if (marketId == null || pairId == null) {
//            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
//        }
        map = chartService.getHistroyInfo(1l, 2l, fromDate, toDate, resolution);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * This method is use for trade history of chart.
     * @param symbol
     * @param from
     * @param to
     * @param resolution
     */
    @RequestMapping(value = UrlConstant.HISTORY_INFO, method = RequestMethod.GET)
	ResponseEntity<Object> tradeChartHistory(@RequestParam String symbol,@RequestParam String from,@RequestParam String to,@RequestParam String resolution) {
		///Map<String, Object> result = new HashMap<String,Object>();
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<Object> mapType = new TypeReference<Object>() {};
		InputStream is = TypeReference.class.getResourceAsStream("/json/tradeHistory.json");
		Object tradeHistoryList=new Object();
		try {
			tradeHistoryList = mapper.readValue(is, mapType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//result.put("history", tradeHistoryList);
        return new ResponseEntity<>(tradeHistoryList, HttpStatus.OK);
	}

}
