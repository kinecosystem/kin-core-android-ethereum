package kin.sdk.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import kin.sdk.core.exception.EthereumClientException;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class KinClientTest extends BaseTest {
    private static final String PASSPHRASE = "testPassphrase";

    @Test
    public void testWrongServiceProvider() {
        Context context = InstrumentationRegistry.getContext();
        ServiceProvider wrongProvider = new ServiceProvider("wrongProvider", 12);
        try {
            kinClient = new KinClient(context, wrongProvider);
        } catch (EthereumClientException e) {
            assertEquals("provider - could not establish connection to the provider", e.getMessage());
        }
    }

    @Test
    public void testCreateAccount() {
        try {
            // Create first account.
            KinAccount kinAccount = createAccount();
            assertNotNull(kinAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateSecondAccount() {
        try {
            // Create account on the first time - should be ok
            KinAccount firstAccount = createAccount();

            // Try to create second account
            // should return the same account (firstAccount).
            KinAccount secondAccount = createAccount();

            assertEquals(firstAccount, secondAccount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteAccount() {
        try {
            createAccount();
            assertTrue(kinClient.hasAccount());

            kinClient.deleteAccount(PASSPHRASE);
            assertFalse(kinClient.hasAccount());
            assertNull(kinClient.getAccount());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAccount_isNull() throws Exception {
        // No account were created, thus the account is null
        KinAccount kinAccount = kinClient.getAccount();
        assertNull(kinAccount);
    }

    @Test
    public void testGetAccount_notNull() throws Exception {
        // Create first account, should return same account.
        KinAccount kinAccount = createAccount();
        KinAccount sameAccount = kinClient.getAccount();

        assertNotNull(sameAccount);
        assertEquals(kinAccount, sameAccount);
    }

    @Test
    public void testHasAccounts_noAccount() throws Exception {
        // No account created
        // Check if has account
        assertFalse(kinClient.hasAccount());
    }

    @Test
    public void testHasAccounts() throws Exception {
        // Create first account
        createAccount();
        // Check if has account
        assertTrue(kinClient.hasAccount());
    }

    private KinAccount createAccount() throws Exception {
        return kinClient.createAccount(PASSPHRASE);
    }
}