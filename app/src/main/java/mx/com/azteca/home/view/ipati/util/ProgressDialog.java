package mx.com.azteca.home.view.ipati.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

public class ProgressDialog extends android.app.ProgressDialog {

    private CountDownTimer countDownTimer;
    private static ProgressDialog PROGRESS_DIALOG;

    private ProgressDialog(Context context, String titulo, String mensaje) {
        super(context);
        super.setTitle(titulo);
        super.setMessage(mensaje);
        super.setCancelable(false);
        super.setIndeterminate(true);
    }

    private ProgressDialog(Context context, String titulo, String mensaje, int timer) {
        super(context);
        super.setTitle(titulo);
        super.setMessage(mensaje);
        super.setCancelable(false);
        super.setIndeterminate(true);
        countDownTimer = new CountDownTimer(timer, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.e(getClass().getName(), "Tiempo:" +( millisUntilFinished / 1000));
            }
            public void onFinish() {
                try {
                    ProgressDialog.super.dismiss();
                }
                catch (Exception ex) {
                    Log.e("ProgressDialog", "Error:" + ex.getMessage());
                }
            }
        }.start();
    }

    public static ProgressDialog cargarProgressDialog(Context context, String titulo, String mensaje) {
        if (PROGRESS_DIALOG != null && PROGRESS_DIALOG.isShowing()) {
            PROGRESS_DIALOG.dismiss();
        }
        return PROGRESS_DIALOG = new ProgressDialog(context, titulo, mensaje);
    }

    public static ProgressDialog cargarProgressDialog(Context context, String titulo, String mensaje, int timer) {
        if (PROGRESS_DIALOG != null && PROGRESS_DIALOG.isShowing()) {
            PROGRESS_DIALOG.dismiss();
        }
        return PROGRESS_DIALOG = new ProgressDialog(context, titulo, mensaje, timer);
    }

    public ProgressDialog mostrar() {
        try {
            super.show();
        }
        catch (Exception ex ) {
            Log.e(getClass().getName(), ex.toString());
        }
        return this;
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
        }
        catch (Exception ex) {
            Log.e("ProgressDialog", "Error:" + ex.getMessage());
        }
    }
}
