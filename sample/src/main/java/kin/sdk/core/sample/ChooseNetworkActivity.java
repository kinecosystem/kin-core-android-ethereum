package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kin.sdk.core.KinClient;

/**
 * User is given a choice to create or use an account on the MAIN or ROPSTEN(test) ethereum networks
 */
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
                createKinClient(KinClientSampleApplication.NetWorkType.MAIN);
            }
        });

        findViewById(R.id.btn_test_net).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createKinClient(KinClientSampleApplication.NetWorkType.ROPSTEN);
            }
        });
    }

    private void createKinClient(KinClientSampleApplication.NetWorkType netWorkType) {
        KinClientSampleApplication application = (KinClientSampleApplication) getApplication();
        KinClient kinClient = application.createKinClient(netWorkType);
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
