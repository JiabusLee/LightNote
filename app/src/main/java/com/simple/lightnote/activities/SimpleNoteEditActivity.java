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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.evernote.client.android.EvernoteSession;
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
import com.simple.lightnote.utils.NetworkUtils;
import com.simple.lightnote.utils.ToastUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 编辑页面
 *
 * @author homelink
 */
public class SimpleNoteEditActivity extends BaseSwipeActivity {
    private static final String TAG = "SimpleNoteEditActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.simpleNote_edt_noteContent)
    EditText edt_noteContent;
    @BindView(R.id.simpleNote_ll_actionbar)
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
    private long noteId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplenoteedit);
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
        loadData();

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
        saveToDB();
        View currentFocus = getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);

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
        noteId = getIntent().getLongExtra("noteId", -1);
        Log.e(TAG, "loadData: " + noteId);
        if (noteId > 0) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private void loadData() {

        List<SimpleNote> list = noteDao.queryBuilder().where(NoteDao.Properties.Id.eq(noteId)).list();
        if (!ListUtils.isEmpty(list)) {
            note = list.get(0);
            Observable.just(note)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<SimpleNote, Observable<?>>() {
                        @Override
                        public Observable<?> call(SimpleNote simpleNote) {
                            if (simpleNote.getContent() != null)
                                return Observable.just(simpleNote);
                            else {
                                if (simpleNote.getGuid() != null) {

                                    String guid = simpleNote.getGuid();
                                    helper.downloadNote(guid);
                                    Observable.just(0).repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                                        @Override
                                        public Observable<?> call(Observable<? extends Void> observable) {
                                            return observable.delay(1, TimeUnit.SECONDS);
                                        }
                                    }).doOnNext(__ -> loadData()).subscribe();
                                }
                            }
                            return null;
                        }
                    })
                    .filter(new Func1<Object, Boolean>() {
                        @Override
                        public Boolean call(Object o) {
                            if (o != null) {
                                SimpleNote simpleNote = (SimpleNote) o;
                                Observable
                                        .just(simpleNote)
                                        .subscribeOn(AndroidSchedulers.mainThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnNext(new Action1<SimpleNote>() {
                                            @Override
                                            public void call(SimpleNote simpleNote) {
                                                s_noteContent = simpleNote.getContent();
                                                if (!TextUtils.isEmpty(s_noteContent)) {
                                                    edt_noteContent.setText(s_noteContent);
                                                    edt_noteContent.setSelection(s_noteContent.length());
                                                }

                                            }
                                        }).subscribe();
                                return true;
                            }
                            return false;
                        }
                    }).subscribe();

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
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.progress(false, -1);
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
                                if (note.getGuid() != null) {
                                    helper.updateNote(this.note);
                                } else helper.createNote(note);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).subscribe(new Subscriber<Integer>() {

                    private MaterialDialog show;

                    @Override
                    public void onCompleted() {
                        if (show != null)
                            show.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        show = builder.show();
                    }
                });
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
                                LogUtils.e(TAG, "新建Note: " + note);
                                boolean connected = NetworkUtils.isConnected(SimpleNoteEditActivity.this);
                                if (connected) {
                                    Note eNote = helper.createNote(note);
                                    note = SimpleNote.toSimpleNote(eNote);
                                } else {
                                    note.setCreated(System.currentTimeMillis());
                                    note.setUpdated(System.currentTimeMillis());
                                }
                                long insert = noteDao.insert(note);
                                note.set_id(insert);
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
                    intent.putExtra("noteId", note.get_id());
                    startActivity(intent);
                } else {

                    finish();
                }
                break;
        }
    }

}
