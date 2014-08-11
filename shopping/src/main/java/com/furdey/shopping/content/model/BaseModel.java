package com.furdey.shopping.content.model;

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

	private Long id;
	private Date changed;
	private Date deleted;
	private Boolean standard;
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
