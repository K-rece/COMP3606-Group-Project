package com.example.comp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.List;

/** Sends SMS using the same SmsManager pattern as the class example. */
public final class SmsSender {
    private static String lastError = "";

    private SmsSender() {}

    /** Returns true if the message was handed to the SMS service. On failure see getLastError(). */
    public static boolean send(Context ctx, String phone, String message) {
        if (phone == null || phone.trim().isEmpty()) {
            lastError = "No phone number";
            return false;
        }
        if (ctx.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            lastError = "SMS permission denied";
            return false;
        }
        if (!ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            lastError = "SMS is not supported on this device";
            return false;
        }
        try {
            SmsManager manager;
            if (Build.VERSION.SDK_INT >= 31) {
                manager = ctx.getSystemService(SmsManager.class);
            } else {
                manager = SmsManager.getDefault();
            }
            if (manager == null) {
                lastError = "SMS service unavailable";
                return false;
            }
            // multipart so messages longer than 160 characters still go through
            ArrayList<String> parts = manager.divideMessage(message);
            manager.sendMultipartTextMessage(phone.trim(), null, parts, null, null);
            lastError = "";
            return true;
        } catch (Exception e) {
            lastError = "SMS failed: " + e.getMessage();
            return false;
        }
    }

    /** Sends the same text to every buddy number. Returns how many were sent successfully. */
    public static int sendToAll(Context ctx, List<String> phones, String message) {
        int ok = 0;
        for (String p : phones) if (send(ctx, p, message)) ok++;
        return ok;
    }

    public static String getLastError() { return lastError; }
}
