package date.kojuro.dooraccess;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FragmentManager mFragmentManager;

    private DBService mDBService;
    private ReaderLocationDao mRLocationDao;
    private List<ReaderLocation> mRLocaitonList;
    private ArrayAdapter<ReaderLocation> mRLocationAdapter;

    private ListView vRLocationList;
    private FloatingActionButton vCreate;

    private final static int LOC_CONTEXT_MENU_MODIFY = 3;
    private final static int LOC_CONTEXT_MENU_TAG = 4;
    private final static int LOC_CONTEXT_MENU_DELETE = 5;

    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.location_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(Menu.NONE, LOC_CONTEXT_MENU_MODIFY, Menu.NONE, "Modify");
        menu.add(Menu.NONE, LOC_CONTEXT_MENU_TAG, Menu.NONE, "Tag");
        menu.add(Menu.NONE, LOC_CONTEXT_MENU_DELETE, Menu.NONE, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {

        final AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo = (AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo();

        /**
         * menuInfo.position for List index
         * menuItem.getItemId for operation index
         */
        ReaderLocation rLocation = mRLocaitonList.get(menuInfo.position);
        switch(menuItem.getItemId()) {
            case LOC_CONTEXT_MENU_MODIFY:
                Toast.makeText(getContext(), "LOC_CONTEXT_MENU_MODIFY", Toast.LENGTH_SHORT);
                ModifyLocationFragment modifyLocationFragment = new ModifyLocationFragment(rLocation, menuInfo.position);
                modifyLocationFragment.setTargetFragment(this, 0);
                modifyLocationFragment.show(mFragmentManager, "ModifyLocation");
                break;
            case LOC_CONTEXT_MENU_TAG:
                Toast.makeText(getContext(), "LOC_CONTEXT_MENU_TAG", Toast.LENGTH_SHORT);
                AssociateTagFragment associateTagFragment = new AssociateTagFragment(rLocation, menuInfo.position);
                associateTagFragment.setTargetFragment(this, 0);
                associateTagFragment.show(mFragmentManager, "AssociateTag");
                break;
            case LOC_CONTEXT_MENU_DELETE:
                Toast.makeText(getContext(), "LOC_CONTEXT_MENU_DELETE", Toast.LENGTH_SHORT);
                DeleteLocation(rLocation);
                break;
        }

        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location, container, false);

        mDBService = DBService.getInstance(getActivity().getApplicationContext());
        mRLocationDao = mDBService.getReaderLocationDao();
        mRLocaitonList = mRLocationDao.loadAll();

        vRLocationList = (ListView)v.findViewById(R.id.LocationList);
        vCreate = (FloatingActionButton)v.findViewById(R.id.Create);

        mRLocationAdapter = new ArrayAdapter<ReaderLocation>(getActivity(), 0 /* useless */, mRLocaitonList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ReaderLocation rLocation = (ReaderLocation)getItem(position);
                if(convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
                }

                final TextView desc = (TextView)convertView.findViewById(android.R.id.text1);
                final TextView loc = (TextView)convertView.findViewById(android.R.id.text2);

                desc.setText(rLocation.getDescription());
                loc.setText(rLocation.getLocationString());

                return convertView;
            }
        };

        vRLocationList.setAdapter(mRLocationAdapter);
        registerForContextMenu(vRLocationList);

        vCreate.setOnClickListener(this);

        return v;
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

        CreateLocaitonFragment createLocaitonFragment = new CreateLocaitonFragment();
        createLocaitonFragment.setTargetFragment(this, 0);
        createLocaitonFragment.show(mFragmentManager, "CreateLocationFragment");
    }

    public void CreateNewLocation(ReaderLocation rLocation) {

        mRLocaitonList.add(rLocation);
        mRLocationDao.insert(rLocation);
        mRLocationAdapter.notifyDataSetChanged();
    }

    public void ModifyLocation(ReaderLocation rLocaiton, int pos) {

        mRLocaitonList.set(pos, rLocaiton);
        mRLocationDao.update(rLocaiton);
        mRLocationAdapter.notifyDataSetChanged();
    }

    public void DeleteLocation(ReaderLocation rLocation) {

        mRLocationAdapter.remove(rLocation);
        mRLocationDao.delete(rLocation);
        mRLocationAdapter.notifyDataSetChanged();
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
}
