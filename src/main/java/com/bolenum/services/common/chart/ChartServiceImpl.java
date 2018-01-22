package com.bolenum.services.common.chart;

import com.bolenum.model.Currency;
import com.bolenum.repo.order.book.TradeRepository;
import com.bolenum.services.admin.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ChartServiceImpl implements ChartService {
    private static final String[] SUPPORTED_RESOLUTIONS = new String[]{"60", "180", "360", "720", "D", "W", "M"};
    private static Logger logger = LoggerFactory.getLogger(ChartServiceImpl.class);
    @Autowired
    private CurrencyService currencyService;

    @Autowired

    private TradeRepository tradeRepository;

    @Override
    public Map<String, Object> getSymbolInfo(Long marketId, Long pairId) {
        Currency marketCurrency = currencyService.findCurrencyById(marketId);
        Currency pairCurrency = currencyService.findCurrencyById(pairId);
        Map<String, Object> result = new HashMap<>();
        if (marketCurrency != null && pairCurrency != null) {
            result.put("name", pairCurrency.getCurrencyAbbreviation() + " / " + marketCurrency.getCurrencyAbbreviation());
           // result.put("exchange-traded", marketCurrency.getCurrencyAbbreviation());
            result.put("exchange-listed", marketCurrency.getCurrencyAbbreviation());
            result.put("timezone", "Asia/Kolkata");
            result.put("minmovement", 1);
            result.put("minmovement2", 0);
            result.put("pointvalue", 1);
            result.put("session", "0001-2330");
            result.put("has_intraday", true);
            result.put("has_daily", true);
            result.put("has_weekly_and_monthly", true);
            result.put("has_no_volume", false);
            result.put("description", "Bolenum Exchange " + pairCurrency.getCurrencyAbbreviation() + "/" + marketCurrency.getCurrencyAbbreviation());
            result.put("type", "stock");
            result.put("supported_resolutions", SUPPORTED_RESOLUTIONS);
            result.put("pricescale", 100);
            result.put("ticker", marketCurrency.getCurrencyAbbreviation());
        }
        return result;
    }

    @Override
    public Map<String, Object> getChartConfig() {
        Map<String, Object> map = new HashMap<>();
        map.put("supports_search", true);
        map.put("supports_group_request", false);
        map.put("supports_marks", false);
        map.put("supports_timescale_marks", false);
        map.put("supported_resolutions", SUPPORTED_RESOLUTIONS);
        return map;
    }

    @Override
    public Map<String, Object> getHistroyInfo(Long marketId, Long pairId, String fromDate, String toDate, String resolution) {
        Currency marketCurrency = currencyService.findCurrencyById(marketId);
        Currency pairCurrency = currencyService.findCurrencyById(pairId);

        Map<String, Object> result = new HashMap<>();

        Long start = Long.parseLong(fromDate);
        long stLong = start.longValue() * 1000;
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(stLong);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(startDate.getTime());

        Long end = Long.parseLong(toDate);
        long endLong = end.longValue() * 1000;
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(endLong);
        SimpleDateFormat endSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strendDate = endSdf.format(endDate.getTime());
        BigDecimal timeInterval = BigDecimal.valueOf(0);
        // Get open close high low according to time interval
        Map<String, Object> historyAndTimeinterval = getHistoryFilterByTimeInterval(marketCurrency.getCurrencyId(), pairCurrency.getCurrencyId(), strDate, strendDate, resolution);
        List<Object[]> historyObj = (List<Object[]>) historyAndTimeinterval.get("historyObj");

        timeInterval = (BigDecimal) historyAndTimeinterval.get("timeInterval");

        // Get existance and non-existance data according to charting library api
        Map<String, Object> mainData = getData(historyObj, marketCurrency, pairCurrency, fromDate, toDate, resolution);
        result.put("history", mainData);
        return result;
    }

    private Map<String, Object> getData(List<Object[]> historyObj, Currency marketCurrency, Currency pairCurrency, String fromDate, String toDate, String resolution) {
        // Build json object according charting library
        Map<String, Object> mainData = getJsonAccordingToChartApi(historyObj);
        List<BigDecimal> timestamp = (List<BigDecimal>) mainData.get("t");
        mainData.put("t", mainData.get("t"));
        mainData.put("v", mainData.get("v"));
        mainData.put("o", mainData.get("o"));
        mainData.put("h", mainData.get("h"));
        mainData.put("l", mainData.get("l"));
        mainData.put("c", mainData.get("c"));

        if (!timestamp.isEmpty()) {
            /*if (timestamp.size() >= 1 && timestamp.size() <= 5) {
                Trade tradeChartObj = tradeRepository.getSecondLastPrice(Long.parseLong(marketId));
                if (tradeChartObj != null) {
                    stLong = tradeChartObj.getBidTime().getTime() - (86400);
                    mainData = singleRecord(marketCurrency, pairCurrency, fromDate, toDate, resolution);
                }
            }*/
            mainData.put("s", "ok");
        } else {
            mainData.put("s", "no_data");
        }
        return mainData;
    }

    // If trade chart have only one entry in database
    @SuppressWarnings("unchecked")
    public Map<String, Object> singleRecord(Currency marketCurrency, Currency pairCurrency, String from, String to, String resolution) {
        Long start = new Long(from);
        long stLong = start.longValue() * 1000;
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(stLong);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(startDate.getTime());
        Long end = new Long(to);
        long endLong = end.longValue() * 1000;
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(endLong);
        SimpleDateFormat endSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strendDate = endSdf.format(endDate.getTime());
        BigDecimal timeInterval = new BigDecimal(300);

        // Get open high low close according to time interval
        Map<String, Object> historyAndTimeinterval = getHistoryFilterByTimeInterval(marketCurrency.getCurrencyId(), pairCurrency.getCurrencyId(), strDate, strendDate, resolution);
        List<Object[]> historyObj = (List<Object[]>) historyAndTimeinterval.get("historyObj");
        timeInterval = (BigDecimal) historyAndTimeinterval.get("timeInterval");

        // Build json object according charting library
        Map<String, Object> mainData = getJsonAccordingToChartApi(historyObj);
        return mainData;
    }

    private Map<String, Object> getJsonAccordingToChartApi(List<Object[]> historyObj) {
        List<BigDecimal> timestamp = new ArrayList<>();
        List<BigDecimal> volume = new ArrayList<>();
        List<BigDecimal> open = new ArrayList<>();
        List<BigDecimal> high = new ArrayList<>();
        List<BigDecimal> low = new ArrayList<>();
        List<BigDecimal> close = new ArrayList<>();
        for (Object[] current : historyObj) {
            BigDecimal currTimestamp = new BigDecimal(current[0].toString());
            BigDecimal currVolume = new BigDecimal(current[1].toString());
            BigDecimal currOpen = new BigDecimal(current[2].toString());
            BigDecimal currHigh = new BigDecimal(current[3].toString());
            BigDecimal currLow = new BigDecimal(current[4].toString());
            BigDecimal currClose = new BigDecimal(current[5].toString());
            timestamp.add(currTimestamp);
            volume.add(currVolume);
            open.add(currOpen);
            high.add(currHigh);
            low.add(currLow);
            close.add(currClose);
        }
        //////////////////////////set data //////////////////////////////
        Map<String, Object> mainData = new HashMap<>();
        mainData.put("t", timestamp);
        mainData.put("v", volume);
        mainData.put("o", open);
        mainData.put("h", high);
        mainData.put("l", low);
        mainData.put("c", close);
        return mainData;
    }

    private Map<String, Object> getHistoryFilterByTimeInterval(Long marketCurrency, Long pairCurrency, String strDate, String strendDate, String resolution) {
        Map<String, Object> result = new HashMap<>();
        List<Object[]> historyObj;
        BigDecimal interval = BigDecimal.valueOf(0);
        switch (resolution) {
            case "60":
                interval = new BigDecimal(3600);
                historyObj = tradeRepository.getTradeHistory(interval, marketCurrency, pairCurrency, strDate, strendDate);
                break;
            case "180":
                interval = new BigDecimal(10800);
                historyObj = tradeRepository.getTradeHistory(interval, marketCurrency, pairCurrency, strDate, strendDate);
                break;
            case "360":
                interval = new BigDecimal(21600);
                historyObj = tradeRepository.getTradeHistory(interval, marketCurrency, pairCurrency, strDate, strendDate);
                break;
            case "720":
                interval = new BigDecimal(1800);
                historyObj = tradeRepository.getTradeHistory(interval, marketCurrency, pairCurrency, strDate, strendDate);
                break;
            case "D":
                interval = new BigDecimal(86400);
                historyObj = tradeRepository.getTradeHistory(interval, marketCurrency, pairCurrency, strDate, strendDate);
                break;
            case "W":
                interval = new BigDecimal(604800);
                historyObj = tradeRepository.getTradeHistory(interval, marketCurrency, pairCurrency, strDate, strendDate);
                break;
//            case "M":
//                interval = new BigDecimal(18144000);
//                historyObj = tradeRepository.getTradeHistoryMonth(marketCurrency, pairCurrency, strDate, strendDate);
//                break;
            default:
                historyObj = tradeRepository.getTradeHistory(interval, marketCurrency, pairCurrency, strDate, strendDate);
                break;
        }
        result.put("historyObj", historyObj);
        result.put("timeInterval", interval);
        return result;
    }


}
