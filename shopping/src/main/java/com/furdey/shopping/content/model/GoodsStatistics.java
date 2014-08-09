package com.furdey.shopping.content.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Buy statistics for goods inside the category
 * 
 * @author Stepan Furdey
 */
@DatabaseTable(tableName = GoodsStatistics.TABLE_NAME)
public class GoodsStatistics extends BaseModel {

	private static final long serialVersionUID = 1353393024496627615L;
	public static final String TABLE_NAME = "goods_statistics";

	public static final String PREV_GOOD_FIELD_NAME = "prev_good_id";
	public static final String NEXT_GOOD_FIELD_NAME = "next_good_id";
	public static final String BUY_COUNT_FIELD_NAME = "buy_count";

	@DatabaseField(canBeNull = true, foreign = true, columnName = PREV_GOOD_FIELD_NAME)
	private Goods prevGood;

	@DatabaseField(canBeNull = false, foreign = true, columnName = NEXT_GOOD_FIELD_NAME)
	private Goods nextGood;

	@DatabaseField(canBeNull = false, columnName = BUY_COUNT_FIELD_NAME)
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
