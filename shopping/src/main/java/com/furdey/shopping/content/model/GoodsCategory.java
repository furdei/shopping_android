package com.furdey.shopping.content.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Goods categories dictionary
 * 
 * @author Stepan Furdey
 */
@DatabaseTable(tableName = GoodsCategory.TABLE_NAME)
public class GoodsCategory extends BaseModel {

	private static final long serialVersionUID = -2033457871156046146L;

	public static final String TABLE_NAME = "goods_categories";

	public static final String NAME_FIELD_NAME = "name";
	public static final String DESCR_FIELD_NAME = "descr";
	public static final String ICON_FIELD_NAME = "icon";

	@DatabaseField(canBeNull = false, dataType = DataType.STRING)
	private String name;

	@DatabaseField(canBeNull = true, dataType = DataType.STRING)
	private String descr;

	@DatabaseField(canBeNull = true, dataType = DataType.STRING)
	private String icon;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

}
