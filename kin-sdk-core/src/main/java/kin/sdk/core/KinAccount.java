package kin.sdk.core;

import java.math.BigDecimal;

import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;

public interface KinAccount {

    /**
     * @return String the public address of the account
     */
    String getPublicAddress();

    /**
     * @param passphrase the passphrase used when creating the account
     * @return String the private key
     */
    String getPrivateKey(String passphrase) throws PassphraseException;

    /**
     * Create, sign and send a transaction of the given amount in kin to the specified public address
     * Ethereum gas will be handled internally.
     * The method will run on a background thread.
     * @param publicAddress the account address to send the specified kin amount
     * @param amount the amount of kin to transfer
     * @param callback to be called when method has completed
     */
     void sendTransaction(String publicAddress, String passphrase, BigDecimal amount, ResultCallback<TransactionId> callback);

    /**
     * Create, sign and send a transaction of the given amount in kin to the specified public address
     * Ethereum gas will be handled internally.
     * The method will accesses a blockchain
     * node on the network and should not be called on the android main thread.
     * @param publicAddress the account address to send the specified kin amount
     * @param amount the amount of kin to transfer
     * @return TransactionId the transaction identifier
     */
     TransactionId sendTransactionSync(String publicAddress, String passphrase, BigDecimal amount)
                  throws InsufficientBalanceException, OperationFailedException, PassphraseException;

    /**
     * Get the current confirmed balance in kin
     * The method will run on a background thread
     * @param callback to be called when method has completed
     */
     void getBalance(ResultCallback<Balance> callback);

    /**
     * Get the current confirmed balance in kin
     * The method will accesses a blockchain
     * node on the network and should not be called on the android main thread.
     * @return Balance the balance in kin
     */
     Balance getBalanceSync() throws OperationFailedException;

    /**
     * Get the pending balance in kin
     * The method will run on a background thread
     * @param callback to be called when method has completed
     * @return BigDecimal the balance in kin
     */
     void getPendingBalance(ResultCallback<Balance> callback);

    /**
     * Get the pending balance in kin
     * The method will accesses a blockchain
     * node on the network and should not be called on the android main thread.
     * @return Balance the balance amount in kin
     */
     Balance getPendingBalanceSync() throws OperationFailedException;

}
