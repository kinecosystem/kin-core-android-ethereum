
# kin-sdk-core-android
A library responsible for creating a new ethereum account 
and managing balance and transactions in Kin.

Initial interface 

#### KinClient
```java

public class KinClient {

    /**
     * Responsible for
     * @param nodeProviderUrl url of the node provider to use
     */
    public KinClient(String nodeProviderUrl);

    /**
     * Create the initial account if it hasn't yet been created.
     * Multiple calls to this method will not create an additional account.
     * @param passphrase a passphrase provided by the user that will be used to store
     * the account private key securely.
     */
    void initAccount(String passphrase) throws CreateAccountException;

    /**
     * @return the main account if that has been created or null if there is no such account
     */
    KinAccount getAccount();

    /**
     * @return true if there is an existing account
     */
    boolean hasAccounts();
}
```


#### KinAccount
```java

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
     * The method will run on a background thread and callback calls will be done
     * on the main thread
     * @param publicAddress the account address to send the specified kin amount
     * @param amount the amount of kin to transfer
     * @param callback to be called when transaction is sent
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
     * @param callback to be called when balance is ready or on error
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
     * The method will run on a background thread and callback calls will be done
     * on the main thread
     * @param callback to be called when balance is ready or on error
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
```

#### ResultCallback
```java

public interface ResultCallback<T> {
    /**
     * Method will be called when operation has completed successfully
     * @param T the result received
     */
    void onResult(T result);

    /**
     * Method will be called when operation has completed with error
     * @param the exception in case of error
     */
    void onError(Exception e);
}
```

#### Balance
```java

public interface Balance {

    /**
     * @return BigDecimal the balance value
     */
    BigDecimal value();

    /**
     * @param precision the number of decimals points
     * @return String the balance value as a string with specified precision
     */
    String value(int precision);

    /**
     * The regular toString method will return a String representation of the balance value
     */
    String toString();
}
```

#### TransactionId
```java

public interface TransactionId {
    /**
     * @return the transaction id
     */
    String id();
}
```
