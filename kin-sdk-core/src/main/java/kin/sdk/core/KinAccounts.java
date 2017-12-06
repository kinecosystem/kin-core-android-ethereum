package kin.sdk.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.exception.DeleteAccountException;
import org.ethereum.geth.Account;
import org.ethereum.geth.Accounts;
import org.ethereum.geth.KeyStore;

public class KinAccounts {

    private List<KinAccount> kinAccountsList = new ArrayList<>();
    private KeyStore keyStore;
    private EthClientWrapper ethClientWrapper;

    public KinAccounts(EthClientWrapper ethClientWrapper) {
        this.ethClientWrapper = ethClientWrapper;
        keyStore = ethClientWrapper.getKeyStore();
    }

    public long count() {
        Accounts accounts = keyStore.getAccounts();
        if (accounts != null) {
            return accounts.size();
        }
        return 0;
    }

    public synchronized KinAccount addAccount(String passphrase) throws CreateAccountException{
        kinAccountsList = Collections.synchronizedList(kinAccountsList);
        synchronized (kinAccountsList) {
            Account account = null;
            try {
                account = keyStore.newAccount(passphrase);
            } catch (Exception e) {
                throw new CreateAccountException(e);
            }
            KinAccount kinAccount = new KinAccountImpl(ethClientWrapper, account);
            kinAccountsList.add(kinAccount);
            return kinAccount;
        }
    }

    public synchronized void deleteAccount(int index, String passphrase) throws DeleteAccountException {
        kinAccountsList = Collections.synchronizedList(kinAccountsList);
        synchronized (kinAccountsList) {
            Account account = null;
            try {
                final KinAccount kinAccount = getAccount(index);
                if(kinAccount == null){
                    throw new DeleteAccountException("No account for index " + index );
                }
                Accounts accounts = keyStore.getAccounts();
                if(accounts != null && accounts.size() > index) {
                    account = keyStore.getAccounts().get(index);
                    keyStore.deleteAccount(account, passphrase);
                }
                kinAccountsList.remove(index);
            } catch (Exception e) {
                throw new DeleteAccountException(e);
            }
        }
    }

    public synchronized KinAccount getAccount(int index) {
        synchronized (kinAccountsList) {
            if (index >= 0 && index < count()) {
                return kinAccountsList.get(index);
            }
            return null;
        }
    }

    public boolean hasAccounts() {
        return count() > 0;
    }


    public synchronized void wipeoutAccount() {
        kinAccountsList = Collections.synchronizedList(kinAccountsList);
        synchronized (kinAccountsList) {
            Iterator<KinAccount> iterator = kinAccountsList.iterator();
            while (iterator.hasNext()) {
                KinAccount kinAccount = iterator.next();
                if (kinAccount instanceof KinAccountImpl) {
                    ((KinAccountImpl) kinAccount).markAsDeleted();
                }
            }
            kinAccountsList.clear();
        }
    }
}
