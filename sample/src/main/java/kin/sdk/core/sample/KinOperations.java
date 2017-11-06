package kin.sdk.core.sample;

import kin.sdk.core.Balance;
import kin.sdk.core.ResultCallback;

/**
 * Created by shaybaz on 06/11/2017.
 */

public interface KinOperations {

    void createAccount();

    void sendTransaction();

    String getPublicAddress();

    void getBalance(ResultCallback<Balance> callback);

    void onError(String title, String error);

    void setMainNetwork(boolean isMain);

}
