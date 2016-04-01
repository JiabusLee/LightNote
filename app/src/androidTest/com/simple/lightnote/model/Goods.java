package com.simple.lightnote.model;
/**
 * 保洁物品
 * @author homelink
 *
 */
public class Goods {
	private String goodsId;
	private int goodsNum;
	private String goodsName;
	private double unitPrice;
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public int getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
	@Override
	public String toString() {
		return "Goods [goodsId=" + goodsId + ", goodsNum=" + goodsNum
				+ ", goodsName=" + goodsName + ", unitPrice=" + unitPrice + "]";
	}
	
}
