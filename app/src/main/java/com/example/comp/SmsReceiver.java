package com.example.comp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/**
 * Receives incoming SMS (declared in the manifest) and processes buddy commands.
 * Texts from numbers that are not saved buddies are ignored.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) return;

        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (messages == null || messages.length == 0) return;

        // A long text arrives in several parts; join them into one body.
        String sender = messages[0].getOriginatingAddress();
        StringBuilder body = new StringBuilder();
        for (SmsMessage m : messages) body.append(m.getMessageBody());

        handle(context, sender, body.toString());
    }

    private void handle(Context context, String sender, String body) {
        SmsDataProvider data = SmsHub.getProvider();
        if (data == null || sender == null) return;
        if (!data.isBuddyNumber(sender)) return;   // only buddies can talk to the app

        data.onMessageLogged(true, sender, body);
        SmsMessages.Parsed cmd = SmsMessages.parse(body);

        switch (cmd.type) {
            case PROGRESS: {
                String reply = data.getProgressReply(cmd.arg);
                if (reply == null) reply = "No matching assignment found.";
                reply(context, data, sender, reply);
                break;
            }
            case LEADERBOARD:
                reply(context, data, sender, data.getLeaderboardReply());
                break;
            case HELP:
                reply(context, data, sender, SmsMessages.HELP_REPLY);
                break;
            case NUDGE:
                data.onNudge(sender);
                NudgeNotifier.show(context, "Your buddy says get to work!");
                break;
            case FINISHED:
                data.onBuddyFinished(sender, cmd.name, cmd.title, cmd.points, cmd.total);
                break;
            default:
                // Status texts and replies from the other app: log only, never reply.
                // Replying to unknown text would make two phones answer each other forever.
                break;
        }
    }

    private void reply(Context context, SmsDataProvider data, String to, String text) {
        if (SmsSender.send(context, to, text)) data.onMessageLogged(false, to, text);
    }
}
