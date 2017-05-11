package com.simple.lightnote.rx;

import com.simple.lightnote.rx.datasource.sample.Data;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * onStart
 * doOnNext:201
 * onNext301
 * completed
 */

/**
 * doOnNext 执行顺序
 * Created by homelink on 2016/8/3.
 */
public class DoOnNextTest {
    @Test
    public void test() {
        Observable.create(new ObservableOnSubscribe<Data>() {
            @Override
            public void subscribe(ObservableEmitter<Data> e) throws Exception {

                Data Data = new Data(201);
                e.onNext(Data);
            }

        }).doOnNext(new Consumer<Data>() {
            @Override
            public void accept(Data data) throws Exception {
                System.out.println("doOnNext:" + Data.age);
                Data.age = 301;
            }


        }).subscribe(new Observer<Data>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Data data) {
                System.out.println("onNext" + data.age);
                onComplete();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }

}
