package com.simple.lightnote.rx;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by homelink on 2016/7/25.
 */
public class SchedulerTest {
    Integer intArrays[] = {1, 2, 4, 5, 6, 3, 33, 24, 53, 6, 5464, 75, 7, 5, 6290, 928};

    @org.junit.Test
    public void test() {
        Observable.timer(5, TimeUnit.SECONDS).map(new Function<Long, String>() {
            @Override
            public String apply(Long aLong) throws Exception {
                return aLong + "call";
            }


        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
            }
        });
        Observable.interval(5, 5, TimeUnit.SECONDS).fromArray(intArrays).subscribe(i -> System.out.println("print=" + i));
        Observable.zip(Observable.interval(1000, TimeUnit.SECONDS), Observable.fromArray(intArrays), new BiFunction<Long, Integer, Integer>() {
            @Override
            public Integer apply(Long aLong, Integer integer) throws Exception {
                return integer;
            }
        }).subscribe(System.out::println);


        Observable.zip(Observable.interval(1000, TimeUnit.SECONDS), Observable.fromArray(intArrays), new BiFunction<Long, Integer, Integer>() {
            @Override
            public Integer apply(Long aLong, Integer integer) throws Exception {
                return integer;
            }
        }).subscribe(System.out::println);


        Observable.fromArray(intArrays).flatMap(new Function<Integer, Observable<?>>() {
            @Override
            public Observable<?> apply(Integer integer) throws Exception {
                System.out.println(integer);
                return Observable.just(integer);
            }
        }).zipWith(Observable.interval(3, TimeUnit.SECONDS), new BiFunction<Object, Long, String>() {
            @Override
            public String apply(Object o, Long aLong) throws Exception {
                System.out.println(o);
                return (String) o;
            }


        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
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
                .subscribe(i -> System.out.println(i));

        //后者:
        Observable.timer(2, TimeUnit.SECONDS)
                .flatMap(aLong -> Observable.range(0, 10))
                .subscribe(System.out::println);


        Observable.zip(
                Observable.interval(1, TimeUnit.SECONDS),
                Observable.range(0, 10), new BiFunction<Long, Integer, Integer>() {
                    @Override
                    public Integer apply(Long aLong, Integer integer) throws Exception {
                        return integer;
                    }

                })
                .subscribe(System.out::println);
        Observable.fromArray(intArrays).timer(3, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {

            }
        });
    }

    @org.junit.Test
    public void test3() {
        Observable.interval(3, TimeUnit.SECONDS).timeInterval().subscribe(System.out::println);
    }

    @org.junit.Test
    public void test4() {
        Observable//
                .interval(10, TimeUnit.SECONDS)//
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("something went wrong in TimingDemoFragment example");
                    }

                    @Override
                    public void onNext(Long number) {
                        System.out.println(String.format("B2 [%s]     NEXT", number));
                    }
                });
    }

    @org.junit.Test
    public void timerTest() {
        System.out.println("wait ---->>>>" + System.currentTimeMillis());
        Observable.timer(10, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                //
                //.just(1).delay(2, TimeUnit.SECONDS)//
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onComplete() {
                        System.out.println(String.format("A1 [%s] XXX COMPLETE"));
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("something went wrong in TimingDemoFragment example");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long number) {
                        System.out.println((String.format("A1 [%s]     NEXT", number)));
                    }
                });
        System.out.println("end ---->>>>" + System.currentTimeMillis());
    }
}
