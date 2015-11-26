package com.urucas.legofy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

/**
 * Created by vruno on 11/26/15.
 */
public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String html = getResources().getString(R.string.about_text);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.loadData(html, "text/html", "utf-8");

        Button shareBtt = (Button) findViewById(R.id.shareBtt);
        shareBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
    }

    private void share() {

    }
}
