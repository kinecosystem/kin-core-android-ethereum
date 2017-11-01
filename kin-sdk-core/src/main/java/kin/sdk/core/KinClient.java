package kin.sdk.core;


import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.mock.MockKinAccount;

public class KinClient {

    KinAccount kinAccount;

    /**
     * KinClient is an account manager for a single KinAccount on the
     * ethereum blockchain.
     *
     * @param provider the service provider to use to connect to an ethereum node
     */
    public KinClient(ServiceProvider provider) {
    }

    /**
     * Create the account if it hasn't yet been created.
     * Multiple calls to this method will not create an additional account.
     * Once created, the account information will be stored securely on device and can
     * be accessed again via the getAccount method
     * @param passphrase a passphrase provided by the user that will be used to store
     * the account private key securely.
     * @return KinAccount the account created
     */
    public KinAccount createAccount(String passphrase) throws CreateAccountException {

        if (kinAccount != null) {
            kinAccount = new MockKinAccount();
        }

        return kinAccount;
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
