/**
 * 
 */
package com.bolenum.repo.order.book;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.orders.book.PartialTrade;

/**
 * @author chandan kumar singh
 * @date 15-Nov-2017
 */
public interface PartialTradeRepository extends JpaRepository<PartialTrade, Serializable> {

}
