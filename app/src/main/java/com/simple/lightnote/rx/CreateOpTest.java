package com.simple.lightnote.rx;

import android.util.Log;

import com.simple.lightnote.utils.LogUtils;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by homelink on 2016/7/27.
 */
public class CreateOpTest {
    private static final String TAG = "CreateOpTest";
    @org.junit.Test public void test(){
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
    @org.junit.Test public void test2(){
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
    }
}
