package date.kojuro.dooraccess;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetUIDFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetUIDFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetUIDFragment extends Fragment implements FloatingActionButton.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /* MainFragment */

    private final static String TAG = "DoorAccess";
    public final static byte ATQA = 0x00;
    public final static byte SAK = 0x20;
    public final static byte[] HIST = new byte[]{};

    private final static int UID_CONTEXT_MENU_MODIFY = Menu.FIRST;
    private final static int UID_CONTEXT_MENU_DELETE = Menu.FIRST + 1;

    private DBService mDBService;
    private TagDao mTagDao;
    private List<Tag> mTagList = new ArrayList<>();
    private ArrayAdapter<Tag> mTagAdapter;
    private Tag mCurrentTag;

    private DaemonConfiguration mDaemon;

    private ListView vTagList;

    private FloatingActionButton vCreate;
    private FragmentManager mFragmentManager;

    public SetUIDFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetUIDFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetUIDFragment newInstance(String param1, String param2) {
        SetUIDFragment fragment = new SetUIDFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mFragmentManager = getFragmentManager();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setuid, container, false);

        mDBService = DBService.getInstance(getActivity().getApplicationContext());
        mTagDao = mDBService.getTagDao();

        mTagList = mTagDao.loadAll();

        DaemonConfiguration.Init(getActivity());
        mDaemon = DaemonConfiguration.getInstance();

        vTagList = (ListView)v.findViewById(R.id.UIDList);
        vCreate = (FloatingActionButton)v.findViewById(R.id.Create);

        /* Set action */

        mTagAdapter = new ArrayAdapter<Tag>(getActivity(), 0 /* useless */, mTagList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Tag tag = (Tag) getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
                }

                final TextView desc = (TextView) convertView.findViewById(android.R.id.text1);
                final TextView uid = (TextView) convertView.findViewById(android.R.id.text2);

                uid.setText(tag.getUID());
                desc.setText(tag.getDescription());

                return convertView;
            }

        };
        vTagList.setAdapter(mTagAdapter);

        vTagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentTag = mTagList.get(position);
                Toast.makeText(getActivity().getApplicationContext(), mCurrentTag.getDescription() + "\nUID: " + mCurrentTag.getUID(), Toast.LENGTH_SHORT).show();

                mDaemon.disablePatch();
                mDaemon.uploadConfiguration(ATQA, SAK, HIST, HexToBytes(mCurrentTag.getUID()));
                mDaemon.enablePatch();
            }
        });


        registerForContextMenu(vTagList);

        vCreate.setOnClickListener(this);

        return v;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.setuid_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.Disable:
                mDaemon.disablePatch();
                Toast.makeText(getContext(), "Static UID Disable", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(Menu.NONE, UID_CONTEXT_MENU_MODIFY, Menu.NONE, "Modify");
        menu.add(Menu.NONE, UID_CONTEXT_MENU_DELETE, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {

        final AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo();

        /**
         * menuInfo.position for List index
         * menuItem.getItemId for operation index
         */
        final Tag tag = mTagList.get(menuInfo.position);
        switch(menuItem.getItemId()) {
            case UID_CONTEXT_MENU_MODIFY:
                // TODO
                Toast.makeText(getContext(), "Modify Tag", Toast.LENGTH_SHORT);
                ModifyTagFragment modifyTag = new ModifyTagFragment(tag, menuInfo.position);
                modifyTag.setTargetFragment(this, 0);
                modifyTag.show(mFragmentManager, "ModifyTag");
                break;

            case UID_CONTEXT_MENU_DELETE:
                /* TODO Need verify operation */
                DeleteTag(tag);
                break;
        }

        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        TagFragment createTag = new TagFragment();
        createTag.setTargetFragment(this, 0);
        createTag.show(mFragmentManager, "CreateTag");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void CreateNewTag(Tag tag) {

        //mTagAdapter.add(tag);
        mTagList.add(tag);
        mTagDao.insert(tag);
    }

    public void ModifyTag(Tag tag, int pos) {

        mTagList.set(pos, tag);
        mTagDao.update(tag);
    }

    public void DeleteTag(Tag tag) {

        mTagAdapter.remove(tag);
        mTagDao.delete(tag);
    }


    public static byte[] HexToBytes(String hex) {

        if (hex.length() < 8)
            return null;
        int len = hex.length();

        byte[] result = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
        }

        return result;
    }
}
