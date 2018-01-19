/*@Description Of interface
 * 
 * ErrorService interface is responsible for below listed task: 
 *   
 *   save error    
 **/

package com.bolenum.services.user;

import com.bolenum.model.Error;


public interface ErrorService {

	/**@Description: Use to save error
	 * 
	 * @param error
	 * @return savedError
	 */
	Error saveError(Error error);

}
