package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.math.BigDecimal;

import kin.sdk.core.KinAccount;
import kin.sdk.core.ResultCallback;
import kin.sdk.core.TransactionId;

/**
 * Created by shaybaz on 06/11/2017.
 */

public class TransactionActivity extends BaseActivity {

    public static final String TAG = TransactionActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, TransactionActivity.class);
    }

    private View sendTransaction, progressBar;
    private EditText toAddressInput, amountInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity);

        getSupportActionBar().setTitle(R.string.transaction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initWidgets();
    }

    private void initWidgets(){
        sendTransaction = findViewById(R.id.send_transaction_btn);
        progressBar = findViewById(R.id.transaction_progress);
        toAddressInput = (EditText) findViewById(R.id.to_address_input);
        amountInput = (EditText) findViewById(R.id.amount_input);

        if(getKinClientApplication().isMainNet()) {
            sendTransaction.setBackgroundResource(R.drawable.button_main_network_bg);
        }
        toAddressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!sendTransaction.isEnabled() && charSequence.length() !=0 && amountInput.getText().length() != 0){
                    sendTransaction.setEnabled(true);
                }else if(sendTransaction.isEnabled()){
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
                if(!sendTransaction.isEnabled() && charSequence.length() !=0 && toAddressInput.getText().length() != 0){
                    sendTransaction.setEnabled(true);
                }else if(sendTransaction.isEnabled()){
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

    private void sendTransaction(String toAdress, BigDecimal amount) {

        final String passphrase = getKinClientApplication().getPassphrase();
        progressBar.setVisibility(View.VISIBLE);
        getKinClient().getAccount().sendTransaction(toAdress, passphrase, amount, new ResultCallback<TransactionId>() {
            @Override
            public void onResult(TransactionId result) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                alert(e.getMessage());
            }
        });
        Log.d("###", "#### sendTransaction");

    }


}
