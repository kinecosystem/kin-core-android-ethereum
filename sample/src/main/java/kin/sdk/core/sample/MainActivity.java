package kin.sdk.core.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import kin.sdk.core.Balance;
import kin.sdk.core.KinClient;
import kin.sdk.core.ResultCallback;
import kin.sdk.core.ServiceProvider;
import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.exception.EthereumClientException;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    KinClient kinClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.output);
        initKinClient();
    }

    private void updateOutput(String output) {
        textView.setText(output);
    }

    private void initKinClient() {
        String infuraToken = "yourinfuratoken";
        try {
            kinClient = new KinClient(getApplicationContext(),
                    new ServiceProvider("https://ropsten.infura.io/"+infuraToken, ServiceProvider.NETWORK_ID_ROPSTEN));
        } catch (EthereumClientException e) {
            e.printStackTrace();
        }
    }

    public void checkBalance(View view) {
        if(kinClient.hasAccounts()) {
            kinClient.getAccount().getBalance(new ResultCallback<Balance>() {
                @Override
                public void onResult(Balance result) {
                    updateOutput("balance " + result.value().toString());
                }

                @Override
                public void onError(Exception e) {
                    updateOutput("error " + e.getMessage());
                }
            });
        }else{
            updateOutput("Account not created");
        }
    }

    public void createAccount(View view) {
        try {
            kinClient.createAccount("abcd1234");
            updateOutput("client created " + kinClient.getAccount().getPublicAddress());
        } catch (CreateAccountException e) {
            e.printStackTrace();
        }
    }
}
