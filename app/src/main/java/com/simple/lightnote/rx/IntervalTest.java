package com.simple.lightnote.rx;

import com.simple.lightnote.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;


/**
 * Created by homelink on 2016/8/2.
 */
public class IntervalTest {
    private static final String TAG = "IntervalTest";

    @org.junit.Test
    public void test() {
        testInterval();

    }

    private void testInterval() {
        Integer intArrays[] = {1, 2, 4, 5, 6, 3, 33, 24, 53, 6, 5464, 75, 7, 5, 6290, 928};
        Observable.zip(Observable.interval(1, 3, TimeUnit.SECONDS), Observable.fromArray(intArrays), new BiFunction<Long, Integer, Integer>() {
            @Override
            public Integer apply(Long aLong, Integer integer) throws Exception {
                return integer;

            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onComplete() {
                LogUtils.e(TAG, "onCompleted ");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                LogUtils.e(TAG, "onNext: " + integer);

            }

            @Override
            public void onSubscribe(Disposable d) {
                
            }
        });
    }

}
