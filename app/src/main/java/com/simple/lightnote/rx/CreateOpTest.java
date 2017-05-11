package com.simple.lightnote.rx;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * Created by homelink on 2016/7/27.
 */
public class CreateOpTest {
    private static final String TAG = "CreateOpTest";

    @org.junit.Test
    public void test() {
        Observable.fromArray(new String[]{"xxx", "bbb", "CCC"}).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "call: " + s);
            }
        });


        Observable.just("xxx").flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(String s) throws Exception {
                return Observable.fromArray(new String[]{"xxbx", "bbb", "CCC", "xxxb", "xxx"});
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return s != null && s.contains("b");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "call: 符合条件的数据:" + s);
            }
        });

    }

    @org.junit.Test
    public void test2() {
        //被观察者
        Observable<String> myObservable = Observable.create(
                new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        e.onNext("start observable");
                    }
                }
        );


        //订阅者/观察者
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
//                System.out.println(s);
//                LogUtils.e(TAG, "onNext:" + s);
                System.out.println("onNext" + s);
            }


            @Override
            public void onComplete() {
//                Log.e(TAG, "onCompleted: ");
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onSubscribe(Subscription s) {

            }
        };

        myObservable.subscribe();

        Observable<String> myObservable2 = Observable.just("Hello, world!");


        myObservable2.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "call: " + s);
            }


        });

        Observable.just("MMMMS").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "call: " + s);
            }


        });

        Observable.just("Hello Java").map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return (s + ":HHH").hashCode();
            }


        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                Log.e(TAG, "call: " + integer);
                return (integer + Integer.MAX_VALUE) + "";
            }


        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                Log.e(TAG, "call: " + s);
                return Integer.valueOf(s) % 10000;
            }

        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "call: " + integer);
            }

        });
    }
}
