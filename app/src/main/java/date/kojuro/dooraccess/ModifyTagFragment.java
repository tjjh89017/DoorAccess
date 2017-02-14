package date.kojuro.dooraccess;

import android.widget.EditText;

/**
 * Created by date on 2017/2/14.
 */

public class ModifyTagFragment extends TagFragment {

    private Tag mTag;
    private int position;

    public ModifyTagFragment(Tag tag, int pos) {

        mTag = tag;
        position = pos;
    }

    @Override
    public void preInput(EditText vEditUID, EditText vEditDesc) {

        vEditUID.setText(mTag.getUID());
        vEditDesc.setText(mTag.getDescription());
    }

    @Override
    public void postInput(Tag tag) {

        tag.setId(mTag.getId());

        SetUIDFragment setUIDFragment = (SetUIDFragment) getTargetFragment();
        setUIDFragment.ModifyTag(tag, position);
    }
}
