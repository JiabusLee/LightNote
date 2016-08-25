package com.simple.lightnote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;
import com.simple.lightnote.LightNoteApplication;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseSwipeActivity;
import com.simple.lightnote.constant.SPConstans;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.EvernoteHelper;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.util.SPUtil;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;
import com.simple.lightnote.utils.ToastUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 编辑页面
 *
 * @author homelink
 */
public class SimpleNoteEditActivity extends BaseSwipeActivity {
    private static final String TAG = "SimpleNoteEditActivity";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.simpleNote_edt_noteContent)
    EditText edt_noteContent;
    @Bind(R.id.simpleNote_ll_actionbar)
    LinearLayout ll_acitonBar;


    private NoteDao noteDao;
    private SimpleNote note;
    private EvernoteNoteStoreClient noteStoreClient;
    private EvernoteHelper helper;
    /**
     * 笔记是否改变
     */
    private boolean textChanged;
    //    private String noteId;
    private String guid;
    private String s_noteContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplenoteedit);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
        loadData();
//        loadData();

    }

    /***
     * 从数据库与网络加载数据
     */
    private void loadDataFromMutiSource() {
        // TODO: 2016/8/18 从多个数据源获取数据
        Observable<SimpleNote> memorys = Observable.empty();
        Observable<SimpleNote> databases = Observable.empty();//取得数据后保存到memorys
        Observable<SimpleNote> network = Observable.empty();//得取数据后保存到memorys和databases

        Observable<SimpleNote> source = Observable
                .concat(memorys, databases, network)
                .first();
        Subscription subscribe = source.subscribe(new Action1<SimpleNote>() {
            @Override
            public void call(SimpleNote note) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                saveToDB();
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                textChanged = false;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                com.evernote.edam.type.Note note = new com.evernote.edam.type.Note();
                note.setContent(edt_noteContent.getText().toString().trim());
                noteStoreClient.createNoteAsync(note, new EvernoteCallback<com.evernote.edam.type.Note>() {
                    @Override
                    public void onSuccess(com.evernote.edam.type.Note result) {

                    }

                    @Override
                    public void onException(Exception exception) {

                    }
                });
            }
        });
    }

    private void initListener() {
        edt_noteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initData() {
        LightNoteApplication application = (LightNoteApplication) getApplication();
        DaoSession daoSession = application.getDaoSession();
        noteDao = daoSession.getNoteDao();

        noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        int showToolBar = SPUtil.getInstance(this).getInt(SPConstans.EDIT_TOOL_BAR, -1);
        setToolBarVisible(showToolBar);
        helper = new EvernoteHelper(SimpleNoteEditActivity.this, noteDao);

    }

    private void loadData() {

        guid = getIntent().getStringExtra("noteId");
        Log.e(TAG, "loadData: " + this.guid);
        if (TextUtils.isEmpty(guid)) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {

            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {

                    loadData(guid);
                    if (!TextUtils.isEmpty(s_noteContent)) {
                        Observable.create(subscriber1 -> helper.downloadNote(guid)).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe();
                    } else {
                        subscriber.onNext(s_noteContent);
                    }

                }


            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(str -> {
                if (!TextUtils.isEmpty(str)) {
                    edt_noteContent.setText(str);
                    edt_noteContent.setSelection(str.length());
                }
            });
        }


    }

    private void loadData(String guid) {
        List<SimpleNote> list = noteDao.queryBuilder().where(NoteDao.Properties.guid.eq(guid)).list();
        if (!ListUtils.isEmpty(list)) {
            note = list.get(0);
            Observable.just(0)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .doOnNext(__ -> saveToDB()).subscribe();
            s_noteContent = note.getContent();
        }

        if (!TextUtils.isEmpty(s_noteContent)) {
            edt_noteContent.setText(s_noteContent);
            edt_noteContent.setSelection(s_noteContent.length());
        }
    }

    private void initView() {
        mToolbar.setTitle("编辑");
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simplenoteedit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void saveToDB() {
        s_noteContent = edt_noteContent.getText().toString().trim();
        if (note != null) {
            //更新
            if (!note.getContent().trim().equals(s_noteContent)) {
                note.setTitle(note.getTitle());
                note.setContent(s_noteContent);
                LogUtils.e(TAG, "更新Note: " + note);

                Observable.just(0)
                        .observeOn(Schedulers.newThread())
                        .subscribeOn(Schedulers.io())
                        .doOnNext(__ -> {
                            try {
                                helper.updateLocalNote(note.getGuid(), note.get_id());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).subscribe();
            }
        } else {
            //新建
            if (!TextUtils.isEmpty(s_noteContent)) {
                note = new SimpleNote();
                note.setTitle(note.getTitle());
                note.setContent(s_noteContent);
                Observable.just(0)
                        .observeOn(Schedulers.newThread())
                        .subscribeOn(Schedulers.newThread())
                        .doOnNext(__ -> {
                            try {
                                Note eNote = helper.createNote(note);
                                LogUtils.e(TAG, "新建Note: " + eNote);
                                noteDao.insert(SimpleNote.toSimpleNote(eNote));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).subscribe();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_simplenoteedit_actionBar:

                int flag_actionBar = -1;
                if (ll_acitonBar.getVisibility() == View.VISIBLE) {
                    setToolBarVisible(1);
                    flag_actionBar = 1;
                } else {
                    setToolBarVisible(0);
                    flag_actionBar = 0;
                }
                SPUtil.getEditor(SimpleNoteEditActivity.this).putInt(SPConstans.EDIT_TOOL_BAR, flag_actionBar).apply();
                return true;

            case R.id.menu_simplenoteedit_caption:
                ToastUtils.showToast(SimpleNoteEditActivity.this, "caption");
                return true;
            case R.id.menu_simplenoteedit_orderlist:
                ToastUtils.showToast(SimpleNoteEditActivity.this, "有序");
                return true;
            case R.id.menu_simplenoteedit_list:
                ToastUtils.showToast(SimpleNoteEditActivity.this, "清单");
                return true;
            case R.id.menu_simplenoteedit_info:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void setToolBarVisible(int i) {
        if (i == 0) {
            ll_acitonBar.setVisibility(View.VISIBLE);
            AlphaAnimation alpha = new AlphaAnimation(0, 1);
            alpha.setDuration(300);
            alpha.setFillAfter(true);
            ll_acitonBar.startAnimation(alpha);
        } else {
            ll_acitonBar.setVisibility(View.GONE);
            AlphaAnimation alpha = new AlphaAnimation(1, 0);
            alpha.setDuration(300);
            alpha.setFillAfter(true);
            ll_acitonBar.startAnimation(alpha);
        }
    }

    public void onToolBarClick(View v) {
        String trim = edt_noteContent.getText().toString().trim();

        switch (v.getId()) {
            case R.id.edit_toolbar_item_1:
                if (trim.length() > 0) {
//                    int selectionStart = edt_noteContent.getSelectionStart();
                    int selectionEnd = edt_noteContent.getSelectionEnd();
                    if (trim.trim().lastIndexOf('#') == trim.length() - 2) {
                        edt_noteContent.append("#");
                    } else {
                        edt_noteContent.append("# ");
                    }
                } else {
                    edt_noteContent.append("# ");
                }

                break;
            case R.id.edit_toolbar_item_2:
                break;
            case R.id.edit_toolbar_item_3:
                break;
            case R.id.edit_toolbar_item_4:
                break;
            case R.id.edit_toolbar_item_5:
                int selectionEnd = edt_noteContent.getSelectionEnd();
                if (selectionEnd > 1) {
                    edt_noteContent.append("\n---\n", selectionEnd - 1, selectionEnd);
                } else {
                    edt_noteContent.append("---\n");
                }

                break;
            case R.id.edit_toolbar_item_save:
                if (textChanged) {
                    saveToDB();
                }
                if (!TextUtils.isEmpty(trim)) {
                    Intent intent = new Intent(this, NotePreViewActivity.class);
                    intent.putExtra("sourceType", NotePreViewActivity.Source_id);
                    intent.putExtra("noteId", guid);
                    startActivity(intent);
                } else {

                    finish();
                }
                break;
        }
    }

}
