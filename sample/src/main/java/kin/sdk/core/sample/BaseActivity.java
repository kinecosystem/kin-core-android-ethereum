package kin.sdk.core.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import kin.sdk.core.KinClient;

/**
 * Created by shaybaz on 09/11/2017.
 */

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

    public void alert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
