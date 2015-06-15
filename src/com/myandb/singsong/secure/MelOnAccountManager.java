package com.myandb.singsong.secure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;

/**
 * MelOn Account Manager
 * @author josuyeong
 *
 */
public class MelOnAccountManager  {
	
	public static final class EasyLoginAccount {
		
		private final Account account;
		private final String authToken;
		
		public EasyLoginAccount(Account account, String authToken) {
			this.account = account;
			this.authToken = authToken;
		}
		
		public String getUsername() {
			return account.name;
		}
		
		public String getAuthToken() {
			return authToken;
		}
	}
	
	/**
	 * Account type string.
	 */
	private static final String ACCOUNT_TYPE = "com.iloen.auth.login";

	/**
	 * Authtoken type string.
	 */
	private static final String AUTHTOKEN_TYPE = "com.iloen.auth.login";
	
	private static final int MAX_ACCOUNT_STORE_NUM = 3;

	private final AccountManager mAccountManager;
	
	private Account[] accounts;

	/**
	 * Melon Account Manager Constructor
	 * @param context
	 */
	public MelOnAccountManager(Context context) {
		if(context == null) throw new IllegalArgumentException("context is null");
		mAccountManager = AccountManager.get(context);
	}

	/**
	 * get android.accounts.AccountManager
	 * @return AccountManager
	 */
	public AccountManager getAccountManager(){
		return mAccountManager;
	}

	/**
	 * Lists all accounts of a melon account type.
	 * @return
	 */
	public Account[] getMelOnAccounts(){
		if (accounts == null) {
			accounts = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
		}
		return accounts;
	}
	
	public boolean hasMelOnAccounts() {
		return getMelOnAccounts().length > 0;
	}
	
	public boolean hasMelOnAccount(final String userId) {
		return getMelOnAccount(userId) != null;
	}
	
	public List<EasyLoginAccount> getEasyLoginAccounts() {
		List<EasyLoginAccount> easyLoginAccounts = new ArrayList<EasyLoginAccount>();
		Account[] accounts = getMelOnAccounts();
		for (Account account : accounts) {
			try {
				String authToken = getAuthToken(account, false);
				EasyLoginAccount easyLoginAccount = new EasyLoginAccount(account, authToken);
				easyLoginAccounts.add(easyLoginAccount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return easyLoginAccounts;
	}
	
	/**
	 * Account of the particular melon user.
	 * @param userId
	 * @return
	 */
	public Account getMelOnAccount(final String userId){
		Account[] accounts = getMelOnAccounts();
		Account myAccount = null;
		for (Account account : accounts) {
			if(account.name.equalsIgnoreCase(userId)) {
				myAccount = account;
				break;
			}
		}
		return myAccount;
	}


	/**
	 * Asks the user to add an account of melon account type.
	 * this method must not be used on the main thread.
	 * @param userId
	 * @param authToken
	 * @return result of add account
	 */
	public boolean addMelOnAccount(String userId, String authToken){
		if (getMelOnAccounts().length < MAX_ACCOUNT_STORE_NUM) {
			Account account = new Account(userId, ACCOUNT_TYPE);
			Bundle userdata = new Bundle();
			userdata.putString(AccountManager.KEY_ACCOUNT_NAME, userId);
			userdata.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
			userdata.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			boolean isAdded = mAccountManager.addAccountExplicitly(account, "", userdata);
			mAccountManager.setAuthToken(account, AUTHTOKEN_TYPE, authToken);
			return isAdded;
		} else {
			Account account = getMelOnAccount(userId);
			if (account != null) {
				mAccountManager.setAuthToken(account, AUTHTOKEN_TYPE, authToken);
				return true;
			} else {
				throw new IllegalStateException();
			}
		}
	}
	
	public boolean canAddMelOnAccount(String userId) {
		if (getMelOnAccounts().length < MAX_ACCOUNT_STORE_NUM) {
			return true;
		} else {
			Account account = getMelOnAccount(userId);
			if (account != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes an account from MelOnAccountManager
	 * this method must not be used on the main thread.
	 * @param userId
	 * @return
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	public boolean removeMelOnAccount(String userId) throws OperationCanceledException, AuthenticatorException, IOException{
		Account account = getMelOnAccount(userId);
		AccountManagerFuture<Boolean> result = mAccountManager.removeAccount(account, null, null);
		return result.getResult();
	}

	/**
	 * Removes an account from MelOnAccountManager
	 * this method must not be used on the main thread.
	 * @param account
	 * @return
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	public boolean removeAccount(Account account) throws OperationCanceledException, AuthenticatorException, IOException{
		AccountManagerFuture<Boolean> result = mAccountManager.removeAccount(account, null, null);
		return result.getResult();
	}

	/**
	 * Gets an auth token of melon account type for particular user
	 * this method must not be used on the main thread.
	 * @param userId
	 * @param notifyAuthFailure
	 * @return auth token
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	public String getMelOnAuthToken(String userId, boolean notifyAuthFailure) throws OperationCanceledException, AuthenticatorException, IOException{
		Account account = getMelOnAccount(userId);		
		return getAuthToken(account, notifyAuthFailure);		
	}

	/**
	 * Gets an auth token of account type for particular account
	 * this method must not be used on the main thread.
	 * @throws IOException 
	 * @throws AuthenticatorException 
	 * @throws OperationCanceledException 
	 * 
	 * 
	 */
	public String getAuthToken(Account account, boolean notifyAuthFailure) throws OperationCanceledException, AuthenticatorException, IOException {
		return mAccountManager.blockingGetAuthToken(account, account.type, notifyAuthFailure);
	}

	/**
	 * Gets an auth token of specified type for a particular user
	 * this method must not be used on the main thread.
	 * @param userId
	 * @param accountType
	 * @param notifyAuthFailure
	 * @return auth token
	 * @throws OperationCanceledException
	 * @throws AuthenticatorException
	 * @throws IOException
	 */
	public String getAuthToken(String userId, String accountType, boolean notifyAuthFailure) throws OperationCanceledException, AuthenticatorException, IOException{
		Account[] accounts = mAccountManager.getAccountsByType(accountType);
		for (Account account : accounts) {
			if(account.name.equalsIgnoreCase(userId)) {
				return getAuthToken(account, notifyAuthFailure);
			}
		}		
		return null;
	}

	/**
	 * Adds an auth token to the MelOnAccountManager cache for an user.
	 * this method must not be used on the main thread.
	 * 
	 * @param userId
	 * @param authToken
	 */
	public void setMelOnAuthToken(String userId, String authToken){
		Account account = getMelOnAccount(userId);
		mAccountManager.setAuthToken(account, AUTHTOKEN_TYPE, authToken);
	}
	/**
	 * Adds an auth token to the MelOnAccountManager cache for an account
	 * this method must not be used on the main thread.
	 * @param account
	 * @param authTokenType
	 * @param authToken
	 */
	public void setAuthToken(Account account, String authTokenType, String authToken){
		mAccountManager.setAuthToken(account, authTokenType, authToken);
	}	

	/**
	 * List the available accounts
	 * @return Account
	 */
	public Account[] getAvailableAccounts() {
		return mAccountManager.getAccounts();
	}

	/**
	 * Removes an auth token from the MelOnAccountManager's cache
	 * this method must not be used on the main thread.
	 * @param authToken
	 */
	public void invalidateAuthToken(String accountType, String authToken) {
		mAccountManager.invalidateAuthToken(accountType, authToken);
	}


	
}
