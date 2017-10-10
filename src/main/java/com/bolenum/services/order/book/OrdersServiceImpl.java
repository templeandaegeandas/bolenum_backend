package com.bolenum.services.order.book;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.constant.OrderStandard;
import com.bolenum.constant.OrderStatus;
import com.bolenum.constant.OrderType;
import com.bolenum.model.orders.book.Orders;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrdersRepository;
import com.bolenum.repo.order.book.TradeRepository;

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
	private TradeRepository tradeRepository;
	
	public static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);
	
	@Override
	public Orders addNewOrder(Orders orders) {
		return ordersRepository.save(orders);
	}

	@Override
	public Orders deleteOrder(Long ordersId) {
		Orders orders = ordersRepository.findOne(ordersId);
		orders.setDeletedOn(new Date());
		orders.setOrderStatus(OrderStatus.CANCELLED);
		return ordersRepository.save(orders);
	}

	@Override
	public Orders UpdateOrderVolume(Long ordersId, Double volume) {
		Orders orders = ordersRepository.findOne(ordersId);
		orders.setVolume(volume);
		return ordersRepository.save(orders);
	}

	@Override
	public Orders processMarketOrder(Orders orders) {
		OrderType orderType = orders.getOrderType();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		if (orderType.equals(OrderType.BUY)) {
			List<Orders> buyOrderList = ordersRepository.findByOrderTypeAndOrderStatusOrderByPriceAsc(OrderType.SELL, OrderStatus.SUBMITTED);
			logger.debug("getting best buy: {}", getBestBuy());
			while ((countOrderByOrderType(OrderType.SELL) > 0) && (remainingVolume > 0)) {
				logger.debug("inner buy while");
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				addNewOrder(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
		} else {
			List<Orders> sellOrderList = ordersRepository.findByOrderTypeAndOrderStatusOrderByPriceDesc(OrderType.BUY, OrderStatus.SUBMITTED);
			logger.debug("getting best sell: {}", getBestSell());
			while ((countOrderByOrderType(OrderType.BUY) > 0) && (remainingVolume > 0)) {
				logger.debug("inner sell while");
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				addNewOrder(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
		}
		return null;
	}

	@Override
	public Orders processLimitOrder(Orders orders) {
		OrderType orderType = orders.getOrderType();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		Double price = orders.getPrice();
		logger.debug("Order type is equal with buy: {}", orderType.equals(OrderType.BUY));
		if (orderType.equals(OrderType.BUY)) {
			List<Orders> buyOrderList = ordersRepository.findByOrderTypeAndOrderStatusAndPriceLessThanEqualOrderByPriceAsc("SELL", "SUBMITTED", price);
			logger.debug("getting best buy: {}", getBestBuy());
			while ((countOrderByOrderTypeWithGreaterAndLesThan(OrderType.SELL, price) > 0) && (remainingVolume > 0) && (price >= getBestBuy())) {
				logger.debug("inner buy while");
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				addNewOrder(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
		} else {
			List<Orders> sellOrderList = ordersRepository.findByOrderTypeAndOrderStatusAndPriceGreaterThanEqualOrderByPriceDesc("BUY", "SUBMITTED", price);
			logger.debug("getting best sell: {}", getBestSell());
			while ((countOrderByOrderTypeWithGreaterAndLesThan(OrderType.BUY, price) > 0) && (remainingVolume > 0) && (price <= getBestSell())) {
				logger.debug("inner sell while");
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				addNewOrder(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
		}
		return null;
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
				if (orderType.equals(OrderType.BUY)) {
					UpdateOrderVolume(matchedOrder.getId(), matchedOrder.getVolume() - remainingVolume);
					logger.debug("matching sell not completed: {}", matchedOrder.getVolume() - remainingVolume);
				} else {
					UpdateOrderVolume(matchedOrder.getId(), matchedOrder.getVolume() - remainingVolume);
					logger.debug("matching buy not completed: {}", matchedOrder.getVolume() - remainingVolume);
				}
				remainingVolume = 0.0;
			} else {
				qtyTraded = matchedOrder.getVolume();
				remainingVolume -= qtyTraded;
				removeOrderFromList(ordersList);
				matchedOrder.setVolume(0.0);
				matchedOrder.setOrderStatus(OrderStatus.COMPLETED);
				ordersRepository.save(matchedOrder);
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
			tradeRepository.save(trade);
			logger.debug("saving trade ompleted");
		}
		return remainingVolume;
	}

//	public Double totalVolume(String pair) {
//		return orderRepository.getSumVolumeByPair(pair);
//	}

	@Override
	public Page<Orders> getBuyOrdersListByPair(String pair, OrderStandard orderStandard) {
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, "price");
		Page<Orders> orderBook = ordersRepository.findAll(pageRequest);
		return orderBook;
	}

	@Override
	public Page<Orders> getSellOrdersListByPair(String pair, OrderStandard orderStandard) {
		PageRequest pageRequest = new PageRequest(0, 10, Direction.ASC, "price");
		Page<Orders> orderBook = ordersRepository.findAll(pageRequest);
		return orderBook;
	}

	@Override
	public Double getBestBuy() {
		return ordersRepository.getBestBuy();
	}

	@Override
	public Double getWorstBuy() {
		return ordersRepository.getWrostBuy();
	}

	@Override
	public Double getBestSell() {
		return ordersRepository.getBestSell();
	}

	@Override
	public Double getWorstSell() {
		return ordersRepository.getWrostSell();
	}

	@Override
	public Long countOrderByOrderTypeWithGreaterAndLesThan(OrderType orderType, Double price) {
		if (orderType.equals("BUY")) {
			return ordersRepository.countOrderByOrderTypeAndPriceGreaterThan(orderType, price);
		}
		else {
			return ordersRepository.countOrderByOrderTypeAndPriceLessThan(orderType, price);

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
