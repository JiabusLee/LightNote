package com.simple.lightnote.rx;

import com.simple.lightnote.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;

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
        Observable.zip(Observable.interval(1, 3, TimeUnit.SECONDS), Observable.from(intArrays), new Func2<Long, Integer, Integer>() {
            @Override
            public Integer call(Long aLong, Integer integer) {
                return integer;
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
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
            public void onStart() {
                super.onStart();
                LogUtils.e(TAG, "onStart: ");
            }
        });
    }

}
