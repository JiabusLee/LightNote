package com.simple.lightnote.rx;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

/**
 * Created by homelink on 2016/7/22.
 */
public class Operator2Test {

    public void testRx() {

        Observable
                .empty()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Object, Observable<?>>() {
                    @Override
                    public Observable<?> call(Object o) {
                        return null;
                    }
                })
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });


        Observable
                .just(10, 233)
                .flatMap(new Func1<Integer, Observable<?>>() {
                    @Override
                    public Observable<?> call(Integer integer) {
                        return Observable.range(integer, 2);
                    }
                })
                .concatMap(new Func1<Object, Observable<?>>() {

                    @Override
                    public Observable<?> call(Object o) {
                        return null;
                    }
                })
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                    }
                });


        Observable
                .just(new int[]{1, 2, 5, 6, 4, 10, 22, 34, 55, 44})
                .groupBy(new Func1<int[], Integer>() {
                    @Override
                    public Integer call(int[] ints) {
                        for (int i : ints) {
                            if (i >= 10) return i;
                        }
                        return 0;
                    }
                })
                .subscribe(new Action1<GroupedObservable<Integer, int[]>>() {
                    @Override
                    public void call(GroupedObservable<Integer, int[]> integerGroupedObservable) {
                        System.out.println(integerGroupedObservable.getKey());
                    }
                });

/*
        Observable.just(1, 2, 3, 4, "1", "2", "3").groupBy(new Func1<Object, String>() {
            @Override
            public String call(Object o) {
                if (o instanceof Number) {
                    return "num";
                } else if (o instanceof String) {
                    return "string";
                }
                return "other";
            }
        }).subscribe(new Action1<GroupedObservable<String, Serializable>>() {
            @Override
            public void call(GroupedObservable<String, Serializable> stringSerializableGroupedObservable) {

            }
        })*/
    }
}
