package com.simple.lightnote.rx;


import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by homelink on 2016/7/18.
 */
public class LiftTest {
    @org.junit.Test
    public void test() {

        Observable.just(1, 2).lift(new ObservableOperator<String, Integer>() {
            @Override
            public Observer<? super Integer> apply(Observer<? super String> observer) throws Exception {
                return new Observer<Integer>() {

                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.disposable = d;
                    }

                    @Override
                    public void onComplete() {


                        if (!disposable.isDisposed()) {
                            onComplete();
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        if (!disposable.isDisposed()) {
                            onError(e);
                        }
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (!disposable.isDisposed()) {
                            observer.onNext("hello number + " + integer);
                        }
                    }
                };
            }


        });
    }
}
