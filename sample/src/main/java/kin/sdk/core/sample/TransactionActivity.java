package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by shaybaz on 06/11/2017.
 */

public class TransactionActivity extends AppCompatActivity {

    public static final String TAG = TransactionActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, TransactionActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity);

        getSupportActionBar().setTitle(R.string.transaction);
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

        Log.d("###", "#### sendTransaction");

    }


}
