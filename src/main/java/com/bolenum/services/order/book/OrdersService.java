package com.bolenum.services.order.book;

import java.util.List;

import org.springframework.data.domain.Page;

import com.bolenum.enums.OrderType;
import com.bolenum.model.orders.book.Orders;

/**
 * 
 * @author Vishal Kumar
 * @date 06-Oct-2017
 *
 */
public interface OrdersService {

	Orders deleteOrder(Long ordersId);

	Boolean processMarketOrder(Orders orders);

	Boolean processLimitOrder(Orders orders);

	Double processOrderList(List<Orders> ordersList, Double remainingVolume, Orders orders);

	Long countOrderByOrderTypeWithGreaterAndLesThan(OrderType orderType, Long pairId, Double price);

	Long countOrderByOrderType(OrderType orderType);

	Orders matchedOrder(List<Orders> ordersList);

	void removeOrderFromList(List<Orders> ordersList);

	Boolean processOrder(Orders orders);

	Page<Orders> getOrdersListByPair(Long pairId, OrderType orderType);

	Double getWorstBuy(List<Orders> buyOrderList);

	Double getBestSell(List<Orders> sellOrderList);

	Double getWorstSell(List<Orders> sellOrderList);

	Double getBestBuy(List<Orders> buyOrderList);

}
