package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import kin.sdk.core.Balance;

public class WalletActivity extends BaseActivity {

    public static final String TAG = WalletActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, WalletActivity.class);
    }

    private TextView balance, pendingBalance, publicKey;
    private View balanceProgress, pendingBalanceProgress;
    private DisplayCallback<Balance> balanceCallback, pendingBalanceCallback;

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

    private void initWidgets() {
        balance = (TextView) findViewById(R.id.balance);
        pendingBalance = (TextView) findViewById(R.id.pending_balance);
        publicKey = (TextView) findViewById(R.id.public_key);

        balanceProgress = findViewById(R.id.balance_progress);
        pendingBalanceProgress = findViewById(R.id.pending_balance_progress);

        final View transaction = findViewById(R.id.send_transaction_btn);
        final View refresh = findViewById(R.id.refresh_btn);
        final View getKin = findViewById(R.id.get_kin_btn);

        if (getKinClientApplication().isMainNet()) {
            transaction.setBackgroundResource(R.drawable.button_main_network_bg);
            refresh.setBackgroundResource(R.drawable.button_main_network_bg);
            getKin.setVisibility(View.GONE);
        }
        else {
            getKin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewUtils.alert(view.getContext(), "This is not implemented yet");
                }
            });
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

    private void updatePublicKey() {
        publicKey.setText(getKinClient().getAccount().getPublicAddress());
    }

    private void updateBalance() {
        balanceProgress.setVisibility(View.VISIBLE);
        balanceCallback = new DisplayCallback<Balance>(balanceProgress, balance) {
            @Override
            public void displayResult(Context context, View view, Balance result) {
                balance.setText(result.value(3));
            }
        };
        getKinClient().getAccount().getBalance(balanceCallback);
    }

    private void updatePendingBalance() {
        pendingBalanceProgress.setVisibility(View.VISIBLE);
        pendingBalanceCallback = new DisplayCallback<Balance>(pendingBalanceProgress, pendingBalance) {
            @Override
            public void displayResult(Context context, View view, Balance result) {
                pendingBalance.setText(result.value(3));
            }
        };
        getKinClient().getAccount().getPendingBalance(pendingBalanceCallback);
    }

    @Override
    Intent getBackIntent() {
        return ChooseNetworkActivity.getIntent(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pendingBalanceCallback != null) {
            pendingBalanceCallback.cancel();
        }
        if (balanceCallback != null) {
            balanceCallback.cancel();
        }
        pendingBalance = null;
        balance = null;
    }
}
