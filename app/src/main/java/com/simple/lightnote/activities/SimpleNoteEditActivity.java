package com.simple.lightnote.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseSwipeActivity;
import com.simple.lightnote.constant.Constans;
import com.simple.lightnote.constant.SPConstans;
import com.simple.lightnote.db.DaoMaster;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.model.Note;
import com.simple.lightnote.util.SPUtil;
import com.simple.lightnote.utils.LogUtils;
import com.simple.lightnote.utils.MD5Utils;
import com.simple.lightnote.utils.ToastUtils;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * 编辑页面
 *
 * @author homelink
 */
public class SimpleNoteEditActivity extends BaseSwipeActivity {
    private static final String TAG = "SimpleNoteEditActivity";

    private LinearLayout ll_acitonBar;
    private EditText edt_noteContent;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private NoteDao noteDao;
    private String s_noteContent;
    private String md5;
    private Note note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        ViewGroup docurview = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
//
//        docurview.getChildAt(0).setBackgroundColor(getResources().getColor(android.R.color.transparent));


        setContentView(R.layout.activity_simplenoteedit);

        initView();
        initData();

    }

    private void initData() {
        String clickItem = getIntent().getStringExtra("clickItem");
        Log.e(TAG, "initData: " + clickItem);
        if (clickItem != null) {
            note = JSON.parseObject(clickItem, Note.class);
            String noteContent = note.getNoteContent();
            if (!TextUtils.isEmpty(noteContent)) {
                edt_noteContent.setText(noteContent);
                edt_noteContent.setSelection(noteContent.length());
            }

        } else {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        int showToolBar = SPUtil.getInstance(this).getInt(SPConstans.EDIT_TOOL_BAR, -1);
        setToolBarVisible(showToolBar);
    }

    private void initView() {
        ll_acitonBar = (LinearLayout) findViewById(R.id.simpleNote_ll_actionbar);
        edt_noteContent = (EditText) findViewById(R.id.simpleNote_edt_noteContent);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("编辑");
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        edt_noteContent = (EditText) findViewById(R.id.simpleNote_edt_noteContent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simplenoteedit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        saveToDB();
    }

    /**
     * 保存数据到数据库
     */
    private void saveToDB() {
        String trim = edt_noteContent.getText().toString().trim();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO 保存文件
        s_noteContent = edt_noteContent.getText().toString().trim();
        md5 = MD5Utils.MD5Encode(s_noteContent);
        if (note != null) {
            //更新
            if (!note.getNoteContent().trim().equals(s_noteContent)) {
                note.setNoteType(Constans.NoteType.normal);
                note.setNoteMd5(md5);
                note.setNoteTitle(note.getNoteTitle());
                note.setCreateTime(note.getCreateTime());
                note.setLastModifyTime(System.currentTimeMillis());
                note.setNoteContent(s_noteContent);
                LogUtils.e(TAG, "更新");
                LogUtils.e(TAG, note);
                insertAndUpdate();
            }
        } else {
            //新建
            if (!TextUtils.isEmpty(s_noteContent)) {
                note = new Note();
                note.setLastModifyTime(System.currentTimeMillis());
                note.setCreateTime(System.currentTimeMillis());
                note.setLastModifyTime(System.currentTimeMillis());
                note.setNoteType(Constans.NoteType.normal);
                note.setNoteMd5(md5);
                note.setNoteTitle(note.getNoteTitle());
                note.setNoteContent(s_noteContent);
                LogUtils.e(TAG, "新建");
                LogUtils.e(TAG, note);
                insertAndUpdate();
            }

        }


    }

    private void insertAndUpdate() {
        ToastUtils.showToast(SimpleNoteEditActivity.this, "正在保存");
        Observable
                .just(note)
                .filter(new Func1<Note, Boolean>() {
                    @Override
                    public Boolean call(Note note) {
                        return note != null;
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(SimpleNoteEditActivity.this, "lightnote", null);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        noteDao = daoSession.getNoteDao();
                        Long id = note.getId();
                        if (id == null) {
                            noteDao.insert(note);
                        } else {
                            noteDao.update(note);
                        }


                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Note>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.d(TAG, "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Note note) {
                        onCompleted();
                    }
                });


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
                SPUtil.getEditor(SimpleNoteEditActivity.this).putInt(SPConstans.EDIT_TOOL_BAR, flag_actionBar).commit();
                //TODO 修改点击和隐藏的状态
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
        String trim = edt_noteContent.getText().toString();

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
                String trim1 = edt_noteContent.getText().toString().trim();
                if (!TextUtils.isEmpty(trim1)) {
                    Intent intent = new Intent(this, NotePreViewActivity.class);
                    intent.putExtra("sourceType", 1);
                    intent.putExtra("filePath", trim1);
                    startActivity(intent);
                } else {

                    finish();
                }
                break;
        }
    }


}
