package com.simple.lightnote.rx;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by homelink on 2016/7/18.
 */
public class LiftTest {
    @org.junit.Test
  public  void test(){
        Observable.just(1,2).lift(new Observable.Operator<String, Integer>() {
            @Override
            public Subscriber<? super Integer> call(Subscriber<? super String> subscriber) {
                return new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onError(e);
                        }
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext("hello number + " + integer);
                        }
                    }
                };
            }
        });
    }
}
