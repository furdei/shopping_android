package com.furdey.shopping.content.model;

/**
 * Goods dictionary
 *
 * @author Stepan Furdey
 */
public class Goods extends BaseModel {
	
	private static final long serialVersionUID = -2552163512840704667L;
	
	private String name;
	   
	/**
	 * Units by default. It can be changed by the user
	 * for every single purchase.
	 */
	private Unit defaultUnits;

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
