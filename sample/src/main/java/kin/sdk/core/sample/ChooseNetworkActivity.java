package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kin.sdk.core.KinClient;

public class ChooseNetworkActivity extends BaseActivity {

    public static final String TAG = ChooseNetworkActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, ChooseNetworkActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_network_activity);
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

    @Override
    Intent getBackIntent() {
        return null;
    }
}
