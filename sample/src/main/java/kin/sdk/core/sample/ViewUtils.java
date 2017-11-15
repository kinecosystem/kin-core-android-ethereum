package kin.sdk.core.sample;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ViewUtils {

    public static void alert(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
