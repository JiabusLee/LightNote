package com.simple.lightnote.rx;

import org.junit.Test;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by homelink on 2016/8/4.
 */
public class RxUtil {

    public static Observable.Transformer<String, Integer> mainAsync() {
        return new Observable.Transformer<String, Integer>() {
            @Override
            public Observable<Integer> call(Observable<String> o) {

                return o.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(x -> Integer.valueOf(x));
            }
        };
    }

    @Test
    public void test() {
        Observable.just("12345").compose(RxUtil.mainAsync()).subscribe(s -> System.out.println(s));
    }
}
