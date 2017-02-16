package date.kojuro.dooraccess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by date on 2017/2/16.
 */

public class ModifyLocationFragment extends DialogFragment {

    private View dialogView;
    private EditText vEditDesc;

    private int position;
    private ReaderLocation rLocation;

    public ModifyLocationFragment(ReaderLocation location, int pos) {

        rLocation = location;
        position = pos;
    }

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
                        rLocation.setDescription(vEditDesc.getText().toString());
                        postInput();
                    }
                })
                .setNegativeButton("Cancel", null);


        return builder.create();
    }

    public void preInput(EditText vEditDesc) {
        vEditDesc.setText(rLocation.getDescription());
    }

    public void postInput() {

        LocationFragment locationFragment = (LocationFragment) getTargetFragment();
        locationFragment.ModifyLocation(rLocation, position);
    }
}
