package kin.sdk.core.impl;

import org.ethereum.geth.Account;
import org.ethereum.geth.KeyStore;

import java.math.BigDecimal;

import kin.sdk.core.Balance;
import kin.sdk.core.KinAccount;
import kin.sdk.core.TransactionId;
import kin.sdk.core.ethereum.EthClientWrapper;
import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;

public class KinAccountImpl extends AbstractKinAccount {

    private KeyStore keyStore;
    private EthClientWrapper ethClient;
    private Account account;

    /**
     * Creates a new {@link Account}.
     *
     * @param ethClientWrapper that will be use to call to Kin smart-contract.
     * @param passphrase that will be used to store the account private key securely.
     * @throws Exception if go-ethereum was unable to generate the account (unable to generate new key or store the
     * key).
     */
    public KinAccountImpl(EthClientWrapper ethClientWrapper, String passphrase) throws Exception {
        this.keyStore = ethClientWrapper.getKeyStore();
        this.account = keyStore.newAccount(passphrase);
        this.ethClient = ethClientWrapper;
    }

    /**
     * Creates a {@link KinAccount} from existing {@link Account}
     *
     * @param ethClientWrapper that will be use to call to Kin smart-contract.
     * @param account the existing Account.
     */
    public KinAccountImpl(EthClientWrapper ethClientWrapper, Account account) {
        this.keyStore = ethClientWrapper.getKeyStore();
        this.account = account;
        this.ethClient = ethClientWrapper;
    }

    @Override
    public String getPublicAddress() {
        return account.getAddress().getHex();
    }

    @Override
    public String exportKeyStore(String oldPassphrase, String newPassphrase) throws PassphraseException {
        // TODO replace with real implementation
        return "{\"address\":\"82cdc15705ce9f4565dda07d78c92ff3d2717854\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"189f02138a83be004e97a531224b00b99423a0825d676e5ef92b910c274d8f8d\",\"cipherparams\":{\"iv\":\"d0b2f7fd02923d4b54bc2eb9fd4509f5\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,\"salt\":\"ce0915bfe693c918de990cee29493b03dc78c657627922c9ac83a00b44e231d6\"},\"mac\":\"601dec6cacfe5f7c651b7ad8bfda3a24e641f97cb78d6a1aaa56b918937c6894\"},\"id\":\"84d6035d-8d0f-4343-9d23-dfbae8f0ce6d\",\"version\":3}";
    }

    @Override
    public TransactionId sendTransactionSync(String publicAddress, String passphrase, BigDecimal amount)
        throws InsufficientBalanceException, OperationFailedException, PassphraseException {
        return ethClient.sendTransaction(account, publicAddress, passphrase, amount);
    }

    @Override
    public Balance getBalanceSync() throws OperationFailedException {
        return ethClient.getBalance(account);
    }

    @Override
    public Balance getPendingBalanceSync() throws OperationFailedException {
        return ethClient.getPendingBalance(account);
    }
}
