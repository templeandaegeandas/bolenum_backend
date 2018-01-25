package com.bolenum.services.common.chart;

import com.bolenum.model.Currency;

import java.util.Map;

public interface ChartService {
    Map<String, Object> getSymbolInfo(Currency marketId, Currency pairId);

    Map<String, Object> getChartConfig();

    Map<String, Object> getHistroyInfo(Long marketId, Long pairId, String fromDate, String toDate, String resolution);
}
