package com.bolenum.services.order.book;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Page;

import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.CurrencyPair;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;

/**
 * 
 * @author Vishal Kumar
 * @date 06-Oct-2017
 *
 */
public interface OrdersService {

	Orders deleteOrder(Long ordersId);

	Boolean processMarketOrder(Orders orders) throws InterruptedException, ExecutionException;

	Boolean processLimitOrder(Orders orders) throws InterruptedException, ExecutionException;

	Double processOrderList(List<Orders> ordersList, Double remainingVolume, Orders orders, CurrencyPair pair)
			throws InterruptedException, ExecutionException;

	Long countOrderByOrderTypeWithGreaterAndLesThan(OrderType orderType, Long pairId, Double price);

	Long countOrderByOrderType(OrderType orderType);

	Orders matchedOrder(List<Orders> ordersList);

	void removeOrderFromList(List<Orders> ordersList);

	Boolean processOrder(Orders orders) throws InterruptedException, ExecutionException;

	Page<Orders> getBuyOrdersListByPair(Long pairId);

	Page<Orders> getSellOrdersListByPair(Long pairId);

	Double getWorstBuy(List<Orders> buyOrderList);

	Double getBestSell(List<Orders> sellOrderList);

	Double getWorstSell(List<Orders> sellOrderList);

	Double getBestBuy(List<Orders> buyOrderList);

	String checkOrderEligibility(User user, Orders order, Long pairId);

	String getPairedBalance(Orders orders, CurrencyPair currencyPair, double qtyTraded);

	List<Orders> findOrdersListByUserAndOrderStatus(User user, OrderStatus orderStatus);

	Double totalUserBalanceInBook(User user, Currency toCurrency, Currency pairedCurrency);

	Long countActiveOpenOrder();
	
	Long getTotalCountOfNewerBuyerAndSeller(OrderType orderType);

    Long countOrdersByOrderTypeAndUser(User user,OrderType orderType);
	
    public Orders getOrderDetails(long orderId);
    
	double getPlacedOrderVolume(User user);

	Page<Orders> getListOfLatestOrders(int pageNumber, int pageSize, String sortOrder, String sortBy,String searchData);
}