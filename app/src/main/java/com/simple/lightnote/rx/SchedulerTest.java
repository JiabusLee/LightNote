package com.simple.lightnote.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by homelink on 2016/7/25.
 */
public class SchedulerTest {
    Integer intArrays[] = {1, 2, 4, 5, 6, 3, 33, 24, 53, 6, 5464, 75, 7, 5, 6290, 928};

    @org.junit.Test
    public void test() {
        Observable.timer(5,TimeUnit.SECONDS).map(new Func1<Long, String>() {
            @Override
            public String call(Long aLong) {
                return aLong+"call";
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
        Observable.interval(5, 5, TimeUnit.SECONDS).from(intArrays).subscribe(i -> System.out.println("print=" + i));
        Observable.zip(Observable.interval(1000, TimeUnit.SECONDS), Observable.from(intArrays), new Func2<Long, Integer, Integer>() {
            @Override
            public Integer call(Long aLong, Integer integer) {
                return integer;
            }
        }).subscribe(System.out::println);


        Observable.zip(Observable.interval(1000, TimeUnit.SECONDS), Observable.from(intArrays), new Func2<Long, Integer, Integer>() {
            @Override
            public Integer call(Long aLong, Integer integer) {
                return integer;
            }
        }).subscribe(System.out::println);








        Observable.from(intArrays).flatMap(new Func1<Integer, Observable<?>>() {
            @Override
            public Observable<?> call(Integer integer) {
                System.out.println(integer);
                return Observable.just(integer);
            }
        }).zipWith(Observable.interval(3, TimeUnit.SECONDS), new Func2<Object, Long, String>() {
            @Override
            public String call(Object o, Long aLong) {
                System.out.println(o);
                return (String)o;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });

    }

    @org.junit.Test
    public void test1() {
        //前者:
        Observable.zip(
                Observable.interval(1, TimeUnit.SECONDS),
                Observable.range(0, 10), (aLong, integer) -> integer)
                .subscribe(i-> System.out.println(i));

        //后者:
        Observable.timer(2, TimeUnit.SECONDS)
                .flatMap(aLong -> Observable.range(0, 10))
                .subscribe(System.out::println);


        Observable.zip(
                Observable.interval(1, TimeUnit.SECONDS),
                Observable.range(0, 10), new Func2<Long, Integer, Integer>() {
                    @Override
                    public Integer call(Long aLong, Integer integer) {
                        return integer;
                    }
                })
                .subscribe(System.out::println);
        Observable.from(intArrays).timer(3,TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                System.out.println(aLong);
            }
        });
    }

    @org.junit.Test public void test3(){
        Observable.interval(3,TimeUnit.SECONDS).timeInterval().subscribe(System.out::println);
    }

    @org.junit.Test public void test4(){
        Observable//
                .interval(10, TimeUnit.SECONDS)//
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("something went wrong in TimingDemoFragment example");
                    }

                    @Override
                    public void onNext(Long number) {
                        System.out.println(String.format("B2 [%s]     NEXT",number));
                    }
                });
    }
    @org.junit.Test public void timerTest(){
        System.out.println("wait ---->>>>"+System.currentTimeMillis());
        Observable.timer(10, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                //
                //.just(1).delay(2, TimeUnit.SECONDS)//
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        System.out.println(String.format("A1 [%s] XXX COMPLETE"));
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
        System.out.println("end ---->>>>"+System.currentTimeMillis());
    }
}
