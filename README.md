# Kin core SDK for Android
Android library responsible for creating a new Ethereum account and managing KIN balance and transactions.
![Kin Token](kin_android.png)

## Disclosure

The SDK is not yet ready for third-party use by digital services in the Kin ecosystem.
It is still tested internally by Kik as part of [initial product launch, version 2](https://medium.com/kinfoundation/context-around-iplv2-4b4ec3734417).

## Build

* Add this to your module's `build.gradle` file.
```gradle
repositories {
    ...
    maven {
        url "https://dl.bintray.com/kinfoundation/go-ethereum"
    }
    maven {
        url 'https://jitpack.io'
        credentials { username YOUR-JITPACK-AUTHTOKEN }
    }
}
...
dependencies {
    ...
    compile "kinfoundation.ethereum:geth:1.0.0@aar"
    compile "com.github.kinfoundation:kin-sdk-core-android:LATEST-COMMIT-ON-DEV-BRANCH"
}
```
In the above `build.gradle`:
* YOUR-JITPACK-AUTHTOKEN won't be needed once repository is changed to public.
For the time being to get a token, go to https://jitpack.io and sign in with your github account. 
Authorize jitpack, then navigate to https://jitpack.io/w/user to get your AccessToken. Ensure that jitpack is authorized 
for private repositories
* LATEST-COMMIT-ON-DEV-BRANCH is a short commit hash for example 1st 10 characters of a commit: f367f300f5

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

### Public Address and JSON keystore 
Your account can be identified via it's public address. To retrieve the account public address use:
```java
account.getPublicAddress();
```

You can export the account keystore file as JSON using the `exportKeyStore` method
**`exportKeyStore` is NOT IMPLEMENTED YET.**
**At the moment this will always return a mock JSON String**
```java
 try {
    String oldPassphrase = "yourPassphrase";
    String newPassphrase = "newPassphrase";
    String json = account.exportKeyStore(oldPassphrase, newPassphrase);
    Log.d("example", "The keystore JSON: " + json);
 }
 catch (PassphraseException e){
    e.printStackTrace();
 }
```

### Retrieving Balance
To retrieve the balance of your account in KIN call the `getBalance` method: 
```java
account.getBalance(new ResultCallback<Balance>() {
    
    @Override
    public void onResult(Balance result) {
        Log.d("example", "The balance is: " + result.value(2));
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

**At the moment it is not supported when working with `ServiceProvider` that uses Geth nodes, due to [known issue](https://github.com/ethereum/go-ethereum/issues/15359).
In this case, `getPendingBalance` will return the same `Balance` as `getBalance`.**

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
As you have seen above we have provided a very simple solution for accessing the ethereum network on a background thread.
In this solution `getBalance`, `getPendingBalance` and `sendTransaction` methods, run on a background thread 
and accesses the Ethereum network. The `onResult` and `onError` callback methods are executed on the android UI thread.
This solution suffers from inability to cancel the tasks and you need to use it very carefully to avoid leaking Activity context.
An example of how this can be used without leaking context can be found in our [Sample App](sample/).

We will be providing with a better solution shortly. In the meantime you are welcome to use the synchronous version of `getBalance`, 
`sendTransaction` and `getPendingBalance` methods making sure you call them in a background thread in any way that you are accustomed.
```java
try {
    account.getBalanceSync();
}
catch (OperationFailedException e) {
   // something went wrong - check the exception message
}

try {
    account.getPendingBalanceSync();
}
catch (OperationFailedException e){
   // something went wrong - check the exception message
}

try {
    account.sendTransactionSync(toAddress, passphrase, amountInKin);
}
catch (InsufficientBalanceException e){
    // you don't have enough kin in your account
    // this could also occur if you don't have enough ether for gas
} 
catch (PassphraseException e){
    // there passphrase used was wrong
}
catch (OperationFailedException e){
    // something else went wrong - check the exception message
} 
```

### Sample Application 
For a more detailed example on how to use the library please take a look at our [Sample App](sample/).

## Contributing
Please review our [CONTRIBUTING.md](CONTRIBUTING.md) guide before opening issues and pull requests.

## License
The kin-sdk-core-android library is licensed under [MIT license](LICENSE).
