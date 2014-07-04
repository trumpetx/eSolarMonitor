package com.trumpetx.egauge.widget.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.trumpetx.egauge.widget.SettingsActivity;

/**
 * Helper class to retrieve intents for this applciation.
 */
public class EgaugeIntents {
    public static final String EGAUGE_WIDGET_UPDATE = "com.trumpetx.egauge.widget.EGAUGE_WIDGET_UPDATE";

    public static PendingIntent createSettingsPendingIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;
    }

    public static PendingIntent createRefreshPendingIntent(Context context) {
        Intent intent = new Intent(EGAUGE_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public static PendingIntent createWebviewPendingIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        return pendingIntent;
    }
}
