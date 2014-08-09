package com.furdey.shopping.content.model;

import java.math.BigDecimal;
import java.util.Date;

import com.furdey.shopping.utils.DateUtils;

/**
 * Purchases are the main objects in the app. They are shown on the start page.
 * 
 * @author Stepan Furdey
 */
// @DatabaseTable(tableName = Purchase.TABLE_NAME)
public class Purchase extends BaseModel {

	private static final long serialVersionUID = -7522102470310694852L;

	// public static final String TABLE_NAME = "purchases";
	//
	// public static final String COUNT_FIELD_NAME = "count";
	// public static final String DESCR_FIELD_NAME = "descr";
	// public static final String GOODS_FIELD_NAME = "good_id";
	// public static final String UNITS_FIELD_NAME = "units_id";
	// public static final String STATE_FIELD_NAME = "state";
	// public static final String STRDATE_FIELD_NAME = "strdate";
	// public static final String FINDATE_FIELD_NAME = "findate";
	// public static final String SORTORDER_FIELD_NAME = "sortorder";

	public enum PurchaseState {
		ENTERED(0), ACCEPTED(1);

		private final int value;

		private PurchaseState(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	};

	// @DatabaseField(canBeNull = false, dataType = DataType.BIG_DECIMAL_NUMERIC)
	private BigDecimal count;

	// @DatabaseField(canBeNull = true, dataType = DataType.STRING)
	private String descr;

	// @DatabaseField(canBeNull = false, foreign = true)
	private Goods goods;

	/**
	 * Every good has it's default units, but the user can select different units
	 * per each purchase.
	 */
	// @DatabaseField(canBeNull = false, foreign = true)
	private Unit units;

	// @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING)
	private PurchaseState state;

	// @DatabaseField(canBeNull = false, dataType = DataType.DATE, format =
	// BaseDao.DATE_FORMAT)
	private Date strdate;

	// @DatabaseField(canBeNull = false, dataType = DataType.DATE, format =
	// BaseDao.DATE_FORMAT)
	private Date findate;

	// @DatabaseField(canBeNull = true, dataType = DataType.INTEGER)
	private int sortorder;

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public Unit getUnits() {
		return units;
	}

	public void setUnits(Unit units) {
		this.units = units;
	}

	public PurchaseState getState() {
		return state;
	}

	public void setState(PurchaseState state) {
		this.state = state;
	}

	public Date getStrdate() {
		return strdate;
	}

	public void setStrdate(Date strdate) {
		this.strdate = DateUtils.getDateWoTime(strdate);
	}

	public Date getFindate() {
		return findate;
	}

	public void setFindate(Date findate) {
		this.findate = DateUtils.getDateWoTime(findate);
	}

	public int getSortorder() {
		return sortorder;
	}

	public void setSortorder(int sortorder) {
		this.sortorder = sortorder;
	}

}
