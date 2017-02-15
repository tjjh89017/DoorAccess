package date.kojuro.dooraccess;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by date on 2017/2/15.
 */

public class LocationService extends Service {

    private final static String TAG = "LocationService";
    private LocationManager mLocationManager = null;
    /*  Set interval to 1 min */
    //private final static int LOCATION_INTERVAL = 60 * 1000;
    private final static int LOCATION_INTERVAL = 100;
    /* Set distance to 3 m*/
    //private final static float LOCATION_DISTANCE = 3f;
    private final static float LOCATION_DISTANCE = 0f;

    private final LocationBinder locationBinder = new LocationBinder();
    private LocationCallback locationCallback = null;

    public class LocationBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }
    }

    public interface LocationCallback {
        public void updateLocation(Location location);
    }

    public void setLocationCallback(LocationCallback callback) {
        locationCallback = callback;
    }


    private class iLocationListener implements LocationListener {

        private Location mLocation;

        public iLocationListener(String provider) {

            mLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            mLocation.set(location);
            if(locationCallback != null) {
                locationCallback.updateLocation(mLocation);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled");
        }
    }

    private iLocationListener[] mLocationListeners = new iLocationListener[]{
            new iLocationListener(LocationManager.NETWORK_PROVIDER),
            new iLocationListener(LocationManager.GPS_PROVIDER)
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        initLocationManager();

        /*
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (SecurityException ex) {
            Log.i(TAG, "NETWORK Fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "NETWORK provider does not exist, ", ex);
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[1]
            );
        } catch (SecurityException ex) {
            Log.i(TAG, "GPS Fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "GPS provider does not exist, ", ex);
        }
        */

        /* just find out Location when screen on*/
        /* register screen on event */
        IntentFilter screenOn = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "screen on");
                requestLocation();
            }
        }, screenOn);
    }

    private void initLocationManager() {
        if(mLocationManager == null) {
            mLocationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void requestLocation() {

        initLocationManager();
        try {
            mLocationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER,
                    mLocationListeners[0],
                    null
            );

        } catch (SecurityException ex) {
            Log.i(TAG, "NETWORK Fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "NETWORK provider does not exist, ", ex);
        }

        try {
            mLocationManager.requestSingleUpdate(
                    LocationManager.GPS_PROVIDER,
                    mLocationListeners[1],
                    null
            );
        } catch (SecurityException ex) {
            Log.i(TAG, "GPS Fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "GPS provider does not exist, ", ex);
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.i(TAG, "onBind");
        return locationBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
