package com.furdey.shopping.content.model;

import com.furdey.shopping.content.ContentUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Purchases are the main objects in the app. They are shown on the start page.
 * 
 * @author Stepan Furdey
 */
public class Purchase extends BaseModel {

    public static final float MINIMAL_VIEWABLE_COUNT = 0.001f;

    private static final long serialVersionUID = -7522102470310694852L;

	public enum PurchaseState {
		ENTERED, ACCEPTED;
	};

	private BigDecimal count;
	private String descr;
	private Goods goods;

	/**
	 * Every good has it's default units, but the user can select different units
	 * per each purchase.
	 */
	private Unit units;

	private PurchaseState state;
	private Date strdate;
	private Date findate;
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
		this.strdate = ContentUtils.getDateWoTime(strdate);
	}

	public Date getFindate() {
		return findate;
	}

	public void setFindate(Date findate) {
		this.findate = ContentUtils.getDateWoTime(findate);
	}

	public int getSortorder() {
		return sortorder;
	}

	public void setSortorder(int sortorder) {
		this.sortorder = sortorder;
	}

}
