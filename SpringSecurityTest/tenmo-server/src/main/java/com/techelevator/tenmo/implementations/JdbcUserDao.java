package com.techelevator.tenmo.implementations;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.security.Principal;

@Component
public class JdbcUserDao implements UserDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
        }
        String sql2 = "INSERT INTO account (user_id, balance) VALUES (?, ?)";
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        try {
            jdbcTemplate.update(sql2, newUserId, initialBalance);
        } catch (DataAccessException e){
            return false;
        }
        return true;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }

    public User getUserById(int id){
        String sql = "SELECT user_id, username, passwrod_hash FROM tenmo_user WHERE user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if(result.next()){
            return mapRowToUser(result);
        } else {
            return null;
        }
    }

    @Override
    public boolean isPrincipalAccount(Principal principal, int id){
        boolean isAccount = false;
        String sql = "SELECT account_id FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE  tenmo_user.user_id = ?";
        Integer id1 = jdbcTemplate.queryForObject(sql, Integer.class, principal);
        if (id1 == id){
            isAccount = true;
        }
        return isAccount;
    }

    @Override
    public int principalUserId(Principal principal){
        int principalId = 0;
        String sql = "SELECT account_id FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE  tenmo_user.user_id = ?";
        Integer id1 = jdbcTemplate.queryForObject(sql, Integer.class, principal);
        principalId = id1;
        return principalId;
    }

    @Override
    public int selectedUserId(User user){
        int selectedId = 0;
        String sql = "SELECT user_id FROM tenmo_user WHERE user_id = ?";
        selectedId = jdbcTemplate.queryForObject(sql, Integer.class, user);
        return selectedId;
    }

    @Override
    public int getPrincipalAccountIdFromUserId(int id){
        int principalAccountId = 0;
        String sql = "SELECT account_id FROM account JOIN tenmo_user ON tenmo_user.user_id = account.user_id WHERE tenmo_user.user_id = ?";
        principalAccountId = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return principalAccountId;
    }


}
