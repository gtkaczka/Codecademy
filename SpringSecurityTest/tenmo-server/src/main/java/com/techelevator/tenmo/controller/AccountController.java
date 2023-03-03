package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.BuildingAccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.BuildingAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;
    private BuildingAccountDao buildingAccountDao;

    public AccountController(AccountDao accountDao, UserDao userDao, BuildingAccountDao buildingAccountDao){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.buildingAccountDao = buildingAccountDao;
    }

    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public BigDecimal getIndividualBalance(Principal balance){
        int userId = userDao.findIdByUsername(balance.getName());
        int accountId = userDao.getPrincipalAccountIdFromUserId(userId);
        BigDecimal accountBalance = accountDao.getBalance(accountId);
        return accountBalance;
    }

    @RequestMapping(path = "/principalAccount", method = RequestMethod.GET)
    public int getPrincipalAccount(Principal principal){
        int principalAccount = accountDao.getPrincipalAccount(principal);
        return principalAccount;
    }

    @RequestMapping(path = "/getAllAccounts", method = RequestMethod.GET)
    public List<BuildingAccount> listAllAccounts(){
        List<BuildingAccount> allAccounts = buildingAccountDao.getAllAccounts();
        return allAccounts;
    }


}
