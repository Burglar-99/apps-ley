package mx.com.azteca.home.view;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.BaseController;
import mx.com.azteca.home.controller.CUsuario;

public class CrearCuentaActivity extends BaseActivity<CUsuario> {

    EditText txtNombre;
    EditText txtApPaterno;
    EditText txtApMaterno;
    EditText txtUsuario;
    EditText txtContrasena;
    EditText txtConfirmacion;
    CheckBox chkTermino;
    TextView lblCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setSubtitle("Crear cuenta");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> {
                CrearCuentaActivity.this.onBackPressed();
            });
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        txtNombre           = (EditText) findViewById(R.id.txtNombre);
        txtApPaterno        = (EditText) findViewById(R.id.txtApPaterno);
        txtApMaterno        = (EditText) findViewById(R.id.txtApMaterno);
        txtUsuario          = (EditText) findViewById(R.id.txtUsuario);
        txtContrasena       = (EditText) findViewById(R.id.txtContrasena);
        txtConfirmacion     = (EditText) findViewById(R.id.txtConfirmacion);
        lblCreate           = (TextView) findViewById(R.id.lblCreate);
        chkTermino          = (CheckBox) findViewById(R.id.chkTermino);

        if (lblCreate != null) {
            lblCreate.setOnClickListener(view -> {

                if (!chkTermino.isChecked()) {
                    notificarUsuario("Aviso de privacidad", "Los terminos y condiciones deben ser aceptados para el uso de la aplicaci\u00f3n");
                    return;
                }

                String nombre           = txtNombre.getText().toString();
                String apPaterno        = txtApPaterno.getText().toString();
                String apMaterno        = txtApMaterno.getText().toString();
                String usuario          = txtUsuario.getText().toString();
                String contrasena       = txtContrasena.getText().toString();
                String confirmacion     = txtConfirmacion.getText().toString();

                boolean completo = true;

                if (nombre.trim().length() == 0) {
                    completo = false;
                    txtNombre.setError("Requerido");
                }
                else {
                    txtNombre.setError(null);
                }

                if (usuario.trim().length() == 0) {
                    completo = false;
                    txtUsuario.setError("Requerido");
                }
                else {
                    txtUsuario.setError(null);
                }

                if (contrasena.trim().length() == 0) {
                    completo = false;
                    txtContrasena.setError("Requerido");
                }
                else {
                    txtContrasena.setError(null);
                }

                if (confirmacion.trim().length() == 0) {
                    completo = false;
                    txtConfirmacion.setError("Requerido");
                }
                else {
                    txtConfirmacion.setError(null);
                }

                if (!contrasena.equals(confirmacion)) {
                    completo = false;
                    txtConfirmacion.setError("No coinciden");
                }
                else {
                    txtConfirmacion.setError(null);
                }

                if (completo) {
                    getController().registrarCuenta(nombre, apPaterno, apMaterno, usuario, contrasena, confirmacion);
                }
            });
        }
        onCreateActivity(CUsuario.class);
    }

    @Override
    public void onCreateActivity(Class<? extends BaseController> controller) {
        super.onCreateActivity(controller);
    }
}
