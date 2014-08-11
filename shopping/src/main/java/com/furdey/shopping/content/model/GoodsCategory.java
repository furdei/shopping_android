package com.furdey.shopping.content.model;

/**
 * Goods categories dictionary
 * 
 * @author Stepan Furdey
 */
public class GoodsCategory extends BaseModel {

	private static final long serialVersionUID = -2033457871156046146L;

	private String name;
	private String descr;
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
