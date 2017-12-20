/**
 * 
 */
package com.bolenum.services.user.trade;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolenum.model.User;
import com.bolenum.model.coin.UserCoin;
import com.bolenum.repo.common.coin.UserCoinRepository;
import com.bolenum.services.admin.CurrencyService;
import com.bolenum.services.common.coin.Erc20TokenService;
import com.bolenum.services.user.notification.NotificationService;
import com.bolenum.util.GenericUtils;

/**
 * @author chandan kumar singh
 * @date 19-Dec-2017
 */
@Service
public class TradeTransactionServiceImpl implements TradeTransactionService {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(TradeTransactionServiceImpl.class);

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserCoinRepository userCoinRepository;
	@Autowired
	private Erc20TokenService erc20TokenService;

	@Override
	public Boolean performTradeTransaction(String currencyAbr, double qtyTraded, User buyer, User seller, boolean isFee,
			Long tradeId) {

		String currencyType = currencyService.findByCurrencyAbbreviation(currencyAbr).getCurrencyType().toString();
		String msg = "";
		String msg1 = "";
		logger.debug("perform transaction for admin fee: {}", isFee);
		if (!isFee) {
			msg = "Hi " + seller.getFirstName() + ", Your transaction of selling "
					+ GenericUtils.getDecimalFormatString(qtyTraded) + " " + currencyAbr
					+ " have been processed successfully!";
			msg1 = "Hi " + buyer.getFirstName() + ", Your transaction of buying "
					+ GenericUtils.getDecimalFormatString(qtyTraded) + " " + currencyAbr
					+ " have been processed successfully!";
		}
		Boolean txStatus;
		switch (currencyType) {
		case "CRYPTO":
			if (currencyAbr == "BTC") {
				logger.debug("BTC trade started");
				txStatus = performBtcTrade(seller,currencyAbr, buyer, qtyTraded, tradeId);
				logger.debug("is BTC trade successed: {}", txStatus);
				/**
				 * if trade for users, then return result with mail notification
				 * to users
				 */
				if (txStatus && !isFee) {
					notificationService.sendNotification(seller, msg);
					notificationService.saveNotification(buyer, seller, msg);
					notificationService.sendNotification(buyer, msg1);
					notificationService.saveNotification(buyer, seller, msg1);
					return true;
				}
				/**
				 * if transaction for admin, then return result without mail
				 * notification
				 */
				if (txStatus && isFee) {
					return true;
				}
			} else if (currencyAbr == "ETH") {
				logger.debug("ETH transaction started");
				txStatus = performEthTrade(seller, currencyAbr, buyer, qtyTraded, tradeId);
				logger.debug("is ETH transaction successed: {}", txStatus);
				/**
				 * if transaction for users, then return result with mail
				 * notification to users
				 */
				if (txStatus && !isFee) {
					notificationService.sendNotification(seller, msg);
					notificationService.saveNotification(buyer, seller, msg);
					notificationService.sendNotification(buyer, msg1);
					notificationService.saveNotification(buyer, seller, msg1);
					logger.debug("Message : {}", msg);
					logger.debug("Message : {}", msg1);
					return true;
				}
				/**
				 * if transaction for admin, then return result without mail
				 * notification
				 */
				if (txStatus && isFee) {
					return true;
				}
			}
			break;
		case "ERC20TOKEN":
			logger.debug("ERC20TOKEN trade started");
			txStatus = performErc20Trade(seller, currencyAbr, buyer, qtyTraded, tradeId);
			logger.debug("is ERC20TOKEN trade successed: {}", txStatus);
			/**
			 * if transaction for users, then return result with mail
			 * notification to users
			 */
			if (txStatus && !isFee) {
				notificationService.sendNotification(seller, msg);
				notificationService.saveNotification(buyer, seller, msg);
				notificationService.sendNotification(buyer, msg1);
				notificationService.saveNotification(buyer, seller, msg1);
				logger.debug("Message : {}", msg);
				logger.debug("Message : {}", msg1);
				return txStatus;
			}
			/**
			 * if transaction for admin, then return result without mail
			 * notification
			 */
			if (txStatus && isFee) {
				return txStatus;
			}
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * @description perform Erc20token Trade
	 * @param seller
	 * @param currencyAbr
	 * @param buyer
	 * @param qtyTraded
	 * @param tradeId
	 * @return Boolean
	 * 
	 */
	@Override
	public Boolean performErc20Trade(User seller, String currencyAbr, User buyer, double qtyTraded, Long tradeId) {
		UserCoin userErc20TokenSeller = userCoinRepository.findByTokenNameAndUser(currencyAbr, seller);
		UserCoin userErc20TokenBuyer = userCoinRepository.findByTokenNameAndUser(currencyAbr, buyer);
		// TODO remove once existing users has created their wallet
		if (userErc20TokenSeller == null || userErc20TokenBuyer == null) {
			erc20TokenService.createErc20Wallet(buyer, currencyAbr);
			erc20TokenService.createErc20Wallet(seller, currencyAbr);
			return false;
		}
		if (userErc20TokenSeller.getBalance() > 0) {
			Double newBalanceSeller = userErc20TokenSeller.getBalance() - qtyTraded;
			Double newBalanceBuyer = userErc20TokenBuyer.getBalance() + qtyTraded;

			logger.debug("seller: {}, existing balance: {} {}", seller.getEmailId(), userErc20TokenSeller.getBalance(),
					currencyAbr);
			logger.debug("After trade seller:{}, new balance: {} {}", seller.getEmailId(),
					GenericUtils.getDecimalFormatString(newBalanceSeller), currencyAbr);
			userErc20TokenSeller.setBalance(newBalanceSeller);

			logger.debug("buyer: {}, existing balance: {} {}", buyer.getEmailId(), userErc20TokenBuyer.getBalance(),
					currencyAbr);
			logger.debug("After trade buyer:{}, new balance: {} {}", buyer.getEmailId(),
					GenericUtils.getDecimalFormatString(newBalanceBuyer), currencyAbr);

			userErc20TokenBuyer.setBalance(newBalanceBuyer);
			logger.debug("saving trade transaction started for: {} tradeId: {}", currencyAbr, tradeId);
			userCoinRepository.save(userErc20TokenBuyer);
			userCoinRepository.save(userErc20TokenSeller);
			logger.debug("saving trade transaction completed for: {} tradeId: {}", currencyAbr, tradeId);
			return true;
		}
		return false;
	}

	/**
	 * @description perform eth Trade
	 * @param seller
	 * @param currencyAbr
	 * @param buyer
	 * @param qtyTraded
	 * @param tradeId
	 * @return Boolean
	 * 
	 */
	@Override
	public Boolean performEthTrade(User seller, String currencyAbr, User buyer, double qtyTraded, Long tradeId) {
		return null;
	}

	/**
	 * @description perform btc Trade
	 * @param seller
	 * @param currencyAbr
	 * @param buyer
	 * @param qtyTraded
	 * @param tradeId
	 * @return Boolean
	 * 
	 */
	@Override
	public Boolean performBtcTrade(User seller, String currencyAbr, User buyer, double qtyTraded, Long tradeId) {
		return null;
	}
}
