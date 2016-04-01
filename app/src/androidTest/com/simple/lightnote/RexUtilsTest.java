package com.simple.lightnote;

import com.simple.lightnote.util.TagCons;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * Created by homelink on 2016/3/16.
 */
public class RexUtilsTest {

    @Test
    public void test(){
        String str="** asterisks** 会被转成strong**double asterisks** 会被转成strong**double ** 会被转成strong";
        String[] split = Pattern.compile(TagCons.strong).split(str);
        System.out.println(split);
    }

}
