package com.simple.lightnote.model;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;

public class JsonUtils {
	private static Goods good;

	public static void main(String[] args) {
		ArrayList<Goods> list=new ArrayList<Goods>();
		
		for (int i = 0; i < 5; i++) {
			good = new Goods();
			good.setGoodsId("ff80808150134e2d0150136db70c0005" + (5+i)% 3);
			good.setGoodsNum(2 + (5+i) % 3);
			good.setGoodsName("立白洗洁精" +  (5+i) % 3);
			good.setUnitPrice(15 +  (5+i) % 3);
			list.add(good);
		}
		System.out.println(JSON.toJSONString(list));
	}
	
	
	
	
	
	
	
	
}
