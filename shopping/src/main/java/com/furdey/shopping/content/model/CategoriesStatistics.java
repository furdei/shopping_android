package com.furdey.shopping.content.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Buy statistics for goods categories
 * 
 * @author Stepan Furdey
 */
@DatabaseTable(tableName = CategoriesStatistics.TABLE_NAME)
public class CategoriesStatistics extends BaseModel {

	private static final long serialVersionUID = -4055789827056889411L;
	public static final String TABLE_NAME = "categories_statistics";

	public static final String PREV_CATEGORY_FIELD_NAME = "prev_category_id";
	public static final String NEXT_CATEGORY_FIELD_NAME = "next_category_id";
	public static final String BUY_COUNT_FIELD_NAME = "buy_count";

	@DatabaseField(canBeNull = true, foreign = true, columnName = PREV_CATEGORY_FIELD_NAME)
	private GoodsCategory prevCategory;

	@DatabaseField(canBeNull = false, foreign = true, columnName = NEXT_CATEGORY_FIELD_NAME)
	private GoodsCategory nextCategory;

	@DatabaseField(canBeNull = false, columnName = BUY_COUNT_FIELD_NAME)
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
