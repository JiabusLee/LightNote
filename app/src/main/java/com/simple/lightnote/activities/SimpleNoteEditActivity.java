package com.simple.lightnote.activities;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.simple.lightnote.db.NoteSqliteOpenHelper;
import com.simple.lightnote.model.Note;
import com.simple.lightnote.util.SharePreferenceUtil;
import com.simple.lightnote.utils.LogUtils;
import com.simple.lightnote.utils.MD5Utils;
import com.simple.lightnote.utils.ToastUtils;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 编辑页面
 *
 * @author homelink
 */
public class SimpleNoteEditActivity extends BaseSwipeActivity {
    private static final String TAG = "SimpleNoteEditActivity";

    private EditText edt_noteContent;
    private LinearLayout ll_acitonBar;
    private EditText edt_content;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private NoteDao noteDao;
    private String s_noteContent;
    private String md5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simplenoteedit);

        initView();
        initData();

    }

    private void initData() {
        String clickItem = getIntent().getStringExtra("clickItem");
        Note note = JSON.parseObject(clickItem, Note.class);
        String noteContent = note.getNoteContent();
        if (!TextUtils.isEmpty(noteContent)) {
            edt_content.setText(noteContent);
            edt_content.setSelection(noteContent.length());
        }

        int showToolBar = SharePreferenceUtil.getInstance(this).getInt(SPConstans.EDIT_TOOL_BAR, -1);
        setToolBarVisible(showToolBar);
    }

    private void initView() {
        ll_acitonBar = (LinearLayout) findViewById(R.id.simpleNote_ll_actionbar);
        edt_content = (EditText) findViewById(R.id.simpleNote_edt_noteContent);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("编辑");
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edt_noteContent = (EditText) findViewById(R.id.simpleNote_edt_noteContent);

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
        String trim = edt_content.getText().toString().trim();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO 保存文件
        s_noteContent = edt_noteContent.getText().toString().trim();
        md5 = MD5Utils.MD5Encode(s_noteContent);
        Note note = null;
        if (!TextUtils.isEmpty(s_noteContent)) {


            SQLiteOpenHelper instance = NoteSqliteOpenHelper.getInstance(this, "lightnote", 1000);
            SQLiteDatabase writableDatabase = instance.getWritableDatabase();

            writableDatabase.beginTransaction();
            String sql = "insert into note(noteTitle,noteContent,noteMd5,createTime,lastModifyTime,noteType) " +
                    "values(null,'" + s_noteContent + "','" + md5 + "',"
                    + System.currentTimeMillis() + "," + System.currentTimeMillis() + ",'" + Constans.NoteType.normal + "')";
            writableDatabase.execSQL(sql);
            writableDatabase.endTransaction();

            note = new Note();
            note.setCreateTime(System.currentTimeMillis());
            note.setLastModifyTime(System.currentTimeMillis());
            note.setNoteContent(s_noteContent);
            note.setNoteType(Constans.NoteType.normal);
            note.setNoteMd5(md5);
            note.setNoteTitle("");
            LogUtils.e(TAG, note);
        }


        Observable
                .just(note)
                .filter(new Func1<Note, Boolean>() {
                    @Override
                    public Boolean call(Note note) {
                        return note != null;
                    }
                })
                .map(new Func1<Note, Long>() {
                    @Override
                    public Long call(Note note) {
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(SimpleNoteEditActivity.this, "lightnote", null);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        noteDao = daoSession.getNoteDao();
                        return noteDao.insert(note);

                    }
                })

                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted:");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long l) {

                        Log.e(TAG, "onNext:" + l);
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
                SharePreferenceUtil.getEditor(SimpleNoteEditActivity.this).putInt(SPConstans.EDIT_TOOL_BAR, flag_actionBar).commit();
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


}
