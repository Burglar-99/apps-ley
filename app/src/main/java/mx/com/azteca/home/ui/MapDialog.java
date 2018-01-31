package mx.com.azteca.home.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mx.com.azteca.home.R;


public class MapDialog extends Dialog {

    private GoogleMap gMap;
    private Marker marker;
    private LatLng latLng;
    private Point[] points;
    private LatLng centerPosition;
    private float zoom;

    public MapDialog(Context context) {
        this(context, null);
    }

    public MapDialog(Context context, String title) {
        super(context);

        setContentView(R.layout.widget_map_dialog);
        setTitle(title);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        }
        findViewById(R.id.btnAceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapDialog.this.dismiss();
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                try {
                    Fragment fragment = getAppCompactActivity().getSupportFragmentManager().findFragmentById(R.id.gMap);
                    getAppCompactActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    getAppCompactActivity().getSupportFragmentManager().executePendingTransactions();
                }
                catch (Exception ex) {
                    Log.e(getClass().getName(), ex.toString());
                }
            }
        });

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                try {
                    if (gMap == null) {
                        ((SupportMapFragment) getAppCompactActivity().getSupportFragmentManager().findFragmentById(R.id.gMap)).getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                gMap = googleMap;
                                gMap.setMyLocationEnabled(true);
                                gMap.getUiSettings().setZoomControlsEnabled(true);
                            }
                        });

                    }
                    if (MapDialog.this.latLng != null) {
                        if (MapDialog.this.marker == null) {
                            MapDialog.this.marker = gMap.addMarker(new MarkerOptions().position(latLng));
                        }
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                    }
                    else if (MapDialog.this.points != null) {
                        gMap.clear();
                        for (Point point : points) {
                            Marker marker = gMap.addMarker(new MarkerOptions().position(point.getLocation()).title(point.getTitle()));
                            marker.setIcon(BitmapDescriptorFactory.fromResource(point.getClasificacion()));
                        }

                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapDialog.this.centerPosition, zoom));
                    }
                }
                catch (Exception ex) {
                    Log.e(getClass().getName(), ex.toString());
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
    }

    public void setLocation(String title, double latitude, double longitude) {
        if (this.latLng == null) {
            this.latLng = new LatLng(latitude, longitude);
        }
        if (this.marker == null && gMap != null) {
            this.marker = gMap.addMarker(new MarkerOptions().position(latLng).title(title));
        }
        else if (marker != null) {
            marker.setPosition(latLng);
        }
        if (gMap != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    public void setLocation(String title, Point ... points) {
        this.points = points;
        setTitle(title);
        if (gMap != null) {
            gMap.clear();
            for (Point point : points) {
                Marker marker = gMap.addMarker(new MarkerOptions().position(point.getLocation()).title(point.getTitle()));
                marker.setIcon(BitmapDescriptorFactory.fromResource(point.getClasificacion()));
            }
        }
    }

    public void setCenter(LatLng location, double zoom) {
        if (gMap != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, Float.parseFloat(String.valueOf(zoom))));
        }
    }

    public void setCenter(LatLng latLng) {
        this.centerPosition = latLng;
        if (gMap != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    public AppCompatActivity getAppCompactActivity() throws Exception {
        throw new Exception("Unimplemented method");
    }


    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public class Point {

        private String title;
        private String descripcion;
        private int clasificacion;
        private LatLng location;
        private double latitude;
        private double longitude;


        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public void setClasificacion(int clasificacion) {
            this.clasificacion = clasificacion;
        }

        public void setLocation(LatLng location) {
            this.location = location;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getTitle() {
            return title;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public int getClasificacion() {
            return clasificacion;
        }

        public LatLng getLocation() {
            if (this.location == null) {
                this.location = new LatLng(latitude, longitude);
            }
            return location;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

}
