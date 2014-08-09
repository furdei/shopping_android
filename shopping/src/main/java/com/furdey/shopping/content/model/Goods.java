package com.furdey.shopping.content.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Goods dictionary
 *
 * @author Stepan Furdey
 */
@DatabaseTable(tableName = Goods.TABLE_NAME)
public class Goods extends BaseModel {
	
	private static final long serialVersionUID = -2552163512840704667L;
	
	public static final String TABLE_NAME = "goods";

	public static final String NAME_FIELD_NAME = "name";
	public static final String DEFAULTUNITS_FIELD_NAME = "defaultUnits_id";
	public static final String CATEGORY_FIELD_NAME = "category_id";

	@DatabaseField(canBeNull = false, dataType = DataType.STRING)
	private String name;
	   
	/**
	 * Units by default. It can be changed by the user
	 * for every single purchase.
	 */
	@DatabaseField(canBeNull = false, foreign = true)
	private Unit defaultUnits;

	@DatabaseField(canBeNull = false, foreign = true)
	private GoodsCategory category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Unit getDefaultUnits() {
		return defaultUnits;
	}

	public void setDefaultUnits(Unit defaultUnits) {
		this.defaultUnits = defaultUnits;
	}

	public GoodsCategory getCategory() {
		return category;
	}

	public void setCategory(GoodsCategory category) {
		this.category = category;
	}

}
