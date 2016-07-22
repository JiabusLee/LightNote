package com.simple.lightnote;

import android.app.Application;

import com.evernote.client.android.EvernoteSession;

import org.xutils.x;

public class LightNoteApplication extends Application{
	public static final String AppPath="lightnote";
	private static final String CONSUMER_KEY = "glovve";
	private static final String CONSUMER_SECRET = "3b5ff558595f2510";
	private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
	EvernoteSession mEvernoteSession;
	private static final boolean SUPPORT_APP_LINKED_NOTEBOOKS = true;
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

	}

}
