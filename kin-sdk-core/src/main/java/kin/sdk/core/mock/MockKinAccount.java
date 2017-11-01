package kin.sdk.core.mock;

import java.math.BigDecimal;

import kin.sdk.core.Balance;
import kin.sdk.core.KinAccount;
import kin.sdk.core.ResultCallback;
import kin.sdk.core.TransactionId;
import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;

public class MockKinAccount implements KinAccount {

    @Override
    public String getPublicAddress() {
        return "thePublicAddress";
    }

    @Override
    public String getPrivateKey(String passphrase) throws PassphraseException {
        return "thePrivateKey";
    }

    @Override
    public void sendTransaction(String publicAddress, String passphrase, BigDecimal amount,
                                ResultCallback<TransactionId> callback) {
        callback.onResult(new MockTransactionId());
    }

    @Override
    public TransactionId sendTransactionSync(String publicAddress, String passphrase, BigDecimal amount)
            throws InsufficientBalanceException, OperationFailedException, PassphraseException {
        return new MockTransactionId();
    }

    @Override
    public void getBalance(ResultCallback<Balance> callback) {
        callback.onResult(new MockBalance());
    }

    @Override
    public Balance getBalanceSync() throws OperationFailedException {
        return new MockBalance();
    }

    @Override
    public void getPendingBalance(ResultCallback<Balance> callback) {
        callback.onResult(new MockBalance());
    }

    @Override
    public Balance getPendingBalanceSync() throws OperationFailedException {
        return new MockBalance();
    }
}

