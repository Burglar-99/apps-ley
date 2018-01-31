package mx.com.azteca.home.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.BaseController;
import mx.com.azteca.home.controller.CPago;
import mx.com.azteca.home.model.provider.pojo.Pago;
import mx.com.azteca.home.view.ipati.itemanimator.CustomItemAnimator;


public class PagoActivity extends BaseActivity<CPago> implements TarjetaDialog.Listener {

    private RecyclerView recyclerView;
    private TarjetaDialog tarjetaDialog;
    final int SCAN_REQUEST_CODE = 1001;

    String operacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener( v -> {
                PagoActivity.this.onBackPressed();
            });
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView lblAgregar = (TextView) findViewById(R.id.lblAgregar);
        if (lblAgregar != null) {
            lblAgregar.setOnClickListener(view -> {
                PagoActivity.this.tarjetaDialog = new TarjetaDialog(PagoActivity.this);
                PagoActivity.this.tarjetaDialog.show(PagoActivity.this, TarjetaDialog.Actividad.REGISTRAR);
            });
        }

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new CustomItemAnimator());
        onCreateActivity(CPago.class);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("OPERACION") ) {
            this.operacion =  getIntent().getExtras().getString("OPERACION");
        }
    }

    @Override
    public void onCreateActivity(Class<? extends BaseController> controller) {
        super.onCreateActivity(controller);
        suscribe(getController().new ListenerPago(){

            @Override
            public void cargarMetodosPago(List<Pago> pagos) {
                if (pagos != null) {
                    SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
                    sectionAdapter.addSection(new PagoActivity.PagoSection("M\u00e9todos de pago", pagos) {
                        @Override
                        public void onItemSelected(Pago pago) {
                            if (operacion == null) {
                                PagoActivity.this.tarjetaDialog = new TarjetaDialog(PagoActivity.this);
                                PagoActivity.this.tarjetaDialog.setDatos(pago.numTarjeta, pago.mesExpira, pago.anioExpira, "");
                                PagoActivity.this.tarjetaDialog.show(PagoActivity.this, TarjetaDialog.Actividad.MODIFICAR);
                            }
                            else if (operacion.equals("SELECTOR")) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("METODO_PAGO", pago.numTarjeta);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        }
                    });
                    recyclerView.setAdapter(sectionAdapter);
                }

            }

            @Override
            public void onTarjetaRegistrada() {
                PagoActivity.this.tarjetaDialog.dismiss();
                getController().loadMetodosPago();
            }
        } );
        getController().loadMetodosPago();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                String cardNumber   = scanResult.cardNumber;
                int month           = 0;
                int year            = 0;
                String cvv          = "";

                Log.e(getClass().getSimpleName(), scanResult.cardNumber);

                if (scanResult.isExpiryValid()) {
                    month = scanResult.expiryMonth;
                    year = scanResult.expiryYear;
                }

                if (scanResult.cvv != null) {
                    cvv = scanResult.cvv;
                }

                if (PagoActivity.this.tarjetaDialog != null) {
                    PagoActivity.this.tarjetaDialog.setDatos(cardNumber, month, year, cvv);
                }
            }
        }
    }

    @Override
    public void leerTarjeta() {
        Intent scanIntent = new Intent(PagoActivity.this, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
        startActivityForResult(scanIntent, SCAN_REQUEST_CODE);
    }

    @Override
    public void guardarTarjeta(String tarjeta, int month, int year, String cvv) {
        getController().update(tarjeta, month, year, cvv);
    }

    @Override
    public void actualizarTarjeta(String tarjeta, int month, int year, String cvv) {
        getController().update(tarjeta, month, year, cvv);
    }

    @Override
    public void eliminar(String tarjeta) {
        getController().delete(tarjeta);
    }

    private class PagoSection extends StatelessSection {

        private String title;
        private List<Pago> list;

        private PagoSection(String title, List<Pago> list) {
            super(R.layout.row_pago, R.layout.row_pago_detail);
            this.title = title;
            this.list = list;
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new PagoActivity.ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            Pago pago = this.list.get(position);
            final PagoActivity.ItemViewHolder viewHolder = (PagoActivity.ItemViewHolder) holder;
            viewHolder.name.setText(pago.getNumTarjeta());
            viewHolder.imgCard.setBackground(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_credit_card).color(Color.parseColor("#03abf6")));
            viewHolder.itemView.setOnClickListener(v -> onItemSelected(list.get(position)));
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new PagoActivity.HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            PagoActivity.HeaderViewHolder headerHolder = (PagoActivity.HeaderViewHolder) holder;
            headerHolder.tvTitle.setText(title);
        }

        public void onItemSelected(Pago pago) {

        }

    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;

        private HeaderViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.lblNombre);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        private ImageView imgCard;

        private ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.lblDocumento);
            imgCard = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }


}
