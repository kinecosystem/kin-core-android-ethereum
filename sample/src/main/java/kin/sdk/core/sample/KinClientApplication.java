package kin.sdk.core.sample;

import android.app.Application;
import android.graphics.Color;

import kin.sdk.core.KinClient;
import kin.sdk.core.ServiceProvider;
import kin.sdk.core.exception.EthereumClientException;

/**
 * Created by shaybaz on 08/11/2017.
 */

public class KinClientApplication extends Application {
    private final String INFURA_ROPSTEN_BASE_URL = "https://ropsten.infura.io/";
    private final String INFURA_MAIN_BASE_URL = "https://mainnet.infura.io/";
    //replace with your infura token
    private final String infuraToken = "yourInfuraToken";
    private NetWorkType netWorkType;

    public enum NetWorkType {
        MAIN(Color.parseColor("#FF8A00")),
        ROPSTEN(Color.parseColor("#26BEFF"));

        NetWorkType(int color){
            this.color = color;
        }
        int color;
    }



    private KinClient kinClient;

    public KinClient initKinClient(NetWorkType type) {
        netWorkType = type;
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

    public int getNetWorkColor(){
        return netWorkType.color;
    }

    public KinClient getKinClient() {
        return kinClient;
    }
}
