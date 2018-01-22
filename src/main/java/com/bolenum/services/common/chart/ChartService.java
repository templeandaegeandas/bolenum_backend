package com.bolenum.services.common.chart;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface ChartService {
    Map<String, Object> getSymbolInfo(Long marketId, Long pairId);

    Map<String, Object> getChartConfig();

    Map<String, Object> getHistroyInfo(Long marketId, Long pairId, String fromDate, String toDate, String resolution);
}
