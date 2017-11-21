package kin.sdk.core.sample;

import android.app.Application;
import kin.sdk.core.KinClient;
import kin.sdk.core.ServiceProvider;
import kin.sdk.core.exception.EthereumClientException;

public class KinClientSampleApplication extends Application {

    //based on parity
    private final String ROPSTEN_URL = "http://159.89.240.122:8545";
    private final String INFURA_MAIN_URL = "http://159.89.240.147:8545";


    public enum NetWorkType {
        MAIN,
        ROPSTEN;
    }

    private KinClient kinClient = null;

    public KinClient createKinClient(NetWorkType type) {
        String providerUrl;
        int netWorkId;
        switch (type) {
            case MAIN:
                providerUrl = INFURA_MAIN_URL;
                netWorkId = ServiceProvider.NETWORK_ID_MAIN;
                break;
            case ROPSTEN:
                providerUrl = ROPSTEN_URL;
                netWorkId = ServiceProvider.NETWORK_ID_ROPSTEN;
                break;
            default:
                providerUrl = ROPSTEN_URL;
                netWorkId = ServiceProvider.NETWORK_ID_ROPSTEN;
        }
        try {
            kinClient = new KinClient(this,
                new ServiceProvider(providerUrl, netWorkId));
        } catch (EthereumClientException e) {
            e.printStackTrace();
        }
        return kinClient;
    }

    public KinClient getKinClient() {
        return kinClient;
    }
}
