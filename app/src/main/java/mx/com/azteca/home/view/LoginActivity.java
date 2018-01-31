package mx.com.azteca.home.view;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.BaseController;
import mx.com.azteca.home.controller.CUsuario;

public class LoginActivity extends BaseActivity<CUsuario> {

    EditText txtEmail;
    EditText txtPasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.txtEmail = (EditText) findViewById(R.id.txtEmail);
        this.txtPasswd = (EditText) findViewById(R.id.txtPasswd);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        TextView lblOlvido = (TextView) findViewById(R.id.lblOlvido);
        TextView lblCreate = (TextView) findViewById(R.id.lblCreate);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(view -> {
                String email = txtEmail.getText().toString();
                String passwd = txtPasswd.getText().toString();
                if (email.trim().length() == 0) {
                    notificarUsuario("Error ", "Ingrese el correo electr\u00f3nico");
                }
                else if (passwd.trim().length() == 0) {
                    notificarUsuario("Error ", "Ingrese la contrase\u00f1a");
                }
                else {
                    getController().login(email, passwd);
                }
            });
        }

        if (lblOlvido != null) {
            lblOlvido.setOnClickListener(view -> {
                RecuperarDialog recuperarDialog = new RecuperarDialog(LoginActivity.this);
                recuperarDialog.show(usuario -> getController().recuperarCuenta(usuario));
            });
        }

        if (lblCreate != null) {
            lblCreate.setOnClickListener(view -> {
                Intent intCrearCuenta = new Intent(LoginActivity.this, CrearCuentaActivity.class);
                startActivity(intCrearCuenta);
            });
        }

        onCreateActivity(CUsuario.class);
    }

    @Override
    public void onCreateActivity(Class<? extends BaseController> controller) {
        super.onCreateActivity(controller);
        getController().subscribe(getController().new ListenerUsuario() {
            @Override
            public void onRecuperarCuenta() {
                notificarUsuario("Cuenta", "Se ha enviado un correo con las instruciones para recuperar la cuenta");
            }
        });
    }


}
