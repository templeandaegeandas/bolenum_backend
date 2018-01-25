/**
 * 
 */
package com.bolenum.config.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.bolenum.config.security.ApplicationUserDetail;
import com.bolenum.model.AuthenticationToken;
import com.bolenum.repo.common.AuthenticationTokenRepo;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
public class TokenAuthenticationFilter extends GenericFilterBean {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;

		// extract token from header
		String token = httpRequest.getHeader("Authorization");
		if (token != null && !token.isEmpty()) {
			AuthenticationTokenRepo authenticationTokenRepository = WebApplicationContextUtils
					.getRequiredWebApplicationContext(httpRequest.getServletContext())
					.getBean(AuthenticationTokenRepo.class);
			// check whether token is valid
			AuthenticationToken authToken = authenticationTokenRepository.findByToken(token);
			if (authToken != null) {
				// Add user to SecurityContextHolder
				final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						authToken.getUser(), null, new ApplicationUserDetail(authToken.getUser()).getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		chain.doFilter(request, response);
		SecurityContextHolder.clearContext();

	}
}