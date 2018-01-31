package mx.com.azteca.home.view;


import android.os.Bundle;

import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.BaseController;
import mx.com.azteca.home.controller.CUsuario;

public class PresentacionActivity extends BaseActivity<CUsuario> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentacion);
        onCreateActivity(CUsuario.class);
    }

    @Override
    public void onCreateActivity(Class<? extends BaseController> controller) {
        super.onCreateActivity(controller, false);
        getController().iniciarAplicacion();
    }
}