package com.bolenum.services.order.book;

import java.util.List;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.bolenum.constant.UrlConstant;
import com.bolenum.controller.user.FiatOrderController;
import com.bolenum.enums.MessageType;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.repo.order.book.TradeRepository;
import com.bolenum.services.admin.CurrencyPairService;

@Service
public class OrderAsyncServicesImpl implements OrderAsyncService {
	
	private Logger logger = LoggerFactory.getLogger(FiatOrderController.class);

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private TradeRepository tradeRepository;
	
	@Autowired
	private CurrencyPairService currencyPairService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Override
	public List<Orders> saveOrder(List<Orders> ordersList) {
		return ordersRepository.save(ordersList);
	}

	@Override
	public List<Trade> saveTrade(List<Trade> tradeList) {
		return tradeRepository.save(tradeList);
	}

	@Override
	public Orders saveOrder(Orders orders) {
		return ordersRepository.save(orders);
	}

	@Override
	public Trade saveTrade(Trade trade) {
		return tradeRepository.save(trade);
	}
	
	@Async
	@Override
	public Future<Boolean> saveLastPrice(long pairId, Double price) {
		CurrencyPair currencyPair = currencyPairService.findByPairId(pairId);
		if(currencyPair.getLastPrice() == null || currencyPair.getLastPrice() > price) {
			currencyPair.setLastPrice(price);
			currencyPairService.saveCurrencyPair(currencyPair);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("MARKET_UPDATE", MessageType.MARKET_UPDATE);
				jsonObject.put("pairId", pairId);
				jsonObject.put("price", price);
			} catch (JSONException e) {
				logger.error("Error in sending websocket message: {}", e);
			}
			simpMessagingTemplate.convertAndSend(UrlConstant.WS_BROKER + UrlConstant.WS_LISTNER_MARKET, jsonObject.toString());
		}
		return new AsyncResult<>(true);
	}
}