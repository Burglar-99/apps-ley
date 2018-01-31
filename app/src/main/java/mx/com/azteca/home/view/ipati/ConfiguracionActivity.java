package mx.com.azteca.home.view.ipati;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import mx.com.azteca.home.R;
import mx.com.azteca.home.model.ipati.pojo.BaseInfo;
import mx.com.azteca.home.util.ListAdapter;

public class ConfiguracionActivity extends BaseActivity {

    private Spinner cmbEmpresa;
    private Spinner cmbSucursal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.configuracion_titulo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfiguracionActivity.this.finish();
            }
        });

        this.cmbEmpresa = (Spinner) findViewById(R.id.cmbEmpresa);
        this.cmbSucursal = (Spinner) findViewById(R.id.cmbSucursal);

        this.cmbEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BaseInfo info = (BaseInfo) adapterView.getItemAtPosition(i);
                ListAdapter adapter = new ListAdapter(ConfiguracionActivity.this, R.layout.list_item_view, info.getChilds()) {
                    @Override
                    public void onEntry(Object entry, View view, int position) {
                        BaseInfo info = (BaseInfo) entry;
                        ((TextView)view.findViewById(R.id.lblNombre)).setText(info.toString());
                    }
                };
                cmbSucursal.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        FloatingActionButton mFabButton = (FloatingActionButton) findViewById(R.id.fab_normal);
        mFabButton.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_save).color(Color.WHITE).actionBar());
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfiguracionActivity.this.saveConfiguracion();
            }
        });

        super.onCreateActivity(ConfiguracionActivity.class);
    }

    @Override
    protected void loadEmpresas(List<BaseInfo> info) {
        ListAdapter adapter = new ListAdapter(ConfiguracionActivity.this, R.layout.list_item_view, info) {
            @Override
            public void onEntry(Object entry, View view, int position) {
                BaseInfo info = (BaseInfo) entry;
                ((TextView)view.findViewById(R.id.lblNombre)).setText(info.toString());
            }
        };
        cmbEmpresa.setAdapter(adapter);
    }

    @Override
    protected Object getData(String name) {
        if (name.equals("Empresa")) {
            return cmbEmpresa.getSelectedItem();
        }
        else if (name.equals("Sucursal")) {
            return cmbSucursal.getSelectedItem();
        }
        return null;
    }
}