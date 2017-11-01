package kin.sdk.core;


import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.mock.MockKinAccount;

public class KinClient {

    KinAccount kinAccount;

    /**
     * Responsible for
     * @param nodeProviderUrl url of the node provider to use
     */
    public KinClient(String nodeProviderUrl) {
    }

    /**
     * Create the initial account if it hasn't yet been created.
     * Multiple calls to this method will not create an additional account.
     * @param passphrase a passphrase provided by the user that will be used to store
     * the account private key securely.
     */
    public void initAccount(String passphrase) throws CreateAccountException {

        if (kinAccount != null) {
            kinAccount = new MockKinAccount();
        }
    }

    /**
     * @return the main account if that has been created or null if there is no such account
     */
    public KinAccount getAccount() {

        return kinAccount;
    }

    /**
     * @return true if there is an existing account
     */
    public boolean hasAccounts() {

        return kinAccount != null;
    }
}
