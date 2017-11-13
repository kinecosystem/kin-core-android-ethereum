# Kin core SDK for Android
Android library responsible for creating a new Ethereum account and managing KIN balance and transactions.
![Kin Token](kin_android.png)

## Build

* Copy [kin-sdk-core/libs/geth.aar](kin-sdk-core/libs/geth.aar) to a library folder in your project module.  
_Note: In the near future we will upload the aar to jcenter so that you can pull it too by dependency_

* Add this to your module's `build.gradle` file. Where:
```gradle
repositories {
    ...
    flatDir {
       dirs 'YOUR-LIB-FOLDER-NAME'
    }
    maven {
        url 'https://jitpack.io'
        credentials { username YOUR-JITPACK-AUTHTOKEN }
    }
}
...
dependencies {
    ...
    compile(name:'geth', ext:'aar')
    compile "com.github.kinfoundation:kin-sdk-core-android:LATEST-COMMIT-ON-DEV-BRANCH"
}
```
In the above `build.gradle`:
* YOUR-LIB-FOLDER-NAME is the folder you copied geth.aar to
* YOUR-JITPACK-AUTHTOKEN won't be needed once repository is changed to public.
For the time being to get a token, go to https://jitpack.io and sign in with your github account. 
Authorize jitpack, then navigate to https://jitpack.io/w/user to get your AccessToken. Ensure that jitpack is authorized 
for private repositories
* LATEST-COMMIT-ON-DEV-BRANCH is an abbreviated commit hash for example: f367f300f5

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
**`getPendingBalance` IS NOT IMPLEMENTED YET.**
**At the moment `account.getPendingBalance()` always returns the same value as `account.getBalance()`**

In the meantime, you are welcome to read here how it is intended to work:

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
For a more detailed example on how to use the library we will soon be providing a sample app.

## Contributing
Please review our [CONTRIBUTING.md](CONTRIBUTING.md) guide before opening issues and pull requests.

## License
The kin-sdk-core-android library is licensed under GNU Lesser General Public License v3.0, 
also included in our repository in the [COPYING.LESSER](COPYING.LESSER) file.