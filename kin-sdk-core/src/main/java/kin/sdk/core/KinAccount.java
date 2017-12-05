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
     * Exports the keystore json file
     *
     * @param passphrase the passphrase used to create the account
     * @param newPassphrase the exported json will be encrypted using this new passphrase. The original keystore and
     * passphrase will not change.
     * @return String the json string
     */
    String exportKeyStore(String passphrase, String newPassphrase) throws PassphraseException, OperationFailedException;

    /**
     * Create, sign and send a transaction of the given amount in kin to the specified public address
     * Ethereum gas will be handled internally.
     * The method will run on a background thread and callback calls will be done
     * on the main thread
     *
     * @param publicAddress the account address to send the specified kin amount
     * @param amount the amount of kin to transfer
     */
    Request<TransactionId> sendTransaction(String publicAddress, String passphrase, BigDecimal amount);

    /**
     * Create, sign and send a transaction of the given amount in kin to the specified public address
     * Ethereum gas will be handled internally.
     * The method will accesses a blockchain
     * node on the network and should not be called on the android main thread.
     *
     * @param publicAddress the account address to send the specified kin amount
     * @param amount the amount of kin to transfer
     * @return TransactionId the transaction identifier
     */
    TransactionId sendTransactionSync(String publicAddress, String passphrase, BigDecimal amount)
        throws InsufficientBalanceException, OperationFailedException, PassphraseException;

    /**
     * Get the current confirmed balance in kin
     * The method will run on a background thread and callback calls will be done
     * on the main thread
     */
    Request<Balance> getBalance();

    /**
     * Get the current confirmed balance in kin
     * The method will accesses a blockchain
     * node on the network and should not be called on the android main thread.
     *
     * @return Balance the balance in kin
     */
    Balance getBalanceSync() throws OperationFailedException;

    /**
     * Get the pending balance in kin
     * The method will run on a background thread and callback calls will be done
     * on the main thread
     *
     * @return BigDecimal the balance in kin
     */
    Request<Balance> getPendingBalance();

    /**
     * Get the pending balance in kin
     * The method will accesses a blockchain
     * node on the network and should not be called on the android main thread.
     *
     * @return Balance the balance amount in kin
     */
    Balance getPendingBalanceSync() throws OperationFailedException;
}
