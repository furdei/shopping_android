package com.furdey.shopping.content.model;

/**
 * Units for goods amounts
 *
 * @author Stepan Furdey
 */
public class Unit extends BaseModel {

	private static final long serialVersionUID = 8864463748913992613L;
	
	public enum UnitType {
		MASS(0), DISTANCE(1), SQUARE(2), VOLUME(3), COUNT(4);
		
		private final int value;
		private UnitType(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	};
	
	private String name;
	private String descr;
	private Integer decimals;
	private UnitType unitType;
	private Boolean isDefault;
	
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

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

}
