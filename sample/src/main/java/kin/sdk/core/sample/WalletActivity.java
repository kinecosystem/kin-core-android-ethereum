package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import kin.sdk.core.exception.CreateAccountException;

/**
 * Created by shaybaz on 06/11/2017.
 */

public class WalletActivity extends AppCompatActivity {

    public static final String TAG = WalletActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, WalletActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_activity);

        getSupportActionBar().setTitle(R.string.balance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateLayout();
        findViewById(R.id.send_transaction_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTransaction();
            }
        });
    }

    private void updateLayout(){
        findViewById(R.id.send_transaction_btn).setBackgroundColor(getKinClientApplication().getNetWorkColor());
    }

    private KinClientApplication getKinClientApplication(){
        return (KinClientApplication) getApplication();
    }

    private void sendTransaction() {
        startActivity(TransactionActivity.getIntent(this));


    }


}
