package kin.sdk.core.impl;

import org.ethereum.geth.Account;
import org.ethereum.geth.KeyStore;

import java.math.BigDecimal;

import kin.sdk.core.Balance;
import kin.sdk.core.TransactionId;
import kin.sdk.core.ethereum.EthClientWrapper;
import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;
import kin.sdk.core.mock.MockBalance;
import kin.sdk.core.mock.MockTransactionId;


/**
 * Project - Kin SDK
 * Created by Oren Zakay on 02/11/2017.
 * Copyright © 2017 Kin Foundation. All rights reserved.
 */
public class KinAccountImpl extends AbstractKinAccount {

    private final long PROCESSING_DURATION = 3000;
    private KeyStore keyStore;
    private EthClientWrapper ethClient;
    private Account account;

    public KinAccountImpl(EthClientWrapper ethClientWrapper, String passphrase) throws Exception {
        this.keyStore = ethClientWrapper.getKeyStore();
        this.account = keyStore.newAccount(passphrase);
        this.ethClient = ethClientWrapper;
    }

    @Override
    public String getPublicAddress() {
        return account.getAddress().getHex();
    }

    @Override
    public String getPrivateKey(String passphrase) throws PassphraseException {
        return "0xMock01ForPrivateKey";
    }

    @Override
    public TransactionId sendTransactionSync(String publicAddress, String passphrase, BigDecimal amount)
            throws InsufficientBalanceException, OperationFailedException, PassphraseException {
        try {
            Thread.sleep(PROCESSING_DURATION);
            return new MockTransactionId();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new InsufficientBalanceException();
        }
    }

    @Override
    public Balance getBalanceSync() throws OperationFailedException {
        try {
            Thread.sleep(PROCESSING_DURATION);
            return new MockBalance();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new OperationFailedException();
        }

    }

    @Override
    public Balance getPendingBalanceSync() throws OperationFailedException {
        try {
            Thread.sleep(PROCESSING_DURATION);
            return new MockBalance();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new OperationFailedException();
        }
    }
}
