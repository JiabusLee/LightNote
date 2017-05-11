package com.simple.lightnote.rx;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by homelink on 2016/7/22.
 */
public class Operator2Test {

    public void testRx() {

        Observable
                .empty()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return null;
                    }
                })

                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

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
                .flatMap(new Function<Integer, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Integer integer) throws Exception {
                        return Observable.range(integer, 2);
                    }
                }).concatMap(new Function<Object, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Object o) throws Exception {
                return null;
            }
        })

                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                });


        Observable
                .just(new int[]{1, 2, 5, 6, 4, 10, 22, 34, 55, 44})
                .groupBy(new Function<int[], Integer>() {
                    @Override
                    public Integer apply(int[] ints) throws Exception {
                        for (int i : ints) {
                            if (i >= 10) return i;
                        }
                        return 0;
                    }
                })

                .subscribe(new Consumer<GroupedObservable<Integer, int[]>>() {
                    @Override
                    public void accept(GroupedObservable<Integer, int[]> integerGroupedObservable) throws Exception {
                        System.out.println(integerGroupedObservable.getKey());
                    }


                });

/*
        Observable.just(1, 2, 3, 4, "1", "2", "3").groupBy(new Function<Object, String>() {
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
