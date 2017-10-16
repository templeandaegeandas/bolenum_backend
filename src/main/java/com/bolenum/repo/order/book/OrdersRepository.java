package com.bolenum.repo.order.book;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bolenum.enums.OrderStatus;
import com.bolenum.enums.OrderType;
import com.bolenum.model.orders.book.Orders;

/**
 * 
 * @author Vishal kumar
 * @date 06-Oct-2017
 *
 */
public interface OrdersRepository extends JpaRepository<Orders, Long> {
	// Double getSumVolumeByPairId(Long pairId);

	List<Orders> findByOrderTypeAndOrderStatusAndPairIdAndPriceLessThanEqualOrderByPriceAsc(OrderType ordertype,
			OrderStatus orderStatus, Long pairId, Double price);

	List<Orders> findByOrderTypeAndOrderStatusAndPairIdAndPriceGreaterThanEqualOrderByPriceDesc(OrderType ordertype,
			OrderStatus orderStatus, Long pairId, Double price);

	List<Orders> findByOrderTypeAndOrderStatusAndPairIdOrderByPriceAsc(OrderType ordertype, OrderStatus orderStatus,
			Long pairId);

	List<Orders> findByOrderTypeAndOrderStatusAndPairIdOrderByPriceDesc(OrderType ordertype, OrderStatus orderStatus,
			Long pairId);

	@Query("select min(o.price) from Orders o where o.orderType = 'SELL' and o.orderStatus = 'SUBMITTED' and o.pairId= :pairId")
	Double getBestBuy(@Param("pairId") Long pairId);

	@Query("select max(o.price) from Orders o where o.orderType = 'SELL' and o.orderStatus = 'SUBMITTED' and o.pairId= :pairId")
	Double getWrostBuy(@Param("pairId") Long pairId);

	@Query("select max(o.price) from Orders o where o.orderType = 'BUY' and o.orderStatus = 'SUBMITTED' and o.pairId= :pairId")
	Double getBestSell(@Param("pairId") Long pairId);

	@Query("select min(o.price) from Orders o where o.orderType = 'BUY' and o.orderStatus = 'SUBMITTED' and o.pairId= :pairId")
	Double getWrostSell(@Param("pairId") Long pairId);

	@Query("select count(o) from Orders o where o.orderType = :orderType and o.orderStatus = 'SUBMITTED' and o.pairId= :pairId and o.price >= :price")
	Long countOrderByOrderTypeAndPriceGreaterThan(@Param("orderType") OrderType orderType, @Param("pairId") Long pairId,
			@Param("price") Double price);

	@Query("select count(o) from Orders o where o.orderType = :orderType and o.orderStatus = 'SUBMITTED' and o.pairId= :pairId and o.price <= :price")
	Long countOrderByOrderTypeAndPriceLessThan(@Param("orderType") OrderType orderType, @Param("pairId") Long pairId, @Param("price") Double price);

	@Query("select count(o) from Orders o where o.orderType = :orderType and o.orderStatus = 'SUBMITTED'")
	Long countOrderByOrderType(@Param("orderType") OrderType orderType);
	
	Page<Orders> findByPairIdAndOrderTypeAndOrderStatus(Long pairId, OrderType orderType, OrderStatus orderStatus, Pageable pageable);
}
