package com.simple.lightnote;

import android.app.Application;

import com.evernote.client.android.EvernoteSession;
import com.simple.lightnote.db.DaoMaster;
import com.simple.lightnote.db.DaoSession;

import org.greenrobot.greendao.database.Database;
import org.xutils.x;

public class LightNoteApplication extends Application {
    public static final String AppPath = "lightnote";
    public static final boolean ENCRYPTED = false;
    private static final String CONSUMER_KEY = "glovve";
    private static final String CONSUMER_SECRET = "3b5ff558595f2510";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
    EvernoteSession mEvernoteSession;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        CustomCrashHandler.getInstance().initCrashHandler(getApplicationContext());
        x.Ext.init(this);
        x.Ext.setDebug(true);

		/*String consumerKey;
        if ("Your consumer key".equals(CONSUMER_KEY)) {
			consumerKey = BuildConfig.EVERNOTE_CONSUMER_KEY;
		} else {
			// isn't the default value anymore
			consumerKey = CONSUMER_KEY;
		}

		String consumerSecret;
		if ("Your consumer secret".equals(CONSUMER_SECRET)) {
			consumerSecret = BuildConfig.EVERNOTE_CONSUMER_SECRET;
		} else {
			// isn't the default value anymore
			consumerSecret = CONSUMER_SECRET;
		}
*/
//		CustomCrashHandler.getInstance().initCrashHandler(getApplicationContext());
    /*	Stetho.initialize(
				Stetho.newInitializerBuilder(this)
						.enableDumpapp(
								Stetho.defaultDumperPluginsProvider(this))
						.enableWebKitInspector(
								Stetho.defaultInspectorModulesProvider(this))
						.build());*/

        mEvernoteSession = new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .setSupportAppLinkedNotebooks(SUPPORT_APP_LINKED_NOTEBOOKS)
                .build(CONSUMER_KEY, CONSUMER_SECRET)
                .asSingleton();
//		registerActivityLifecycleCallbacks(new LoginChecker());
        initDB();
    }

    private void initDB() {
//		DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "lightnote");
//		Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lightnote");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
