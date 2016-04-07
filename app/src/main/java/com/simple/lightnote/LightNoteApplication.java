package com.simple.lightnote;

import android.app.Application;

public class LightNoteApplication extends Application{
	@Override
	public void onCreate() {
		super.onCreate();

		CustomCrashHandler.getInstance().initCrashHanler(getApplicationContext());
	/*	Stetho.initialize(
				Stetho.newInitializerBuilder(this)
						.enableDumpapp(
								Stetho.defaultDumperPluginsProvider(this))
						.enableWebKitInspector(
								Stetho.defaultInspectorModulesProvider(this))
						.build());*/


	}
}
