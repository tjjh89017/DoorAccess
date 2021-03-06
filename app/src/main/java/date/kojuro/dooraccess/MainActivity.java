package date.kojuro.dooraccess;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationService.LocationCallback {

    private final static String TAG = "MainActivity";

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Intent locationIntent;
    private ServiceConnection mServiceConnection;
    private boolean serviceAttached = false;
    private Location mLocation;
    private LocationService mLocationService;

    private SwitchCompat vAutoSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new SetUIDFragment();
        fragmentManager.beginTransaction().replace(R.id.content_main_activity, fragment).commit();

        /* Start Location Service */
        locationIntent = new Intent(this, LocationService.class);
        startService(locationIntent);

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                LocationService.LocationBinder binder = (LocationService.LocationBinder)service;
                mLocationService = binder.getService();
                mLocationService.setGlobalLocationCallback(MainActivity.this);
                serviceAttached = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceAttached = false;
            }
        };

        bindService(locationIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
        vAutoSwitch = (SwitchCompat) view.getMenu().findItem(R.id.nav_auto).getActionView().findViewById(R.id.auto_switch);
        vAutoSwitch.setChecked(true);
        vAutoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "auto switch to " + isChecked);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        switch(id) {
            case R.id.nav_setuid:
                fragment = new SetUIDFragment();
                break;
            case R.id.nav_location:
                fragment = new LocationFragment();
                break;
            default:
                return true;
        }

        fragmentManager.beginTransaction().replace(R.id.content_main_activity, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {

        if(serviceAttached) {
            serviceAttached = false;
            unbindService(mServiceConnection);
        }

        super.onDestroy();
    }

    @Override
    public void updateLocation(Location location) {
        Log.i(TAG, "updateLocation");

        /* maybe save the location for create location-tag relationship */
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }

    public LocationService getLocationService() {

        return mLocationService;
    }
}
