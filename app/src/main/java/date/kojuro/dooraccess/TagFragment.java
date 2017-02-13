package date.kojuro.dooraccess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by date on 2017/2/13.
 */

public class TagFragment extends DialogFragment {

    private View dialogView;
    private EditText vEditUID;
    private EditText vEditDesc;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.tag_fragment, null);
        vEditUID = (EditText)dialogView.findViewById(R.id.EditUID);
        vEditDesc = (EditText)dialogView.findViewById(R.id.EditDesc);
        preInput(vEditUID, vEditDesc);

        builder.setView(dialogView)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Tag tag = new Tag();
                        tag.setUID(vEditUID.getText().toString());
                        tag.setDescription(vEditDesc.getText().toString());

                        postInput(tag);
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    public void preInput(EditText vEditUID, EditText vEditDesc) {

    }

    public void postInput(Tag tag) {

        MainActivity activity = (MainActivity)getActivity();
        activity.CreateNewTag(tag);
    }
}
