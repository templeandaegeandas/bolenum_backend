package com.bolenum.services.common.chart;

import com.bolenum.model.Currency;

import java.util.Map;

public interface ChartService {
	
    /**
     * This method is use to get Symbol Info
     * @param marketId
     * @param pairId
     * @return
     */
    Map<String, Object> getSymbolInfo(Currency marketId, Currency pairId);

    /**
     * This method is use to get Chart Config
     * @return
     */
    Map<String, Object> getChartConfig();

    /**
     * This method is use to get Histroy Info
     * @param marketId
     * @param pairId
     * @param fromDate
     * @param toDate
     * @param resolution
     * @return
     */
    Map<String, Object> getHistroyInfo(Long marketId, Long pairId, String fromDate, String toDate, String resolution);
}
