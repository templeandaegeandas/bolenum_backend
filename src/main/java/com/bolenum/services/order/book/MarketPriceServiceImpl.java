/**
 * 
 */
package com.bolenum.services.order.book;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.bolenum.model.Currency;
import com.bolenum.repo.common.CurrencyRepo;

/**
 * @author chandan kumar singh
 * @date 11-Oct-2017
 */
@Service
@Transactional
public class MarketPriceServiceImpl implements MarketPriceService {
	private Logger logger = LoggerFactory.getLogger(MarketPriceServiceImpl.class);
	/*
	 * @Autowired private MarketPriceRepo marketPriceRepo;
	 */
	@Autowired
	private CurrencyRepo currencyRepo;
	private List<Currency> list;

	/*
	 * @Override public MarketPrice savePrice(MarketPrice marketPrice) { return
	 * marketPriceRepo.saveAndFlush(marketPrice); }
	 */

	private void loadCurrecy() {
		list = currencyRepo.findAll();
	}

	@Override
	public void priceFromCoinMarketCap() {
		loadCurrecy();
		// logger.debug("every 20 sec :{}", new Date());
		String url = "https://api.coinmarketcap.com/v1/ticker/?start=0&limit=600";
		RestTemplate restTemplate = new RestTemplate();
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
		String res = restTemplate.getForObject(builder.toUriString(), String.class);
		String symbol = null, price_usd = null, price_btc = null;
		try {
			JSONArray array = new JSONArray(res);
			for (int n = 0; n < array.length(); n++) {
				JSONObject object = array.getJSONObject(n);
				if (!object.isNull("symbol") && !object.get("symbol").equals(null)) {
					symbol = (String) object.get("symbol");
				}
				if (!object.isNull("price_usd") && !object.get("price_usd").equals(null)) {
					price_usd = (String) object.get("price_usd");
				}
				if (!object.isNull("price_btc") && !object.get("price_btc").equals(null)) {
					price_btc = (String) object.get("price_btc");
				}
				Currency currency = searchSymbol(symbol);
				if (symbol != null && currency != null && price_btc != null && price_usd != null) {
					saveMarketPrice(currency, price_btc, price_usd);
				}
			}
		} catch (JSONException e) {
			logger.error("json parse error: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	private Currency searchSymbol(String symbol) {
		Predicate<Currency> predicate = c -> c.getCurrencyAbbreviation().equalsIgnoreCase(symbol);
		Optional<Currency> currencies = list.stream().filter(predicate).findFirst();
		if (currencies.isPresent()) {
			return currencies.get();
		}
		return null;
	}

	private void saveMarketPrice(Currency currency, String priceBtc, String priceUsd) {
		currency.setPriceBTC(Double.valueOf(priceBtc));
		currency.setPriceUSD(Double.valueOf(priceUsd));
		currencyRepo.save(currency);
	}

	/*
	 * @Override public MarketPrice findByCurrency(Currency currency) { return
	 * marketPriceRepo.findByCurrency(currency); }
	 * 
	 * @Override public MarketPrice findByCurrencyId(String
	 * currencyAbbreviation) { return
	 * marketPriceRepo.findByCurrencyCurrencyAbbreviation(currencyAbbreviation);
	 * }
	 */
}
