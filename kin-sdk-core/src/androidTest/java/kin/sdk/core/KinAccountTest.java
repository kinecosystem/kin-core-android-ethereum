package kin.sdk.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import android.support.test.runner.AndroidJUnit4;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import kin.sdk.core.Config.EcdsaAccount;
import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class KinAccountTest extends BaseTest {

    private final String PASSPHRASE = "testPassphrase";
    private final String TO_ADDRESS = "0x82CdC15705CE9f4565DDa07d78c92ff3d2717854";

    /**
     * First new account with 0 TOKEN and 0 ETH.
     */
    private KinAccount kinAccount;

    /**
     * Imported account via private ECDSA Key, from testConfig.json
     * The first account (importedAccounts.get(0)) will have 1000 TOKEN and 100 ETH, and can sendTransactions.
     * All other accounts (1-9) will have only 100 ETH.
     */
    private List<KinAccount> importedAccounts = new ArrayList<>(10);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        kinAccount = kinClient.createAccount(PASSPHRASE);
        importAccounts();
    }

    /**
     * Import all accounts from {@link Config}.
     */
    private void importAccounts() {
        List<EcdsaAccount> accounts = config.getAccounts();
        for (EcdsaAccount account : accounts) {
            try {
                KinAccount importedAccount = kinClient.importAccount(account.getKey(), PASSPHRASE);
                importedAccounts.add(importedAccount);
            } catch (OperationFailedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testGetPublicAddress() {
        String address = kinAccount.getPublicAddress();

        assertNotNull(address);
        assertThat(address, CoreMatchers.startsWith("0x"));
        assertEquals(40, address.substring(2, address.length()).length());
    }

    @Test
    public void exportKeyStore() throws PassphraseException {
        String exportedKeyStore = kinAccount.exportKeyStore(PASSPHRASE, "newPassphrase");

        assertNotNull(exportedKeyStore);
        assertThat(exportedKeyStore, CoreMatchers.containsString("address"));
        assertThat(exportedKeyStore, CoreMatchers.containsString("crypto"));
        assertThat(exportedKeyStore, CoreMatchers.containsString("cipher"));
        assertThat(exportedKeyStore, CoreMatchers.containsString("ciphertext"));
        assertThat(exportedKeyStore, CoreMatchers.containsString("cipherparams"));
        assertThat(exportedKeyStore, CoreMatchers.containsString("iv"));
        assertThat(exportedKeyStore, CoreMatchers.containsString("kdf"));
        assertThat(exportedKeyStore, CoreMatchers.containsString("kdfparams"));
        assertThat(exportedKeyStore, CoreMatchers.containsString("dklen"));
        assertThat(exportedKeyStore, CoreMatchers.containsString("salt"));
    }

    @Test
    public void exportKeyStore_wrongPassphrase() {
        try {
            kinAccount.exportKeyStore("wrongPassphrase", "newPassphrase");
        } catch (PassphraseException e) {
            assertEquals("Wrong passphrase - could not decrypt key with given passphrase", e.getMessage());
        }
    }

    @Test
    public void sendTransactionSync_insufficientBalance() {
        try {
            kinAccount.sendTransactionSync(TO_ADDRESS, PASSPHRASE, new BigDecimal(1));
        } catch (Exception e) {
            assertTrue(e instanceof InsufficientBalanceException);
        }
    }

    @Test
    public void sendTransactionSync_negativeAmount() {
        try {
            kinAccount.sendTransactionSync(TO_ADDRESS, PASSPHRASE, new BigDecimal(-1));
        } catch (Exception e) {
            assertTrue(e instanceof OperationFailedException);
            assertEquals("Amount can't be negative", e.getMessage());
        }
    }

    @Test
    public void sendTransactionSync_wrongPublicAddress() {
        try {
            kinAccount.sendTransactionSync(null, PASSPHRASE, new BigDecimal(0));
        } catch (Exception e) {
            assertTrue(e instanceof OperationFailedException);
            assertEquals("Addressee not valid - public address can't be null or empty", e.getMessage());
        }

        try {
            kinAccount.sendTransactionSync("0xShortAddress", PASSPHRASE, new BigDecimal(0));
        } catch (Exception e) {
            assertEquals("invalid address hex length: 12 != 40", e.getCause().getMessage());
        }

        try {
            kinAccount.sendTransactionSync("", PASSPHRASE, new BigDecimal(0));
        } catch (Exception e) {
            assertTrue(e instanceof OperationFailedException);
            assertEquals("Addressee not valid - public address can't be null or empty", e.getMessage());
        }
    }

    @Test
    public void sendTransactionSync_wrongPassphrase() {
        try {
            kinAccount.sendTransactionSync(TO_ADDRESS, "wongPassphrase", new BigDecimal(0));
        } catch (Exception e) {
            assertEquals("Wrong passphrase - could not decrypt key with given passphrase", e.getCause().getMessage());
        }
    }

    @Test
    public void sendTransactionSync() throws Exception {
        KinAccount senderAccount = importedAccounts.get(0);
        Balance senderBalance = senderAccount.getBalanceSync();
        BigDecimal amountToSend = new BigDecimal(10);

        senderAccount.sendTransactionSync(kinAccount.getPublicAddress(), PASSPHRASE, amountToSend);

        Balance kinAccountBalance = kinAccount.getBalanceSync();
        assertTrue(kinAccountBalance.value(0).equals("10"));

        Balance afterBalance = senderAccount.getBalanceSync();
        assertTrue((senderBalance.value().subtract(amountToSend).compareTo(afterBalance.value())) == 0);
    }

    @Test
    public void getBalanceSync() throws OperationFailedException {
        Balance balance = kinAccount.getBalanceSync();

        assertNotNull(balance);
        assertEquals("0", balance.value(0));
        assertEquals("0.0", balance.value(1));
    }

    @Test
    public void getPendingBalanceSync_withNoTransaction() throws Exception {
        Balance balance = kinAccount.getBalanceSync();
        Balance pendingBalance = kinAccount.getPendingBalanceSync();

        assertNotNull(pendingBalance);
        assertEquals(balance.value(), pendingBalance.value());
    }

    @Test
    public void getPendingBalanceSync() throws Exception {
        KinAccount senderAccount = importedAccounts.get(0);
        senderAccount.sendTransactionSync(kinAccount.getPublicAddress(), PASSPHRASE, new BigDecimal(10));
        Balance kinAccountPendingBalance = kinAccount.getPendingBalanceSync();

        assertNotNull(kinAccountPendingBalance);
    }
}