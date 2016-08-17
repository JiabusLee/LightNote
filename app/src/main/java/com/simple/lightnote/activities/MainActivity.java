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
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.Builder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.simple.lightnote.LightNoteApplication;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseActivity;
import com.simple.lightnote.activities.base.FileSelectActivity;
import com.simple.lightnote.adapter.RecycleViewNoteListAdapter;
import com.simple.lightnote.constant.SPConstans;
import com.simple.lightnote.db.DaoMaster;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.interfaces.DefaultActionListener;
import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.test.NoteContentGenerator;
import com.simple.lightnote.util.SPUtil;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;
import com.simple.lightnote.utils.ToastUtils;
import com.simple.lightnote.view.CommonDialog;
import com.simple.lightnote.view.SwipeMenuRecyclerView;

import org.greenrobot.greendao.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private NoteDao noteDao;
    private Cursor cursor;
    private RecycleViewNoteListAdapter noteAdapter;
    private long end;
    private long start;
    private CommonDialog commonDialog;
    private ProgressDialog dialog;

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
        getEverNoteList();
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
                    getDBlist();
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
                                        getEverNoteList();

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
/*        SimpleNote note = null;
        for (int i = 0; i < 50; i++) {
            note = new SimpleNote();
            String md5Encode = MD5Utils.MD5Encode(String.valueOf(System
                    .currentTimeMillis()) + i + "note");
            note.setNoteTitle(md5Encode);
            note.setNoteMd5(md5Encode);
            noteList.add(note);
        }*/
//		noteListAdapter = new NoteListAdapter(this, noteList);

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
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainActivity.this, "lightnote", null);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        noteDao = daoSession.getNoteDao();
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
    private void getEverNoteList() {
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            ToastUtils.showToast(this, "还没有绑定evernote");
            return;
        }


        Observable
                .create(new Observable.OnSubscribe<Notebook>() {
                    @Override
                    public void call(Subscriber<? super Notebook> subscriber) {

                        subscriber.onStart();
                        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

                        try {
//                            List<Notebook> notebooks = noteStoreClient.listNotebooks();
                            noteStoreClient.listNotebooksAsync(new EvernoteCallback<List<Notebook>>() {
                                @Override
                                public void onSuccess(List<Notebook> result) {
                                    for (Notebook note : result)
                                        subscriber.onNext(note);
                                    subscriber.onCompleted();
                                }

                                @Override
                                public void onException(Exception exception) {
                                    subscriber.unsubscribe();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }

                    }
                })

                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Notebook>() {
                               @Override
                               public void call(Notebook notebook) {
                                   {
                                       LogUtils.e(TAG, notebook.toString());
                                       NoteFilter noteFilter = new NoteFilter();
                                       noteFilter.setOrder(NoteSortOrder.UPDATED.getValue());

                                       try {
                                           EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient()
                                                   .findNotesAsync(noteFilter, 0, 20, new EvernoteCallback<NoteList>() {
                                                       @Override
                                                       public void onSuccess(NoteList result) {
                                                           List<com.evernote.edam.type.Note> notes = result.getNotes();

                                                           List<SimpleNote> simpleNotes = generateSimpleList(notes);

                                                           // TODO: 2016/8/17 generate SimpleNote
                                                           getContent(simpleNotes);
                                                           noteAdapter.setList(simpleNotes);
                                                           noteAdapter.notifyDataSetChanged();


                                                           for (com.evernote.edam.type.Note note : notes) {
                                                               String title = note.getTitle();
                                                               LogUtils.e(TAG, "onNext: title==> " + title);
//                                                               LogUtils.e(TAG, "onNext: note===>: " + note);

                                                               getNote(note.getGuid());
                                                           }
                                                       }

                                                       @Override
                                                       public void onException(Exception exception) {

                                                       }
                                                   });

                                       } catch (Exception e) {
                                           e.printStackTrace();
                                       }

                                   }
                               }
                           }

                );


    }

    private List<SimpleNote> generateSimpleList(List<Note> notes) {
        if (!ListUtils.isEmpty(notes)) {

            List<SimpleNote> noteList = new ArrayList<>();
            SimpleNote simpleNote;
            for (Note note : notes) {
                simpleNote = new SimpleNote();
                simpleNote.setContent(note.getContent());
                simpleNote.setActive(note.isSetActive());
                simpleNote.setGuid(note.getGuid());
                simpleNote.setContentHash(new String(note.getContentHash()));
                simpleNote.setUpdated(note.getCreated());
                simpleNote.setCreated(note.getCreated());
                simpleNote.setDeleted(note.getDeleted());
                simpleNote.setNotebookGuid(note.getNotebookGuid());
                simpleNote.setTitle(note.getTitle());
                simpleNote.setTagNames(note.getTagNames());
                simpleNote.setContentLength(note.getContentLength());
                simpleNote.setTagGuids(note.getTagGuids());
                noteList.add(simpleNote);
            }
            return noteList;
        }
        return null;

    }


    public void getContent(List<SimpleNote> notes) {
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        Observable.from(notes)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(note -> {
                    String noteContent = null;
                    try {
                        noteContent = noteStoreClient.getNoteContent(note.getGuid());
                    } catch (EDAMUserException e) {
                        e.printStackTrace();
                    } catch (EDAMSystemException e) {
                        e.printStackTrace();
                    } catch (EDAMNotFoundException e) {
                        e.printStackTrace();
                    } catch (TException e) {
                        e.printStackTrace();
                    }
                    note.setContent(noteContent);
                    return note;
                })
                .map(new Func1<SimpleNote, Object>() {
                    @Override
                    public Object call(SimpleNote note) {
                        LightNoteApplication application = (LightNoteApplication) getApplication();
                        boolean success = true;
                        application.getDaoSession().getNoteDao().update(note);
                        return success;
                    }

                })
                .subscribe(__ -> {
                    LogUtils.e(TAG, "getContent: over");
                });
    }


    /**
     * 获取指定笔记的id
     *
     * @param guid
     */
    public void getNoteContent(String guid) {
        Observable.just(guid)
                .subscribeOn(Schedulers.newThread())
                .map(s -> {
                    try {
                        return EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient().getNoteContent(s);
                    } catch (EDAMUserException e) {
                        e.printStackTrace();
                    } catch (EDAMSystemException e) {
                        e.printStackTrace();
                    } catch (EDAMNotFoundException e) {
                        e.printStackTrace();
                    } catch (TException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: over");
                        if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        dialog = new ProgressDialog(MainActivity.this);
                        dialog.setProgressStyle(R.style.MaterialDialog);
                        dialog.show();
                    }
                });
    }

    /**
     * 获取笔记
     *
     * @param guid
     */
    private void getNote(String guid) {
        Observable.interval(5, TimeUnit.SECONDS)
                .create(new Observable.OnSubscribe<com.evernote.edam.type.Note>() {
                    @Override
                    public void call(Subscriber<? super com.evernote.edam.type.Note> subscriber) {
                        LogUtils.e(TAG, "call: " + "getNote------->");
                        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
                        try {
                            com.evernote.edam.type.Note note = noteStoreClient.getNote(guid, true, false, false, false);
                            subscriber.onNext(note);
                        } catch (EDAMUserException e) {
                            e.printStackTrace();
                        } catch (EDAMSystemException e) {
                            e.printStackTrace();
                        } catch (TException e) {
                            e.printStackTrace();
                        } catch (EDAMNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(note -> {
                    System.out.println(note);
                    LogUtils.e(TAG, "getNote: " + note);
                });
    }

    /**
     * 获取数据库中的notlist
     */
    private void getDBlist() {
        Observable.
                create(new Observable.OnSubscribe<List<SimpleNote>>() {
                    @Override
                    public void call(Subscriber<? super List<SimpleNote>> subscriber) {
                        subscriber.onStart();
                        LogUtils.e(TAG, "call2: " + Thread.currentThread());
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainActivity.this, "lightnote", null);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        noteDao = daoSession.getNoteDao();
                        long count = noteDao.count();

                        if (count < 20) {
                            int sum = 0;
                            if (count < 10) {
                                sum = 10;
                            } else {
                                sum = 2;
                            }
                            for (int i = 0; i < sum; i++) {
                                db.execSQL("insert into note(noteTitle,noteContent,noteMd5,createTime,lastModifyTime,noteType) values (null,'" + NoteContentGenerator.getRandomIndex() + "','8385c78768d7952a42f29a267a6c0827', " + (System.currentTimeMillis() - (new Random().nextInt(10000000) + 10000000)) + ", " + System.currentTimeMillis() + ",'normal');");
                            }
                        }
                        String orderRule = "desc";
                        String orderBy = SPUtil.getString(MainActivity.this, SPConstans.ORDER_SORTBY, SPConstans.ORDER_SORTBY_DEFAULT);
                        Property order;
                        if (orderBy.equals(NoteDao.Properties.createTime.columnName)) {
                            order = NoteDao.Properties.createTime;
                            orderRule = "asc";
                        } else if (orderBy.equals(NoteDao.Properties.content.columnName)) {
                            order = NoteDao.Properties.content;
                            orderRule = "asc";
                        } else {
                            order = NoteDao.Properties.updateTime;
                        }
                        List<SimpleNote> list = noteDao.queryBuilder().orderCustom(order, orderRule).list();
                        LogUtils.e(TAG, "call3: " + Thread.currentThread());
                        subscriber.onNext(list);
                    }
                })
                .filter(new Func1<List<SimpleNote>, Boolean>() {
                    @Override
                    public Boolean call(List<SimpleNote> notes) {
                        LogUtils.e(TAG, "call4: " + Thread.currentThread());
                        return !ListUtils.isEmpty(notes);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .doOnNext(new Action1<List<SimpleNote>>() {
                    @Override
                    public void call(List<SimpleNote> notes) {
                        LogUtils.e(TAG, "call5: " + Thread.currentThread());
                        System.out.println(notes);
                    }
                })
                .subscribe(new Subscriber<List<SimpleNote>>() {
                    @Override
                    public void onCompleted() {

                        LogUtils.e(TAG, "onCompleted:  ");
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


}
