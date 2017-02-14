package date.kojuro.dooraccess;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "DoorAccess";
    private final static byte ATQA = 0x00;
    private final static byte SAK = 0x20;
    private final static byte[] HIST = new byte[]{};
    private final static int MENU_MODIFY = Menu.FIRST;
    private final static int MENU_DELETE = Menu.FIRST + 1;

    private DBService mDBService;
    private TagDao mTagDao;
    private List<Tag> mTagList = new ArrayList<>();
    private ArrayAdapter<Tag> mTagAdapter;
    private Tag mCurrentTag;

    private DaemonConfiguration mDaemon;

    private ListView vTagList;

    private FloatingActionButton vCreate;
    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDBService = DBService.getInstance(getApplicationContext());
        mTagDao = mDBService.getTagDao();

        mTagList = mTagDao.loadAll();

        DaemonConfiguration.Init(this);
        mDaemon = DaemonConfiguration.getInstance();

        vTagList = (ListView)findViewById(R.id.UIDList);
        vCreate = (FloatingActionButton)findViewById(R.id.Create);
        mFragmentManager = getFragmentManager();

        mTagAdapter = new ArrayAdapter<Tag>(this, 0 /* useless */, mTagList) {
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
                Toast.makeText(getApplicationContext(), mCurrentTag.getDescription() + "\nUID: " + mCurrentTag.getUID(), Toast.LENGTH_SHORT).show();

                mDaemon.disablePatch();
                mDaemon.uploadConfiguration(ATQA, SAK, HIST, HexToBytes(mCurrentTag.getUID()));
                mDaemon.enablePatch();
            }
        });

        vTagList.setOnCreateContextMenuListener(new ListView.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                menu.add(Menu.NONE, MENU_MODIFY, Menu.NONE, "Modify");
                menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
            }
        });

        vCreate.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                TagFragment createTag = new TagFragment();
                createTag.show(mFragmentManager, "CreateTag");
            }
        });
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
            case MENU_MODIFY:
                TagFragment createTag = new TagFragment(){
                    @Override
                    public void preInput(EditText vEditUID, EditText vEditDesc) {
                        vEditUID.setText(tag.getUID());
                        vEditDesc.setText(tag.getDescription());
                    }
                    @Override
                    public void postInput(Tag tag) {
                        ModifyTag(tag, menuInfo.position);
                    }
                };
                createTag.show(mFragmentManager, "ModifyTag");
                break;

            case MENU_DELETE:
                /* TODO Need verify operation */
                DeleteTag(tag);
                break;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.Disable:
                mDaemon.disablePatch();
                Toast.makeText(getApplicationContext(), "Static UID Disable", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static byte[] HexToBytes(String hex) {

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
