package com.simple.lightnote.rx;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by homelink on 2016/8/4.
 */
public class RxUtil {

    public static ObservableTransformer<String, Integer> mainAsync() {
        return new ObservableTransformer<String, Integer>() {
            @Override
            public ObservableSource<Integer> apply(Observable<String> upstream) {
                return upstream.subscribeOn(Schedulers.io())
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
