package mx.com.azteca.home.view.ipati.util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DialogResponse extends AlertDialog.Builder implements DialogInterface.OnClickListener {

    private CallBack callBack;

    public DialogResponse(Context context) {
        super(context);
    }

    public void show(CallBack callBack) {
        this.callBack = callBack;
        super.show();
    }

    public void setItems(String title, CharSequence[] items) {
        super.setTitle(title);
        super.setItems(items, this);
    }

    public AlertDialog.Builder setPositiveButton(CharSequence text) {
        return super.setPositiveButton(text, this);
    }

    public AlertDialog.Builder setNegativeButton(CharSequence text) {
        return super.setNegativeButton(text, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (callBack != null)
            callBack.onClickListener(which);
    }

    public interface CallBack {
        public void onClickListener(int which);
    }

    public static DialogResponse createDialogResponse(Context context ,String title, String message, String positive, String negative) {
        return DialogResponse.createDialogResponse(context, title, message, positive, negative, true);
    }

    public static DialogResponse createDialogResponse(Context context ,String title, String message, String positive, String negative, boolean cancelable) {
        DialogResponse dialogResponse = new DialogResponse(context);
        dialogResponse.setTitle(title);
        dialogResponse.setCancelable(cancelable);
        dialogResponse.setMessage(message);
        if (positive != null)
            dialogResponse.setPositiveButton(positive);
        if (negative != null)
            dialogResponse.setNegativeButton(negative);
        dialogResponse.create();
        return dialogResponse;
    }

    @Override
    public AlertDialog show() {
        try {
            return super.show();
        }
        catch (Exception ex ){
            Toast.makeText(getContext(), "Sin respuesta", Toast.LENGTH_LONG).show();
        }
        return null;
    }
}

