package date.kojuro.dooraccess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by date on 2017/2/16.
 */

public class CreateLocaitonFragment extends DialogFragment {

    private View dialogView;
    private EditText vEditDesc;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.create_location_fragment, null);
        vEditDesc = (EditText)dialogView.findViewById(R.id.EditDesc);
        preInput(vEditDesc);

        builder.setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        /* TODO find a better way to find out current location */
                        MainActivity mainActivity = (MainActivity) getActivity();
                        LocationService locationService = mainActivity.getLocationService();

                        final String desc = vEditDesc.getText().toString();

                        /* TODO only need one record */
                        locationService.requestLocation(
                                new LocationService.LocationCallback() {
                                    @Override
                                    public void updateLocation(Location location) {
                                        ReaderLocation rLocation = new ReaderLocation();
                                        rLocation.setDescription(desc);
                                        rLocation.setLatitude(location.getLatitude());
                                        rLocation.setLongitude(location.getLongitude());

                                        postInput(rLocation);
                                    }
                                },
                                null
                        );

                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    public void preInput(EditText vEditDesc) {

    }

    public void postInput(ReaderLocation rLocation) {

        LocationFragment locationFragment = (LocationFragment) getTargetFragment();
        locationFragment.CreateNewLocation(rLocation);
    }
}
