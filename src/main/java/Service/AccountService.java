package Service;

import Model.Account;
import DAO.AccountDAO;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    public Account registerAccount(Account account) {
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            return null;
        }
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            return null;
        }
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return null;
        }
        return accountDAO.createAccount(account);
    }

    public Account login(String username, String password) {
        Account account = accountDAO.getAccountByUsername(username);
        if (account != null && account.getPassword().equals(password)) {
            return account;
        }
        return null;
    }
} 