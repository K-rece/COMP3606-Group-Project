package com.example.comp;

import android.app.Application;

/**
 * Runs when the app process starts (declared with android:name=".App" in the manifest).
 * Registering the provider here means texts that arrive while the app is closed still get handled.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SmsHub.setProvider(new BuddyDataProvider(this));
    }
}
