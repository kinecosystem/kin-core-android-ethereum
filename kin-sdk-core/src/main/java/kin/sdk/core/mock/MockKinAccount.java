package kin.sdk.core.mock;

import java.math.BigDecimal;

import kin.sdk.core.Balance;
import kin.sdk.core.TransactionId;
import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;
import kin.sdk.core.impl.AbstractKinAccount;

public class MockKinAccount extends AbstractKinAccount {

    private final long PROCESSING_DURATION = 3000;

    @Override
    public String getPublicAddress() {
        return "#1234567890";
    }

    @Override
    public String getPrivateKey(String passphrase) throws PassphraseException {
        return "PrivateKey";
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

