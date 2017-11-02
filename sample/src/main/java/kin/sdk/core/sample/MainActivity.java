package kin.sdk.core.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import kin.sdk.core.KinClient;
import kin.sdk.core.ServiceProvider;
import kin.sdk.core.exception.CreateAccountException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initKinClient();
    }

    private void initKinClient() {
        // just to see that the project compiles
        // we will create a nice sample app
        // that demonstrates usage of the library soon.
        String infuraToken = "yourinfuratoken";
        KinClient kinClient = new KinClient(getApplicationContext(),
                new ServiceProvider("https://ropsten.infura.io/"+infuraToken, ServiceProvider.NETWORK_ID_ROPSTEN));
        try {
            kinClient.createAccount("abcd1234");
        } catch (CreateAccountException e) {
            e.printStackTrace();
        }
    }
}
