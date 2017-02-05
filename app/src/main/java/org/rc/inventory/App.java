package org.rc.inventory;

import android.app.Application;
import android.support.multidex.MultiDexApplication;


public class App extends MultiDexApplication {

    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
