package kin.sdk.core.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import kin.sdk.core.TransactionId;
import kin.sdk.core.exception.PassphraseException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Enter passphrase to generate Json content that can be used to access the current account
 */
public class GeneratePrivateKeyActivity extends BaseActivity {

    public static final String TAG = GeneratePrivateKeyActivity.class.getSimpleName();

    public static Intent getIntent(Context context) {
        return new Intent(context, GeneratePrivateKeyActivity.class);
    }

    private View generateBtn, copyBtn;
    private EditText passphraseInput;
    private TextView outputTextView;
    private DisplayCallback<TransactionId> transactionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_private_key_activity);
        initWidgets();
    }

    private void initWidgets() {
        copyBtn = findViewById(R.id.copy_btn);
        generateBtn = findViewById(R.id.generate_btn);
        passphraseInput = (EditText) findViewById(R.id.passphrase_input);
        outputTextView = (TextView) findViewById(R.id.output);

        findViewById(R.id.copy_btn).setOnClickListener(view -> {
            selectAll();
            copyToClipboard();
        });

        if (getKinClient().getServiceProvider().isMainNet()) {
            generateBtn.setBackgroundResource(R.drawable.button_main_network_bg);
        }
        passphraseInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    clearOutput();
                    if (!generateBtn.isEnabled()) {
                        generateBtn.setEnabled(true);
                    }
                } else if (generateBtn.isEnabled()) {
                    generateBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passphraseInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(view);
            }
        });

        generateBtn.setOnClickListener(view -> {
            generateBtn.setEnabled(false);
            hideKeyboard(generateBtn);
            try {
                String jsonFormatString = generatePrivateKeyStoreJsonFormat();
                updateOutput(jsonFormatString);
                copyBtn.setEnabled(true);
            } catch (PassphraseException e) {
                clearAll();
                ViewUtils.alert(this, e.getMessage());
            } catch (JSONException e) {
                clearAll();
                ViewUtils.alert(this, e.getMessage());
            }
        });
    }

    private void clearAll() {
        clearOutput();
        passphraseInput.setText("");
    }

    private void selectAll() {
        outputTextView.setSelectAllOnFocus(true);
        outputTextView.clearFocus();
        outputTextView.requestFocus();
        outputTextView.setSelectAllOnFocus(false);
    }

    private void copyToClipboard() {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(
                Context.CLIPBOARD_SERVICE);
            clipboard.setText(outputTextView.getText());
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(
                Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                .newPlainText("Private Key file", outputTextView.getText());
            clipboard.setPrimaryClip(clip);
        }
    }

    private String generatePrivateKeyStoreJsonFormat() throws PassphraseException, JSONException {
        String jsonString = getKinClient().getAccount()
            .exportKeyStore(getPassphrase(), passphraseInput.getText().toString());
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject.toString(1);
    }

    private void updateOutput(String outputString) {
        if (TextUtils.isEmpty(outputString)) {
            outputTextView.setText(outputString);
            outputTextView.setTextIsSelectable(false);
        } else {
            outputTextView.setText(outputString);
            outputTextView.setTextIsSelectable(true);
            outputTextView.requestFocus();
        }
    }

    private void clearOutput() {
        updateOutput(null);
        copyBtn.setEnabled(false);
    }

    @Override
    Intent getBackIntent() {
        return WalletActivity.getIntent(this);
    }

    @Override
    int getActionBarTitleRes() {
        return R.string.generate_private_key;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transactionCallback != null) {
            transactionCallback.onDetach();
        }
    }
}
