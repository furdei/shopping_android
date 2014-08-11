package com.furdey.shopping.content.model;

/**
 * Buying statistics for goods categories
 * 
 * @author Stepan Furdey
 */
public class CategoriesStatistics extends BaseModel {

	private static final long serialVersionUID = -4055789827056889411L;

	private GoodsCategory prevCategory;
	private GoodsCategory nextCategory;
	private Integer buyCount;

	public GoodsCategory getPrevCategory() {
		return prevCategory;
	}

	public void setPrevCategory(GoodsCategory prevCategory) {
		this.prevCategory = prevCategory;
	}

	public GoodsCategory getNextCategory() {
		return nextCategory;
	}

	public void setNextCategory(GoodsCategory nextCategory) {
		this.nextCategory = nextCategory;
	}

	public Integer getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}
}
