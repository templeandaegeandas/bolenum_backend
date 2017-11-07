package com.bolenum.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.Error;
import com.bolenum.repo.user.ErrorRepository;

@Service
public class ErrorServiceImpl implements ErrorService {
	
	@Autowired
	private ErrorRepository errorRepository;

	@Override
	public Error saveError(Error error) {
		return errorRepository.save(error);
	}
}
