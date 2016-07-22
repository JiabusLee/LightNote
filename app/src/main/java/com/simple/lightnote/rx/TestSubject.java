package com.simple.lightnote.rx;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

/**
 * Subject
 * <p>
 * Created by homelink on 2016/7/21.
 */
public class TestSubject {
    @org.junit.Test
    public void testBehaviorSubject() {
        //本质上，BehaviorSubject是一个能够发射最近的那个它所观察的数据对象并且所有后续已订阅的数据每一个都订阅它的这样一个subjec
        BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.create(3);
//        behaviorSubject.onNext(5);
        behaviorSubject
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("integer:" + integer);
                    }
                });

    }

    @org.junit.Test
    public void testPublishSubject() {
        PublishSubject<String> stringPublishSubject = PublishSubject.create();
        Subscription subscriptionPrint = stringPublishSubject.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("Observable	completed");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Oh,no!Something	wrong	happened!");
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }
        });
        stringPublishSubject.onNext("Hello	World");
    }

    public void testReplaySubject() {
//        ReplaySubject会缓存它所订阅的所有数据并向任意一个订阅它的观察者重发
        ReplaySubject<Integer> replaySubject = ReplaySubject.create();

    }

}
