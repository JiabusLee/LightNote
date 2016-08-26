package com.simple.lightnote.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.simple.lightnote.LightNoteApplication;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseActivity;
import com.simple.lightnote.constant.Constans;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.util.HtmlParser;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;
import com.simple.lightnote.view.MarkDownView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by homelink on 2016/3/14.
 */
public class NotePreViewActivity extends BaseActivity {
    /*String*/
    public final static int Source_content = 1;
    /*file*/
    public final static int Source_file = 2;
    /*database id*/
    public final static int Source_id = 3;
    private static final String TAG = "NotePreViewActivity";
    //    private final static String LOAD_HTML = "file:///android_asset/justwetools/markdown.html";
    private final static String LOAD_HTML = "file:///android_asset/editor.html";
    private static String basePath = "/sdcard/lightnote/htmlsource";
    int sourceType;
    private WebView mWebView;
    private MarkDownView preview;
    private String fileContents;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepreview);
        initView();
        loadData();

    }

    private void loadData() {
        sourceType = getIntent().getIntExtra("sourceType", -1);
        switch (sourceType) {
            case Source_content:
                fileContents = getIntent().getStringExtra("fileContents");
                loadMarkDown(fileContents);
                break;
            case Source_file:
                String filePath = getIntent().getStringExtra("filePath");
                if (!TextUtils.isEmpty(filePath)) {
                    loadFileData(filePath);
                }
                loadMarkDown(fileContents);
                break;
            case Source_id:
                long noteId = getIntent().getLongExtra("noteId", -1);
                getNote(noteId);
                loadMarkDown(fileContents);
                break;
            default:
                break;
        }


    }

    /**
     * 从数据据中加载数据
     *
     * @param guid
     */
    private void getNote(long guid) {

        DaoSession daoSession = ((LightNoteApplication) getApplication()).getDaoSession();
        NoteDao noteDao = daoSession.getNoteDao();
        if (guid > 0) {
            List<SimpleNote> list = noteDao.queryBuilder().where(NoteDao.Properties.Id.eq(guid)).list();
            if (!ListUtils.isEmpty(list)) {
                SimpleNote simpleNote = list.get(0);
                fileContents = simpleNote.getContent();
            }

        }
    }

    /**
     * 从文件中加载数据
     *
     * @param filePath
     */
    private void loadFileData(String filePath) {
        Observable.just(filePath)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return getFileContents(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.e(TAG, "load file onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        LogUtils.e(TAG, "Load File Content onNext: " + s);
                        fileContents = s;
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        //开启对话框
                    }
                });

    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("预览");
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    private void saveHtmlToFile(String s, String filePath) {
        File file = new File(filePath);
        String name = file.getName();
        String substring = name.substring(0, name.lastIndexOf('.'));

        File saveFile = new File(basePath, substring + ".html");
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
        initToolBar();
        initWebView();
    }

    private void initWebView() {
        preview = (MarkDownView) findViewById(R.id.preview);
        mWebView = (WebView) findViewById(R.id.webview);

        preview.setVisibility(View.GONE);
        mWebView.setClickable(false);
        mWebView.cancelLongPress();

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDefaultFontSize(18);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        mWebView.setWebViewClient(new WebViewClient() {
                                      public void onPageFinished(WebView view, String url) {
//                mWebView.loadUrl("javascript:alert('onPageFinished')");
                                          loadMarkDown(fileContents);
                                      }

                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                          return true;
                                      }

                                      @Override
                                      public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                                          return true;
                                      }

                                  }

        );
        mWebView.loadUrl(LOAD_HTML);

    }

    /**
     * 读取文件
     *
     * @param filePath
     * @return
     */
    private String getFileContents(@NonNull String filePath) {
        BufferedReader bufferedReadr = null;
        try {
            bufferedReadr = new BufferedReader(new FileReader(new File(filePath)));
            StringBuilder sb = new StringBuilder();
            String temp;
            while ((temp = bufferedReadr.readLine()) != null) {
                sb.append(temp.trim()).append(Constans.newLineFlag);
            }
            fileContents = sb.toString();
            return fileContents;
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
        return null;
    }

    @JavascriptInterface
    private void loadMarkDown(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
//                String encode = URLEncoder.encode(str, "UTF-8");
                String s = str.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");
                Observable
                        .create(new Observable.OnSubscribe<String>() {
                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                subscriber.onStart();
                                LogUtils.e(TAG, "loadMarkDown: create");
                                String s1 = HtmlParser.formatText(s, Constans.newLineFlag);
                                LogUtils.e(TAG, "call: format end:==>" + s1);
                                subscriber.onNext(s1);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                LogUtils.e(TAG, "onCompleted: loadCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(String sHtml) {
                                mWebView.loadUrl("javascript:RE.setHtml(\"" + sHtml + "\")");
                                LogUtils.e(TAG, "loadMarkDown: " + sHtml);
                            }
                        });
                /*
                String formatText = HtmlParser.formatText(str, Constans.newLineFlag);
                LogUtils.e(TAG, "loadMarkDown: " + formatText);
                mWebView.loadUrl("javaScript:parseMarkdown(\"" + str + "\");");
                 *//*URLEncoder.encode(s, "UTF-8")*//*
                mWebView.loadUrl("javascript:RE.setHtml('" + formatText + "');");*/
            } catch (Exception e) {
                // No handling
                e.printStackTrace();
            }
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
                mWebView.loadUrl("javascript:alert('重新加载')");
                loadMarkDown(fileContents);
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

}
