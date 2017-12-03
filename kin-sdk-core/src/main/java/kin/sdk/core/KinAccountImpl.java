package kin.sdk.core;

import java.math.BigDecimal;
import kin.sdk.core.exception.AccountDeletedOpreationFailedException;
import kin.sdk.core.exception.DeleteAccountException;
import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;
import org.ethereum.geth.Account;
import org.ethereum.geth.KeyStore;

final class KinAccountImpl extends AbstractKinAccount {

    private KeyStore keyStore;
    private EthClientWrapper ethClient;
    private Account account;
    private boolean hasDeleted = false;

    /**
     * Creates a new {@link Account}.
     *
     * @param ethClientWrapper that will be use to call to Kin smart-contract.
     * @param passphrase that will be used to store the account private key securely.
     * @throws Exception if go-ethereum was unable to generate the account (unable to generate new key or store the
     * key).
     */
    KinAccountImpl(EthClientWrapper ethClientWrapper, String passphrase) throws Exception {
        this.keyStore = ethClientWrapper.getKeyStore();
        this.account = keyStore.newAccount(passphrase);
        this.ethClient = ethClientWrapper;
        hasDeleted = false;
    }

    /**
     * Creates a {@link KinAccount} from existing {@link Account}
     *
     * @param ethClientWrapper that will be use to call to Kin smart-contract.
     * @param account the existing Account.
     */
    KinAccountImpl(EthClientWrapper ethClientWrapper, Account account) {
        this.keyStore = ethClientWrapper.getKeyStore();
        this.account = account;
        this.ethClient = ethClientWrapper;
        hasDeleted = false;
    }

    @Override
    public String getPublicAddress() {
        if (!hasDeleted) {
            return account.getAddress().getHex();
        }
        return "";
    }

    @Override
    public String exportKeyStore(String passphrase, String newPassphrase)
        throws PassphraseException, OperationFailedException {
        if (hasDeleted) {
            throw new AccountDeletedOpreationFailedException();
        }
        String jsonKeyStore;
        try {
            byte[] keyInBytes = keyStore.exportKey(account, passphrase, newPassphrase);
            jsonKeyStore = new String(keyInBytes, "UTF-8");
        } catch (Exception e) {
            throw new PassphraseException();
        }
        return jsonKeyStore;
    }

    @Override
    public TransactionId sendTransactionSync(String publicAddress, String passphrase, BigDecimal amount)
        throws InsufficientBalanceException, OperationFailedException, PassphraseException {
        if (hasDeleted) {
            throw new AccountDeletedOpreationFailedException();
        }
        return ethClient.sendTransaction(account, passphrase, publicAddress, amount);
    }

    @Override
    public Balance getBalanceSync() throws OperationFailedException {
        if (hasDeleted) {
            throw new AccountDeletedOpreationFailedException();
        }
        return ethClient.getBalance(account);
    }

    @Override
    public Balance getPendingBalanceSync() throws OperationFailedException {
        if (hasDeleted) {
            throw new AccountDeletedOpreationFailedException();
        }
        return ethClient.getPendingBalance(account);
    }

    void delete(String passphrase) throws DeleteAccountException {
        ethClient.deleteAccount(account, passphrase);
        markAsDeleted();
    }

    public void markAsDeleted() {
        hasDeleted = true;
    }
}
