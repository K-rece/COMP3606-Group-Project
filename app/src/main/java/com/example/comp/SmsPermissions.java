package com.example.comp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Runtime permission helper (same idea as the class example: check, then requestPermissions).
 * Call SmsPermissions.request(this) in MainActivity/HomeActivity.onCreate, and check
 * SmsPermissions.hasSmsPermissions(this) before sending.
 */
public final class SmsPermissions {
    public static final int REQUEST_CODE = 100;

    private SmsPermissions() {}

    /** SEND_SMS and RECEIVE_SMS are required. POST_NOTIFICATIONS (Android 13+) is only needed for NUDGE. */
    public static boolean hasSmsPermissions(Context ctx) {
        return ctx.checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && ctx.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /** Asks for anything not yet granted. Does nothing if everything is already granted. */
    public static void request(Activity activity) {
        List<String> missing = new ArrayList<>();
        if (activity.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
            missing.add(Manifest.permission.SEND_SMS);
        if (activity.checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
            missing.add(Manifest.permission.RECEIVE_SMS);
        if (Build.VERSION.SDK_INT >= 33
                && activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            missing.add(Manifest.permission.POST_NOTIFICATIONS);
        if (!missing.isEmpty())
            activity.requestPermissions(missing.toArray(new String[0]), REQUEST_CODE);
    }
}
