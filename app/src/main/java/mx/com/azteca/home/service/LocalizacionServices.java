package mx.com.azteca.home.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


public class LocalizacionServices extends Service implements LocationListener {

    public static Location LOCATION;

    private LocationManager locationManager;
    private Messenger messengerService;
    private Messenger messengerClient;

    public static final int CLIENT_REGISTER = 1;
    public static final int CLIENT_UNREGISTER = 2;
    public static final int SEND_LOCATION = 3;
    public static final int GPS_ENABLE = 4;
    public static final int GPS_DISABLED = 5;
    public static final int LOAD_LOCATION = 6;




    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        messengerService = new Messenger(IncomingMessageHandler.newInstance(new IncomingMessageHandler.Listener() {
            @Override
            public void sendLocationToCustomer() {
                LocalizacionServices.this.sendLocationToCustomer();
            }

            @Override
            public void setMessengerClient(Messenger messenger) {
                LocalizacionServices.this.messengerClient = messenger;
            }
        }));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 15, this);
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 15, this);
            sendLocationToCustomer();
        } catch (SecurityException ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return Service.START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.hasAccuracy()) {
            if (location.getAccuracy() < 50) {
                LocalizacionServices.LOCATION = location;
                sendLocationToCustomer();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            sendGpsEstado(LocalizacionServices.GPS_ENABLE);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            sendGpsEstado(LocalizacionServices.GPS_DISABLED);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messengerService.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        stopForeground(true);
    }

    private void sendLocationToCustomer() {
        if (messengerClient != null) {
            try {
                if (LocalizacionServices.LOCATION == null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        LocalizacionServices.LOCATION = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        return;
                    }
                }
                if (LocalizacionServices.LOCATION != null) {
                    if (LocalizacionServices.LOCATION.hasAccuracy() && LocalizacionServices.LOCATION.getAccuracy() < 25) {
                        Message msg = Message.obtain(null, LocalizacionServices.LOAD_LOCATION);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("latitude" , LocalizacionServices.LOCATION.getLatitude());
                        bundle.putDouble("longitude", LocalizacionServices.LOCATION.getLongitude());
                        bundle.putDouble("accuracy" , LocalizacionServices.LOCATION.getAccuracy());
                        bundle.putDouble("altitude" , LocalizacionServices.LOCATION.getAltitude());
                        msg.setData(bundle);
                        messengerClient.send(msg);
                    }
                }
                if (locationManager != null && locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                    sendGpsEstado(LocalizacionServices.GPS_ENABLE);
                }
                else {
                    sendGpsEstado(LocalizacionServices.GPS_DISABLED);
                }
            }
            catch (Exception e) {
                messengerClient = null;
                Log.e(getClass().getSimpleName(), e.toString());
            }
        }
    }

    public void sendGpsEstado(int gpsEstado) {
        if (messengerClient != null) {
            try {
                Message msg = Message.obtain(null, gpsEstado);
                messengerClient.send(msg);
            }
            catch (Exception e) {
                messengerClient = null;
                Log.e(getClass().getSimpleName(), e.toString());
            }
        }
    }

    private static class IncomingMessageHandler extends Handler {

        private interface Listener {
            void sendLocationToCustomer();
            void setMessengerClient(Messenger messenger);
        }

        private static IncomingMessageHandler newInstance(IncomingMessageHandler.Listener listener) {
            return new IncomingMessageHandler(listener);
        }

        private IncomingMessageHandler.Listener listener;

        private IncomingMessageHandler(IncomingMessageHandler.Listener listener) {
            this.listener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            if (listener == null)
                return;
            synchronized (this) {
                switch (msg.what) {
                    case SEND_LOCATION:
                        listener.sendLocationToCustomer();
                        break;
                    case CLIENT_REGISTER:
                        listener.setMessengerClient(msg.replyTo);
                        break;
                    case CLIENT_UNREGISTER:
                        listener.setMessengerClient(null);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }


}
