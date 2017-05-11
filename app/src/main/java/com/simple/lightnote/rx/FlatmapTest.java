package com.simple.lightnote.rx;

import com.simple.lightnote.rx.model.Student;

import org.junit.Test;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * Created by homelink on 2016/8/23.
 */
public class FlatmapTest {


    @Test
    public void test() {

        Observable.fromArray(DataUtil.getStudents())
                .flatMap(new Function<List<Student>, ObservableSource<Student>>() {
                    @Override
                    public ObservableSource<Student> apply(List<Student> students) throws Exception {
                        for (Student student : students)
                            return Observable.just(student);
                        return Observable.empty();
                    }
                }).filter(new Predicate<Student>() {
            @Override
            public boolean test(Student student) throws Exception {
                return student!=null;
            }
        }).subscribe(new Consumer<Student>() {
            @Override
            public void accept(Student student) throws Exception {
                System.out.println(student.toString());
            }
        });

        Observable.fromArray(DataUtil.getStudents())
                .subscribe(new Consumer<List<Student>>() {
                    @Override
                    public void accept(List<Student> students) throws Exception {

                    }
                });
    }

}
