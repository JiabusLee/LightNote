package com.simple.lightnote.rx;

import com.simple.lightnote.constant.SPConstans;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.test.NoteContentGenerator;
import com.simple.lightnote.util.SPUtil;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;

import org.greenrobot.greendao.Property;

import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by HERO on 2016/8/19.
 */
public class RxDemo {
    private static final String TAG = "RxDemo";

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

                      /*  long count = noteDao.count();

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
                        String orderBy = SPUtil.getString(this, SPConstans.ORDER_SORTBY, SPConstans.ORDER_SORTBY_DEFAULT);
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
                        subscriber.onNext(list);
                        */
                        LogUtils.e(TAG, "call3: " + Thread.currentThread());
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
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<SimpleNote> o) {
//                        noteAdapter.setList(o);
//                        noteAdapter.notifyDataSetChanged();
                        LogUtils.e(TAG, "call6: " + Thread.currentThread());
                        onCompleted();
                    }
                });

    }
}


