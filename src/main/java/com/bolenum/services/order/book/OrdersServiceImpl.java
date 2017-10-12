package com.bolenum.services.order.book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.enums.OrderStandard;
import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrdersRepository;

/**
 * 
 * @author Vishal Kumar
 * @date 06-Oct-2017
 *
 */
@Service
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private OrderAsyncService orderAsyncServices;

	public static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

	List<Orders> ordersList = new ArrayList<>();

	List<Trade> tradeList = new ArrayList<>();

	@Override
	public Boolean processOrder(Orders orders) {
		if (orders.equals(OrderStandard.MARKET)) {
			logger.debug("Processing market order");
			return processMarketOrder(orders);
		} else {
			logger.debug("Processing limit order");
			return processLimitOrder(orders);
		}
	}

	@Override
	public Orders deleteOrder(Long ordersId) {
		Orders orders = ordersRepository.findOne(ordersId);
		orders.setDeletedOn(new Date());
		orders.setOrderStatus(OrderStatus.CANCELLED);
		return ordersRepository.save(orders);
	}

	@Override
	public Boolean processMarketOrder(Orders orders) {
		Boolean processed = false;
		OrderType orderType = orders.getOrderType();
		Long pairId = orders.getPairId();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		if (orderType.equals(OrderType.BUY)) {
			List<Orders> buyOrderList = ordersRepository.findByOrderTypeAndOrderStatusAndPairIdOrderByPriceAsc(
					OrderType.SELL, OrderStatus.SUBMITTED, pairId);
			while (buyOrderList.size() > 0 && (remainingVolume > 0)) {
				logger.debug("inner buy while");
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		} else {
			List<Orders> sellOrderList = ordersRepository.findByOrderTypeAndOrderStatusAndPairIdOrderByPriceDesc(
					OrderType.BUY, OrderStatus.SUBMITTED, pairId);
			while (sellOrderList.size() > 0 && (remainingVolume > 0)) {
				logger.debug("inner sell while");
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		}
		orderAsyncServices.saveOrder(ordersList);
		return processed;
	}

	@Override
	public Boolean processLimitOrder(Orders orders) {
		Boolean processed = false;
		OrderType orderType = orders.getOrderType();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		Double price = orders.getPrice();
		Long pairId = orders.getPairId();
		logger.debug("Order type is equal with buy: {}", orderType.equals(OrderType.BUY));
		if (orderType.equals(OrderType.BUY)) {
			List<Orders> buyOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndPairIdAndPriceLessThanEqualOrderByPriceAsc(OrderType.BUY,
							OrderStatus.SUBMITTED, pairId, price);
			while (buyOrderList.size() > 0 && (remainingVolume > 0) && (price >= getBestBuy(buyOrderList))) {
				logger.debug("inner buy while");
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		} else {
			List<Orders> sellOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndPairIdAndPriceGreaterThanEqualOrderByPriceDesc(OrderType.BUY,
							OrderStatus.SUBMITTED, pairId, price);
			while (sellOrderList.size() > 0 && (remainingVolume > 0) && (price <= getBestSell(sellOrderList))) {
				logger.debug("inner sell while");
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		}
		orderAsyncServices.saveOrder(ordersList);
		return processed;
	}

	@Override
	public Double processOrderList(List<Orders> ordersList, Double remainingVolume, Orders orders) {
		OrderType orderType = orders.getOrderType();
		Long buyerId, sellerId;
		while ((ordersList.size() > 0) && (remainingVolume > 0)) {
			logger.debug("inner proccessing while");
			Double qtyTraded;
			Orders matchedOrder = matchedOrder(ordersList);
			if (remainingVolume < matchedOrder.getVolume()) {
				qtyTraded = remainingVolume;
				matchedOrder.setVolume(matchedOrder.getVolume() - remainingVolume);
				ordersList.add(matchedOrder);
				remainingVolume = 0.0;
			} else {
				qtyTraded = matchedOrder.getVolume();
				remainingVolume -= qtyTraded;
				removeOrderFromList(ordersList);
				matchedOrder.setVolume(0.0);
				matchedOrder.setOrderStatus(OrderStatus.COMPLETED);
				ordersList.add(matchedOrder);
				logger.debug("matching buy/sell completed");
			}
			if (orderType.equals(OrderType.BUY)) {
				buyerId = orders.getUserId();
				sellerId = matchedOrder.getUserId();
			} else {
				buyerId = matchedOrder.getUserId();
				sellerId = orders.getUserId();
			}
			Trade trade = new Trade(matchedOrder.getPrice(), qtyTraded, buyerId, sellerId, OrderStandard.LIMIT);
			tradeList.add(trade);
			logger.debug("saving trade ompleted");
		}
		orderAsyncServices.saveTrade(tradeList);
		return remainingVolume;
	}

	// public Double totalVolume(String pair) {
	// return orderRepository.getSumVolumeByPair(pair);
	// }

	@Override
	public Page<Orders> getOrdersListByPair(Long pairId, OrderType orderType) {
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, "price");
		Page<Orders> orderBook = ordersRepository.findByPairIdAndOrderType(pairId, orderType, pageRequest);
		return orderBook;
	}

	@Override
	public Double getBestBuy(List<Orders> buyOrderList) {
		Double bestBuy = buyOrderList.get(0).getPrice();
		for (int i = 0; i < buyOrderList.size() - 1; i++) {
			if (bestBuy > buyOrderList.get(i + 1).getPrice()) {
				bestBuy = buyOrderList.get(i + 1).getPrice();
			}
		}
		return bestBuy;
	}

	@Override
	public Double getWorstBuy(List<Orders> buyOrderList) {
		Double wrostBuy = buyOrderList.get(0).getPrice();
		for (int i = 0; i < buyOrderList.size() - 1; i++) {
			if (wrostBuy < buyOrderList.get(i + 1).getPrice()) {
				wrostBuy = buyOrderList.get(i + 1).getPrice();
			}
		}
		return wrostBuy;
	}

	@Override
	public Double getBestSell(List<Orders> sellOrderList) {
		Double bestSell = null;
		for (int i = 0; i < sellOrderList.size() - 1; i++) {
			bestSell = sellOrderList.get(0).getPrice();
			if (bestSell < sellOrderList.get(i + 1).getPrice()) {
				bestSell = sellOrderList.get(i + 1).getPrice();
			}
		}
		return bestSell;
	}

	@Override
	public Double getWorstSell(List<Orders> sellOrderList) {
		Double wrostSell = sellOrderList.get(0).getPrice();
		for (int i = 0; i < sellOrderList.size() - 1; i++) {
			if (wrostSell > sellOrderList.get(i + 1).getPrice()) {
				wrostSell = sellOrderList.get(i + 1).getPrice();
			}
		}
		return wrostSell;
	}

	@Override
	public Long countOrderByOrderTypeWithGreaterAndLesThan(OrderType orderType, Long pairId, Double price) {
		if (orderType.equals("BUY")) {
			return ordersRepository.countOrderByOrderTypeAndPriceGreaterThan(orderType, pairId, price);
		} else {
			return ordersRepository.countOrderByOrderTypeAndPriceLessThan(orderType, pairId, price);

		}
	}

	@Override
	public Long countOrderByOrderType(OrderType orderType) {
		return ordersRepository.countOrderByOrderType(orderType);
	}

	@Override
	public Orders matchedOrder(List<Orders> ordersList) {
		Orders matchedOrder = ordersList.get(0);
		return matchedOrder;
	}

	@Override
	public void removeOrderFromList(List<Orders> ordersList) {
		ordersList.remove(0);
	}
}
