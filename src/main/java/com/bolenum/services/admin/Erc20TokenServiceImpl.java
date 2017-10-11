package com.bolenum.services.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.bolenum.model.Erc20Token;
import com.bolenum.repo.admin.Erc20TokenRepository;

/**
 * 
 * @author Vishal Kumar
 * @date 04-Oct-2017
 *
 */
@Service
public class Erc20TokenServiceImpl implements Erc20TokenService{
	
	@Value("${bolenum.deployed.contract.address}")
	private String contractAddress;
	
	@Value("${bolenum.deployed.contract.wallet.address}")
	private String walletAddress;
	
	@Value("${bolenum.deployed.contract.binary}")
	private String binaryKey;
	
	@Autowired
	private Erc20TokenRepository erc20TokenRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(Erc20TokenServiceImpl.class);

	@Override
	public Erc20Token saveToken(Erc20Token erc20Token) {
		Erc20Token existingToken = erc20TokenRepository.findByContractAddress(erc20Token.getContractAddress());
		if (existingToken==null) {
			return erc20TokenRepository.save(erc20Token);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Page<Erc20Token> listAllErc20Token(int pageNumber, int pageSize, String sortBy, String sortOrder, String searchData) {
		Direction sort;
		if (sortOrder.equals("desc")) {
			sort = Direction.DESC;
		}
		else {
			sort = Direction.ASC;
		}
		Pageable pageRequest = new PageRequest(pageNumber, pageSize, sort, sortBy);
		Page<Erc20Token> tokenList = erc20TokenRepository.findByContractAddressOrWalletAddressLike(searchData, pageRequest);
		return tokenList;
	}
	
	@Override
	public Erc20Token getById(Long id) {
		return erc20TokenRepository.findOne(id);
	}
	
	@Override
	public Erc20Token saveBolenumErc20Token() {
		logger.debug("Bolenum binary key is: {}",binaryKey);
		logger.debug("Bolenum contract address is: {}",contractAddress);
		logger.debug("Bolenum deployed wallet address is: {}",walletAddress);
		Erc20Token token = erc20TokenRepository.findByContractAddress(contractAddress);
		if (token == null) {
			Erc20Token erc20Token = new Erc20Token(null, walletAddress, contractAddress);
			return erc20TokenRepository.save(erc20Token);
		}
		else {
			return null;
		}
	}
}
