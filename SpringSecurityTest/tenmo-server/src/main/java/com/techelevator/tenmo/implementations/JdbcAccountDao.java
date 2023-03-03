package com.techelevator.tenmo.implementations;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.BuildingAccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.BuildingAccount;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;
    private BuildingAccountDao buildingAccountDao;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate, UserDao userDao, BuildingAccountDao buildingAccountDao){
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
        this.buildingAccountDao = buildingAccountDao;
    }

    @Override
    public BigDecimal getBalance(int id){
        String sql = "SELECT balance FROM account WHERE account_id = ? ";
        BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, id);

        return balance;
    }

    @Override
    public int getPrincipalAccount(Principal principal){
        String sql = "SELECT account.account_id FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE  tenmo_user.username = ?";
        int principalAccount = jdbcTemplate.queryForObject(sql, Integer.class, principal.getName());
        return principalAccount;
    }

}
