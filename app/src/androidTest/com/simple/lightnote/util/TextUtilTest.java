package com.simple.lightnote.util;

import org.junit.Test;

public class TextUtilTest {
	@Test
	public void main(){
		boolean orderListText = MyTextUtils.orderListText("1. aslkfasdf");
		System.out.println(orderListText);
	}
}
