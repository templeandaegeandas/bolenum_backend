/**
 * 
 */
package com.bolenum.repo.admin;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bolenum.model.fees.WithdrawalFee;

/**
 * @author chandan kumar singh
 * @date 27-Nov-2017
 */
@Repository
public interface WithdrawalFeeRepo extends JpaRepository<WithdrawalFee, Serializable> {

}
