package com.bolenum.services.order.book;

import java.util.Date;
import java.util.Iterator;
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
import com.bolenum.model.orders.book.Order;
import com.bolenum.model.orders.book.Trade;
import com.bolenum.repo.order.book.OrderRepository;
import com.bolenum.repo.order.book.TradeRepository;

/**
 * 
 * @author Vishal Kumar
 * @date 06-Oct-2017
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private TradeRepository tradeRepository;
	
	public static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	public Order addNewOrder(Order order) {
		return orderRepository.save(order);
	}

	public Order deleteOrder(Long orderId) {
		Order order = orderRepository.findOne(orderId);
		order.setDeletedOn(new Date());
		order.setOrderStatus(OrderStatus.CANCELLED);
		return orderRepository.save(order);
	}

	public Order UpdateOrderVolume(Long orderId, Double volume) {
		Order order = orderRepository.findOne(orderId);
		order.setVolume(volume);
		return orderRepository.save(order);
	}

	public Order processMarketOrder(Order order) {
		OrderType orderType = order.getOrderType();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = order.getTotalVolume();
		if (orderType.equals(OrderType.BUY)) {
			List<Order> buyOrderList = orderRepository.findByOrderTypeAndOrderStatusOrderByPriceAsc(OrderType.SELL, OrderStatus.SUBMITTED);
			logger.debug("getting best buy: {}", getBestBuy());
			while ((countOrderByOrderType(OrderType.SELL) > 0) && (remainingVolume > 0)) {
				logger.debug("inner buy while");
				remainingVolume = processOrderList(buyOrderList, remainingVolume, order);
			}
			if (remainingVolume > 0) {
				order.setVolume(remainingVolume);
				addNewOrder(order);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
		} else {
			List<Order> sellOrderList = orderRepository.findByOrderTypeAndOrderStatusOrderByPriceDesc(OrderType.BUY, OrderStatus.SUBMITTED);
			logger.debug("getting best sell: {}", getBestSell());
			while ((countOrderByOrderType(OrderType.BUY) > 0) && (remainingVolume > 0)) {
				logger.debug("inner sell while");
				remainingVolume = processOrderList(sellOrderList, remainingVolume, order);
			}
			if (remainingVolume > 0) {
				order.setVolume(remainingVolume);
				addNewOrder(order);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
		}
		return null;
	}

	public Order processLimitOrder(Order order) {
		OrderType orderType = order.getOrderType();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = order.getTotalVolume();
		Double price = order.getPrice();
		logger.debug("Order type is equal with buy: {}", orderType.equals(OrderType.BUY));
		if (orderType.equals(OrderType.BUY)) {
			List<Order> buyOrderList = orderRepository.findByOrderTypeAndOrderStatusAndPriceLessThanEqualOrderByPriceAsc("SELL", "SUBMITTED", price);
			logger.debug("getting best buy: {}", getBestBuy());
			while ((countOrderByOrderTypeWithGreaterAndLesThan(OrderType.SELL, price) > 0) && (remainingVolume > 0) && (price >= getBestBuy())) {
				logger.debug("inner buy while");
				remainingVolume = processOrderList(buyOrderList, remainingVolume, order);
			}
			if (remainingVolume > 0) {
				order.setVolume(remainingVolume);
				addNewOrder(order);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
		} else {
			List<Order> sellOrderList = orderRepository.findByOrderTypeAndOrderStatusAndPriceGreaterThanEqualOrderByPriceDesc("BUY", "SUBMITTED", price);
			logger.debug("getting best sell: {}", getBestSell());
			while ((countOrderByOrderTypeWithGreaterAndLesThan(OrderType.BUY, price) > 0) && (remainingVolume > 0) && (price <= getBestSell())) {
				logger.debug("inner sell while");
				remainingVolume = processOrderList(sellOrderList, remainingVolume, order);
			}
			if (remainingVolume > 0) {
				order.setVolume(remainingVolume);
				addNewOrder(order);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
		}
		return null;
	}

	public Double processOrderList(List<Order> orderList, Double remainingVolume, Order order) {
		OrderType orderType = order.getOrderType();
		Long buyerId, sellerId;
		while ((orderList.size() > 0) && (remainingVolume > 0)) {
			logger.debug("inner proccessing while");
			Double qtyTraded;
			Order matchedOrder = matchedOrder(orderList);
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
				removeOrderFromList(orderList);
				matchedOrder.setVolume(0.0);
				matchedOrder.setOrderStatus(OrderStatus.COMPLETED);
				orderRepository.save(matchedOrder);
				logger.debug("matching buy/sell completed");
			}
			if (orderType.equals(OrderType.BUY)) {
				buyerId = order.getUserId();
				sellerId = matchedOrder.getUserId();
			} else {
				buyerId = matchedOrder.getUserId();
				sellerId = order.getUserId();
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

	public Page<Order> getBuyOrdersListByPair(String pair, OrderStandard orderStandard) {
		PageRequest pageRequest = new PageRequest(0, 10, Direction.DESC, "price");
		Page<Order> orderBook = orderRepository.findAll(pageRequest);
		return orderBook;
	}

	public Page<Order> getSellOrdersListByPair(String pair, OrderStandard orderStandard) {
		PageRequest pageRequest = new PageRequest(0, 10, Direction.ASC, "price");
		Page<Order> orderBook = orderRepository.findAll(pageRequest);
		return orderBook;
	}

	public Double getBestBuy() {
		return orderRepository.getBestBuy();
	}

	public Double getWorstBuy() {
		return orderRepository.getWrostBuy();
	}

	public Double getBestSell() {
		return orderRepository.getBestSell();
	}

	public Double getWorstSell() {
		return orderRepository.getWrostSell();
	}

	public Long countOrderByOrderTypeWithGreaterAndLesThan(OrderType orderType, Double price) {
		if (orderType.equals("BUY")) {
			return orderRepository.countOrderByOrderTypeAndPriceGreaterThan(orderType, price);
		}
		else {
			return orderRepository.countOrderByOrderTypeAndPriceLessThan(orderType, price);

		}
	}
	
	public Long countOrderByOrderType(OrderType orderType) {
		return orderRepository.countOrderByOrderType(orderType);
	}

	public Order matchedOrder(List<Order> orderList) {
		Order matchedOrder = orderList.get(0);

		Iterator<Order> orderIterator = orderList.iterator();
		while (orderIterator.hasNext()) {
			System.out.println(orderIterator.next().getPrice());
		}
		return matchedOrder;
	}

	public void removeOrderFromList(List<Order> orderList) {
		orderList.remove(0);
	}
}
