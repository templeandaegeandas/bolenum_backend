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

	/**
	 * process market order
	 */
	@Override
	public Boolean processMarketOrder(Orders orders) {
		Boolean processed = false;
		OrderType orderType = orders.getOrderType();
		Long pairId = orders.getPairId();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		if (orderType.equals(OrderType.BUY)) {
			List<Orders> sellOrderList = ordersRepository.findByOrderTypeAndOrderStatusAndPairIdOrderByPriceAsc(
					OrderType.SELL, OrderStatus.SUBMITTED, pairId);
			while (sellOrderList.size() > 0 && remainingVolume > 0) {
				logger.debug("inner buy while loop for buyers remainingVolume: {}", remainingVolume);
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		} else {
			List<Orders> buyOrderList = ordersRepository.findByOrderTypeAndOrderStatusAndPairIdOrderByPriceDesc(
					OrderType.BUY, OrderStatus.SUBMITTED, pairId);
			while (buyOrderList.size() > 0 && remainingVolume > 0) {
				logger.debug("inner sell while loop for sellers remainingVolume: {}", remainingVolume);
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders);
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

	/**
	 * process limit order
	 */
	@Override
	public Boolean processLimitOrder(Orders orders) {
		Boolean processed = false;
		OrderType orderType = orders.getOrderType();
		logger.debug("Order type is: {}", orderType);
		Double remainingVolume = orders.getTotalVolume();
		Double price = orders.getPrice();
		Long pairId = orders.getPairId();
		logger.debug("Order type is equal with buy: {}", orderType.equals(OrderType.BUY));
		// checking the order type is BUY
		if (orderType.equals(OrderType.BUY)) {
			// fetching the seller list whose selling price is less than equal
			// to buying price
			List<Orders> sellOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndPairIdAndPriceLessThanEqualOrderByPriceAsc(OrderType.SELL,
							OrderStatus.SUBMITTED, pairId, price);
			/**
			 * fetch one best seller's price from list of sellers, order by
			 * price in ASC then process the order
			 */
			while (sellOrderList.size() > 0 && (remainingVolume > 0) && (price >= getBestBuy(sellOrderList))) {
				logger.debug("inner buy while loop for buyers and remaining volume: {}", remainingVolume);
				remainingVolume = processOrderList(sellOrderList, remainingVolume, orders);
			}
			if (remainingVolume > 0) {
				orders.setVolume(remainingVolume);
				ordersList.add(orders);
				logger.debug("qty remaining so added in book: {}", remainingVolume);
			}
			processed = true;
		} else {
			/**
			 * fetching the list of BUYERS whose buy price is greater than sell
			 * price
			 */
			List<Orders> buyOrderList = ordersRepository
					.findByOrderTypeAndOrderStatusAndPairIdAndPriceGreaterThanEqualOrderByPriceDesc(OrderType.BUY,
							OrderStatus.SUBMITTED, pairId, price);
			/**
			 * fetch one best buyer's price from list of buyers, order by price
			 * in desc then process the order
			 */
			while (buyOrderList.size() > 0 && (remainingVolume > 0) && (price <= buyOrderList.get(0).getPrice())) {
				logger.debug("inner sell while loop for seller and remaining volume: {}", remainingVolume);
				remainingVolume = processOrderList(buyOrderList, remainingVolume, orders);
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

	/**
	 * process the buyers and sellers order
	 * 
	 */
	@Override
	public Double processOrderList(List<Orders> ordersList, Double remainingVolume, Orders orders) {
		// fetching order type BUY or SELL
		OrderType orderType = orders.getOrderType();
		Long buyerId, sellerId;
		// process till order size and remaining volume is > 0
		while ((ordersList.size() > 0) && (remainingVolume > 0)) {
			logger.debug("inner proccessing while");
			Double qtyTraded; // total number quantity which is processed
			// fetch matched order object
			Orders matchedOrder = matchedOrder(ordersList);
			// checking selling/buying volume less than matched order volume
			if (remainingVolume < matchedOrder.getVolume()) {
				// qtyTraded is total selling/buying volume
				qtyTraded = remainingVolume;
				// setting new required SELL/BUY volume is remaining order
				// volume
				matchedOrder.setVolume(matchedOrder.getVolume() - remainingVolume);
				// adding matched order in list with remaining volume
				ordersList.add(matchedOrder);
				// now selling/buying volume is 0
				remainingVolume = 0.0;
			} else {
				// selling/buying volume greater than matched order volume
				// qtyTraded is total sellers/buyers volume
				qtyTraded = matchedOrder.getVolume();
				// new selling/buying volume is remainingVolume - qtyTraded
				remainingVolume -= qtyTraded;
				// removed processed order
				removeOrderFromList(ordersList);
				// new volume of processed order is 0
				matchedOrder.setVolume(0.0);
				// status of processed order is completed
				matchedOrder.setOrderStatus(OrderStatus.COMPLETED);
				// adding to order list by setting the new volume and status of
				// processed order
				ordersList.add(matchedOrder);
				logger.debug("matching buy/sell completed");
			}
			// checking the order type BUY
			if (orderType.equals(OrderType.BUY)) {
				// buyer is coming order's user
				buyerId = orders.getUserId();
				// seller is matched order's user
				sellerId = matchedOrder.getUserId();
			} else {
				// order type is SELL
				// buyer is matched order's user
				buyerId = matchedOrder.getUserId();
				// seller is coming order's user
				sellerId = orders.getUserId();
			}
			// saving the processed BUY/SELL order in trade
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

	/**
	 * this will calculate the lowest selling price, thats why it is best buy
	 * for buyers
	 */
	@Override
	public Double getBestBuy(List<Orders> sellOrderList) {
		Double bestBuy = sellOrderList.get(0).getPrice();
		for (int i = 0; i < sellOrderList.size() - 1; i++) {
			if (bestBuy > sellOrderList.get(i + 1).getPrice()) {
				bestBuy = sellOrderList.get(i + 1).getPrice();
			}
		}
		return bestBuy;
	}

	/**
	 * this will calculate the highest selling price, thats why it is worst buy
	 * for buyers
	 */
	@Override
	public Double getWorstBuy(List<Orders> sellOrderList) {
		Double wrostBuy = sellOrderList.get(0).getPrice();
		for (int i = 0; i < sellOrderList.size() - 1; i++) {
			if (wrostBuy < sellOrderList.get(i + 1).getPrice()) {
				wrostBuy = sellOrderList.get(i + 1).getPrice();
			}
		}
		return wrostBuy;
	}

	/**
	 * this will calculate the highest buying price, thats why it is best sell
	 * for seller
	 */
	@Override
	public Double getBestSell(List<Orders> buyOrderList) {
		Double bestSell = null;
		for (int i = 0; i < buyOrderList.size() - 1; i++) {
			bestSell = buyOrderList.get(0).getPrice();
			if (bestSell < buyOrderList.get(i + 1).getPrice()) {
				bestSell = buyOrderList.get(i + 1).getPrice();
			}
		}
		return bestSell;
	}

	/**
	 * this will calculate the lowest buying price, thats why it is worst sell
	 * for seller
	 */
	@Override
	public Double getWorstSell(List<Orders> buyOrderList) {
		Double wrostSell = buyOrderList.get(0).getPrice();
		for (int i = 0; i < buyOrderList.size() - 1; i++) {
			if (wrostSell > buyOrderList.get(i + 1).getPrice()) {
				wrostSell = buyOrderList.get(i + 1).getPrice();
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
