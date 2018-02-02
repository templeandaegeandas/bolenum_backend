package com.bolenum.services.order.book;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Page;

import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;

/**
 * 
 * @author Vishal Kumar
 * @date 06-Oct-2017
 *
 */
public interface OrdersService {

	/**
	 * This method is use to delete Order
	 * @param ordersId
	 * @return
	 */
	Orders deleteOrder(Long ordersId);

	/**
	 * This method is use to process Market Order
	 * @param orders
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	Boolean processMarketOrder(Orders orders) throws InterruptedException, ExecutionException;

	/**
	 * This method is use to process Limit Order
	 * @param orders
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	Boolean processLimitOrder(Orders orders) throws InterruptedException, ExecutionException;

	/**
	 * This method is use to process Order List
	 * @param ordersList
	 * @param remainingVolume
	 * @param orders
	 * @param marketCurrency
	 * @param pairedCurrency
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	Double processOrderList(List<Orders> ordersList, Double remainingVolume, Orders orders, Currency marketCurrency,
			Currency pairedCurrency) throws InterruptedException, ExecutionException;

	/**
	 * This method is use to count OrderBy OrderType With Greater And LesThan
	 * @param orderType
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @param price
	 * @return
	 */
	Long countOrderByOrderTypeWithGreaterAndLesThan(OrderType orderType, Long marketCurrencyId, Long pairedCurrencyId,
			Double price);

	/**
	 * This method is use to count OrderBy Order Type
	 * @param orderType
	 * @return
	 */
	Long countOrderByOrderType(OrderType orderType);

	/**
	 * This method is use to matched Order
	 * @param ordersList
	 * @return
	 */
	Orders matchedOrder(List<Orders> ordersList);

	/**
	 * This method is use to remove Order From List
	 * @param ordersList
	 */
	void removeOrderFromList(List<Orders> ordersList);

	/**
	 * This method is use to process Order
	 * @param orders
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	Boolean processOrder(Orders orders) throws InterruptedException, ExecutionException;

	/**
	 * This method is use to get Buy Orders List By Pair
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @return
	 */
	Page<Orders> getBuyOrdersListByPair(long marketCurrencyId, long pairedCurrencyId);

	/**
	 * This method is use to get Sell Orders List By Pair
	 * @param marketCurrencyId
	 * @param pairedCurrencyId
	 * @return
	 */
	Page<Orders> getSellOrdersListByPair(long marketCurrencyId, long pairedCurrencyId);

	/**
	 * This method is use to get Worst Buy
	 * @param buyOrderList
	 * @return
	 */
	Double getWorstBuy(List<Orders> buyOrderList);

	/**
	 * This method is use to get Best Sell
	 * @param sellOrderList
	 * @return
	 */
	Double getBestSell(List<Orders> sellOrderList);

	/**
	 * This method is use to get Worst Sell
	 * @param sellOrderList
	 * @return
	 */
	Double getWorstSell(List<Orders> sellOrderList);

	/**
	 * This method is use to get Best Buy
	 * @param buyOrderList
	 * @return
	 */
	Double getBestBuy(List<Orders> buyOrderList);

	/**
	 * This method is use to check Order Eligibility
	 * @param user
	 * @param order
	 * @return
	 */
	String checkOrderEligibility(User user, Orders order);

	/**
	 * This method is use to find Orders List By User And Order Status
	 * @param user
	 * @param orderStatus
	 * @return
	 */
	List<Orders> findOrdersListByUserAndOrderStatus(User user, OrderStatus orderStatus);

	/**
	 * This method is use for total User Balance In Book
	 * @param user
	 * @param marketCurrency
	 * @param pairedCurrency
	 * @return
	 */
	double totalUserBalanceInBook(User user, Currency marketCurrency, Currency pairedCurrency);

	/**
	 * This method is use to get count Active Open Order
	 * @return
	 */
	Long countActiveOpenOrder();

	/**
	 * This method is use to get Total Count Of Newer Buyer And Seller
	 * @param orderType
	 * @return
	 */
	Long getTotalCountOfNewerBuyerAndSeller(OrderType orderType);

	/**
	 * This method is use to count Orders By Order Type And User
	 * @param user
	 * @param orderType
	 * @return
	 */
	Long countOrdersByOrderTypeAndUser(User user, OrderType orderType);

	/**
	 * This method is use to get Order Details
	 * @param orderId
	 * @return
	 */
	public Orders getOrderDetails(long orderId);

	/**
	 * This method is use to get Placed Order Volume
	 * @param user
	 * @return
	 */
	double getPlacedOrderVolume(User user);

	/**
	 * This method is use to get List Of Latest Orders
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @return
	 */
	Page<Orders> getListOfLatestOrders(int pageNumber, int pageSize, String sortOrder, String sortBy);

	/**
	 * This method is use for is Users Self Order
	 * @param reqOrder
	 * @param orderList
	 * @return
	 */
	public boolean isUsersSelfOrder(Orders reqOrder, List<Orders> orderList);

	/**
	 * This metho is use to find Orders List By User And Order Status
	 * @param pageNumber
	 * @param pageSize
	 * @param sortOrder
	 * @param sortBy
	 * @param user
	 * @param orderStatus
	 * @return
	 */
	Page<Orders> findOrdersListByUserAndOrderStatus(int pageNumber, int pageSize, String sortOrder, String sortBy,
			User user, OrderStatus orderStatus);

	/**
	 * This method is use to find User Order Locked Volume
	 * @param user
	 * @param marketCurrency
	 * @param pairedCurrency
	 * @return
	 */
	double findUserOrderLockedVolume(User user, Currency marketCurrency, Currency pairedCurrency);

	/**
	 * This method is use to find By Order Id
	 * @param orderId
	 * @return
	 */
	Orders findByOrderId(long orderId);

	/**
	 * This method is use to cancel Order
	 * @param order
	 */
	void cancelOrder(Orders order);
}
