package kin.sdk.core;


import android.content.Context;

import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.mock.MockKinAccount;

public class KinClient {

    KinAccount kinAccount;

    /**
     * KinClient is an account manager for a single {@link KinAccount} on the
     * ethereum network.
     *
     * @param context the android application context
     * @param provider the service provider to use to connect to an ethereum node
     */
    public KinClient(Context context, ServiceProvider provider) {
    }

    /**
     * Create the account if it hasn't yet been created.
     * Multiple calls to this method will not create an additional account.
     * Once created, the account information will be stored securely on the device and can
     * be accessed again via the {@link #getAccount()} method
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
     * The method will return an account that has previously been create and stored on the device
     * via the {@link #createAccount(String)} method.
     * @return the account if it has been created or null if there is no such account
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
