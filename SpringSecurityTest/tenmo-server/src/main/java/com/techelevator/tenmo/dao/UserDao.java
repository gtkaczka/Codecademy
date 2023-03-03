package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.security.Principal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    boolean isPrincipalAccount(Principal principal, int id);

    int principalUserId(Principal principal);

    int selectedUserId(User user);

    User getUserById(int id);

    public int getPrincipalAccountIdFromUserId(int id);


}
