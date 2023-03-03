package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.BuildingTransfer;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface TransferDao {


        //use the passed in account id to join to account and update the balance of the account accordingly
        public void transferTo(BuildingTransfer transfer);

        //use the passed in account id to join to account and update the balance of the account accordingly
        public void transferFrom(BuildingTransfer transfer);

        public void transferPendingTo(Transfer transfer);

        public void transferPendingFrom(Transfer transfer);

        public List<Transfer> listFromTransfers(int id);

        public List<Transfer> listToTransfers(int id);

        public List<Transfer> listPendingFromTransfers(int id);

        public List<Transfer> listPendingToTransfers(int id);

        public Transfer createSendTransfer(Transfer transfer);

        public Transfer getTransferByTransferID(int id);

        public Transfer createRequestTransfer(Transfer transfer);

        public void approveRequest(int id);

        public void declineRequest(int id);
        }


