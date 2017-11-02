package kin.sdk.core.impl;

import java.math.BigDecimal;

import kin.sdk.core.Balance;
import kin.sdk.core.KinAccount;
import kin.sdk.core.ResultCallback;
import kin.sdk.core.TransactionId;
import kin.sdk.core.concurrent.Concurrency;
import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;

/**
 */

public abstract class AbstractKinAccount implements KinAccount {

    private final Concurrency concurrency = Concurrency.getInstance();

    @Override
    public void sendTransaction(final String publicAddress, final String passphrase, final BigDecimal amount, final ResultCallback<TransactionId> callback) {
        concurrency.executeOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    final TransactionId transactionId = sendTransactionSync(publicAddress, passphrase, amount);
                    concurrency.executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(transactionId);
                        }
                    });
                } catch (final Exception e) {
                    concurrency.executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getBalance(final ResultCallback<Balance> callback) {
        concurrency.executeOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    final Balance balance = getBalanceSync();
                    concurrency.executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(balance);
                        }
                    });

                } catch (final Exception e) {
                    concurrency.executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getPendingBalance(final ResultCallback<Balance> callback) {
        try {
            final Balance pendingBalance = getPendingBalanceSync();
            concurrency.executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(pendingBalance);
                }
            });
        } catch (final Exception e) {
            concurrency.executeOnMainThread(new Runnable() {
                @Override
                public void run() {
                    callback.onError(e);
                }
            });
        }
    }
}
