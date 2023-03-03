package com.techelevator.tenmo.implementations;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.BuildingTransfer;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void transferTo(BuildingTransfer transfer){
        String sql = "SELECT balance FROM account WHERE account_id = ? ";
        BigDecimal initialBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, transfer.getAccountTo());
        BigDecimal newBalance = initialBalance.add(transfer.getAmount());
        String sql2 = "UPDATE account SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(sql2, newBalance, transfer.getAccountTo());
    }
    @Override
    public void transferFrom(BuildingTransfer transfer){
        String sql = "SELECT balance FROM account WHERE account_id = ? ";
        BigDecimal initialBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, transfer.getAccountFrom());
        BigDecimal newBalance = initialBalance.subtract(transfer.getAmount());
        String sql2 = "UPDATE account SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(sql2, newBalance, transfer.getAccountFrom());
    }

    @Override
    public void transferPendingTo(Transfer transfer){
        String sql = "SELECT balance FROM account WHERE account_id = ? ";
        BigDecimal initialBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, transfer.getAccountTo());
        BigDecimal newBalance = initialBalance.subtract(transfer.getAmount());
        String sql2 = "UPDATE account SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(sql2, newBalance, transfer.getAccountTo());
    }
    @Override
    public void transferPendingFrom(Transfer transfer){
        String sql = "SELECT balance FROM account WHERE account_id = ? ";
        BigDecimal initialBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, transfer.getAccountFrom());
        BigDecimal newBalance = initialBalance.add(transfer.getAmount());
        String sql2 = "UPDATE account SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(sql2, newBalance, transfer.getAccountFrom());
    }

    @Override
    public List<Transfer> listFromTransfers(int id){
        List<Transfer> listOfTransfers = new ArrayList<>();
        String sql = "SELECT transfer.transfer_id, transfer.transfer_type_id, transfer.transfer_status_id, transfer.account_from, transfer.account_to, transfer.amount " +
                "FROM transfer " +
                "JOIN account ON account.account_id = transfer.account_from " +
                //"JOIN account ON account.account_id = transfer.account_to " +
                "WHERE account.account_id = ? ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            listOfTransfers.add(mapRowToTransfer(results));
        }
        return listOfTransfers;
    }

    @Override
    public List<Transfer> listToTransfers(int id){
        List<Transfer> listOfTransfers = new ArrayList<>();
        String sql = "SELECT transfer.transfer_id, transfer.transfer_type_id, transfer.transfer_status_id, transfer.account_from, transfer.account_to, transfer.amount " +
                "FROM transfer " +
                //"JOIN account ON account.account_id = transfer.account_from " +
                "JOIN account ON account.account_id = transfer.account_to " +
                "WHERE account.account_id = ? ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            listOfTransfers.add(mapRowToTransfer(results));
        }
        return listOfTransfers;
    }

    @Override
    public Transfer getTransferByTransferID(int id){
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer " +
                "WHERE transfer_id = ?;";
         SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }

    @Override
    public List<Transfer> listPendingFromTransfers(int id){
        List<Transfer> listOfTransfers = new ArrayList<>();
        String sql = "SELECT transfer.transfer_id, transfer.transfer_type_id, transfer.transfer_status_id, transfer.account_from, transfer.account_to, transfer.amount " +
                "FROM transfer " +
                "JOIN account ON account.account_id = transfer.account_from " +
                "WHERE account.account_id = ? AND transfer.transfer_status_id = 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            listOfTransfers.add(mapRowToTransfer(results));
        }
        return listOfTransfers;
    }

    @Override
    public List<Transfer> listPendingToTransfers(int id){
        List<Transfer> listOfTransfers = new ArrayList<>();
        String sql = "SELECT transfer.transfer_id, transfer.transfer_type_id, transfer.transfer_status_id, transfer.account_from, transfer.account_to, transfer.amount " +
                "FROM transfer " +
                "JOIN account ON account.account_id = transfer.account_to " +
                "WHERE account.account_id = ? AND transfer.transfer_status_id = 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while(results.next()){
            listOfTransfers.add(mapRowToTransfer(results));
        }
        return listOfTransfers;
    }

    @Override
    public Transfer createSendTransfer(Transfer transfer){
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (2, 2, ?, ?, ?) " +
                "RETURNING transfer_id";
        int transferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());

        transfer.setTransferId(transferId);
        return transfer;
    }

    @Override
    public Transfer createRequestTransfer(Transfer transfer){
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (1, 1, ?, ?, ?) " +
                "RETURNING transfer_id";
        int transferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());

        transfer.setTransferId(transferId);
        return transfer;
    }

    @Override
    public void approveRequest(int id){
        String sql = "UPDATE transfer SET transfer_status_id = 2 WHERE transfer_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void declineRequest(int id){
        String sql = "UPDATE transfer SET transfer_status_id = 3 WHERE transfer_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }
}
