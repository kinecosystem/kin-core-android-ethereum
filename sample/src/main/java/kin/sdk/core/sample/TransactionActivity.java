package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import java.math.BigDecimal;
import kin.sdk.core.TransactionId;

/**
 * Displays form to enter public address and amount and a button to send a transaction
 */
public class TransactionActivity extends BaseActivity {

    public static final String TAG = TransactionActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, TransactionActivity.class);
    }

    private View sendTransaction, progressBar;
    private EditText toAddressInput, amountInput;
    private DisplayCallback<TransactionId> transactionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity);

        getSupportActionBar().setTitle(R.string.transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initWidgets();
    }

    private void initWidgets() {
        sendTransaction = findViewById(R.id.send_transaction_btn);
        progressBar = findViewById(R.id.transaction_progress);
        toAddressInput = (EditText) findViewById(R.id.to_address_input);
        amountInput = (EditText) findViewById(R.id.amount_input);

        if (getKinClient().isMainNet()) {
            sendTransaction.setBackgroundResource(R.drawable.button_main_network_bg);
        }
        toAddressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!sendTransaction.isEnabled() && charSequence.length() != 0 && amountInput.getText().length() != 0) {
                    sendTransaction.setEnabled(true);
                } else if (sendTransaction.isEnabled()) {
                    sendTransaction.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!sendTransaction.isEnabled() && charSequence.length() != 0
                    && toAddressInput.getText().length() != 0) {
                    sendTransaction.setEnabled(true);
                } else if (sendTransaction.isEnabled()) {
                    sendTransaction.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sendTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BigDecimal amount = new BigDecimal(amountInput.getText().toString());
                sendTransaction(toAddressInput.getText().toString(), amount);
            }
        });
    }

    @Override
    Intent getBackIntent() {
        return WalletActivity.getIntent(this);
    }

    private void sendTransaction(String toAddress, BigDecimal amount) {

        progressBar.setVisibility(View.VISIBLE);
        transactionCallback = new DisplayCallback<TransactionId>(progressBar) {
            @Override
            public void displayResult(Context context, View view, TransactionId transactionId) {
                ViewUtils.alert(context, "Transaction id " + transactionId.id());
            }
        };
        getKinClient().getAccount().sendTransaction(toAddress, PASSPHRASE, amount, transactionCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transactionCallback != null) {
            transactionCallback.cancel();
        }
        progressBar = null;
    }
}
