package kin.sdk.core.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import kin.sdk.core.KinClient;

public abstract class BaseActivity extends AppCompatActivity {

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
        return getKinClientApplication().getKinClient();
    }

    public KinClientApplication getKinClientApplication() {
        return ((KinClientApplication) getApplication());
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
