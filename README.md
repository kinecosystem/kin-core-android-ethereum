# Kin core SDK for Android
Android library responsible for creating a new Ethereum account and managing KIN balance and transactions.
![Kin Token](kin_android.png)

## Disclaimer

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
    }
}
...
dependencies {
    ...
    compile "kinfoundation.ethereum:geth:1.0.1@aar"
    compile "com.github.kinfoundation:kin-sdk-core-android:LATEST-COMMIT-ON-DEV-BRANCH"
}
```
In the above `build.gradle`:
* LATEST-COMMIT-ON-DEV-BRANCH stands for the first 10 characters of our latest commit on dev branch. For example: d9bb37a7e2

## Usage
### Connecting to a service provider
To start using our SDK, create a new `KinClient` with two arguments: an android `Context` and a `ServiceProvider`. 

A `ServiceProvider` is a service that provides access to the Ethereum network.
The example below creates a `ServiceProvider` that will be used to connect to the main (production) Ethereum 
network, via Infura.  To obtain an Infura token you can register [here](https://infura.io/register.html)
```java
ServiceProvider infuraProvider =  
    new ServiceProvider("https://main.infura.io/INFURA_TOKEN", ServiceProvider.NETWORK_ID_MAIN));
KinClient kinClient = new KinClient(context, infuraProvider);
```

To connect to a test ethereum network use the following ServiceProvider:
```java
new ServiceProvider("https://ropsten.infura.io/INFURA_TOKEN", ServiceProvider.NETWORK_ID_ROPSTEN)
``` 

### <a name="parity">Parity service provider</a>
Unfortunately there is no guarantee that [`getPendingBalance`](#pendingBalance) will work when working with an Infura provider
because of an existing known [issue](https://github.com/ethereum/go-ethereum/issues/15359) with the geth implementation of the ethereum protocol.\
In order for `getPendingBalance` to work as expected you will need to connect to a node running a parity implementation of the protocol.
Let us know if you need help with that.

### Creating and retrieving a KIN account
The first time you use `KinClient` you need to create a new account, using a passphrase. 
The details of the account created will be securely stored on the device.
```java
KinAccount account;
try {
    if (!kinClient.hasAccount()) {
        account = kinClient.createAccount("yourPassphrase");
    }
} catch (CreateAccountException e) {
    e.printStackTrace();
}
```

Once an account has been created there is no need to call `createAccount` again on the same device. 
From then on calling `getAccount` will retrieve the account stored on the device.
```java
if (kinClient.hasAccount()) {
    account = kinClient.getAccount();
}
``` 

You can delete your account from the device using `deleteAccount` with the passphrase you used to create it as a parameter, 
but beware! Unless you export it first using `exportKeyStore` you will lose all your existing KIN if you do this.
```java
kinClient.deleteAccount(String passphrase);
``` 

### Public Address and JSON keystore 
Your account can be identified via it's public address. To retrieve the account public address use:
```java
account.getPublicAddress();
```

You can export the account keystore file as JSON using the `exportKeyStore` method
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

### <a name="pendingBalance"></a>Retrieving Pending Balance

**This may not work with an infura ServiceProvider, as discussed [here](#parity) **

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

## Testing

We use [ethereumjs/testrpc](https://github.com/trufflesuite/ganache-cli) and [Truffle framework](http://truffleframework.com/) unit tests.

When running the SDK test target, pre-action and post-action tasks in build.gradle (Module: kin-sdk-core) 
will setup truffle and testrpc to run for the duration of the test.

### Requirements

Node.js and npm. You can install these using homebrew:

```bash
$ brew install node
```
Next, install specific npm packages using:

```bash
$ cd kin-sdk-core/truffle
$ npm install
```

Next, initialize and update git submodules.  This will include `kin-sdk-core/truffle/kin-token`.

```bash
$ git submodule init && git submodule update
```

### How to run the tests


* From command line<br />
Run the below command from the root directory.<br />
It will run all the tests and also clean testrpc at the end.

```bash
$ make test
```

* From Android Studio<br /> 
Our test classes are [here](kin-sdk-core/src/androidTest/java/kin/sdk/core/).
You can run the tests directly from Android Studio but will still have to run clean testrpc manually.

### Clean testrpc manually
Run the below command from the root directory.
```bash
$ make clean
```


## Contributing
Please review our [CONTRIBUTING.md](CONTRIBUTING.md) guide before opening issues and pull requests.

## License
The kin-sdk-core-android library is licensed under [MIT license](LICENSE.md).
