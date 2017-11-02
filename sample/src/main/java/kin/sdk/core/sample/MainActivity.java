package kin.sdk.core.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import kin.sdk.core.Balance;
import kin.sdk.core.KinAccount;
import kin.sdk.core.KinClient;
<<<<<<< HEAD
import kin.sdk.core.ServiceProvider;
=======
import kin.sdk.core.ResultCallback;
>>>>>>> KIK-7623-add_async_to_api_methods
import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.mock.MockKinAccount;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.output);
        initKinClient();
    }

    private void updateOutput(String output){
        textView.setText(output);
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
            updateOutput(kinClient.getAccount().getPublicAddress());

            kinClient.getAccount().getPendingBalance(new ResultCallback<Balance>() {
                @Override
                public void onResult(Balance result) {
                    updateOutput("balance " + result.value().toString());
                }

                @Override
                public void onError(Exception e) {
                    updateOutput("error "+e.getMessage());
                }
            });
        } catch (CreateAccountException e) {
            e.printStackTrace();
        }
    }
}
