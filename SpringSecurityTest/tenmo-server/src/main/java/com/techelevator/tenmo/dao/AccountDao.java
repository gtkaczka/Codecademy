package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.BuildingAccount;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface AccountDao {

   BigDecimal getBalance(int id);

   public int getPrincipalAccount(Principal principal);

}
