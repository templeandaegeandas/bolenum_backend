package com.bolenum.repo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.OTP;

public interface OTPRepository extends JpaRepository<OTP, Long> {

	/**
	 * This method is use to find By Otp Number
	 * @param otp
	 * @return
	 */
	OTP findByOtpNumber(Integer otp);
}
