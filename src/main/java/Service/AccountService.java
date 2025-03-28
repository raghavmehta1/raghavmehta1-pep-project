package com.revature.service;

import com.revature.dao.AccountDAO;
import com.revature.model.Account;

public class AccountService {
    private AccountDAO accountDAO;
    
    public AccountService() {
        accountDAO = new AccountDAO();
    }
    
    public Account registerAccount(Account account) {
        // Validation: username not blank
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            return null;
        }
        
        // Validation: password at least 4 characters
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            return null;
        }
        
        // Validation: username doesn't already exist
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return null;
        }
        
        return accountDAO.createAccount(account);
    }
    
    public Account loginAccount(Account account) {
        return accountDAO.getAccountByUsernameAndPassword(
            account.getUsername(), 
            account.getPassword()
        );
    }
}
