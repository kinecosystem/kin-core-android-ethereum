package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import kin.sdk.core.KinClient;
import kin.sdk.core.exception.CreateAccountException;

/**
 * This activity is displayed only if there is no existing account stored on device for the given network
 * The activity will just display a button to create an account
 */
public class CreateAccountActivity extends BaseActivity {

    public static final String TAG = CreateAccountActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, CreateAccountActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);
        initWidgets();
    }

    private void initWidgets() {
        View createAccountView = findViewById(R.id.btn_create_account);
        if (isMainNet()) {
            createAccountView.setBackgroundResource(R.drawable.button_main_network_bg);
        }
        createAccountView.setOnClickListener(view -> createAccount());
    }

    private void createAccount() {
        try {
            final KinClient kinClient = getKinClient();
            kinClient.createAccount(getPassphrase());
            startActivity(WalletActivity.getIntent(this));
        } catch (CreateAccountException e) {
            ViewUtils.alert(this, e.getMessage());
        }
    }

    @Override
    Intent getBackIntent() {
        return ChooseNetworkActivity.getIntent(this);
    }

    @Override
    int getActionBarTitleRes() {
        return R.string.create_account;
    }

}
