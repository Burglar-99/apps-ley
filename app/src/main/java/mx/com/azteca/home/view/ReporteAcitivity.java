package mx.com.azteca.home.view;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;

import mx.com.azteca.home.R;

public class ReporteAcitivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(view -> {
                ReporteAcitivity.this.onBackPressed();
            });
        }

        if (getIntent().getExtras() != null) {
            String document = getIntent().getExtras().getString("FILE_NAME");
            com.joanzapata.pdfview.PDFView pdfViewer = (com.joanzapata.pdfview.PDFView) findViewById(R.id.pdfview);
            if (pdfViewer != null && document != null) {
                File file = new File(document);
                if (file.exists()) {
                    pdfViewer.fromFile(file).load();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu_pdf_viewer, menu);
        menu.findItem(R.id.action_info).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_open_in_new).color(Color.WHITE).actionBar());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                if (getIntent().getExtras() != null) {
                    String document = getIntent().getExtras().getString("FILE_NAME");
                    if (document != null) {
                        try {
                            File file = new File(document);
                            if (file.exists()) {

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
                                startActivity(browserIntent);
                                /*
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(file), "pdf/*");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                */
                            }
                        }
                        catch (Exception ex) {
                            notificarUsuario("Informaci\u00f3n", "Para poder abrir el documento es necesario tener un administrador de documentos PDF");
                        }
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
