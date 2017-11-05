package kin.sdk.core.ethereum;

import org.ethereum.geth.Address;
import org.ethereum.geth.BoundContract;
import org.ethereum.geth.Context;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;

import java.io.File;

import kin.sdk.core.ServiceProvider;
import kin.sdk.core.exception.EthereumClientException;

/**
 * Project - Kin SDK
 * Created by Oren Zakay on 02/11/2017.
 * Copyright © 2017 Kin Foundation. All rights reserved.
 *
 * Responsible to preform all the calls to kin smart-contract.
 */
public class EthClientWrapper {

    private Context gethContext;
    private EthereumClient ethereumClient;
    private BoundContract boundContract;

    private KeyStore keyStore;

    private ServiceProvider serviceProvider;

    private static final String ABI = "[{\"constant\":true,\"inputs\":[],\"name\":\"name\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_newOwnerCandidate\",\"type\":\"address\"}],\"name\":\"requestOwnershipTransfer\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_spender\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"totalSupply\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_from\",\"type\":\"address\"},{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"isMinting\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"decimals\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_amount\",\"type\":\"uint256\"}],\"name\":\"mint\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"balance\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"acceptOwnership\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"symbol\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_to\",\"type\":\"address\"},{\"name\":\"_value\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"newOwnerCandidate\",\"outputs\":[{\"name\":\"\",\"type\":\"address\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_tokenAddress\",\"type\":\"address\"},{\"name\":\"_amount\",\"type\":\"uint256\"}],\"name\":\"transferAnyERC20Token\",\"outputs\":[{\"name\":\"success\",\"type\":\"bool\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"_owner\",\"type\":\"address\"},{\"name\":\"_spender\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"name\":\"remaining\",\"type\":\"uint256\"}],\"payable\":false,\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"endMinting\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[],\"name\":\"MintingEnded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"owner\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"spender\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Approval\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"_by\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"OwnershipRequested\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":true,\"name\":\"_from\",\"type\":\"address\"},{\"indexed\":true,\"name\":\"_to\",\"type\":\"address\"}],\"name\":\"OwnershipTransferred\",\"type\":\"event\"}]";

    public EthClientWrapper(android.content.Context androidContext, ServiceProvider serviceProvider) throws EthereumClientException {
        this.serviceProvider = serviceProvider;
        this.gethContext = new Context();
        try {
            this.ethereumClient = Geth.newEthereumClient(serviceProvider.getProviderUrl());
        } catch (Exception e) {
            throw new EthereumClientException("provider - could not establish connection to the provider");
        }

        try {
            Address contractAddress = Geth.newAddressFromHex("0xEF2Fcc998847DB203DEa15fC49d0872C7614910C");
            this.boundContract = Geth.bindContract(contractAddress, ABI, ethereumClient);
        } catch (Exception e) {
            throw new EthereumClientException("contract - could not establish connection to kin contract");
        }
        initKeyStore(androidContext);
    }

    /**
     * Create {@link KeyStore}, to have control over the account management.
     * And the ability to store accounts securely according to go-ethereum encryption protocol.
     *
     * @param context provide the path to internal data directories.
     */
    private void initKeyStore(android.content.Context context) {
        String networkId = String.valueOf(getNetworkId());
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
     * @return the network id that the client is connected to.
     */
    public int getNetworkId(){
        return serviceProvider.getNetworkId();
    }

}
