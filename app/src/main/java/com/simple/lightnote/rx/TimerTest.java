package com.simple.lightnote.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * Created by homelink on 2016/8/2.
 */
public class TimerTest {
    @org.junit.Test
    public void test() {
        testTimer();

    }

    private void testTimer() {
        System.out.println("wait ---->>>>" + System.currentTimeMillis());
        Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                //
                //.just(1).delay(2, TimeUnit.SECONDS)//
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("A1 [%s] XXX COMPLETE");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("something went wrong in TimingDemoFragment example");
                    }

                    @Override
                    public void onNext(Long number) {
                        System.out.println((String.format("A1 [%s]     NEXT", number)));
                    }
                });
        System.out.println("end ---->>>>" + System.currentTimeMillis());
    }

}
