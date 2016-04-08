package com.simple.lightnote.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Builder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseActivity;
import com.simple.lightnote.activities.base.FileSelectActivity;
import com.simple.lightnote.adapter.RecycleViewNoteListAdapter;
import com.simple.lightnote.db.DaoMaster;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.model.Note;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;
import com.simple.lightnote.utils.MD5Utils;
import com.simple.lightnote.utils.ToastUtils;
import com.simple.lightnote.view.DividerItemDecoration;
import com.simple.lightnote.view.SwipeMenuRecyclerView;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    //	private ListView mListView;
    private ArrayList<Note> noteList;
    private RecycleViewNoteListAdapter noteListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private Toolbar mToolbar;
    private SwipeMenuRecyclerView mRecycleView;


    private SQLiteDatabase db;

    private EditText editText;

    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private NoteDao noteDao;

    private Cursor cursor;
    private RecycleViewNoteListAdapter noteAdapter;
    View contentView;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.);
        super.onCreate(savedInstanceState);
        contentView = View.inflate(this, R.layout.activity_main, null);
        setContentView(contentView);
        initView();
        initDrawerView();
        initListener();
        initData();
//		colorChange();
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            // 很明显，这两货是新API才有的。
//			window.setStatusBarColor(colorBurn(R.color.colorPrimary));
//			window.setNavigationBarColor(colorBurn(R.color.colorPrimary));
        }

    }

    private void initDrawerView() {
        drawerView = findViewById(R.id.drawer_view);
        TextView tv_drawer_allNote = (TextView) drawerView.findViewById(R.id.note_select_item_allNote);
        tv_drawer_allNote.setOnClickListener(this);
        drawerView.findViewById(R.id.note_select_item_noteBook).setOnClickListener(this);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ab_search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("搜索");
            case R.id.openFile:
                startActivity(new Intent(MainActivity.this, FileSelectActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("NewApi")
    private void initView() {
        mRecycleView = (SwipeMenuRecyclerView) findViewById(R.id.listView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("全部内容");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, mToolbar, R.string.open_string,
                R.string.close_string);
        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        mSwipeRefreshLayout.setEnabled(true);

        mRecycleView.setOpenInterpolator(new BounceInterpolator());
        mRecycleView.setCloseInterpolator(new BounceInterpolator());
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        //TODO 添加分隔线
        mRecycleView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


//          fab.attachToListView(mRecycleView, );
        ScrollDirectionListener listener = new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                LogUtils.d("ListViewFragment", "onScrollDown()");
            }

            @Override
            public void onScrollUp() {
                LogUtils.d("ListViewFragment", "onScrollUp()");
            }
        };/*, new AbsListView.OnScrollListener() {
              @Override
              public void onScrollStateChanged(AbsListView view, int scrollState) {
                  LogUtils.d("ListViewFragment", "onScrollStateChanged()");
              }

              @Override
              public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                  LogUtils.d("ListViewFragment", "onScroll()");
              }
          }*/
        fab.attachToRecyclerView(mRecycleView, listener);
    }

    private void initData() {
        noteList = new ArrayList<Note>();
        Note note = null;
        for (int i = 0; i < 50; i++) {
            note = new Note();
            String md5Encode = MD5Utils.MD5Encode(String.valueOf(System
                    .currentTimeMillis()) + i + "note");
            note.setNoteTitle(md5Encode);
            note.setNoteMd5(md5Encode);
            noteList.add(note);
        }
//		noteListAdapter = new NoteListAdapter(this, noteList);

        //设置 RecycleView的显示方式
        noteAdapter = new RecycleViewNoteListAdapter(noteList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(llm);
        noteAdapter.setActionListener(new ActionListener() {
            @Override
            public void onDelete(final Note note) {
                Snackbar snackbar = Snackbar.make(contentView,
                        "Welcome to AndroidHive", Snackbar.LENGTH_LONG);
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
                snackbar.setAction("取消", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //取消删除操作
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainActivity.this, "lightnote", null);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        noteDao = daoSession.getNoteDao();
                        noteDao.insert(note);
                        noteAdapter.notifyItemInserted(note.getId());
                        ToastUtils.showToast(MainActivity.this,"取消删除");
                    }
                });
                snackbar.show();

            }
        });
//		mRecycleView.setLayoutManager(new GridLayoutManager(this,2));
//		mRecycleView.setLayoutManager(new StaggeredGridLayoutManager(2,OrientationHelper.VERTICAL));
        mRecycleView.setAdapter(noteAdapter);

//		mListView.setAdapter(noteListAdapter);
        //添加分隔线
        mRecycleView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }

        });


        getListData();
    }

    private void getListData() {
        Observable.empty()
                .fromCallable(new Func0<SQLiteDatabase>() {
                    @Override
                    public SQLiteDatabase call() {

                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainActivity.this, "lightnote", null);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        noteDao = daoSession.getNoteDao();
                        String columnName = NoteDao.Properties.LastModifyTime.columnName;
                        String orderBy = columnName + " COLLATE LOCALIZED DESC";
                        cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(), null, null, null, null, orderBy);
                        if (cursor.getCount() <40) {
                            for (int i = 0; i < 40; i++) {
                                db.execSQL("insert into note(noteTitle,noteContent,noteMd5,createTime,lastModifyTime,noteType) values(null,'学生时代的" + i + "','8385c78768d7952a42f29a267a6c0827',1459495723877," + System.currentTimeMillis() + ",'normal')");
                            }

                        }
                        return db;
                    }
                })
                .map(new Func1<SQLiteDatabase, ArrayList<Note>>() {
                    @Override
                    public ArrayList<Note> call(SQLiteDatabase db) {
                        String columnName = NoteDao.Properties.LastModifyTime.columnName;
                        String orderBy = columnName + " COLLATE LOCALIZED DESC";
                        cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(), null, null, null, null, orderBy);
                        int count = cursor.getCount();
                        ArrayList<Note> noteList = new ArrayList<Note>();
                        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
                        {
                            noteList.add(noteDao.readEntity(cursor, 0));
                        }


                        db.close();

                        return noteList;
                    }
                })
                .filter(new Func1<ArrayList<Note>, Boolean>() {
                    @Override
                    public Boolean call(ArrayList<Note> notes) {
                        return !ListUtils.isEmpty(notes);
                    }
                })
                .doOnNext(new Action1<ArrayList<Note>>() {
                    @Override
                    public void call(ArrayList<Note> notes) {
                        System.out.println(notes);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<Note>>() {
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
                    public void onNext(ArrayList<Note> o) {
                        noteAdapter.setList(o);
                        noteAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.note_select_item_allNote:
                Toast.makeText(MainActivity.this, "全部笔记", Toast.LENGTH_LONG).show();
                break;
            case R.id.note_select_item_noteBook:
                break;
            default:
                break;
        }
    }


    /**
     * 界面颜色的更改
     */
    @SuppressLint("NewApi")
    private void colorChange() {
        // 用来提取颜色的Bitmap
        Bitmap drawingCache = mToolbar.getDrawingCache();

        // Palette的部分
        Builder builder = new Builder(drawingCache);

        builder.generate(new Palette.PaletteAsyncListener() {
            /**
             * 提取完之后的回调方法
             */
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrant = palette.getVibrantSwatch();
                /* 界面颜色UI统一性处理,看起来更Material一些 */
                // 其中状态栏、游标、底部导航栏的颜色需要加深一下，也可以不加，具体情况在代码之后说明
                mToolbar.setBackgroundColor(vibrant.getRgb());
                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    Window window = getWindow();
                    // 很明显，这两货是新API才有的。
                    window.setStatusBarColor(colorBurn(vibrant.getRgb()));
                    window.setNavigationBarColor(colorBurn(vibrant.getRgb()));
                }
            }
        });

    }

    /**
     * 颜色加深处理
     *
     * @param RGBValues RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
     *                  Android中我们一般使用它的16进制，
     *                  例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
     *                  red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
     *                  所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
     * @return
     */
    private int colorBurn(int RGBValues) {
        int alpha = RGBValues >> 24;
        int red = RGBValues >> 16 & 0xFF;
        int green = RGBValues >> 8 & 0xFF;
        int blue = RGBValues & 0xFF;
        red = (int) Math.floor(red * (1 - 0.1));
        green = (int) Math.floor(green * (1 - 0.1));
        blue = (int) Math.floor(blue * (1 - 0.1));
        return Color.rgb(red, green, blue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListData();
    }


    public interface ActionListener{
        void onDelete(Note note);

    }



}
