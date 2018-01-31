package mx.com.azteca.home.view;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.Toolbar;

import mx.com.azteca.home.R;

public class TerminosActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener( v -> {
                TerminosActivity.this.onBackPressed();
            });
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }



}
