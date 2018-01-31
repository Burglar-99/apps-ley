package mx.com.azteca.home.view;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Calendar;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import mx.com.azteca.home.R;


public class TarjetaDialog extends Dialog {

    public enum Actividad {
        REGISTRAR,
        MODIFICAR
    }

    private TarjetaDialog.Listener listener;

    private MaskedEditText txtTarjeta;
    private MaskedEditText txtFecha;
    private MaskedEditText txtCVV;

    private Actividad actividad;


    public TarjetaDialog(final Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tarjeta);
        findViewById(R.id.lblError).setVisibility(View.GONE);

        this.txtTarjeta = (MaskedEditText) findViewById(R.id.txtTarjeta);
        this.txtFecha   = (MaskedEditText) findViewById(R.id.txtFecha);
        this.txtCVV     = (MaskedEditText) findViewById(R.id.txtCVV);

        Button btnAceptar = (Button) findViewById(R.id.btnAceptar);
        btnAceptar.setOnClickListener(view -> {
            try {
                String tarjeta  = txtTarjeta.getText().toString();
                String fecha    = txtFecha.getText().toString();
                String cvv      = txtCVV.getText().toString();
                int anio        = 0;
                int mes         = 0;

                if (fecha.trim().equals("") || fecha.trim().equals("MM/AA"))
                    txtFecha.setError("Dato requerido");

                if (cvv.trim().equals("") || cvv.trim().equals("CVV"))
                    txtCVV.setError("Dato requerido");

                if (txtFecha.getError() != null || txtCVV.getError() != null)
                    return;

                String calendarAnio = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                try {
                    anio = Integer.parseInt(calendarAnio.substring(0, 2) +  fecha.split("/")[1]);
                    txtFecha.setError(null);
                }
                catch (Exception ex) {
                    txtFecha.setError("Dato incorrecto");
                    throw ex;
                }

                try {
                    mes = Integer.parseInt(fecha.split("/")[0]);
                    txtFecha.setError(null);
                }
                catch (Exception ex) {
                    txtFecha.setError("Dato incorrecto");
                    throw ex;
                }

                if (listener != null) {
                    if (actividad == Actividad.REGISTRAR)
                        listener.guardarTarjeta(tarjeta, mes, anio, cvv);
                    else
                        listener.actualizarTarjeta(tarjeta, mes, anio, cvv);
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

        findViewById(R.id.btnScanner).setOnClickListener(v -> {
            if (listener != null) {
                listener.leerTarjeta();
            }
        });

        findViewById(R.id.btnEliminar).setOnClickListener(v -> {
            if (listener != null) {
                listener.eliminar(txtTarjeta.getText().toString());
            }
            dismiss();
        });
    }

    public void show(Listener listener, Actividad actividad) {
        this.listener = listener;
        this.actividad = actividad;
        if (actividad == Actividad.MODIFICAR) {
            txtTarjeta.setEnabled(false);
            findViewById(R.id.btnScanner).setVisibility(View.GONE);
            findViewById(R.id.btnEliminar).setVisibility(View.VISIBLE);
        }
        else {
            txtTarjeta.setEnabled(true);
            findViewById(R.id.btnScanner).setVisibility(View.VISIBLE);
            findViewById(R.id.btnEliminar).setVisibility(View.GONE);
        }
        super.show();
    }

    public void setDatos(String tarjeta, int month, int year, String cvv) {
        this.txtTarjeta.setText(tarjeta);
        this.txtFecha.setText((month < 10 ? "0" + month : month) + "" +  (String.valueOf(year).length()  == 4 ? String.valueOf(year).substring(2, 4) : ""));
        this.txtCVV.setText(cvv);
    }

    public interface Listener {
        void leerTarjeta();
        void guardarTarjeta(String tarjeta, int month, int year, String cvv);
        void actualizarTarjeta(String tarjeta, int month, int year, String cvv);
        void eliminar(String tarjeta);
    }

}
