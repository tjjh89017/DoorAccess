package date.kojuro.dooraccess;

import android.content.Context;

/**
 * Created by date on 2017/2/14.
 */

public class DBService {

    private final static String DB_NAME = "DoorAccess.db";
    private static DBService mInstance;

    private DaoSession mDaoSession;

    private DBService(Context context) {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        mDaoSession = daoMaster.newSession();

    }

    public static DBService getInstance(Context context) {

        if(mInstance == null)
            mInstance = new DBService(context);

        return mInstance;
    }

    public DaoSession getSession() {

        return mDaoSession;
    }

    public TagDao getTagDao() {

        return mDaoSession.getTagDao();
    }
}
