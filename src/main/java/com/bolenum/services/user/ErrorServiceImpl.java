/*@Description Of Class
 * 
 * ErrorServiceImpl class is responsible for below listed task: 
 *   
 *   Save error   
 */
package com.bolenum.services.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.Error;
import com.bolenum.repo.user.ErrorRepository;

@Service
public class ErrorServiceImpl implements ErrorService {
	
	@Autowired
	private ErrorRepository errorRepository;

	/**@Description use to save error
	 * @param error
	 * @return error
	 */
	@Override
	public Error saveError(Error error) {
		return errorRepository.save(error);
	}
}
