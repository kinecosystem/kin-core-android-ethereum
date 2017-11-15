package kin.sdk.core.impl;

import java.math.BigDecimal;
import kin.sdk.core.Balance;
import kin.sdk.core.KinAccount;
import kin.sdk.core.TransactionId;
import kin.sdk.core.ethereum.EthClientWrapper;
import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;
import org.ethereum.geth.Account;
import org.ethereum.geth.KeyStore;

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
        String jsonKeyStore;
        try {
            byte[] keyInBytes = keyStore.exportKey(account, oldPassphrase, newPassphrase);
            jsonKeyStore = new String(keyInBytes, "UTF-8");
        } catch (Exception e) {
            throw new PassphraseException();
        }
        return jsonKeyStore;
    }

    @Override
    public TransactionId sendTransactionSync(String publicAddress, String passphrase, BigDecimal amount)
        throws InsufficientBalanceException, OperationFailedException, PassphraseException {
        return ethClient.sendTransaction(account, passphrase, publicAddress, amount);
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
