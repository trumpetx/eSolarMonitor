package com.trumpetx.egauge.widget;

import android.app.AlarmManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.trumpetx.egauge.widget.util.Callback;
import com.trumpetx.egauge.widget.util.EgaugeApiService;
import com.trumpetx.egauge.widget.util.EgaugeIntents;
import com.trumpetx.egauge.widget.util.NetworkConnection;
import com.trumpetx.egauge.widget.xml.Data;
import com.trumpetx.egauge.widget.xml.Register;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EgaugeWidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "eGaugeWidget";
    private static final String POWER = "P";
    private DateFormat df = new SimpleDateFormat("hh:mm");

    /**
     * Called when an update intent is received and also called by onReceive when our clock manager calls the method.
     *
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean enableSync = preferences.getBoolean("enable_sync_checkbox", false) && NetworkConnection.hasNetworkConnection(context);
        final boolean showTime = preferences.getBoolean("show_time_checkbox", true);
        final boolean showSettings = preferences.getBoolean("show_settings_checkbox", true);
        final boolean showRefresh = preferences.getBoolean("show_refresh_checkbox", true);
        final String[] gridRegisters = preferences.getString("egauge_grid_register_text", "Grid").trim().split("\\s*,\\s*");
        final String[] solarRegisters = preferences.getString("egauge_solar_register_text", "Solar").trim().split("\\s*,\\s*");
        final String displayPreference = preferences.getString("display_option_list", "net_usage");


        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        if (showSettings) {
            views.setViewVisibility(R.id.settings_button, View.VISIBLE);
            views.setOnClickPendingIntent(R.id.settings_button, EgaugeIntents.createSettingsPendingIntent(context));
        } else {
            views.setViewVisibility(R.id.settings_button, View.GONE);
        }

        if (showRefresh) {
            views.setViewVisibility(R.id.refresh_button, View.VISIBLE);
            views.setOnClickPendingIntent(R.id.refresh_button, EgaugeIntents.createRefreshPendingIntent(context));
        } else {
            views.setViewVisibility(R.id.refresh_button, View.GONE);
        }

        if (enableSync) {
            for (final int appWidgetId : appWidgetIds) {
                Log.i(LOG_TAG, "Updating eGauge widgets " + appWidgetId);
                try {
                    EgaugeApiService.getInstance(context).getData(new Callback() {
                        @Override
                        public void callback(Object object) {
                            if (showTime) {
                                views.setTextViewText(R.id.updatedLabel, df.format(new Date()));
                                views.setViewVisibility(R.id.updatedLabel, View.VISIBLE);
                            } else {
                                views.setViewVisibility(R.id.updatedLabel, View.GONE);
                            }
                            if (object instanceof String) {
                                views.setTextViewText(R.id.displayLabel, (String) object);
                            } else if (object instanceof Data) {
                                Map<String, Register> registerNames = new HashMap<>();
                                for (Register register : ((Data) object).getRegisters()) {
                                    if (POWER.equals(register.getType()) && !register.getName().endsWith("+")) {
                                        registerNames.put(register.getName(), register);
                                    }
                                }
                                for (Register register : ((Data) object).getRegisters()) {
                                    if (POWER.equals(register.getType()) && register.getName().endsWith("+")) {
                                        String nonPlusName = register.getName().substring(0, register.getName().length() - 1);
                                        registerNames.put(nonPlusName, register); // Overwrite the non-positive only register (don't want to double count)
                                    }
                                }
                                long gridTotal = 0;
                                for (String registerName : gridRegisters) {
                                    if (registerNames.containsKey(registerName)) {
                                        gridTotal += registerNames.get(registerName).getRateOfChange();
                                    }
                                }

                                long generationTotal = 0;
                                for (String registerName : solarRegisters) {
                                    if (registerNames.containsKey(registerName)) {
                                        long rateOfChange = registerNames.get(registerName).getRateOfChange();
                                        // This is probably already the case (the + sign register); however, just in case...
                                        if (rateOfChange > 0) {
                                            generationTotal += rateOfChange;
                                        }
                                    }
                                }

                                long usageTotal = gridTotal + generationTotal;

                                long displayValue;
                                switch (displayPreference) {
                                    case "usage":
                                        displayValue = usageTotal * -1;
                                        break;
                                    case "production":
                                        displayValue = generationTotal;
                                        break;
                                    case "net_usage":
                                    default:
                                        displayValue = gridTotal * -1;
                                }

                                views.setTextViewText(R.id.displayLabel, displayValue + " " + Register.REGISTER_TYPE_LABELS.get(POWER));
                                views.setTextColor(R.id.displayLabel, (displayValue > 0) ? Color.GREEN : Color.RED);

                            }
                            appWidgetManager.updateAppWidget(appWidgetId, views);
                        }
                    });
                } catch (NotConfiguredException nce) {
                    Toast.makeText(context, nce.getMessage(), Toast.LENGTH_SHORT);
                }
            }
        } else {
            Log.i(LOG_TAG, "eGauge sync not enabled.");
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        enableWidget(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        disableWidget(context);
    }


    private void enableWidget(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enableSync = preferences.getBoolean("enable_sync_checkbox", false) && NetworkConnection.hasNetworkConnection(context);
        int refreshIntervalSeconds = -1;
        try {
            refreshIntervalSeconds = Integer.parseInt(preferences.getString("sync_frequency_list", "300"));
        } catch (NumberFormatException e) {
        }

        if (enableSync && refreshIntervalSeconds > 0) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 1);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * refreshIntervalSeconds, EgaugeIntents.createRefreshPendingIntent(context));
            Log.d(LOG_TAG, "eGauge Widget timer set to update widget every " + refreshIntervalSeconds + " seconds");
        }
    }

    private void disableWidget(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(EgaugeIntents.createRefreshPendingIntent(context));
        Log.d(LOG_TAG, "Disabled eGauge Widget timer");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        disableWidget(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(LOG_TAG, "Received intent " + intent);
        if (EgaugeIntents.EGAUGE_WIDGET_UPDATE.equals(intent.getAction())) {
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            onUpdate(context, appWidgetManager, ids);
        } else if ("eGaugePreferencesUpdated".equals(intent.getAction())) {
            disableWidget(context);
            enableWidget(context);
        }
    }
}