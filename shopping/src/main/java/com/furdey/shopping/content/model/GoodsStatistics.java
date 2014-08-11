package com.furdey.shopping.content.model;

/**
 * Buy statistics for goods inside the category
 * 
 * @author Stepan Furdey
 */
public class GoodsStatistics extends BaseModel {

	private static final long serialVersionUID = 1353393024496627615L;

	private Goods prevGood;
	private Goods nextGood;
	private Integer buyCount;

	public Goods getPrevGood() {
		return prevGood;
	}

	public void setPrevGood(Goods prevGood) {
		this.prevGood = prevGood;
	}

	public Goods getNextGood() {
		return nextGood;
	}

	public void setNextGood(Goods nextGood) {
		this.nextGood = nextGood;
	}

	public Integer getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}

}
