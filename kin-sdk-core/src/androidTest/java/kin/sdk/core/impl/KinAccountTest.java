package kin.sdk.core.impl;

import android.support.test.runner.AndroidJUnit4;


import java.math.BigDecimal;
import kin.sdk.core.exception.InsufficientBalanceException;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import kin.sdk.core.Balance;
import kin.sdk.core.BaseTest;
import kin.sdk.core.KinAccount;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class KinAccountTest extends BaseTest {

    private final String PASSPHRASE = "testPassphrase";
    private final String TO_ADDRESS = "0x82CdC15705CE9f4565DDa07d78c92ff3d2717854";

    private KinAccount kinAccount;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        kinAccount = kinClient.createAccount(PASSPHRASE);
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
        //TODO We need to have few accounts with KIN + ETHER balances,
        //TODO so we can send transaction.
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
        //TODO We need to have few accounts with KIN + ETHER balances,
        //TODO so we can send transaction and check the pending balance.
    }
}