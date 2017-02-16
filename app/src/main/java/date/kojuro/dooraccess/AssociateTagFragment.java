package date.kojuro.dooraccess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by date on 2017/2/16.
 */

public class AssociateTagFragment extends DialogFragment {

    private final static String TAG = "AssociateTagFragment";

    private View dialogView;

    private TagDao mTagDao;
    private ArrayAdapter<Tag> mTagAdapter;
    private List<Tag> mTagList;

    private UIDLocationRelationDao mUIDLocationRelationDao;
    private List<UIDLocationRelation> mULRList;
    private UIDLocationRelation mULR = null;

    private ReaderLocation rLocation;
    private int position;

    public AssociateTagFragment(ReaderLocation location, int pos) {

        rLocation = location;
        position = pos;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.tag_location_relation, null);
        mTagDao = DBService.getInstance(getContext()).getTagDao();
        mTagList = mTagDao.loadAll();

        mUIDLocationRelationDao = DBService.getInstance(getContext()).getUIDLocationRelationDao();
        Query<UIDLocationRelation> query = mUIDLocationRelationDao.queryBuilder()
                .where(
                        UIDLocationRelationDao.Properties.ReaderLocationId.eq(rLocation.getId())
                ).build();
        mULRList = query.list();

        Log.i(TAG, mULRList.toString());

        /* if record exist, update it rather than create a new one */
        if(mULRList != null && !mULRList.isEmpty()) {
            mULR = mULRList.get(0);
            Log.i(TAG, mULR.getTag().toString());
        }


        /* Adapter */
        mTagAdapter = new ArrayAdapter<Tag>(getContext(), android.R.layout.simple_spinner_dropdown_item, mTagList);
        mTagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.tag_spinner);
        spinner.setAdapter(mTagAdapter);

        /* TODO if no record, let spinner select NULL or Empty */
        if(mULR != null) {
            int spinnerPos = mTagAdapter.getPosition(mULR.getTag());
            spinner.setSelection(spinnerPos);
        }

        builder.setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(mULR == null) {
                            mULR = new UIDLocationRelation();
                            mULR.setReaderLocation(rLocation);
                            mUIDLocationRelationDao.insert(mULR);
                        }

                        mULR.setTag((Tag) spinner.getSelectedItem());
                        mUIDLocationRelationDao.update(mULR);
                    }
                })
                .setNegativeButton("Cancel", null);


        return builder.create();
    }
}
