package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kin.sdk.core.KinClient;
import kin.sdk.core.exception.CreateAccountException;

/**
 * Created by shaybaz on 06/11/2017.
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
        getSupportActionBar().setTitle(R.string.create_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initButtons();
    }

    private void initButtons() {
        View view = findViewById(R.id.btn_create_account);
        if (getKinClientApplication().isMainNet()) {
            view.setBackgroundResource(R.drawable.button_main_network_bg);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        try {
            final KinClient kinClient = getKinClient();
            final String passphrase = getKinClientApplication().getPassphrase();
            kinClient.createAccount(passphrase);
            startActivity(WalletActivity.getIntent(this));
        } catch (CreateAccountException e) {
            alert(e.getMessage());
        }
    }

    @Override
    Intent getBackIntent() {
        return NetWorksActivity.getIntent(this);
    }

}
