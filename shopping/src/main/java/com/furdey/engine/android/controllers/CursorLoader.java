package com.furdey.engine.android.controllers;

import java.sql.SQLException;

import android.database.Cursor;

/**
 * Abstract interface for some action that loads
 * and returns a cursor
 *
 * @author Stepan Furdey
 */
public interface CursorLoader {
	public Cursor loadCursor() throws SQLException;
}
