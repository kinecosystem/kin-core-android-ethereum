package kin.sdk.core.sample;

import android.app.Application;
import kin.sdk.core.KinClient;
import kin.sdk.core.ServiceProvider;
import kin.sdk.core.exception.EthereumClientException;

public class KinClientSampleApplication extends Application {

    private final String INFURA_ROPSTEN_BASE_URL = "https://ropsten.infura.io/";
    private final String INFURA_MAIN_BASE_URL = "https://mainnet.infura.io/";
    //replace with your infura token
    private final String infuraToken = "your-infura-token-here";

    public enum NetWorkType {
        MAIN,
        ROPSTEN;
    }

    private KinClient kinClient = null;

    public KinClient createKinClient(NetWorkType type) {
        String baseUrl;
        int netWorkId;
        switch (type) {
            case MAIN:
                baseUrl = INFURA_MAIN_BASE_URL;
                netWorkId = ServiceProvider.NETWORK_ID_MAIN;
                break;
            case ROPSTEN:
                baseUrl = INFURA_ROPSTEN_BASE_URL;
                netWorkId = ServiceProvider.NETWORK_ID_ROPSTEN;
                break;
            default:
                baseUrl = INFURA_ROPSTEN_BASE_URL;
                netWorkId = ServiceProvider.NETWORK_ID_ROPSTEN;
        }
        try {
            kinClient = new KinClient(this,
                new ServiceProvider(baseUrl + infuraToken, netWorkId));
        } catch (EthereumClientException e) {
            e.printStackTrace();
        }
        return kinClient;
    }

    public KinClient getKinClient() {
        return kinClient;
    }

}
