package com.example.comp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

/** Shows the notification for an incoming NUDGE. */
final class NudgeNotifier {
    private static final String CHANNEL_ID = "buddy_nudges";

    private NudgeNotifier() {}

    static void show(Context ctx, String text) {
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(new NotificationChannel(
                    CHANNEL_ID, "Buddy nudges", NotificationManager.IMPORTANCE_DEFAULT));
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Assignment Buddy")
                .setContentText(text)
                .setAutoCancel(true);
        nm.notify((int) (System.currentTimeMillis() % 100000), b.build());
    }
}
