package kin.sdk.core;


import android.content.Context;

import kin.sdk.core.ethereum.EthClientWrapper;
import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.exception.EthereumClientException;
import kin.sdk.core.impl.KinAccountImpl;

public class KinClient {

    private KinAccount kinAccount;
    private EthClientWrapper ethClient;

    /**
     * KinClient is an account manager for a single {@link KinAccount} on
     * ethereum network.
     *
     * @param context  the android application context
     * @param provider the service provider to use to connect to an ethereum node
     * @throws EthereumClientException if could not connect to service provider
     * or connection problem with Kin smart-contract problems.
     */
    public KinClient(Context context, ServiceProvider provider) throws EthereumClientException {
        this.ethClient = new EthClientWrapper(context, provider);
    }

    /**
     * Create the account if it hasn't yet been created.
     * Multiple calls to this method will not create an additional account.
     * Once created, the account information will be stored securely on the device and can
     * be accessed again via the {@link #getAccount()} method.
     *
     * @param passphrase a passphrase provided by the user that will be used to store
     *                   the account private key securely.
     * @return KinAccount the account created
     * @throws CreateAccountException if go-ethereum was unable to generate the account
     * (unable to generate new key or store the key).
     */
    public KinAccount createAccount(String passphrase) throws CreateAccountException {
        if (kinAccount != null) {
            try {
                kinAccount = new KinAccountImpl(ethClient, passphrase);
            } catch (Exception e) {
                throw new CreateAccountException(e);
            }
        }
        return kinAccount;
    }

    /**
     * The method will return an account that has previously been create and stored on the device
     * via the {@link #createAccount(String)} method.
     *
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
