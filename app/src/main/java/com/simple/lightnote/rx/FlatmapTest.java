package com.simple.lightnote.rx;

import com.simple.lightnote.rx.model.Student;

import org.junit.Test;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by homelink on 2016/8/23.
 */
public class FlatmapTest {


    @Test
    public void test() {
        Observable.from(DataUtil.getStudents())
                .flatMap(new Func1<Student, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(Student student) {
                        return Observable.just(student);
                    }
                });
    }


}
