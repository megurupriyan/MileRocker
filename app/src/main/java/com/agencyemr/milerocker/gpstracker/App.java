package com.agencyemr.milerocker.gpstracker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.agencyemr.milerocker.gpstracker.models.datamodels.DaoMaster;
import com.agencyemr.milerocker.gpstracker.models.datamodels.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Gurupriyan on 10/18/2016.
 */
public class App extends Application {
    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static final boolean ENCRYPTED = true;

    Activity currentActivity;

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "milerocker-db-encrypted" : "milerocker-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}