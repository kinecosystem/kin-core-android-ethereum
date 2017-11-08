package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import kin.sdk.core.Balance;
import kin.sdk.core.ResultCallback;

/**
 * Created by shaybaz on 06/11/2017.
 */

public class WalletActivity extends BaseActivity {

    public static final String TAG = WalletActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, WalletActivity.class);
    }

    private TextView balance, pendingBalance, publicKey;
    private View balanceProgress, pendingBalanceProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_activity);

        getSupportActionBar().setTitle(R.string.balance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initWidgets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePublicKey();
        updateBalance();
        updatePendingBalance();
    }

    private void initWidgets(){
        balance = (TextView) findViewById(R.id.balance);
        pendingBalance = (TextView) findViewById(R.id.pending_balance);
        publicKey = (TextView) findViewById(R.id.public_key);

        balanceProgress = findViewById(R.id.balance_progress);
        pendingBalanceProgress = findViewById(R.id.pending_balance_progress);

        final View transaction = findViewById(R.id.send_transaction_btn);
        final View refresh = findViewById(R.id.refresh_btn);
        final View reveal = findViewById(R.id.reveal_btn);
        final View getKin = findViewById(R.id.get_kin_btn);

        if(getKinClientApplication().isMainNet()) {
            transaction.setBackgroundResource(R.drawable.button_main_network_bg);
            refresh.setBackgroundResource(R.drawable.button_main_network_bg);
            reveal.setBackgroundResource(R.drawable.button_main_network_bg);
            getKin.setVisibility(View.GONE);
        }

        transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(TransactionActivity.getIntent(WalletActivity.this));
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBalance();
                updatePendingBalance();
            }
        });
    }

    private void updatePublicKey(){
        publicKey.setText(getKinClient().getAccount().getPublicAddress());
    }

    private void updateBalance(){
        balanceProgress.setVisibility(View.VISIBLE);
        getKinClient().getAccount().getBalance(new ResultCallback<Balance>() {
            @Override
            public void onResult(Balance result) {
                balanceProgress.setVisibility(View.GONE);
                balance.setText(result.value(3));
            }

            @Override
            public void onError(Exception e) {
                balanceProgress.setVisibility(View.GONE);
                alert(e.getMessage());
            }
        });
    }

    private void updatePendingBalance(){
        pendingBalanceProgress.setVisibility(View.VISIBLE);
        getKinClient().getAccount().getPendingBalance(new ResultCallback<Balance>() {
            @Override
            public void onResult(Balance result) {
                pendingBalanceProgress.setVisibility(View.GONE);
                pendingBalance.setText(result.value(3));
            }

            @Override
            public void onError(Exception e) {
                pendingBalanceProgress.setVisibility(View.GONE);
                alert(e.getMessage());
            }
        });
    }

    @Override
    Intent getBackIntent() {
        return NetWorksActivity.getIntent(this);
    }


    private void sendTransaction() {
        startActivity(TransactionActivity.getIntent(this));
    }
}
