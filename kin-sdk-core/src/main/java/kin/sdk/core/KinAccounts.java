package kin.sdk.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.exception.DeleteAccountException;
import kin.sdk.core.exception.EthereumClientException;
import org.ethereum.geth.Account;
import org.ethereum.geth.Accounts;

final class KinAccounts {

    private List<KinAccountImpl> accounts = new ArrayList<>();
    private EthClientWrapper ethClient;

    public KinAccounts(EthClientWrapper ethClient) {
        this.ethClient = ethClient;
    }

    KinAccount addAccount(String passphrase) throws CreateAccountException {
        accounts = Collections.synchronizedList(accounts);
        synchronized (accounts) {
            KinAccountImpl kinAccount = null;
            try {
                kinAccount = new KinAccountImpl(ethClient, passphrase);
                accounts.add(kinAccount);
            } catch (Exception e) {
                throw new CreateAccountException(e);
            }
            return kinAccount;
        }
    }

    public KinAccountImpl getAccount(int index) {
        if (isValidIndex(index)) {
            accounts = Collections.synchronizedList(accounts);
            synchronized (accounts) {
                KinAccountImpl kinAccount = accounts.get(index);
                if (kinAccount != null) {
                    return kinAccount;
                }
                Accounts keyStoreAccounts = ethClient.getKeyStore().getAccounts();
                if (accounts != null) {
                    try {
                        Account account = keyStoreAccounts.get(index);
                        if (account != null) {
                            kinAccount = new KinAccountImpl(ethClient, account);
                            this.accounts.add(index, kinAccount);
                            return kinAccount;
                        }
                    } catch (Exception e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private boolean isValidIndex(int index) {
        return hasAccount() && index >= 0 && index < count();
    }

    public int count() {
        return accounts.size();
    }

    public boolean hasAccount() {
        return count() > 0;
    }

    public void deleteAccount(int index, String passphrase) throws DeleteAccountException {
        if (isValidIndex(index)) {
            accounts = Collections.synchronizedList(accounts);
            synchronized (accounts) {
                KinAccountImpl account = getAccount(index);
                if (account != null) {
                    account.delete(passphrase);
                    accounts.remove(index);
                }
            }
        } else {
            throw new DeleteAccountException("Wrong index " + index);
        }
    }

    public void wipeoutAccount() throws EthereumClientException {
        accounts = Collections.synchronizedList(accounts);
        synchronized (accounts) {
            Iterator<KinAccountImpl> iterator = accounts.iterator();
            while (iterator.hasNext()) {
                iterator.next().markAsDeleted();
            }
            accounts.clear();
            //might throw EthereumClientException while try to make directory for keystore (after deleting is complete)
            ethClient.wipeoutAccount();
        }
    }
}
