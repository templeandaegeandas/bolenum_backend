package com.bolenum.repo.order.book;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.Currency;
import com.bolenum.model.User;
import com.bolenum.model.orders.book.Orders;

/**
 * 
 * @author Vishal kumar
 * @date 06-Oct-2017
 *
 */
public interface OrdersRepository extends JpaRepository<Orders, Long> {
	
	@Query("select o from Orders o where o.orderType=:orderType and o.orderStatus='SUBMITTED' and o.marketCurrency=:marketCurrency and o.pairedCurrency=:pairedCurrency and o.price<=:price group by id having (sum(volume)<=:volume)")
	List<Orders> findMatchingOrdersList(@Param("orderType") OrderType ordertype, @Param("marketCurrency") Currency marketCurrency, @Param("pairedCurrency") Currency pairedCurrency, @Param("price") Double price, @Param("volume") Double volume, Pageable pageable);
	
	@Query("select o from Orders o where o.orderType=:orderType and o.orderStatus='SUBMITTED' and o.marketCurrency=:marketCurrency and o.pairedCurrency=:pairedCurrency and o.price<=:price")
	List<Orders> findSingleMatchingOrdersList(@Param("orderType") OrderType ordertype, @Param("marketCurrency") Currency marketCurrency, @Param("pairedCurrency") Currency pairedCurrency, @Param("price") Double price, Pageable pageable);

	List<Orders> findByOrderTypeAndOrderStatusAndMarketCurrencyAndPairedCurrencyAndPriceLessThanEqualOrderByPriceAsc(
			OrderType ordertype, OrderStatus orderStatus, Currency marketCurrency, Currency pairedCurrency,
			Double price);

	List<Orders> findByOrderTypeAndOrderStatusAndAndMarketCurrencyAndPairedCurrencyAndPriceGreaterThanEqualOrderByPriceDesc(
			OrderType ordertype, OrderStatus orderStatus, Currency marketCurrency, Currency pairedCurrency,
			Double price);

	List<Orders> findByOrderTypeAndOrderStatusAndMarketCurrencyAndPairedCurrencyOrderByPriceAsc(OrderType ordertype,
			OrderStatus orderStatus, Currency marketCurrency, Currency pairedCurrency);

	List<Orders> findByOrderTypeAndOrderStatusAndMarketCurrencyAndPairedCurrencyOrderByPriceDesc(OrderType ordertype,
			OrderStatus orderStatus, Currency marketCurrency, Currency pairedCurrency);

	@Query("select min(o.price) from Orders o where o.orderType = 'SELL' and o.orderStatus = 'SUBMITTED' and o.marketCurrency=:marketCurrency and o.pairedCurrency=:pairedCurrency")
	Double getBestBuy(@Param("marketCurrency") Currency marketCurrency,
			@Param("pairedCurrency") Currency pairedCurrency);

	@Query("select max(o.price) from Orders o where o.orderType = 'BUY' and o.orderStatus = 'SUBMITTED' and o.marketCurrency=:marketCurrency and o.pairedCurrency=:pairedCurrency")
	Double getBestSell(@Param("marketCurrency") Currency marketCurrency,
			@Param("pairedCurrency") Currency pairedCurrency);

	@Query("select count(o) from Orders o where o.orderType = :orderType and o.orderStatus = 'SUBMITTED' and o.marketCurrency.currencyId=:marketCurrency and o.pairedCurrency.currencyId=:pairedCurrency and o.price >= :price")
	Long countOrderByOrderTypeAndPriceGreaterThan(@Param("orderType") OrderType orderType,
			@Param("marketCurrency") long marketCurrency, @Param("pairedCurrency") long pairedCurrency,
			@Param("price") Double price);

	@Query("select count(o) from Orders o where o.orderType = :orderType and o.orderStatus = 'SUBMITTED' and o.marketCurrency.currencyId=:marketCurrency and o.pairedCurrency.currencyId=:pairedCurrency and o.price <= :price")
	Long countOrderByOrderTypeAndPriceLessThan(@Param("orderType") OrderType orderType,
			@Param("marketCurrency") long marketCurrency, @Param("pairedCurrency") long pairedCurrency,
			@Param("price") Double price);

	@Query("select count(o) from Orders o where o.orderType = :orderType and o.orderStatus = 'SUBMITTED'")
	Long countOrderByOrderType(@Param("orderType") OrderType orderType);

	@Query("select o from Orders o where o.marketCurrency.currencyId=:marketCurrencyId and o.pairedCurrency.currencyId=:pairedCurrencyId and o.orderType = :orderType and o.orderStatus = :orderStatus order by o.price desc")
	Page<Orders> findBuyOrderList(@Param("marketCurrencyId") long marketCurrencyId,
			@Param("pairedCurrencyId") long pairedCurrencyId, @Param("orderType") OrderType orderType,
			@Param("orderStatus") OrderStatus orderStatus, Pageable pageable);

	@Query("select o from Orders o where o.marketCurrency.currencyId=:marketCurrencyId and o.pairedCurrency.currencyId=:pairedCurrencyId and o.orderType = :orderType and o.orderStatus = :orderStatus order by o.price asc")
	Page<Orders> findSellOrderList(@Param("marketCurrencyId") long marketCurrencyId,
			@Param("pairedCurrencyId") long pairedCurrencyId, @Param("orderType") OrderType orderType,
			@Param("orderStatus") OrderStatus orderStatus, Pageable pageable);

	List<Orders> findByUserAndOrderStatus(User user, OrderStatus orderStatus);

	Page<Orders> findByUserAndOrderStatus(User user, OrderStatus orderStatus, Pageable pageable);

	List<Orders> findByUserAndOrderStatusAndOrderTypeAndMarketCurrency(User user, OrderStatus orderStatus,
			OrderType orderType, Currency marketCurrency);

	List<Orders> findByUserAndOrderStatusAndPairedCurrency(User user, OrderStatus orderStatus, Currency pairedCurrency);

	@Query("select SUM(o.price) from Orders o where o.orderType = 'SELL' and o.user = :user and (o.marketCurrency = :marketCurrency or o.pairedCurrency = :pairedCurrency)")
	Double totalUserBalanceInBook(@Param("user") User user, @Param("marketCurrency") Currency marketCurrency,
			@Param("pairedCurrency") Currency pairedCurrency);

	List<Orders> findByUserAndOrderStatusAndOrderTypeAndPairedCurrency(User user, OrderStatus orderStatus,
			OrderType orderType, Currency pairedCurrency);

	Long countOrdersByCreatedOnBetweenAndOrderStatus(Date startDate, Date endDate, OrderStatus orderStatus);

	Long countOrderByOrderTypeAndCreatedOnBetween(OrderType orderType, Date startDate, Date endDate);

	Long countOrderByUserAndOrderType(User user, OrderType orderType);

	Orders findByMatchedOrder(Orders orders);

	@Query("Select o from Orders o where o.orderStatus=:orderStatus and o.createdOn <= :endDate and o.createdOn >= :startDate")
	Page<Orders> findByOrderStatusAndCreatedOnBetween(@Param("orderStatus") OrderStatus orderStatus,
			@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable page);

	Page<Orders> findByPriceLessThanEqualAndOrderTypeAndOrderStatusAndMarketCurrencyCurrencyIdAndPairedCurrencyCurrencyId(
			Double price, OrderType orderType, OrderStatus orderStatus, long marketCurrencyId, long pairedCurrencyId,
			Pageable page);

	Page<Orders> findByPriceGreaterThanEqualAndOrderTypeAndOrderStatusAndMarketCurrencyCurrencyIdAndPairedCurrencyCurrencyId(
			Double price, OrderType orderType, OrderStatus orderStatus, long marketCurrencyId, long pairedCurrencyId,
			Pageable page);

	@Query("select sum(o.volume) from Orders o where o.marketCurrency.currencyId=:marketCurrencyId and o.pairedCurrency.currencyId=:pairedCurrencyId and o.createdOn > :endDate")
	Double ordersIn24hVolume(@Param("marketCurrencyId") long marketCurrencyId,
			@Param("pairedCurrencyId") long pairedCurrencyId, @Param("endDate") Date endDate);

	@Query("select max(o.volume) from Orders o where o.marketCurrency.currencyId=:marketCurrencyId and o.pairedCurrency.currencyId=:pairedCurrencyId and o.createdOn > :endDate")
	Double ordersIn24hHigh(@Param("marketCurrencyId") long marketCurrencyId,
			@Param("pairedCurrencyId") long pairedCurrencyId, @Param("endDate") Date endDate);

	@Query("select min(o.volume) from Orders o where o.marketCurrency.currencyId=:marketCurrencyId and o.pairedCurrency.currencyId=:pairedCurrencyId and o.createdOn > :endDate")
	Double ordersIn24hLow(@Param("marketCurrencyId") long marketCurrencyId,
			@Param("pairedCurrencyId") long pairedCurrencyId, @Param("endDate") Date endDate);
}
