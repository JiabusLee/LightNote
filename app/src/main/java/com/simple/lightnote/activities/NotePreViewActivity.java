package com.simple.lightnote.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseActivity;
import com.simple.lightnote.constant.Constans;
import com.simple.lightnote.util.HtmlParser;
import com.simple.lightnote.view.MarkDownView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by homelink on 2016/3/14.
 */
public class NotePreViewActivity extends BaseActivity {
    private static final String TAG = "NotePreViewActivity";
    //    private final static String LOAD_HTML = "file:///android_asset/justwetools/markdown.html";
    private final static String LOAD_HTML = "file:///android_asset/editor.html";

    private WebView mWebView;
    private MarkDownView preview;
    private String fileContents;
    private static String basePath = "/sdcard/lightnote/htmlsource";
    private String filePath;
    private Toolbar mToolbar;
    int sourceType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepreview);
        Log.e(TAG, "onCreate:" + getIntent().toString());

       sourceType = getIntent().getIntExtra("sourceType", 0);


        filePath = getIntent().getStringExtra("filePath");
        if(TextUtils.isEmpty(filePath)){
//            Uri data = getIntent().getData();
//            Uri data=Uri.parse("file:///storage/emulated/0/lightnote/markdown/MarkdownDemo.md");
//            Log.e(TAG, "onCreate: data:"+data.toString() );
//            String fileAbsolutePath = FileUtils.getPathFromUri(data, this);
            filePath = getIntent().getData().getPath();

        }
        initToolBar();
        initView();


        Observable.just(filePath)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if(sourceType==1){
                            return s;
                        }else{
                        return getFileContents(s);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        Log.e(TAG, "call:before:" + s);
                        String s1 = HtmlParser.formatText(s, Constans.newLineFlag);
                        Observable.just(s1).subscribeOn(Schedulers.io()).subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                saveHtmlToFile(s);
                            }
                        });
                        return s1;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.e(TAG, "call:" + s);
                        fileContents = s;
//                       preview.setStringSource(s);
                        Log.e(TAG, "call:after:" + s);
//                        mWebView.setStringSource(s);
//                        mWebView.loadUrl("javascript:parseMarkdown(\"" + s + "\")");
//                        mWebView.setStringSource(s);
                        //执行JavaScript方法
                        mWebView.evaluateJavascript("alertData('" + s + "')", null);
                        loadMarkDown(s);
                    }
                });

    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("预览");
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
//        mToolbar.setSupportActionBar(mToolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
/*        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.preview:
                        mWebView.loadUrl("javascript:alertData('重新加载')");
                        loadMarkDown(fileContents);
                        break;

                }
                return false;
            }
        });*/

    }

    private void saveHtmlToFile(String s) {
        File file = new File(filePath);
        String name = file.getName();
        String substring = name.substring(0, name.lastIndexOf('.'));

        File saveFile = new File(basePath, substring + ".htm");
        try {
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));
            bw.write(s);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        preview = (MarkDownView) findViewById(R.id.preview);
        mWebView = (WebView) findViewById(R.id.webview);

        preview.setVisibility(View.GONE);
//        mWebView.setVisibility(View.GONE);

        mWebView.setClickable(false);
        mWebView.cancelLongPress();
        mWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                mToolbar.setVisibility(View.GONE);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDefaultFontSize(18);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
//                mWebView.loadUrl("javascript:alertData('onPageFinished')");
                if (fileContents != null && fileContents.length() > 0) {
                    loadMarkDown(fileContents);
                }
            }
        });
        mWebView.loadUrl(LOAD_HTML);
    }

    @NonNull
    private String getFileContents(String s) {
        BufferedReader bufferedReadr = null;
        try {
            bufferedReadr = new BufferedReader(new FileReader(new File(s)));
            StringBuilder sb = new StringBuilder();
            String temp;
            while ((temp = bufferedReadr.readLine()) != null) {
              sb.append(temp.trim()).append(Constans.newLineFlag);
            }

            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReadr != null)
                    bufferedReadr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    @JavascriptInterface
    private void loadMarkDown(String str) {
        try {
            String encode = URLEncoder.encode(str, "UTF-8");
            String s = encode.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");
//            mWebView.loadUrl("javaScript:parseMarkdown(\"" + str + "\");");
            mWebView.loadUrl("javascript:RE.setHtml('" + URLEncoder.encode(s, "UTF-8") + "');");

        } catch (UnsupportedEncodingException e) {
            // No handling
        }

    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interfaces and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preview:
                mWebView.loadUrl("javascript:alertData('重新加载')");
                loadMarkDown(fileContents);
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
