
# kin-sdk-core-android
A library responsible for creating a new ethereum account 
and managing balance and transactions in Kin.


#### KinClient
```java

public class KinClient {

    /**
     * KinClient is an account manager for a single {@link KinAccount} on the
     * ethereum network.
     *
     * @param provider the service provider to use to connect to an ethereum node
     */
    public KinClient(ServiceProvider provider);

    /**
     * Create the account if it hasn't yet been created.
     * Multiple calls to this method will not create an additional account.
     * Once created, the account information will be stored securely on the device and can
     * be accessed again via the {@link #getAccount()} method
     * @param passphrase a passphrase provided by the user that will be used to store
     * the account private key securely.
     * @return KinAccount the account created
     */
    KinAccount createAccount(String passphrase) throws CreateAccountException;

    /**
     * The method will return an account that has previously been create and stored on the device
     * via the {@link #createAccount(String)} method.
     * @return the account if it has been created or null if there is no such account
     */
    KinAccount getAccount();

    /**
     * @return true if there is an existing account
     */
    boolean hasAccounts();
}
```

#### ServiceProvider
```java

public class ServiceProvider {

   /** main ethereum network */
   static final NETWORK_ID_MAIN = 1;

   /** ropsten ethereum TEST network */
   static final NETWORK_ID_ROPSTEN = 3;

   /** rinkeby ethereum TEST network */
   static final NETWORK_ID_RINKEBY = 4;

   /**
    * A ServiceProvider used to connect to an ethereum node.
    *
    * For example to connect to an infura test node use
    * new ServiceProvider("https://ropsten.infura.io/YOURTOKEN", NETWORK_ID_ROPSTEN);
    * @param providerUrl the provider to use
    * @param networkId for example see {@value #NETWORK_ID_MAIN} {@value NETWORK_ID_ROPSTEN} {@value NETWORK_ID_RINKEBY}
    */
   public ServiceProvider(String providerUrl, int networkId);
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
