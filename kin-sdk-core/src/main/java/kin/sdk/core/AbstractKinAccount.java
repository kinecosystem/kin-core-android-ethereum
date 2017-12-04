package kin.sdk.core;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import kin.sdk.core.concurrent.Concurrency;

abstract class AbstractKinAccount implements KinAccount {

    private final Concurrency concurrency = Concurrency.getInstance();

    @Override
    public void sendTransaction(final String publicAddress, final String passphrase, final BigDecimal amount,
        final ResultCallback<TransactionId> callback) {
        concurrency.execute(new Callable<TransactionId>() {
            @Override
            public TransactionId call() throws Exception {
                return sendTransactionSync(publicAddress, passphrase, amount);
            }
        }, callback);
    }

    @Override
    public void getBalance(final ResultCallback<Balance> callback) {
        concurrency.execute(new Callable<Balance>() {
            @Override
            public Balance call() throws Exception {
                return getBalanceSync();
            }
        }, callback);
    }

    @Override
    public void getPendingBalance(final ResultCallback<Balance> callback) {
        concurrency.execute(new Callable<Balance>() {
            @Override
            public Balance call() throws Exception {
                return getPendingBalanceSync();
            }
        }, callback);
    }

    @Override
    public boolean equals(Object obj) {
        KinAccount account = (KinAccount)obj;
        return getPublicAddress().equals(account.getPublicAddress());
    }
}
