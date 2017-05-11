package com.simple.lightnote.rx;

import android.os.StrictMode;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by homelink on 2016/7/21.
 */
public class OperatorTest {

    String strs[] = {"AAAA", "aaasss", "feggggg", "ggggg", "BBBB", "DDDD", "EEE", "CCLL", "sdsdsdd", "eiooosoos", "z.slsijfj"};
    Integer arrays[] = {1, 2, 4, 5, 6, 3, 33, 24, 53, 6, 5464, 75, 7, 5, 6290, 928};
    Observer subscriber = new Observer() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(Object value) {
            System.out.println(value);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    };

    //    @org.junit.TestUploadImage
    public void test() {
        Observable
                .just(1, "aaaa", "bbbb", 3, true, "sssbb", 'c', "AAA", "GGLLL")
//                .sample(1, TimeUnit.NANOSECONDS)
                .debounce(2, TimeUnit.NANOSECONDS)
                .subscribe(new Observer<Serializable>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onNext(Serializable serializable) {
                        System.out.println(serializable);
                    }
                });
    }

    @org.junit.Test
    public void concatMapTest() {


        Observable
                .just("aaaa", "bbbb", "sssbb", "AAA", "GGLLL")
               /* .concatMap(new Function<String, Observable<?>>() {
                    @Override
                    public Observable<?> call(String s) {
                        s+="concatMap+";
//                        s.length()>4
                        return Observable.from(new String[]{s});
                    }
                })*/
                .switchMap(new Function<String, Observable<?>>() {
                    @Override
                    public Observable<?> apply(String s) throws Exception {
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
                        System.out.println(o);
                    }
                });
    }

    @org.junit.Test
    public void scanTest() {
        Observable.just(1, 2, 3, 4, 5)
                .scan((sum, item) -> sum * item)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Sequence	completed.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Something	went	south!");
                    }

                    @Override
                    public void onNext(Integer item) {
                        System.out.println("item	is:	" + item);
                    }
                });
    }

    @org.junit.Test
    public void groupbyTest() {
        Observable.just(1, 2, 3, 4, 5)
                .groupBy(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(Integer integer) throws Exception {
                        return integer / 2 == 0;
                    }

                }).subscribe(new Consumer<GroupedObservable<Boolean, Integer>>() {
            @Override
            public void accept(GroupedObservable<Boolean, Integer> objectIntegerGroupedObservable) throws Exception {
                System.out.println(objectIntegerGroupedObservable);
                Boolean key = objectIntegerGroupedObservable.getKey();
                System.out.println(key);
                if (key) {



                    objectIntegerGroupedObservable.cache().map(new Function<Integer, String>() {

                        @Override
                        public String apply(Integer integer) throws Exception {
                            return integer + "大于";
                        }
                    }).subscribe(new Consumer<String>() {


                        @Override
                        public void accept(String s) {
                            System.out.println(s);
                        }
                    });
                } else {
                    objectIntegerGroupedObservable.cache().map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer integer) throws Exception {
                            return integer + "小于";
                        }

                    });
                }
/*

                    objectIntegerGroupedObservable.asObservable().forEach(new Consumer<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            System.out.println(integer);
                        }
                    });
*/
            }
        });
    }

    @org.junit.Test
    public void bufferTest() {
        Observable.just(strs).buffer(2, 2).subscribe(subscriber);
        /**buffer(1,1);
         * start
         print = [1]
         print = [2]
         print = [3]
         print = [4]
         print = [5]
         */
        /**buffer(1,3)
         * start
         print = [1]
         print = [4]

         */
        /**
         * buffer(2,2)
         * start
         print = [1, 2]
         print = [3, 4]
         print = [5]
         */
        Observable.fromArray(strs).buffer(1, 1, TimeUnit.NANOSECONDS).subscribe(subscriber);


    }

    @org.junit.Test
    public void castTest() {
        Observable.fromArray(arrays)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        System.out.println(integer);
                        return integer + "20000";
                    }

                })
                .cast(Integer.class)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer s) throws Exception {
                        System.out.println(s);
                        return s + "ov";
                    }


                }).subscribe(subscriber);

    }

    @org.junit.Test
    public void mergeTest() {
        Observable<String> from = Observable.fromArray(strs);
        Observable<Integer> intArrays = Observable.fromArray(arrays);
        Observable<? extends Serializable> merge = Observable.merge(from, intArrays);
        merge.subscribe(subscriber);
        /**
         * start
         print = AAAA
         print = aaasss
         print = feggggg
         print = ggggg
         print = BBBB
         print = DDDD
         ...
         print = 75
         print = 7
         print = 5
         print = 6290
         print = 928
         complete

         */

    }

    @org.junit.Test
    public void zipTest() {
        Observable<String> from = Observable.fromArray(strs);
        Observable<Integer> intArrays = Observable.fromArray(arrays);
        Observable.zip(from, intArrays, new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + "=" + integer;
            }

        }).subscribe(subscriber);
        /**
         * start
         print = AAAA=1
         print = aaasss=2
         print = feggggg=4
         print = ggggg=5
         print = BBBB=6
         print = DDDD=3
         print = EEE=33
         print = CCLL=24
         print = sdsdsdd=53
         print = eiooosoos=6
         print = z.slsijfj=5464
         complete

         */
    }

    /**
     * join 操作符
     * // TODO: 2016/7/22 未成功
     */
    @org.junit.Test
    public void joinTest() {
        Observable<String> from = Observable.fromArray(strs);
        Observable<Long> interval = Observable.fromArray(arrays).interval(100, TimeUnit.MILLISECONDS);

        from.join(interval, new Function<String, Observable<String>>() {
            @Override
            public Observable<String> apply(String s) throws Exception {
                return Observable.just(s);

            }
        }, new Function<Long, Observable<String>>() {
            @Override
            public Observable<String> apply(Long aLong) throws Exception {
                return Observable.timer(aLong, TimeUnit.MILLISECONDS).map(l -> String.valueOf(l));
            }

        }, new BiFunction<String, Long, Object>() {
            @Override
            public Object apply(String s, Long aLong) throws Exception {
                return s + "" + aLong;
            }

        }).subscribe(subscriber);
    }

    /**
     * combinLast
     * <p>
     * // TODO: 2016/7/22 未成功
     */
    @org.junit.Test
    public void combineTest() {
        Observable<String> from = Observable.fromArray(strs);
        Observable<Long> interval = Observable.fromArray(arrays).interval(1500, TimeUnit.MILLISECONDS);
        Observable.combineLatest(from, interval, new BiFunction<String, Long, Object>() {
            @Override
            public Object apply(String s, Long aLong) {
                return null;
            }
        }).observeOn(Schedulers.io())
                .subscribe(subscriber);
    }

    public void andTest() {
        Observable<String> from = Observable.fromArray(strs);
        Observable<Integer> intArrays = Observable.fromArray(arrays);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDialog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());


    }
}
