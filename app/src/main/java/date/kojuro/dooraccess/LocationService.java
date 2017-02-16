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

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

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
    private LocationCallback globalLocationCallback = null;

    /* DB service */
    private DBService mDBService;
    private UIDLocationRelationDao mULRDao;
    private List<UIDLocationRelation> mULRList;

    private ReaderLocationDao mRLDao;
    private List<ReaderLocation> mLList;

    private TagDao mTagDao;

    private DaemonConfiguration mDaemon;

    public class LocationBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }
    }

    public interface LocationCallback {
        public void updateLocation(Location location);
    }

    public void setGlobalLocationCallback(LocationCallback callback) {
        globalLocationCallback = callback;
    }


    private class iLocationListener implements LocationListener {

        private Location mLocation;
        private LocationCallback localLocationCallback = null;

        public iLocationListener(String provider) {

            mLocation = new Location(provider);
        }

        public iLocationListener(String provider, LocationCallback callback) {

            mLocation = new Location(provider);
            localLocationCallback = callback;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            mLocation.set(location);
            if(globalLocationCallback != null) {
                globalLocationCallback.updateLocation(mLocation);
            }
            if(localLocationCallback != null) {
                localLocationCallback.updateLocation(mLocation);
            }

            /* TODO when disable auto update uid */
            /* TODO if `auto` is disable, dont execute to below */

            /* find out the nearest record, and need in URL record */
            QueryBuilder<ReaderLocation> queryLocation = mRLDao.queryBuilder();
            queryLocation
                    .join(UIDLocationRelation.class, UIDLocationRelationDao.Properties.ReaderLocationId)
                    .where(UIDLocationRelationDao.Properties.TagId.isNotNull());
            mLList = queryLocation.list();

            Log.i(TAG, "updateLocation queryLocation: " + mLList.toString());
            /* no location record */
            if(mLList == null || mLList.isEmpty()) {
                return;
            }

            ReaderLocation nearestLocation = mLList.get(0);
            float minDistance = location.distanceTo(mLList.get(0).getLocation());
            for(ReaderLocation rLocation : mLList) {
                float distance = location.distanceTo(rLocation.getLocation());
                if(distance < minDistance) {
                    minDistance = distance;
                    nearestLocation = rLocation;
                }
            }

            Log.i(TAG, "updateLocation nearestLocation: " + nearestLocation.getDescription());

            /* join 3 tables */
            QueryBuilder<Tag> queryBuilder = mTagDao.queryBuilder();
            queryBuilder
                    .join(UIDLocationRelation.class, UIDLocationRelationDao.Properties.TagId)
                    .where(UIDLocationRelationDao.Properties.ReaderLocationId.eq(nearestLocation.getId()));

            List<Tag> result = queryBuilder.list();
            Log.i(TAG, "tag result: " + result.toString());

            /* TODO need check the result ? */
            if(result != null && !result.isEmpty()) {
                enableTag(result.get(0));
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

        /*  DB service before listen the event */
        mDBService = DBService.getInstance(getApplicationContext());
        mULRDao = mDBService.getUIDLocationRelationDao();
        mTagDao = mDBService.getTagDao();
        mRLDao = mDBService.getReaderLocationDao();

        DaemonConfiguration.Init(getApplicationContext());
        mDaemon = DaemonConfiguration.getInstance();

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

    public void requestLocation(LocationCallback networkCallback, LocationCallback gpsCallback) {

        initLocationManager();
        try {
            mLocationManager.requestSingleUpdate(
                    LocationManager.NETWORK_PROVIDER,
                    new iLocationListener(LocationManager.NETWORK_PROVIDER, networkCallback),
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
                    new iLocationListener(LocationManager.GPS_PROVIDER, gpsCallback),
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

    public void enableTag(Tag tag) {

        /* It will execute by service be for MainActivity onCreate */
        if(mDaemon == null) {
            mDaemon = DaemonConfiguration.getInstance();
        }

        mDaemon.disablePatch();
        mDaemon.uploadConfiguration(
                SetUIDFragment.ATQA,
                SetUIDFragment.SAK,
                SetUIDFragment.HIST,
                SetUIDFragment.HexToBytes(tag.getUID())
        );
        mDaemon.enablePatch();
    }
}
