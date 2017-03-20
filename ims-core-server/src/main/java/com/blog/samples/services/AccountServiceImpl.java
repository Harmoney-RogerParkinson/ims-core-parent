package com.blog.samples.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.blog.samples.webservices.Account;
import com.blog.samples.webservices.EnumAccountStatus;
import com.harmoney.ims.core.server.web.Scheduler;

/**
 * The Class AccountService.
 */
@Service
public class AccountServiceImpl implements AccountService
{
	
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    
    public AccountServiceImpl() {
    	log.debug("instantiating AccountServiceImpl");
    }
	
	/**
	 * Gets the account details.
	 *
	 * @param accountNumber the account number
	 * @return the account details
	 */
	public Account getAccountDetails(String accountNumber)
	{

		/* hard coded account data - in reality this data would be retrieved
		 * from a database or back end system of some sort */
		Account account = new Account();
		account.setAccountNumber("12345");
		account.setAccountStatus(EnumAccountStatus.ACTIVE);
		account.setAccountName("Joe Bloggs");
		account.setAccountBalance(3400);
		
		return account;
	}
}
