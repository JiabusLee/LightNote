package com.simple.lightnote.webview;

import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by homelink on 2016/7/27.
 */
public class MyWebClient extends WebViewClient {
    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return true;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        // reject anything other
        return true;
    }
    @Override
    public void onPageFinished(WebView view, String url) {
//                mWebView.loadUrl("javascript:alert('onPageFinished')");
//        loadMarkDown(fileContents);
    }


}
