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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
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


