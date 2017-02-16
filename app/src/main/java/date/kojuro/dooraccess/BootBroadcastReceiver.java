package date.kojuro.dooraccess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by date on 2017/2/17.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent locationServiceIntent = new Intent(context, LocationService.class);
        context.startService(locationServiceIntent);
    }
}
