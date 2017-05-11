package com.simple.lightnote.rx;

import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


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
                create(new ObservableOnSubscribe<List<SimpleNote>>() {

                    @Override
                    public void subscribe(ObservableEmitter<List<SimpleNote>> e) throws Exception {

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

                .filter(new Predicate<List<SimpleNote>>() {
                    @Override
                    public boolean test(List<SimpleNote> simpleNotes) throws Exception {
                        LogUtils.e(TAG, "call4: " + Thread.currentThread());
                        return !ListUtils.isEmpty(simpleNotes);
                    }

                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .doOnNext(new Consumer<List<SimpleNote>>() {
                    @Override
                    public void accept(List<SimpleNote> simpleNotes) throws Exception {
                        LogUtils.e(TAG, "call5: " + Thread.currentThread());
                        System.out.println(simpleNotes);
                    }
                }).subscribe(new Observer<List<SimpleNote>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<SimpleNote> value) {
                LogUtils.e(TAG, "call6: " + Thread.currentThread());
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                LogUtils.e(TAG, "onCompleted:  ");
            }
        });
    }
}


