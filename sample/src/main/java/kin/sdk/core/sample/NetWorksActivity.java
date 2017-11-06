package kin.sdk.core.sample;

import android.app.Application;
import android.app.IntentService;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import kin.sdk.core.KinClient;

/**
 * Created by shaybaz on 06/11/2017.
 */

public class NetWorksActivity extends AppCompatActivity {

    public static final String TAG = NetWorksActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.networks_activity);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        findViewById(R.id.btn_main_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initClient(KinClientApplication.NetWorkType.MAIN);
            }
        });

        findViewById(R.id.btn_test_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initClient(KinClientApplication.NetWorkType.ROPSTEN);
            }
        });
    }

    private void initClient(KinClientApplication.NetWorkType netWorkType) {
        KinClientApplication application = (KinClientApplication) getApplication();
        KinClient kinClient = application.initKinClient(netWorkType);
        if (kinClient.hasAccounts()) {

            startActivity(WalletActivity.getIntent(this));
        } else {
            startActivity(CreateAccountActivity.getIntent(this));
        }


    }


}
