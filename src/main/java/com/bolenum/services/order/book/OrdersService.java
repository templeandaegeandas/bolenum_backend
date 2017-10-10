package com.bolenum.services.order.book;

import java.util.List;

import org.springframework.data.domain.Page;

import com.bolenum.constant.OrderType;
import com.bolenum.model.orders.book.Orders;

/**
 * 
 * @author Vishal Kumar
 * @date 06-Oct-2017
 *
 */
public interface OrdersService {

	Orders addNewOrder(Orders orders);

	Orders deleteOrder(Long ordersId);

	Orders UpdateOrderVolume(Long ordersId, Double volume);

	Boolean processMarketOrder(Orders orders);

	Boolean processLimitOrder(Orders orders);

	Double processOrderList(List<Orders> ordersList, Double remainingVolume, Orders orders);

	Double getBestBuy();

	Double getWorstBuy();

	Double getBestSell();

	Double getWorstSell();

	Long countOrderByOrderTypeWithGreaterAndLesThan(OrderType orderType, Double price);

	Long countOrderByOrderType(OrderType orderType);

	Orders matchedOrder(List<Orders> ordersList);

	void removeOrderFromList(List<Orders> ordersList);

	Boolean processOrder(Orders orders);

	Page<Orders> getOrdersListByPair(Long pairId, OrderType orderType);

}
