package com.bolenum.repo.order.book;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.constant.OrderStatus;
import com.bolenum.constant.OrderType;
import com.bolenum.model.orders.book.Orders;

/**
 * 
 * @author Vishal kumar
 * @date 06-Oct-2017
 *
 */
public interface OrdersRepository extends JpaRepository<Orders, Long> {
	// Double getSumVolumeByPairId(Long pairId);

	List<Orders> findByOrderTypeAndOrderStatusAndPriceLessThanEqualOrderByPriceAsc(String ordertype, String orderStatus,
			Double price);

	List<Orders> findByOrderTypeAndOrderStatusAndPriceGreaterThanEqualOrderByPriceDesc(String ordertype,
			String orderStatus, Double price);

	List<Orders> findByOrderTypeAndOrderStatusOrderByPriceAsc(OrderType ordertype, OrderStatus orderStatus);

	List<Orders> findByOrderTypeAndOrderStatusOrderByPriceDesc(OrderType ordertype, OrderStatus orderStatus);

	@Query("select min(o.price) from Orders o where o.orderType = 'SELL' and o.orderStatus = 'SUBMITTED'")
	Double getBestBuy();

	@Query("select max(o.price) from Orders o where o.orderType = 'SELL' and o.orderStatus = 'SUBMITTED'")
	Double getWrostBuy();

	@Query("select max(o.price) from Orders o where o.orderType = 'BUY' and o.orderStatus = 'SUBMITTED'")
	Double getBestSell();

	@Query("select min(o.price) from Orders o where o.orderType = 'BUY' and o.orderStatus = 'SUBMITTED'")
	Double getWrostSell();

	@Query("select count(o) from Orders o where o.orderType = :orderType and o.orderStatus = 'SUBMITTED' and o.price >= :price")
	Long countOrderByOrderTypeAndPriceGreaterThan(@Param("orderType") OrderType orderType,
			@Param("price") Double price);

	@Query("select count(o) from Orders o where o.orderType = :orderType and o.orderStatus = 'SUBMITTED' and o.price <= :price")
	Long countOrderByOrderTypeAndPriceLessThan(@Param("orderType") OrderType orderType, @Param("price") Double price);

	@Query("select count(o) from Orders o where o.orderType = :orderType and o.orderStatus = 'SUBMITTED'")
	Long countOrderByOrderType(@Param("orderType") OrderType orderType);
}
