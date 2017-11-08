package kin.sdk.core.ethereum;

import org.ethereum.geth.Account;
import org.ethereum.geth.Address;
import org.ethereum.geth.BigInt;
import org.ethereum.geth.BoundContract;
import org.ethereum.geth.Context;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Interface;
import org.ethereum.geth.Interfaces;
import org.ethereum.geth.KeyStore;
import org.ethereum.geth.TransactOpts;
import org.ethereum.geth.Transaction;

import java.io.File;
import java.math.BigDecimal;

import kin.sdk.core.Balance;
import kin.sdk.core.ServiceProvider;
import kin.sdk.core.TransactionId;
import kin.sdk.core.exception.EthereumClientException;
import kin.sdk.core.exception.InsufficientBalanceException;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;
import kin.sdk.core.impl.TransactionIdImpl;
import kin.sdk.core.mock.MockBalance;

/**
 * Project - Kin SDK
 * Created by Oren Zakay on 02/11/2017.
 * <p>
 * Responsible to preform all the calls to Kin smart-contract.
 */
public class EthClientWrapper {

    private static final long DEFAULT_GAS_LIMIT = 4300000;

    private Context gethContext;
    private EthereumClient ethereumClient;
    private BoundContract boundContract;

    private KeyStore keyStore;

    private ServiceProvider serviceProvider;

    private long nonce = -1;
    private BigInt gasPrice = null;

    private static final String ABI = "[{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_newOwnerCandidate\",\"type\":\"address\"}],\"name\":\"requestOwnershipTransfer\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"isMinting\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_amount\",\"type\":\"uint256\"}],\"name\":\"mint\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"balance\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"acceptOwnership\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"newOwnerCandidate\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_tokenAddress\",\"type\":\"address\"},{\"name\":\"_amount\",\"type\":\"uint256\"}],\"name\":\"transferAnyERC20Token\",\"outputs\":[{\"name\":\"success\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"},{\"name\":\"_spender\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"name\":\"remaining\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"endMinting\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[],\"name\":\"MintingEnded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"spender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Approval\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"_by\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"OwnershipRequested\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"_from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"OwnershipTransferred\",\"type\":\"event\"}]";

    public EthClientWrapper(android.content.Context androidContext, ServiceProvider serviceProvider) throws EthereumClientException {
        this.serviceProvider = serviceProvider;
        this.gethContext = new Context();
        initEthereumClient();
        initKinContract();
        initKeyStore(androidContext);
    }

    /**
     * Create {@link EthereumClient}, that will be a connection to Ethereum network.
     *
     * @throws EthereumClientException if go-ethereum could not establish connection to the provider.
     */
    private void initEthereumClient() throws EthereumClientException {
        try {
            this.ethereumClient = Geth.newEthereumClient(serviceProvider.getProviderUrl());
        } catch (Exception e) {
            throw new EthereumClientException("provider - could not establish connection to the provider");
        }
    }

    /**
     * Create {@link BoundContract}, that will handle all the calls to Kin smart-contract.
     *
     * @throws EthereumClientException if go-ethereum could not establish connection to Kin smart-contract.
     */
    private void initKinContract() throws EthereumClientException {
        try {
            String kinContractAddress = "0xEF2Fcc998847DB203DEa15fC49d0872C7614910C";
            Address contractAddress = Geth.newAddressFromHex(kinContractAddress);
            this.boundContract = Geth.bindContract(contractAddress, ABI, ethereumClient);
        } catch (Exception e) {
            throw new EthereumClientException("contract - could not establish connection to Kin smart-contract");
        }
    }

    /**
     * Create {@link KeyStore}, to have control over the account management.
     * And the ability to store accounts securely according to go-ethereum encryption protocol.
     * The keystore path is unique to each network id,
     * for example Ropsten network will be: ../data/kin/keystore/3/
     *
     * @param context provide the path to internal data directories.
     */
    private void initKeyStore(android.content.Context context) {
        String networkId = String.valueOf(serviceProvider.getNetworkId());
        String keyStorePath = new StringBuilder(context.getFilesDir().getAbsolutePath())
                .append(File.separator)
                .append("kin")
                .append(File.separator)
                .append("keystore")
                .append(File.separator)
                .append(networkId).toString();

        // Make directories if necessary, the keystore will be saved there.
        File keystoreDir = new File(keyStorePath);
        if (!keystoreDir.exists()) {
            keystoreDir.mkdir();
        }
        // Create a keyStore instance according to go-ethereum encryption protocol.
        keyStore = Geth.newKeyStore(keystoreDir.getAbsolutePath(), Geth.LightScryptN, Geth.LightScryptP);
    }

    /**
     * @return {@link KeyStore} that will handle all operations related to accounts.
     */
    public KeyStore getKeyStore() {
        return keyStore;
    }

    /**
     * Transfer amount of KIN from account to the specified public address.
     *
     * @param from          the sender {@link Account}
     * @param passphrase    the passphrase used to create the account
     * @param publicAddress the address to send the KIN to
     * @param amount        the amount of KIN to send
     * @return {@link TransactionId} of the transaction
     * @throws InsufficientBalanceException if the account has not enough KIN
     * @throws PassphraseException          if the transaction could not be signed with the passphrase specified
     * @throws OperationFailedException     another error occurred
     */
    public TransactionId sendTransaction(Account from, String passphrase, String publicAddress, BigDecimal amount)
            throws InsufficientBalanceException, OperationFailedException, PassphraseException {
        Transaction transaction;
        Address toAddress;
        BigInt amountBigInt = null;
        try {
            // Verify public address is valid.
            if (publicAddress == null || publicAddress.isEmpty()) {
                throw new OperationFailedException("Addressee not valid - public address can't be null or empty");
            }
            // Create the public Address.
            toAddress = Geth.newAddressFromHex(publicAddress);

            // Make sure the amount is positive and the sender account has enough Kin to send.
            if (amount.signum() != -1) {
                amount = KinConverter.toKin(amount);
                if (hasEnoughBalance(amount)) {
                    amountBigInt = KinConverter.fromKin(amount);
                } else {
                    throw new InsufficientBalanceException();
                }
            } else {
                throw new OperationFailedException("Amount can't be negative");
            }

            nonce = ethereumClient.getPendingNonceAt(gethContext, from.getAddress());
            gasPrice = ethereumClient.suggestGasPrice(gethContext);

            // Create TransactionOps and send to Kin smart-contract with the required params.
            TransactOpts transactOpts = new TransactOpts();
            transactOpts.setContext(gethContext);
            transactOpts.setGasLimit(DEFAULT_GAS_LIMIT);
            transactOpts.setGasPrice(gasPrice);
            transactOpts.setNonce(nonce);
            transactOpts.setFrom(from.getAddress());
            transactOpts.setSigner(new KinSigner(from, getKeyStore(), passphrase, serviceProvider.getNetworkId()));

            Interface paramToAddress = Geth.newInterface();
            paramToAddress.setAddress(toAddress);

            Interface paramAmount = Geth.newInterface();
            paramAmount.setBigInt(amountBigInt);

            // Set param values to the slice.
            Interfaces params = Geth.newInterfaces(2);
            params.set(0, paramToAddress);
            params.set(1, paramAmount);

            // Send transfer call to Kin smart-contract.
            transaction = boundContract.transact(transactOpts, "transfer", params);
        } catch (Exception e) {
            throw new OperationFailedException(e);
        }

        return new TransactionIdImpl(transaction.getHash().getHex());
    }

    private boolean hasEnoughBalance(BigDecimal amount) {
        Balance balance = getBalance();
        // (== -1) means bigger than the amount.
        return balance.value().subtract(amount).compareTo(BigDecimal.ZERO) == -1;
    }

    public Balance getBalance() {
        return new MockBalance();
    }
}
