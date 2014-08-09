package com.furdey.shopping.content.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Units for goods amounts
 *
 * @author Stepan Furdey
 */
@DatabaseTable(tableName = Unit.TABLE_NAME)
public class Unit extends BaseModel {

	private static final long serialVersionUID = 8864463748913992613L;
	
	public static final String TABLE_NAME = "units";
	
	public static final String NAME_FIELD_NAME = "name";
	public static final String DESCR_FIELD_NAME = "descr";
	public static final String DECIMALS_FIELD_NAME = "decimals";
	public static final String UNITTYPE_FIELD_NAME = "unitType";
	public static final String ISDEFAULT_FIELD_NAME = "isDefault";
	
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
	
	@DatabaseField(canBeNull = false, dataType = DataType.STRING)
	private String name;

	@DatabaseField(canBeNull = true, dataType = DataType.STRING)
	private String descr;

	@DatabaseField(canBeNull = false, dataType = DataType.INTEGER_OBJ)
	private Integer decimals;
	
	@DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING)
	private UnitType unitType;
	
	@DatabaseField(canBeNull = false, dataType = DataType.BOOLEAN_OBJ)
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
