package mx.com.azteca.home.view;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.regex.Pattern;

import mx.com.azteca.home.R;

public class RecuperarDialog extends Dialog {

    private Listener listener;


    public RecuperarDialog(final Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_recuperar);

        EditText txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        Button btnAceptar   = (Button) findViewById(R.id.btnAceptar);

        btnAceptar.setOnClickListener(view -> {
            try {
                String usuario = txtUsuario.getText().toString();
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (usuario.trim().length() == 0) {
                    txtUsuario.setError("Ingrese tu email");
                }
                else if (pattern.matcher(usuario).matches()) {
                    txtUsuario.setError(null);
                    if (listener != null) {
                        listener.onEnviar(usuario);
                    }
                    dismiss();
                }
                else {
                    txtUsuario.setError("Email incorrecto");
                }
            }
            catch (Exception ex) {
                Log.e(getClass().getSimpleName(), ex.toString());
            }
        });

        ImageButton imgClose = (ImageButton) findViewById(R.id.imgCerrar);
        imgClose.setOnClickListener(view -> {
            dismiss();
        });
    }

    public void show(RecuperarDialog.Listener listener) {
        this.listener = listener;
        super.show();
    }

    public interface Listener {
        void onEnviar(String usuario);
    }


}
