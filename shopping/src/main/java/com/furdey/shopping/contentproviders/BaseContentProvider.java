package com.furdey.shopping.contentproviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.net.Uri;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BaseContentProvider<COLUMNS extends com.furdey.shopping.contentproviders.BaseContentProvider.Columns>
		extends ContentProvider {

	public static interface Columns {
		String name();

		String getDbName();
	}

	private DatabaseHelper dbHelper;

	final protected DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	private void setDbHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	@Override
	public boolean onCreate() {
		setDbHelper(new DatabaseHelper(getContext()));
		return true;
	}

	final protected void checkColumns(String[] projection) {
		if (projection != null) {
			Set<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			// check if all columns which are requested are available
			if (!getAvailableColumnAliases().containsAll(requestedColumns)) {
				requestedColumns.removeAll(getAvailableColumnAliases());
				;
				throw new IllegalArgumentException("Unknown columns in projection: "
						+ Arrays.toString(requestedColumns.toArray(new String[requestedColumns.size()]))
						+ ". Available columns: "
						+ Arrays.toString(getAvailableColumnAliases().toArray(
								new String[getAvailableColumnAliases().size()])));
			}
		}
	}

	private Set<String> columnAliases;

	private Set<String> getAvailableColumnAliases() {
		if (columnAliases == null) {
			synchronized (this) {
				if (columnAliases == null) {
					columnAliases = new HashSet<String>();
					for (COLUMNS column : getColumns()) {
						columnAliases.add(column.name());
					}
				}
			}
		}

		return columnAliases;
	}

	private Map<String, String> columnsMap;
	private static final String COLUMN_ALIAS_TEMPLATE = "%s AS %s";

	final protected Map<String, String> getColumnsMap() {
		if (columnsMap == null) {
			synchronized (this) {
				if (columnsMap == null) {
					columnsMap = new HashMap<String, String>();

					for (COLUMNS column : getColumns()) {
						columnsMap.put(column.toString(),
								String.format(COLUMN_ALIAS_TEMPLATE, column.getDbName(), column.name()));
					}
				}
			}
		}

		return columnsMap;
	}

	protected COLUMNS[] getColumns() {
		return null;
	}

	/**
	 * @param uri
	 * @return ID if uri contains a valid ID or throws IllegalArgumentException
	 */
	final protected String getId(Uri uri) {
		return Long.toString(ContentUris.parseId(uri));
	}

}
