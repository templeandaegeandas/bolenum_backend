/**
 * 
 */
package com.bolenum.config.security;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.bolenum.model.User;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public class ApplicationUserDetail implements UserDetails,Serializable {

	private static final long serialVersionUID = 1L;
	
	transient User user;

	public ApplicationUserDetail(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
//		List<GrantedAuthority> authList=new ArrayList<>();
//		GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getPrivileges().toString());
//		authList.add(authority);
//		return authList;
		return AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole().getName());
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmailId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !user.getIsLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getIsEnabled();
	}

}
