package com.simple.lightnote.rx;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

/**
 * Subject
 * <p>
 * Created by homelink on 2016/7/21.
 */
public class SubjectTest {
    @org.junit.Test
    public void testBehaviorSubject() {
        //本质上，BehaviorSubject是一个能够发射最近的那个它所观察的数据对象并且所有后续已订阅的数据每一个都订阅它的这样一个subjec
        BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.createDefault(3);
//        behaviorSubject.onNext(5);
        behaviorSubject
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("integer:" + integer);
                    }


                });

    }

    @org.junit.Test
    public void testPublishSubject() {
        PublishSubject<String> stringPublishSubject = PublishSubject.create();



        stringPublishSubject.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {
                System.out.println(value);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Oh,no!Something	wrong	happened!");
            }

            @Override
            public void onComplete() {
                System.out.println("Observable	completed");
            }
        });

        stringPublishSubject.onNext("Hello	World");
    }

    public void testReplaySubject() {
//        ReplaySubject会缓存它所订阅的所有数据并向任意一个订阅它的观察者重发
        ReplaySubject<Integer> replaySubject = ReplaySubject.create();

    }

}
