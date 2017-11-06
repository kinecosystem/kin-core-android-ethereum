package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import kin.sdk.core.KinClient;
import kin.sdk.core.exception.CreateAccountException;

/**
 * Created by shaybaz on 06/11/2017.
 */

public class CreateAccountActivity extends AppCompatActivity {

    public static final String TAG = CreateAccountActivity.class.getSimpleName();
    public static final String PASSPHRASE = "12345";

    public static Intent getIntent(Context context) {
        return new Intent(context, CreateAccountActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);

        getSupportActionBar().setTitle(R.string.create_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateLayout();
        findViewById(R.id.btn_create_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void updateLayout(){
        findViewById(R.id.btn_create_account).setBackgroundColor(getKinClientApplication().getNetWorkColor());
    }

    private String getPassPhrase() {
        return PASSPHRASE;
    }

    private KinClientApplication getKinClientApplication(){
        return (KinClientApplication) getApplication();
    }

    private void createAccount() {
        try {
            getKinClientApplication().getKinClient().createAccount(getPassPhrase());
            startActivity(WalletActivity.getIntent(this));
        } catch (CreateAccountException e) {
            Log.d("####", "#### error dialog");
            e.printStackTrace();
        }


    }


}
