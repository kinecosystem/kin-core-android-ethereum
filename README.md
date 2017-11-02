# kin-sdk-core-android
![Kin Token](kin_android.png)

A library responsible for creating a new ethereum account
and managing balance and transactions in Kin.

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
Create a new KinClient passing to it the android application context and a ServiceProvider. 
The example below creates a ServiceProvider that will be used to connect to the main ethereum 
network, via Infura. To obtain an infura token you can register [here](https://infura.io/register.html)
```java

   KinClient kinClient = new KinClient(context,
             new ServiceProvider("https://main.infura.io/YOUR_UNFURA_TOKEN",
                                 ServiceProvider.NETWORK_ID_MAIN));
```
 
The first time you use the client you need to create a new account, using a passphrase. 
The details of the account created will be securely stored on the device.
```java
        KinAccount account;
        try {
            if ( !kinClient.hasAccounts() ) {
                account = kinClient.createAccount("yourPassphrase");
            }
        } catch (CreateAccountException e) {
        
        }
```

Once an account has been created there is no need to call createAccount again on the same device. 
From then on calling getAccount() will retrieve the account stored on the device.
```java
        if ( kinClient.hasAccounts() ) {
            account = kinClient.getAccount();
        }
``` 

Your account can be identified via it's public address. To retrieve the account public address use:
```java
        account.getPublicAddress();
```    
   
### Retrieving Balance
To retrieve the confirmed balance of your account in Kin call the getBalance method: 
getBalance will run on a background thread and access the ethereum network. 
onResult & onError are executed on the android UI thread.
```java
        account.getBalance(new ResultCallback<Balance>() {
            @Override
            public void onResult(Balance result) {
                Log.d("example", "The balance is "+result.toString());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
```

### Transfering Kin to another account
To transfer kin to another account, you need the public address of the account you want 
to transfer the kin to. The following code will transfer 20Kin to account:AB1234. 
sendTransaction runs on a background thread and accesses the ethereum network. 
onResult & onError are executed on the android UI thread.
```java
        account.sendTransaction("AB1234", "yourPassphrase",
                new BigDecimal("20"), new ResultCallback<TransactionId>() {
            @Override
            public void onResult(TransactionId result) {
                Log.d("example","The transaction id"+result.toString());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
```

### Retrieving Pending Balance
It takes some time for the transaction to be confirmed. Until then, you should be able to see the
difference in your balance by accessing the account pending balance: 
getPendingBalance will run on a background thread and access the ethereum network. 
onResult & onError are executed on the android UI thread.
```java
        account.getPendingBalance(new ResultCallback<Balance>() {
            @Override
            public void onResult(Balance result) {
                Log.d("example", "The balance is "+result.toString());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
```

### Sync vs Async
If you are already on a background thread and wish to use a synchronous version of getBalance, 
sendTransaction and getPendingBalance you can use the following methods instead:
```java
    account.getBalanceSync();
    account.getPendingBalanceSync();
    account.sendTransactionSync("AB1234", "yourPassphrase", new BigDecimal("20"));
```

### Sample Application 
For a more detailed example on how to use the library you are welcome to take a look at our sample
application [here](sample/)

## Contributing

Please review our [CONTRIBUTING.md](CONTRIBUTING.md) guide before opening issues and pull requests.

## Thanks and credits

Many thanks to all of our colleagues at KIK who have contributed to this project directly 
and indirectly!  In addition we would like to thank:
* [geth](https://github.com/ethereum/go-ethereum)
* [Infura](https://infura.io/)
* [Jitpack](https://jitpack.io/)
* [gradle](https://github.com/gradle/gradle)
* [travis](https://travis-ci.org/)
* [google styleguide](https://github.com/google/styleguide)


## License

The kin-sdk-core-android library is licensed under **GPL LICENCE TO BE ADDED**