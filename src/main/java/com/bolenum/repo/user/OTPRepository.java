package com.bolenum.repo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.OTP;

public interface OTPRepository extends JpaRepository<OTP, Long> {

	OTP findByOtpNumber(Integer otp);
}
