package mx.com.azteca.home.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.BaseController;
import mx.com.azteca.home.controller.CDocumentInstance;
import mx.com.azteca.home.model.provider.pojo.ClienteAvaluoInfo;
import mx.com.azteca.home.util.Functions;

public class MetodoPagoActivity extends BaseActivity<CDocumentInstance> {

    final int TARJETA_SELECCION = 1001;

    TextView lblFecha;
    TextView lblPrecio;
    LinearLayout layoutTarjeta;
    LinearLayout layoutOxxo;
    LinearLayout layoutPaypal;
    TextView lblRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metodo_pago);
        onCreateActivity(CDocumentInstance.class);

        this.lblFecha       = (TextView) findViewById(R.id.lblFecha);
        this.lblPrecio      = (TextView) findViewById(R.id.lblPrecio);
        this.layoutTarjeta  = (LinearLayout) findViewById(R.id.layout_tarjeta);
        this.layoutOxxo     = (LinearLayout) findViewById(R.id.layout_oxxo);
        this.layoutPaypal   = (LinearLayout) findViewById(R.id.layout_paypal);
        this.lblRegresar    = (TextView) findViewById(R.id.lblRegresar);

        this.layoutTarjeta.setOnClickListener((View v) ->{
            Intent intent = new Intent(MetodoPagoActivity.this, PagoActivity.class);
            intent.putExtra("OPERACION", "SELECTOR");
            startActivityForResult(intent, TARJETA_SELECCION);
        });

        this.layoutOxxo.setOnClickListener((View v) ->{
            getController().generarCargoOxxo();
        });

        this.layoutPaypal.setOnClickListener((View v) ->{
            notificarUsuario("Informaci\u00f3n", "Opci\u00f3n proximamente disponible");
        });
    }

    @Override
    public void onCreateActivity(Class<? extends BaseController> controller) {
        super.onCreateActivity(controller);
        getController().subscribe(getController(). new ListenerDocumentInstance() {
            @Override
            public void onLoadClienteAvaluo(ClienteAvaluoInfo avaluo) {
                lblFecha.setText(Functions.getInstance(Functions.Format.DATE_TIME_TEXT).applyTime(avaluo.fecha.getTime()));
                lblPrecio.setText(String.format("$ %s", Functions.getInstance(Functions.Format.DECIMAL).applyNumber(avaluo.precio)));
            }

            @Override
            public void onCargoGenerado() {
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void onReferenciaGenerada() {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        getController().loadClienteAvaluo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TARJETA_SELECCION) {
                if (data.getExtras() != null && data.getExtras().containsKey("METODO_PAGO")) {
                    String metodoPago = data.getExtras().getString("METODO_PAGO");
                    getController().generarCargoTarjeta(metodoPago);
                }
            }
        }
    }

    @Override
    public CDocumentInstance getController() {
        return super.getController();
    }
}
