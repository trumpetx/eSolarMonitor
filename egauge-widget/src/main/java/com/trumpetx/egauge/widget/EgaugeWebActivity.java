package com.trumpetx.egauge.widget;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;
import com.trumpetx.egauge.widget.util.PreferencesUtil;

public class EgaugeWebActivity extends Activity {
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_layout);

        webView = (WebView) findViewById(R.id.egauge_web);
        webView.getSettings().setJavaScriptEnabled(true);

        try {
            webView.loadUrl(PreferencesUtil.getEgaugeUrl(getApplicationContext()));
        } catch (NotConfiguredException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            onBackPressed();
        }
    }
}
