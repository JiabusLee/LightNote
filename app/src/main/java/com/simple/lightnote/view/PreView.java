package com.simple.lightnote.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Note PreView
 */
public class PreView extends WebView {

    private Context context;
    private ProgressDialog progressDialog = null;
    private final static String LOAD_HTML = "file:///android_asset/editor.html";
    private String str = "";

    public PreView(Context context) {
        super(context);
        init(context);
    }

    public PreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public PreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        progressDialog = ProgressDialog.show(context, "请等待", "正在载入...", true);
        this.setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        this.getSettings().setBuiltInZoomControls(true);
        this.getSettings().setDisplayZoomControls(false);
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setLoadWithOverviewMode(true);
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.getSettings().setDefaultFontSize(18);
        this.getSettings().setAppCacheEnabled(true);
        this.getSettings().setCacheMode(this.getSettings().LOAD_CACHE_ELSE_NETWORK);
        loadUrl(LOAD_HTML);
        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100 && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }
        });

    }

    private void setUpWebView(final String mdText) {
        this.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 100 && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @JavascriptInterface
    private void loadMarkDown(String str) {
        try {
            String encode = URLEncoder.encode(str, "UTF-8");
            String s = encode.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");
//            mWebView.loadUrl("javaScript:parseMarkdown(\"" + str + "\");");
            this.loadUrl("javascript:RE.setHtml('" + URLEncoder.encode(s, "UTF-8") + "');");

        } catch (UnsupportedEncodingException e) {
            // No handling
        }

    }

    public void setStringSource(String str) {
        this.str = str;
        this.loadUrl(LOAD_HTML);
        setUpWebView(str);
    }

}
