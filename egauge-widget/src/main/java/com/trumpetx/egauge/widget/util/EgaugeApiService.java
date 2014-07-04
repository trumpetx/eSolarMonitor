package com.trumpetx.egauge.widget.util;

import android.content.Context;
import android.util.Log;
import com.trumpetx.egauge.widget.NotConfiguredException;
import com.trumpetx.egauge.widget.xml.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EgaugeApiService {

    private static EgaugeApiService singleton;

    public static EgaugeApiService getInstance(Context context) throws NotConfiguredException {
        if (singleton == null) {
            singleton = new EgaugeApiService();
        }
        singleton.setUrlBase(PreferencesUtil.getEgaugeUrl(context));

        return singleton;
    }

    private static final String LOG_TAG = "eGaugeApiService";
    private static final Map<String, String> DATA;

    static {
        DATA = new HashMap<>();
        DATA.put("inst", null);
        DATA.put("v1", null);
    }

    private String urlBase;

    public void getData(final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Serializer serializer = new Persister();
                try {
                    callback.callback(serializer.read(Data.class, getXml("egauge", DATA)));
                } catch (Exception e) {
                    callback.callback(e.getMessage());
                }
            }
        }, "GetDataThread").start();
    }

    private InputStream getXml(String target, Map<String, String> params) throws IOException {
        StringBuilder url = new StringBuilder(urlBase).append(target);
        String appender = "?";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(appender).append(entry.getKey());
            if (entry.getValue() != null) {
                url.append("=").append(entry.getValue());
            }
            appender = "&";
        }

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
        HttpConnectionParams.setSoTimeout(httpParameters, 5000);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        Log.d(LOG_TAG, "GET: " + url.toString());
        HttpGet get = new HttpGet(url.toString());
        HttpResponse httpResponse = httpClient.execute(get);
        return httpResponse.getEntity().getContent();
    }

    private void setUrlBase(String urlBase) {
        this.urlBase = (urlBase.endsWith("/") ? urlBase : urlBase + "/") + "cgi-bin/";
    }
}
