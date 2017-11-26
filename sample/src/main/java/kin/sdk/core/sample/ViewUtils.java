package kin.sdk.core.sample;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewUtils {

    public interface onAlertClicked {

        void onConfirm();
    }

    public static void alert(Context context, String message) {
        AlertDialog dialog;
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context);
        if (TextUtils.isEmpty(message)) {
            message = context.getResources().getString(R.string.error_no_message);
        }
        builder.setView(buildAlertView(context, message));
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.ok),
            (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }

    public static void confirmAlert(Context context, String message, String button, onAlertClicked onAlertClicked) {
        AlertDialog dialog;
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(context);
        if (TextUtils.isEmpty(message)) {
            message = context.getResources().getString(R.string.error_no_message);
        }
        builder.setView(buildAlertView(context, message));
        dialog = builder.create();
        dialog.setCancelable(true);
        if (TextUtils.isEmpty(button)) {
            button = context.getResources().getString(R.string.ok);
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, button,
            (dialogInterface, i) -> {
                dialogInterface.dismiss();
                if (onAlertClicked != null) {
                    onAlertClicked.onConfirm();
                }
            });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancel),
            (dialogInterface, i) -> dialogInterface.dismiss());
        dialog.show();
    }

    private static View buildAlertView(Context context, String message) {
        TextView textView = new TextView(context);
        textView.setTextColor(R.drawable.text_color);
        textView.setTextIsSelectable(true);
        textView.setTextSize(18f);
        textView.setText(message);
        textView.setGravity(Gravity.LEFT);
        textView.setPadding(35, 35, 35, 0);
        textView.setOnLongClickListener(v -> {
            copyToClipboard(v.getContext(), message);
            Toast.makeText(v.getContext(), R.string.copied_to_clipboard,
                Toast.LENGTH_SHORT)
                .show();
            return true;
        });
        return textView;
    }

    public static void copyToClipboard(Context context, CharSequence textToCopy) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
            clipboard.setText(textToCopy);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                .newPlainText("copied text", textToCopy);
            clipboard.setPrimaryClip(clip);
        }
    }
}
