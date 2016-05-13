package com.simple.lightnote.activities.base;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.simple.lightnote.R;
import com.simple.lightnote.adapter.FileSelectAdapter;
import com.simple.lightnote.constant.Constans;
import com.simple.lightnote.db.DaoMaster;
import com.simple.lightnote.db.DaoSession;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.utils.LogUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by homelink on 2016/3/8.
 */
public class FileSelectActivity extends BaseActivity {
    private static final String TAG = "FileSelectActivity";
    List<File> files;
    private RecyclerView recyclerView;
    private FileSelectAdapter fileSelectAdapter;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //File Directory
        setContentView(R.layout.activity_fileselect);
        initView();
        initData();


    }

    private void initData() {
        //被观察者
        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onStart();
                        sub.onNext("start observable");
                        sub.onCompleted();
                    }
                }
        );
        //订阅者/观察者
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                System.out.println(s);
                LogUtils.e(TAG, "onNext:" + s);
            }


            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onStart() {

                Log.e(TAG, "onStart: ");
            }

        };
        myObservable.subscribe(mySubscriber);

        Observable<String> myObservable2 = Observable.just("Hello, world!");
        myObservable2.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, "call: " + s);
            }
        });

        Observable.just("MMMMS").subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, "call: " + s);
            }
        });

        Observable.just("Hello Java").map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                return (s + ":HHH").hashCode();
            }
        }).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                Log.e(TAG, "call: " + integer);
                return (integer + Integer.MAX_VALUE) + "";
            }
        }).map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                Log.e(TAG, "call: " + s);
                return Integer.valueOf(s) % 10000;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "call: " + integer);
            }
        });
        String appRoot = "/lightnote/markdown";
        Observable.just(appRoot)
                .map(new Func1<String, List<File>>() {
                    @Override
                    public List<File> call(String s) {
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            File rootDir = Environment.getExternalStorageDirectory();
                            File lightNoteRoot = new File(rootDir + s);
                            if (!lightNoteRoot.exists()) {
                                lightNoteRoot.mkdirs();
                            } else {
                                File[] fileList = lightNoteRoot.listFiles();
                                String[] list = lightNoteRoot.list();
                                if (list != null && list.length > 0) {
                                    files = Arrays.asList(fileList);
                                }
                            }
                        }

                        return files;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<File>>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted:success");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<File> fileList) {
                        Log.e(TAG, "onNext: " + fileList.toString());
                        fileSelectAdapter.setmList(fileList);
                        fileSelectAdapter.notifyDataSetChanged();
                    }
                });

        Observable.from(new String[]{"xxx", "bbb", "CCC"}).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, "call: " + s);
            }
        });
        Observable.just("xxx").flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                return Observable.from(new String[]{"xxbx", "bbb", "CCC", "xxxb", "xxx"});
            }
        }).filter(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return s != null && s.contains("b");
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String string) {
                Log.e(TAG, "call: 符合条件的数据:" + string);
            }
        });

    }


    /**
     * 取得SD中的指定目录的文件列表
     */
    private void getFileList() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File rootDir = Environment.getExternalStorageDirectory();
            File lightNoteRoot = new File(rootDir + Constans.saveDir);
            if (!lightNoteRoot.exists()) {
                lightNoteRoot.mkdirs();
            } else {
                File[] fileList = lightNoteRoot.listFiles();
                String[] list = lightNoteRoot.list();
                files = Arrays.asList(fileList);
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.selectfile_recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        RecyclerView.Adapter adpater = getGenerateAdapter();
        recyclerView.setAdapter(adpater);
    }

    private RecyclerView.Adapter getGenerateAdapter() {
        fileSelectAdapter = new FileSelectAdapter(files);
        return fileSelectAdapter;
    }

    private void getListData() {

    }
}


