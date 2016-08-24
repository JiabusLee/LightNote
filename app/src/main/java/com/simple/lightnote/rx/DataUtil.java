package com.simple.lightnote.rx;

import com.simple.lightnote.rx.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by homelink on 2016/8/23.
 */
public class DataUtil {
    static Integer intArrays[] = {1, 2, 4, 5, 6, 3, 33, 24, 53, 6, 5464, 75, 7, 5, 6290, 928};


    public static List<Student> getStudents() {
        Student stu = null;
        List<Student> stuList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            stu = new Student();

            stu.id = i + 1;
            stu.age = 15 + new Random().nextInt(10) + 16;
            stu.name = "stu" + (stu.id);
            stu.score = new Random().nextInt(50) + 50;
            stuList.add(stu);

        }
        return stuList;
    }

}
