package com.simple.lightnote.rx;

import com.simple.lightnote.rx.datasource.sample.Data;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 *   onStart
     doOnNext:201
     onNext301
     completed
 */

/**doOnNext 执行顺序
 * Created by homelink on 2016/8/3.
 */
public class DoOnNextTest {
    @Test
    public void test() {
        Observable.create(new Observable.OnSubscribe<Data>() {
            @Override
            public void call(Subscriber<? super Data> subscriber) {
                Data Data = new Data(201);
                subscriber.onNext(Data);
            }
        }).doOnNext(new Action1<Data>() {
            @Override
            public void call(Data Data) {
                System.out.println("doOnNext:"+Data.age);
                Data.age = 301;
            }
        }).subscribe(new Subscriber<Data>() {
            @Override
            public void onCompleted() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Data data) {
                System.out.println("onNext"+data.age);
                onCompleted();
            }

            @Override
            public void onStart() {
                super.onStart();
                System.out.println("onStart");

            }
        });
    }

}
