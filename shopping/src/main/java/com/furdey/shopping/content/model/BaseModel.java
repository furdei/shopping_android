package com.furdey.shopping.content.model;

import com.furdey.shopping.utils.DateUtils;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

/**
 * A <code>BaseModel</code> class takes care of filling <code>changed</code>
 * field. It is base class for every model class.
 * 
 * @author Stepan Furdey
 */
public class BaseModel implements Serializable {

	private static final long serialVersionUID = 6213003123327201673L;

	public static final String ID_FIELD_NAME = "_id";
	public static final String CHANGED_FIELD_NAME = "changed";
	public static final String DELETED_FIELD_NAME = "deleted";
	public static final String STANDARD_FIELD_NAME = "standard";
	public static final String SYNCHRONIZEDTM_FIELD_NAME = "synchronizedtm";

	@DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
	private Long id;

	@DatabaseField(canBeNull = false, dataType = DataType.DATE, format = DateUtils.DATETIME_FORMAT)
	private Date changed;

	@DatabaseField(canBeNull = true, dataType = DataType.DATE, format = DateUtils.DATETIME_FORMAT)
	private Date deleted;

	@DatabaseField(canBeNull = false, dataType = DataType.BOOLEAN_OBJ)
	private Boolean standard;

	@DatabaseField(canBeNull = true, dataType = DataType.DATE, format = DateUtils.DATETIME_FORMAT)
	private Date synchronizedtm;

	public BaseModel() {
		this.changed = new Date();
		this.standard = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getChanged() {
		return changed;
	}

	public void setChanged(Date changed) {
		this.changed = changed;
	}

	public Date getDeleted() {
		return deleted;
	}

	public void setDeleted(Date deleted) {
		this.deleted = deleted;
	}

	public Boolean getStandard() {
		return standard;
	}

	public void setStandard(Boolean standard) {
		this.standard = standard;
	}

	public Date getSynchronizedtm() {
		return synchronizedtm;
	}

	public void setSynchronizedtm(Date synchronizedtm) {
		this.synchronizedtm = synchronizedtm;
	}
}
