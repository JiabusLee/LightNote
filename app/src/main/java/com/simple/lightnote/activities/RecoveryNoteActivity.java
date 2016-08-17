package com.simple.lightnote.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.SearchView;

import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseSwipeActivity;
import com.simple.lightnote.activities.base.FileSelectActivity;
import com.simple.lightnote.adapter.RecycleViewNoteListAdapter;
import com.simple.lightnote.constant.SQLConstants;
import com.simple.lightnote.db.DaoMaster;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.interfaces.DefaultActionListener;
import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;
import com.simple.lightnote.utils.ToastUtils;
import com.simple.lightnote.view.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class RecoveryNoteActivity extends BaseSwipeActivity {
    private static final String TAG = "RecoveryNoteActivity";
    private ArrayList<SimpleNote> noteList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    private SwipeMenuRecyclerView mRecycleView;


    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private NoteDao noteDao;


    private RecycleViewNoteListAdapter noteAdapter;
    View contentView;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = View.inflate(this, R.layout.activity_main, null);
        setContentView(contentView);
        getWindow().getDecorView().setBackground(getDrawable(R.drawable.main_list_bg));
        initView();
        initListener();
        initData();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("搜索");
            case R.id.action_openFile:
                startActivity(new Intent(RecoveryNoteActivity.this, FileSelectActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NewApi")
    private void initView() {
        mRecycleView = (SwipeMenuRecyclerView) findViewById(R.id.listView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("垃圾篓");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
//        setSupportActionBar(mToolbar);
//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
//                this, drawerLayout, mToolbar, R.string.open_string,
//                R.string.close_string);
//        actionBarDrawerToggle.syncState();
//        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setEnabled(true);

        findViewById(R.id.fab).setVisibility(View.GONE);
        mRecycleView.setOpenInterpolator(new BounceInterpolator());
        mRecycleView.setCloseInterpolator(new BounceInterpolator());
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        //TODO 添加分隔线
//        mRecycleView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getListData();
                        // 执行刷新操作
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }, 3000);
                    }
                });

    }

    private void initData() {
        noteList = new ArrayList<SimpleNote>();

        //设置 RecycleView的显示方式
        noteAdapter = new RecycleViewNoteListAdapter(noteList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(llm);
        noteAdapter.setActionListener(new DefaultActionListener() {
            @Override
            public void onDelete(final SimpleNote note) {
                Snackbar snackbar = Snackbar.make(contentView,
                        "删除内容", Snackbar.LENGTH_LONG);
                snackbar.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
//                        ToastUtils.showToast(MainActivity.this, "关闭了。。。");
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);

                    }
                });
                snackbar.setAction("撒销", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //取消删除操作
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RecoveryNoteActivity.this, "lightnote", null);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        noteDao = daoSession.getNoteDao();
                        noteDao.update(note);
//                        noteAdapter.notifyItemInserted(note.getId());
                        ToastUtils.showToast(RecoveryNoteActivity.this, "取消删除");
                    }
                });
                snackbar.show();

            }
        });
        mRecycleView.setAdapter(noteAdapter);
        getListData();
    }

    private void getListData() {
        Observable.just(SQLConstants.noteState_deleted)
                .map(new Func1<Integer, List<SimpleNote>>() {
                    @Override
                    public List<SimpleNote> call(Integer noteState) {
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(RecoveryNoteActivity.this, "lightnote", null);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        noteDao = daoSession.getNoteDao();
                        Log.e(TAG, "call: " + noteDao.queryBuilder().list().toString());
                        List<SimpleNote> list = noteDao.queryBuilder().orderDesc(NoteDao.Properties.updateTime).list();
                        System.out.println(list);
                        LogUtils.e(TAG, "call3: " + Thread.currentThread());
                        return list;
                    }
                })

                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .filter(new Func1<List<SimpleNote>, Boolean>() {
                    @Override
                    public Boolean call(List<SimpleNote> notes) {
                        LogUtils.e(TAG, "call4: " + Thread.currentThread());
                        return !ListUtils.isEmpty(notes);
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<List<SimpleNote>>() {
                    @Override
                    public void call(List<SimpleNote> notes) {
                        LogUtils.e(TAG, "call5: " + Thread.currentThread());
                        System.out.println(notes);
                    }
                })

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SimpleNote>>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("over");
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<SimpleNote> o) {
                        noteAdapter.setList(o);
                        noteAdapter.notifyDataSetChanged();
                        LogUtils.e(TAG, "call6: " + Thread.currentThread());
                        onCompleted();
                    }
                });


    }

    private void initListener() {
        drawerLayout.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }


}
