# Kin core SDK for Android
Android library responsible for creating a new Ethereum account and managing KIN balance and transactions.
![Kin Token](kin_android.png)

## Build

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
        allprojects {
            repositories {
                ...
                maven { url "https://jitpack.io" }
            }
        }
```

Add this to your module's `build.gradle` file

```gradle
        dependencies {
            ...
            compile 'com.github.TO-BE-ADDED-SOON'
        }
```

## Usage
### Creating and retrieving an account
Create a new `KinClient` with two arguments: an android `Context` and a `ServiceProvider`. 

The example below creates a `ServiceProvider` that will be used to connect to the main Ethereum 
network, via Infura.  To obtain an Infura token you can register [here](https://infura.io/register.html)
```java
        ServiceProvider infuraProvider =  
            new ServiceProvider("https://main.infura.io/INFURA_TOKEN", ServiceProvider.NETWORK_ID_MAIN));
        KinClient kinClient = new KinClient(context, infuraProvider);
```
 
The first time you use `KinClient` you need to create a new account, using a passphrase. 
The details of the account created will be securely stored on the device.
```java
        KinAccount account;
        try {
            if (!kinClient.hasAccounts()) {
                account = kinClient.createAccount("yourPassphrase");
            }
        } catch (CreateAccountException e) {
            e.printStackTrace();
        }
```

Once an account has been created there is no need to call `createAccount` again on the same device. 
From then on calling `getAccount` will retrieve the account stored on the device.
```java
        if (kinClient.hasAccounts()) {
            account = kinClient.getAccount();
        }
``` 

Your account can be identified via it's public address. To retrieve the account public address use:
```java
        account.getPublicAddress();
```

### Retrieving Balance
To retrieve the balance of your account in KIN call the `getBalance` method: 
```java
        account.getBalance(new ResultCallback<Balance>() {
            
            @Override
            public void onResult(Balance result) {
                Log.d("example", "The balance is: " + result.toString());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
```

### Transfering KIN to another account
To transfer KIN to another account, you need the public address of the account you want 
to transfer the KIN to. 

The following code will transfer 20 KIN to account "#AB12349ACF123". 
```java
        String toAddress = "#AB12349ACF123";
        String passphrase = "yourPassphrase";
        BigDecimal amountInKin = new BigDecimal("20");
        
        account.sendTransaction(toAddress, passphrase, amountInKin, new ResultCallback<TransactionId>() {
            
            @Override
            public void onResult(TransactionId result) {
                Log.d("example","The transaction id: " + result.toString());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
```

### Retrieving Pending Balance
It takes some time for transactions to be confirmed.  In the meantime you can call `getPendingBalance` 
to get the amount of KIN that you will have once all your pending transactions are confirmed.

For example, if you have 40KIN and then transfer 5KIN to your friend, until the transaction of the 5KIN 
gets to be confirmed `getBalance` will return 40KIN and `getPendingBalance` will return 35KIN.

Similarly if you have 30KIN and someone else transfer 2KIN to you, until the transaction gets to be confirmed
`getBalance` will return 30KIN and `getPendingBalance` will return 32KIN.
```java
        account.getPendingBalance(new ResultCallback<Balance>() {
            
            @Override
            public void onResult(Balance result) {
                Log.d("example", "The balance is: " + result.toString());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
```

### Sync vs Async
The `getBalance`, `getPendingBalance` and `sendTransaction` methods described above, run on a background thread 
and accesses the Ethereum network. 

The `onResult` and `onError` callback methods are executed on the android UI thread.

If you are already on a background thread and wish to use a synchronous version of `getBalance`, 
`sendTransaction` and `getPendingBalance` methods, then use the following methods instead:
```java
        account.getBalanceSync();
        account.getPendingBalanceSync();
        account.sendTransactionSync(toAddress, passphrase, amountInKin);
```

### Sample Application 
For a more detailed example on how to use the library you are welcome to take a look at our sample
application [here](sample/)

## Contributing

Please review our [CONTRIBUTING.md](CONTRIBUTING.md) guide before opening issues and pull requests.

## Thanks and credits

Many thanks to all of our colleagues at Kik who have contributed to this project directly 
and indirectly!  In addition we would like to thank all of the open source projects that helped with 
creating this one:
* [geth](https://github.com/ethereum/go-ethereum)
* [Infura](https://infura.io/)
* [Jitpack](https://jitpack.io/)
* [gradle](https://github.com/gradle/gradle)
* [travis](https://travis-ci.org/)
* [google styleguide](https://github.com/google/styleguide)


## License

The kin-sdk-core-android library is licensed under **GPL LICENCE TO BE ADDED**