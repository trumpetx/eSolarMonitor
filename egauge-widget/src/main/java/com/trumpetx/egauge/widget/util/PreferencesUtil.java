package com.trumpetx.egauge.widget.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.trumpetx.egauge.widget.NotConfiguredException;

public class PreferencesUtil {
    public static String getEgaugeUrl(Context context) throws NotConfiguredException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String egaugeName = preferences.getString("monitor_name_text", null);
        if ("eGauge####".equals(egaugeName) || null == egaugeName || "".equals(egaugeName.trim())) {
            throw new NotConfiguredException("eGauge Monitor Name is not configured.");
        }
        String proxyServer = preferences.getString("proxy_server_text", "");
        if (null == proxyServer) {
            throw new NotConfiguredException("eGauge Proxy Server is not configured.");
        }
        String url = "http://" + egaugeName + "." + proxyServer;
        return url;
    }
}
