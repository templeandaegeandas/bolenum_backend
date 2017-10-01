/**
 * 
 */
package com.bolenum.config.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.bolenum.config.security.filter.TokenAuthenticationFilter;
import com.bolenum.constant.UrlConstant;

/**
 * @author chandan kumar singh
 * @date 13-Sep-2017
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private Environment environment;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {

		http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
				.antMatchers(HttpMethod.POST, "/api/v1/user/register").permitAll().anyRequest().authenticated();

		// Implementing Token based authentication in this filter
		final TokenAuthenticationFilter tokenFilter = new TokenAuthenticationFilter();
		http.addFilterBefore(tokenFilter, BasicAuthenticationFilter.class);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/favicon.ico");
		web.ignoring().antMatchers("/api/v1/authorize/role");
		web.ignoring().antMatchers("/api/v1/user/verify");
		web.ignoring().antMatchers("/monitoring");
		web.ignoring().antMatchers("/refresh");
		web.ignoring().antMatchers("/api/v1/forgetpassword");
		web.ignoring().antMatchers("/api/v1/forgetpassword/verify");
		web.ignoring().antMatchers(UrlConstant.BASE_USER_URI_V1 + UrlConstant.VERIFY_2FA_OTP);
		
		
		// Check if Active profiles contains "dev" or "stag"
		if (Arrays.stream(environment.getActiveProfiles())
				.anyMatch(env -> (env.equalsIgnoreCase("dev") || env.equalsIgnoreCase("stag")))) {
			web.ignoring().antMatchers("/resources/**");
			web.ignoring().antMatchers("/swagger-ui.html");
			web.ignoring().antMatchers("/webjars/**");
			web.ignoring().antMatchers("/configuration/**");
			web.ignoring().antMatchers("/swagger-resources/**");
			web.ignoring().antMatchers("/v2/**");
			

		}
	}
}
