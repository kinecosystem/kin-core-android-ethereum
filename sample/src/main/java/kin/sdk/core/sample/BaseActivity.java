package kin.sdk.core.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import kin.sdk.core.KinClient;

public abstract class BaseActivity extends AppCompatActivity {

    // ideally user should be asked for a passphrase when
    // creating an account and then the same passphrase
    // should be used when sending transactions
    // To make the UI simpler for the sample application
    // we are using a hardcoded passphrase.
    static final String PASSPHRASE = "12345";
    abstract Intent getBackIntent();

    @Override
    public void onBackPressed() {
        Intent intent = getBackIntent();
        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
        finish();
    }

    public KinClient getKinClient() {
        KinClientSampleApplication application = (KinClientSampleApplication) getApplication();
        return application.getKinClient();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

}
