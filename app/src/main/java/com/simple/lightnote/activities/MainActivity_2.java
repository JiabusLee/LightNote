package com.simple.lightnote.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.thrift.TException;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.simple.lightnote.R;
import com.simple.lightnote.activities.base.BaseActivity;
import com.simple.lightnote.activities.base.FileSelectActivity;
import com.simple.lightnote.adapter.RecycleViewNoteListAdapter;
import com.simple.lightnote.constant.SPConstans;
import com.simple.lightnote.constant.SQLConstants;
import com.simple.lightnote.db.DaoMaster;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.interfaces.DefaultActionListener;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity_2 extends BaseActivity {
    private static final String TAG = "MainActivity";


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.listView)
    SwipeMenuRecyclerView listView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.note_select_item_allNote)
    TextView noteSelectItemAllNote;
    @Bind(R.id.note_select_item_noteBook)
    TextView noteSelectItemNoteBook;
    @Bind(R.id.note_select_item_noteLabel)
    TextView noteSelectItemNoteLabel;
    @Bind(R.id.note_select_item_recovery)
    TextView noteSelectItemRecovery;
    @Bind(R.id.note_select_item_set)
    TextView noteSelectItemSet;
    @Bind(R.id.note_select_item)
    RelativeLayout noteSelectItem;
    @Bind(R.id.drawer_view)
    LinearLayout drawerView;
    @Bind(R.id.drawer)
    DrawerLayout drawer;
    //	private ListView mListView;
    View contentView;


    private EditText editText;


    private ArrayList<Note> noteList;
    private RecycleViewNoteListAdapter noteListAdapter;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private NoteDao noteDao;
    private RecycleViewNoteListAdapter noteAdapter;
    private long end;
    private long start;
    private CommonDialog commonDialog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        start = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        getWindow().getDecorView().setBackgroundResource(R.drawable.main_list_bg);
        initView();
        initListener();
        initData();
//		colorChange();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            // 很明显，这两货是新API才有的。
//			window.setStatusBarColor(colorBurn(R.color.colorPrimary));
//			window.setNavigationBarColor(colorBurn(R.color.colorPrimary));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        end = System.currentTimeMillis();
        long l = end - start;
        LogUtils.e(TAG, "onResume: 应用的启动时间:" + l);
        getEverNoteList();
    }


    @OnClick({R.id.note_select_item_allNote,
            R.id.note_select_item_noteBook,
            R.id.note_select_item_recovery,
            R.id.note_select_item_set,
            R.id.note_select_item_noteLabel})
    public void onDrawableClick(View view) {

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
                startActivity(new Intent(MainActivity_2.this, FileSelectActivity.class));
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
                            SPUtil.getEditor(MainActivity_2.this).putString(SPConstans.ORDER_SORTBY, SPConstans.ORDER_SORTBY_LASTMODIFYTIME).apply();
                            break;
                        case 1:
                            SPUtil.getEditor(MainActivity_2.this).putString(SPConstans.ORDER_SORTBY, SPConstans.ORDER_SORTBY_CREATETIME).apply();

                            break;
                        case 2:
                            SPUtil.getEditor(MainActivity_2.this).putString(SPConstans.ORDER_SORTBY, SPConstans.ORDER_SORTBY_NOTECONTENT).apply();
                            break;
                    }
                    getDBList();
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
        toolbar.setTitle("全部内容");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.open_string,
                R.string.close_string);
        actionBarDrawerToggle.syncState();
        drawer.setDrawerListener(actionBarDrawerToggle);
        swipeRefreshLayout.setEnabled(true);

        listView.setOpenInterpolator(new BounceInterpolator());
        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setItemAnimator(new DefaultItemAnimator());
        //TODO 添加分隔线
//        mRecycleView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
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
        fab.attachToRecyclerView(listView, listener);
    }

    private void initData() {
        noteList = new ArrayList<Note>();
        //设置 RecycleView的显示方式
        noteAdapter = new RecycleViewNoteListAdapter(noteList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(llm);
        noteAdapter.setActionListener(new DefaultActionListener() {
            @Override
            public void onDelete(final Note note) {
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
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainActivity_2.this, "lightnote", null);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        daoMaster = new DaoMaster(db);
                        daoSession = daoMaster.newSession();
                        noteDao = daoSession.getNoteDao();
                        note.setNoteState(SQLConstants.noteState_normal);
                        noteDao.update(note);
//                        noteAdapter.notifyItemInserted(note.getId());
                        ToastUtils.showToast(MainActivity_2.this, "取消删除");
                    }
                });
                snackbar.show();

            }
        });
        listView.setAdapter(noteAdapter);
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
                .subscribe(new Subscriber<Notebook>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Notebook notebook) {
                        LogUtils.e(TAG, notebook.toString());
                        NoteFilter noteFilter = new NoteFilter();
//                        noteFilter.setNotebookGuid(notebook.getGuid());
                        noteFilter.setOrder(NoteSortOrder.UPDATED.getValue());

                        try {
                            NoteList notes = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient().findNotes(noteFilter, 0, 20);
                            List<com.evernote.edam.type.Note> notes1 = notes.getNotes();


                            for (com.evernote.edam.type.Note note : notes1) {
                                String title = note.getTitle();
                                LogUtils.e(TAG, "onNext: title==> " + title);
                            }
                        } catch (EDAMUserException e) {
                            e.printStackTrace();
                        } catch (EDAMSystemException e) {
                            e.printStackTrace();
                        } catch (EDAMNotFoundException e) {
                            e.printStackTrace();
                        } catch (TException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    private void getNoteLight(String guid) {
        Observable.interval(5, TimeUnit.SECONDS)
                .create(new Observable.OnSubscribe<com.evernote.edam.type.Note>() {
                    @Override
                    public void call(Subscriber<? super com.evernote.edam.type.Note> subscriber) {
                        LogUtils.e(TAG, "call: " + "getNoteLight------->");
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
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(note -> {
                    System.out.println(note);
                    LogUtils.e(TAG, "getNoteLight: " + note);
                });
    }

    /**
     * 加载数据库中的notlist
     */
    private void getDBList() {
        Observable.
                create(new Observable.OnSubscribe<List<Note>>() {
                    @Override
                    public void call(Subscriber<? super List<Note>> subscriber) {
                        subscriber.onStart();
                        LogUtils.e(TAG, "call2: " + Thread.currentThread());
                        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainActivity_2.this, "lightnote", null);
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
                        String orderBy = SPUtil.getString(MainActivity_2.this, SPConstans.ORDER_SORTBY, SPConstans.ORDER_SORTBY_DEFAULT);
                        Property order;
                        if (orderBy.equals(NoteDao.Properties.CreateTime.columnName)) {
                            order = NoteDao.Properties.CreateTime;
                            orderRule = "asc";
                        } else if (orderBy.equals(NoteDao.Properties.NoteContent.columnName)) {
                            order = NoteDao.Properties.NoteContent;
                            orderRule = "asc";
                        } else {
                            order = NoteDao.Properties.LastModifyTime;
                        }
                        List<Note> list = noteDao.queryBuilder().where(NoteDao.Properties.NoteState.eq(SQLConstants.noteState_normal)).orderCustom(order, orderRule).list();
                        LogUtils.e(TAG, "call3: " + Thread.currentThread());
                        subscriber.onNext(list);
                    }
                })
                .filter(new Func1<List<Note>, Boolean>() {
                    @Override
                    public Boolean call(List<Note> notes) {
                        LogUtils.e(TAG, "call4: " + Thread.currentThread());
                        return !ListUtils.isEmpty(notes);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .doOnNext(new Action1<List<Note>>() {
                    @Override
                    public void call(List<Note> notes) {
                        LogUtils.e(TAG, "call5: " + Thread.currentThread());
                        System.out.println(notes);
                    }
                })
                .subscribe(new Subscriber<List<Note>>() {
                    @Override
                    public void onCompleted() {

                        LogUtils.e(TAG, "onCompleted:  ");
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Note> o) {
                        noteAdapter.setList(o);
                        noteAdapter.notifyDataSetChanged();
                        LogUtils.e(TAG, "call6: " + Thread.currentThread());
                        onCompleted();
                    }
                });

    }

    private void initListener() {
        drawer.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }



    @OnClick({R.id.toolbar, R.id.listView, R.id.swipeRefreshLayout, R.id.fab, R.id.note_select_item_allNote,
            R.id.note_select_item_noteBook, R.id.note_select_item_noteLabel, R.id.note_select_item_recovery,
            R.id.note_select_item_set, R.id.note_select_item, R.id.drawer_view, R.id.drawer})
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        drawer.closeDrawers();
        switch (v.getId()) {
            case R.id.note_select_item_allNote:
                Toast.makeText(MainActivity_2.this, "全部笔记", Toast.LENGTH_LONG).show();
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

            case R.id.toolbar:
                break;
            case R.id.listView:
                break;
            case R.id.swipeRefreshLayout:
                break;

            case R.id.note_select_item:
                break;
            case R.id.drawer_view:
                break;
            case R.id.drawer:
                break;
        }
    }
}
