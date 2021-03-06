package com.bolenum.dto.common;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

/**
 * 
 * @author vishal_kumar
 * @date 14-sep-2017
 *
 */

public class PasswordForm {

	@NotNull
	private String oldPassword;
	@NotNull
	@Length(min = 8, max = 64, message = "password length must be between 8 and 64 character")
	@Pattern.List({ @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain one digit."),
			@Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one lowercase letter."),
			@Pattern(regexp = "(?=.*[A-Z]).+", message = "Password must contain one upper letter."),
			@Pattern(regexp = "(?=.*[!@#$%^&*+=?-_()/\"\\.,<>~`;:]).+", message = "Password must contain one special character."),
			@Pattern(regexp = "(?=\\S+$).+", message = "Password must contain no whitespace.") })
	private String newPassword;
	@NotNull
	private String confirmPassword;

	

	/**
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return oldPassword;
	}



	/**
	 * @param oldPassword the oldPassword to set
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}



	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}



	/**
	 * @param newPassword the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}



	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}



	/**
	 * @param confirmPassword the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}



	@AssertTrue(message = "password did not match")
	private boolean isValid() {
		return this.newPassword.equals(this.confirmPassword);
	}

}
