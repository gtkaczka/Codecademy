package com.techelevator.tenmo.implementations;


import com.techelevator.tenmo.dao.BuildingAccountDao;
import com.techelevator.tenmo.model.BuildingAccount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcBuildingAccountDao implements BuildingAccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcBuildingAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<BuildingAccount> getAllAccounts(){
        List<BuildingAccount> listOfAccounts = new ArrayList<>();
        String sql = "SELECT account.account_id, tenmo_user.username FROM account JOIN tenmo_user ON account.user_id = tenmo_user.user_id ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()){
            listOfAccounts.add(mapRowToAccount(results));
        }
        return listOfAccounts;
    }

    private BuildingAccount mapRowToAccount(SqlRowSet rs) {
        BuildingAccount buildingAcc = new BuildingAccount();
        buildingAcc.setAccountId(rs.getInt("account_id"));
        buildingAcc.setAccountName(rs.getString("username"));
        return buildingAcc;
    }

}
