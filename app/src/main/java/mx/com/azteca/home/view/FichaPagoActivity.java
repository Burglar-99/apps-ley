package mx.com.azteca.home.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.BaseController;
import mx.com.azteca.home.controller.CDocumentInstance;
import mx.com.azteca.home.model.provider.pojo.ClienteAvaluoInfo;
import mx.com.azteca.home.util.Functions;
import mx.com.azteca.home.view.ipati.util.DialogResponse;

public class FichaPagoActivity extends BaseActivity<CDocumentInstance> {

    TextView lblMontoPago;
    TextView lblReferencia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_pago);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener( v -> {
                FichaPagoActivity.this.onBackPressed();
            });
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ficha de pago");
        }

        this.lblMontoPago = (TextView) findViewById(R.id.lblMontoPago);
        this.lblReferencia = (TextView) findViewById(R.id.lblReferencia);

        onCreateActivity(CDocumentInstance.class);
    }

    @Override
    public void onCreateActivity(Class<? extends BaseController> controller) {
        super.onCreateActivity(controller);
        getController().subscribe(getController().new ListenerDocumentInstance() {
            @Override
            public void onLoadClienteAvaluo(ClienteAvaluoInfo avaluo) {
                try {
                    if (avaluo != null && avaluo.referencia != null && !avaluo.referencia.equals("")) {
                        lblMontoPago.setText(String.format("$ %s", Functions.getInstance(Functions.Format.DECIMAL).applyNumber(avaluo.precio)));
                        JsonParser parser = new JsonParser();
                        JsonObject jData = parser.parse(avaluo.referencia).getAsJsonObject();
                        JsonArray jCharges = jData.get("charges").getAsJsonObject().get("data").getAsJsonArray();
                        String referencia = jCharges.get(0).getAsJsonObject().get("payment_method").getAsJsonObject().get("reference").getAsString();
                        lblReferencia.setText(referencia);

                    }
                }
                catch (Exception ex) {
                    DialogResponse.createDialogResponse(FichaPagoActivity.this, "Error", "Ocurió un problema al consultar la informaci\u00f3n. Favor de intentarlo m\u00e1s tarde.", "Aceptar", null, false).show(which -> {
                        if (which == Dialog.BUTTON_POSITIVE) {
                            FichaPagoActivity.this.finish();
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                DialogResponse.createDialogResponse(FichaPagoActivity.this, "Error", error, "Aceptar", null, false).show(which -> {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        FichaPagoActivity.this.finish();
                    }
                });
            }
        });

        getController().loadClienteAvaluo(false);
    }

    @Override
    public CDocumentInstance getController() {
        return super.getController();
    }
}
