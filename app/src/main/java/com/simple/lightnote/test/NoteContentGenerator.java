package com.simple.lightnote.test;

import java.util.Random;

/**
 * Created by Lenovo on 2016/4/10.
 */
public class NoteContentGenerator {
    public static final String defaultContent = "哎呦不错哦";
    static Random random = new Random();

    public static String getRandomIndex() {

        int i = random.nextInt(1000) + 1;

        return NoteContentSource.source[i % NoteContentSource.source.length];

    }


}
