package com.bolenum.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bolenum.constant.UrlConstant;
import com.bolenum.model.coin.Erc20Token;
import com.bolenum.services.common.LocaleService;
import com.bolenum.services.common.coin.Erc20TokenService;
import com.bolenum.util.ResponseHandler;

/**
 * 
 * @author Vishal Kumar
 * @date 04-Oct-2017
 *
 */
@RestController
@RequestMapping(value = UrlConstant.BASE_ADMIN_URI_V1)
public class Erc20TokenController {

	public static final Logger logger = LoggerFactory.getLogger(Erc20TokenController.class);

	@Autowired
	private Erc20TokenService erc20TokenService;

	@Autowired
	private LocaleService localeService;

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.ADD_NEW_TOKEN, method = RequestMethod.POST)
	public ResponseEntity<Object> addNewToken(@RequestBody Erc20Token erc20Token) {
		Erc20Token savedToken = erc20TokenService.saveToken(erc20Token);
		if (savedToken != null) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("erc20.new.token.saved"),
					savedToken);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("erc20.new.token.notsaved"), savedToken);
		}
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.GET_TOKEN_LIST, method = RequestMethod.GET)
	public ResponseEntity<Object> getAllTokens(@RequestParam("pageNumber") int pageNumber,
			@RequestParam("pageSize") int pageSize, @RequestParam("sortBy") String sortBy,
			@RequestParam("sortOrder") String sortOrder) {
		Page<Erc20Token> erc20TokenList = erc20TokenService.listAllErc20Token(pageNumber, pageSize, sortBy, sortOrder);
		return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("erc20.token.list"), erc20TokenList);
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = UrlConstant.GET_TOKEN_BY_ID, method = RequestMethod.GET)
	public ResponseEntity<Object> getTokenByTokenId(@RequestParam("id") Long id) {
		Erc20Token erc20Token = erc20TokenService.getById(id);
		if (erc20Token != null) {
			return ResponseHandler.response(HttpStatus.OK, false, localeService.getMessage("erc20.token"), erc20Token);
		} else {
			return ResponseHandler.response(HttpStatus.BAD_REQUEST, true,
					localeService.getMessage("erc20.token.notfound"), null);
		}
	}

}
