package mx.com.azteca.home.view;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.CComparable;
import mx.com.azteca.home.util.Functions;

public class ComparableActivity extends BaseActivity<CComparable> {

    CarouselView carouselView;
    CComparable.ComparableInfo comparableInfo;
    CComparable controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparable);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                try {
                    Glide.with(getContext())
                            .load(comparableInfo.listImagen.get(position))
                            .into(imageView);
                }
                catch ( Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
            }
        });

        super.onCreateActivity(CComparable.class);
        this.controller = getController();
        controller.loadComparable();

        String descripcion = getData("descripcion").toString();
        TextView lblTitulo = (TextView) findViewById(R.id.lblTitulo);
        if (lblTitulo != null)
            lblTitulo.setText(descripcion);

    }

    @Override
    public void loadComparableInfo(CComparable.ComparableInfo comparableInfo) {
        this.comparableInfo = comparableInfo;
        carouselView.setPageCount(comparableInfo.listImagen.size());

        String precio = comparableInfo.precio;
        try {
            double valor = Double.parseDouble(precio);
            precio = "$ " + Functions.getInstance(Functions.Format.DECIMAL).applyNumber(valor);
        }
        catch (NumberFormatException ex){
            Log.e(getClass().getSimpleName(), ex.toString());
        }

        TextView lblPrecio      = (TextView) findViewById(R.id.lblPrecio);
        TextView lblInmueble    = (TextView) findViewById(R.id.lblInmueble);
        TextView lblTipo        = (TextView) findViewById(R.id.lblTipo);
        TextView lblRecamara    = (TextView) findViewById(R.id.lblRecamara);
        TextView lblBano        = (TextView) findViewById(R.id.lblBano);
        TextView lblCochera     = (TextView) findViewById(R.id.lblCochera);

        if (lblPrecio != null)
            lblPrecio.setText(precio);

        if (lblInmueble != null)
            lblInmueble.setText(comparableInfo.inmueble);

        if (lblTipo != null)
            lblTipo.setText(comparableInfo.tipo);

        if (lblRecamara != null)
            lblRecamara.setText(comparableInfo.recamaras);

        if (lblBano != null)
            lblBano.setText(comparableInfo.banos);

        if (lblCochera != null)
            lblCochera.setText(comparableInfo.cochera);
    }
}
