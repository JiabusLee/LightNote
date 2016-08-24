package com.simple.lightnote.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Builder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteCollectionCounts;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.simple.lightnote.LightNoteApplication;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseActivity;
import com.simple.lightnote.activities.base.FileSelectActivity;
import com.simple.lightnote.adapter.RecycleViewNoteListAdapter;
import com.simple.lightnote.constant.Constans;
import com.simple.lightnote.constant.SPConstans;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.EvernoteHelper;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.interfaces.DefaultActionListener;
import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.util.SPUtil;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;
import com.simple.lightnote.utils.ToastUtils;
import com.simple.lightnote.view.CommonDialog;
import com.simple.lightnote.view.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//import com.evernote.edam.type.Note;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    View contentView;
    //	private ListView mListView;
    private ArrayList<SimpleNote> noteList;
    private RecycleViewNoteListAdapter noteListAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DrawerLayout drawerLayout;
    private View drawerView;
    private Toolbar mToolbar;
    private SwipeMenuRecyclerView mRecycleView;
    private SQLiteDatabase db;
    private EditText editText;
    //    private DaoMaster daoMaster;
//    private DaoSession daoSession;
    private NoteDao noteDao;
    private Cursor cursor;
    private RecycleViewNoteListAdapter noteAdapter;
    private long end;
    private long start;
    private CommonDialog commonDialog;
    private ProgressDialog dialog;
    private EvernoteNoteStoreClient noteStoreClient;
    private String bookGuid;
    private LightNoteApplication app;
    private DaoSession daoSession;
    private EvernoteHelper helper;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        start = System.currentTimeMillis();
//		requestWindowFeature(Window.);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


//        getWindow().getDecorView().setBackgroundResource(R.drawable.main_list_bg);
//        getWindow().getDecorView().setBackground(getDrawable(R.drawable.main_list_bg));
        initView();
        initDrawerView();
        initListener();
        initData();
//		colorChange();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            // 很明显，这两货是新API才有的。
//			window.setStatusBarColor(colorBurn(R.color.colorPrimary));
//			window.setNavigationBarColor(colorBurn(R.color.colorPrimary));
        }
        app = (LightNoteApplication) getApplication();
        initDB();
        getEvernteBook();
    }

    private void initDB() {
        daoSession = app.getDaoSession();
        noteDao = daoSession.getNoteDao();
        helper = new EvernoteHelper(this, noteDao);
    }

    @Override
    protected void onResume() {
        super.onResume();
        end = System.currentTimeMillis();
        long l = end - start;
        LogUtils.e(TAG, "onResume: 应用的启动时间:" + l);
    }

    private void initDrawerView() {
        drawerView = findViewById(R.id.drawer_view);
        drawerView.findViewById(R.id.note_select_item_allNote).setOnClickListener(this);
        drawerView.findViewById(R.id.note_select_item_noteBook).setOnClickListener(this);
        drawerView.findViewById(R.id.note_select_item_recovery).setOnClickListener(this);
        drawerView.findViewById(R.id.note_select_item_set).setOnClickListener(this);
        drawerView.findViewById(R.id.note_select_item_noteLabel).setOnClickListener(this);

    }

    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("搜索");
                return true;
            case R.id.action_openFile:
                startActivity(new Intent(MainActivity.this, FileSelectActivity.class));
                return true;
            case R.id.action_options:
                commonDialog = new CommonDialog(this);
                View contentView = getDialogContent(Arrays.asList("标签", "标题", "多行"), R.id.action_options);

                //置Dialog的contentView
                commonDialog.setContentView(contentView);
                commonDialog.show();
                return true;
            case R.id.action_sort:
                commonDialog = new CommonDialog(this);
                contentView = getDialogContent(Arrays.asList("最近更新", "创建日期", "标题", "笔记本"), R.id.action_sort);

                //置Dialog的contentView
                commonDialog.setContentView(contentView);
                commonDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private View getDialogContent(List<String> list, final int type) {
        LinearLayout contentView = (LinearLayout) View.inflate(this, R.layout.dialog_container, null);
        contentView.findViewById(R.id.dialog_conent);

        ListView lv = new ListView(this);
        lv.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, list));

        ViewGroup.MarginLayoutParams layoutParams =
                new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lv.setLayoutParams(layoutParams);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (type == R.id.action_sort) {
                    switch (position) {
                        case 0:
                            SPUtil.getEditor(MainActivity.this).putString(SPConstans.ORDER_SORTBY, SPConstans.ORDER_SORTBY_LASTMODIFYTIME).apply();
                            break;
                        case 1:
                            SPUtil.getEditor(MainActivity.this).putString(SPConstans.ORDER_SORTBY, SPConstans.ORDER_SORTBY_CREATETIME).apply();

                            break;
                        case 2:
                            SPUtil.getEditor(MainActivity.this).putString(SPConstans.ORDER_SORTBY, SPConstans.ORDER_SORTBY_NOTECONTENT).apply();
                            break;
                    }
                }
                commonDialog.dismiss();


            }
        });
        contentView.addView(lv);
        if (type == R.id.action_options) {
            LinearLayout ll = new LinearLayout(this);
            ll.setLayoutParams(layoutParams);
            ll.setOrientation(LinearLayout.HORIZONTAL);

//            contentView.addView(ll);
        }

        return contentView;
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
//        mSwipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        mSwipeRefreshLayout.setEnabled(true);

        mRecycleView.setOpenInterpolator(new BounceInterpolator());
        mRecycleView.setCloseInterpolator(new BounceInterpolator());
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        //TODO 添加分隔线
//        mRecycleView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        Observable
                                .timer(3, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Long>() {
                                    @Override
                                    public void onCompleted() {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                        LogUtils.e(TAG, "onCompleted: ");
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Long aLong) {
                                        LogUtils.e(TAG, "onNext: " + "getEverNoteList");
                                        getEvernteBook();

                                    }

                                    @Override
                                    public void onStart() {
                                        super.onStart();
//                                        getDBlist();
                                        LogUtils.e(TAG, "onStart: ");
                                    }
                                });
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
        };
        fab.attachToRecyclerView(mRecycleView, listener);
        fab.setOnClickListener(this);
//        fab.setOnLongClickListener();
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

                        noteDao.update(note);
//                        noteAdapter.notifyItemInserted(note.getId());
                        ToastUtils.showToast(MainActivity.this, "取消删除");
                    }
                });
                snackbar.show();

            }
        });
//		mRecycleView.setLayoutManager(new GridLayoutManager(this,2));
//		mRecycleView.setLayoutManager(new StaggeredGridLayoutManager(2,OrientationHelper.VERTICAL));
        mRecycleView.setAdapter(noteAdapter);

//		mListView.setAdapter(noteListAdapter);

    }

    /**
     * 从印象笔记中拉取数据
     */
    private void getEvernteBook() {
        if (!EvernoteSession.getInstance().isLoggedIn()) {
//            ToastUtils.showToast(this, "还没有绑定evernote");
//            return;
            List<SimpleNote> list = noteDao.queryBuilder().build().list();
            if (!ListUtils.isEmpty(list)) {
                noteAdapter.setList(list);
                noteAdapter.notifyDataSetChanged();
            }
        } else {
            noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
            bookGuid = SPUtil.getString(MainActivity.this, Constans.DEFAULT_NOTEBOOK_GUID, null);

            if (bookGuid != null) {
                getNoteList(bookGuid);
            } else {
                Observable
                        .create((subscriber) -> {
                            try {
                                Notebook defaultNotebook = noteStoreClient.getDefaultNotebook();
                                bookGuid = defaultNotebook.getGuid();
                                SPUtil.saveString(MainActivity.this, Constans.DEFAULT_NOTEBOOK_GUID, bookGuid);
                                subscriber.onNext(bookGuid);
                            } catch (Exception e) {
                                e.printStackTrace();
                                subscriber.onError(e);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .subscribe(bookGuid -> getNoteList((String) bookGuid));

            }


        }

    }

    private void getNoteList(String bookGuid) {
        {
            NoteFilter noteFilter = new NoteFilter();
            noteFilter.setNotebookGuid(bookGuid);
            noteFilter.setOrder(NoteSortOrder.UPDATED.getValue());
            ExecutorService es = Executors.newFixedThreadPool(10);
            try {
                NoteCollectionCounts noteCounts = noteStoreClient.findNoteCounts(noteFilter, false);
                Integer noteCount = noteCounts.getNotebookCounts().get(bookGuid);

                NotesMetadataResultSpec notesMetadataResultSpec = new NotesMetadataResultSpec();
                notesMetadataResultSpec.setIncludeUpdated(true);
                NotesMetadataList notesMetadata = noteStoreClient.findNotesMetadata(noteFilter, 0, noteCount, notesMetadataResultSpec);

                List<NoteMetadata> notes = notesMetadata.getNotes();

                // TODO: 2016/8/24 sync note
                helper.sync(true, true, new SyncHandler());


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
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
        Intent intent;
        drawerLayout.closeDrawers();
        switch (v.getId()) {
            case R.id.note_select_item_allNote:
                Toast.makeText(MainActivity.this, "全部笔记", Toast.LENGTH_LONG).show();
                break;
            case R.id.note_select_item_noteBook:
                break;
            case R.id.note_select_item_recovery:
                intent = new Intent(this, RecoveryNoteActivity.class);
                startActivity(intent);

                break;
            case R.id.note_select_item_noteLabel:
                intent = new Intent(this, NoteLabelActivity.class);
                startActivity(intent);
                break;
            case R.id.note_select_item_set:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.fab:

                intent = new Intent(this, SimpleNoteEditActivity.class);
                startActivity(intent);
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
                if (Build.VERSION.SDK_INT >= 21) {
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
    @SuppressLint("HandlerLeak")
    class SyncHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EvernoteHelper.SYNC_START:
//                    findViewById(R.id.sync_progress).setVisibility(View.VISIBLE);
                    break;
                case EvernoteHelper.SYNC_END:
//                    findViewById(R.id.sync_progress).setVisibility(View.GONE);
                    List<SimpleNote> list = noteDao.queryBuilder().list();
                    noteAdapter.setList(list);
                    noteAdapter.notifyDataSetChanged();

                    break;
                default:
                    break;
            }
        }
    }
}
