/**
 * 
 */
package com.bolenum.services.user.transactions;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import com.bolenum.enums.CurrencyName;
import com.bolenum.enums.TransactionType;
import com.bolenum.model.Transaction;
import com.bolenum.model.User;
import com.bolenum.repo.user.UserRepository;
import com.bolenum.repo.user.transactions.TransactionRepo;
import com.bolenum.util.EthereumServiceUtil;

import rx.Subscription;

/**
 * @author chandan kumar singh
 * @date 29-Sep-2017
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionServiceImpl.class);
	@Autowired
	UserRepository userRepository;

	@Autowired
	TransactionRepo transactionRepo;

	@Override
	public void saveEthereumIncomingTx() {
		logger.debug("methoda called");
		Web3j web3j = EthereumServiceUtil.getWeb3jInstance();
		Subscription subscription = web3j.transactionObservable().subscribe(Transaction -> {
			User user = userRepository.findByEthWalletaddress(Transaction.getTo());
			if (user != null) {
				logger.debug("new Incoming ethereum transaction for user : {}", user.getEmailId());
				Transaction transaction = new Transaction();
				transaction.setTxHash(Transaction.getBlockHash());
				transaction.setFromAddress(Transaction.getFrom());
				transaction.setToAddress(Transaction.getTo());
				//transaction.setGas(Transaction.getGas());
				transaction.setTxAmmount(Transaction.getValue().doubleValue());
				transaction.setTransactionType(TransactionType.INCOMING);
				transaction.setCurrencyType(CurrencyName.ETHEREUM);
				Transaction saved = transactionRepo.saveAndFlush(transaction);
				if (saved != null) {
					logger.debug("new incoming transaction saved of user: {}", user.getEmailId());
				}
			}
		});
		subscription.unsubscribe();
	}
}
