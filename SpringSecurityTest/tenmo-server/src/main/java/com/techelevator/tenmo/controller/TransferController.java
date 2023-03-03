package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.BuildingAccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.BuildingTransfer;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferDao transferDao;
    private UserDao userDao;
    private AccountDao accountDao;
    private BuildingAccountDao buildingAccountDao;

    public TransferController(TransferDao transferDao, UserDao userDao, AccountDao accountDao, BuildingAccountDao buildingAccountDao){
        this.transferDao = transferDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.buildingAccountDao = buildingAccountDao;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public void sendTransfer(@RequestBody BuildingTransfer sendTransfer, Principal principal){
        Transfer transfer = new Transfer();
        transfer.setAmount(sendTransfer.getAmount());
        transfer.setAccountFrom(sendTransfer.getAccountFrom());
        transfer.setAccountTo(sendTransfer.getAccountTo());
        transferDao.createSendTransfer(transfer);
        BigDecimal d = new BigDecimal(0);
        int ownerId = principalAccountId(principal);
        boolean transferAmountEnteredGreaterThanZero = transfer.getAmount().compareTo(d) > 0;
        boolean balanceGreaterThanAmount = accountDao.getBalance(ownerId).compareTo(sendTransfer.getAmount()) >= 0;
        boolean fromAccountDoesntEqualPrincipalAccount = sendTransfer.getAccountFrom() == principalAccountId(principal);
        if(transferAmountEnteredGreaterThanZero && balanceGreaterThanAmount && fromAccountDoesntEqualPrincipalAccount) {
            transferDao.transferFrom(sendTransfer);
            transferDao.transferTo(sendTransfer);
        } else {
            System.out.println("Sorry something went wrong please try again.");
        }

    }

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    public void requestTransfer(@RequestBody BuildingTransfer sendTransfer, Principal principal){
        Transfer transfer = new Transfer();
        transfer.setAmount(sendTransfer.getAmount());
        transfer.setAccountFrom(sendTransfer.getAccountFrom());
        transfer.setAccountTo(sendTransfer.getAccountTo());
        BigDecimal d = new BigDecimal(0);
        int ownerId = principalAccountId(principal);
        boolean transferAmountEnteredGreaterThanZero = transfer.getAmount().compareTo(d) > 0;
        int fromAccount = sendTransfer.getAccountFrom();
        boolean fromAccountDoesntEqualPrincipalAccount = fromAccount == ownerId;
        if(transferAmountEnteredGreaterThanZero && fromAccountDoesntEqualPrincipalAccount){
            transferDao.createRequestTransfer(transfer);
        }
    }



    @RequestMapping(value = "/approveRequest/{id}", method = RequestMethod.PUT)
    public void approveRequest(@PathVariable int id, Principal principal){
        Transfer transfer = transferDao.getTransferByTransferID(id);
        int principalAID = principalAccountId(principal);
        boolean balanceGreaterThanAmount = accountDao.getBalance(principalAID).compareTo(transfer.getAmount()) >= 0;
        boolean fromAccountDoesntEqualPrincipalAccount = transfer.getAccountFrom() != principalAccountId(principal);
        boolean toAccountIsPrincipalAccount = transfer.getAccountTo() == principalAccountId(principal);
        boolean isTransferStatus1 = transfer.getTransferStatusId() == 1;
        if(isTransferStatus1 && balanceGreaterThanAmount && fromAccountDoesntEqualPrincipalAccount && toAccountIsPrincipalAccount) {
            transferDao.transferPendingFrom(transfer);
            transferDao.transferPendingTo(transfer);
            transferDao.approveRequest(id);
        } else {
            System.out.println("Sorry the amount is greater than the balance.");
        }

    }

    @RequestMapping(value = "/declineRequest/{id}", method = RequestMethod.PUT)
    public void declineRequest(@PathVariable int id, Principal principal){
        Transfer transfer = transferDao.getTransferByTransferID(id);
        boolean isTransferStatus1 = transfer.getTransferStatusId() == 1;
        boolean toAccountIsPrincipalAccount = transfer.getAccountTo() == principalAccountId(principal);
        if(toAccountIsPrincipalAccount && isTransferStatus1) {
            transferDao.declineRequest(id);
            System.out.println("Request declined");
        }
    }


    @RequestMapping(value = "/getTransfer/{id}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable int id){
        return transferDao.getTransferByTransferID(id);
    }

    @RequestMapping(value = "/listPrincipalTransfers", method = RequestMethod.GET)
    public List<Transfer> listPrincipalTransfers(Principal principal){
        int id = principalAccountId(principal);
        List<Transfer> toTransfers = transferDao.listToTransfers(id);
        List<Transfer> fromTransfers = transferDao.listFromTransfers(id);
        List<Transfer> toAndFromTransfers = new ArrayList<>();
        toAndFromTransfers.addAll(toTransfers);
        toAndFromTransfers.addAll(fromTransfers);
        return toAndFromTransfers;
    }

    @RequestMapping(value = "/listPendingTransfers", method = RequestMethod.GET)
    public List<Transfer> listPendingTransfers(Principal principal){
        int id = principalAccountId(principal);
        List<Transfer> fromTransfers = transferDao.listPendingFromTransfers(id);
        List<Transfer> toTransfers = transferDao.listPendingToTransfers(id);
        List<Transfer> toAndFromTransfers = new ArrayList<>();
        toAndFromTransfers.addAll(toTransfers);
        toAndFromTransfers.addAll(fromTransfers);
        return toAndFromTransfers;
    }

    private int principalAccountId(Principal principal){
        int id1 = userDao.findIdByUsername((principal.getName()));
        int id2 = userDao.getPrincipalAccountIdFromUserId(id1);
        return id2;
    }

}
