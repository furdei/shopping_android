package com.furdey.shopping.contentproviders;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.furdey.shopping.content.ContentUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BaseContentProvider<COLUMNS extends com.furdey.shopping.contentproviders.BaseContentProvider.Columns>
		extends ContentProvider {

    private static final String TAG = BaseContentProvider.class.getCanonicalName();

    public static interface Columns {
		String getDbName();
	}

    protected static final int ALL_RECORDS = 10;
    protected static final int PARTICULAR_RECORD = 20;

    protected static final String ERROR_UNKNOWN_URI = "Unknown URI: %s";
    protected static final String ERROR_SELECTION_SELECTION_ARGS_ARE_NOT_SUPPORTED = "Neither selection nor selectionArgs are supported, should be null";
    protected static final String ERROR_FAILED_TO_ADD_A_ROW = "Failed to add a row to %s (id = %d)";

    private static final String ID_COLUMN = "_id";
    private static final String DELETED_COLUMN = "deleted";
    private static final String CHANGED_COLUMN = "changed";
    private static final String STANDARD_COLUMN = "standard";

    protected static final int RUN_COUNT = 3;

	@Override
	public boolean onCreate() {
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
						columnAliases.add(column.toString());
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
								String.format(COLUMN_ALIAS_TEMPLATE, column.getDbName(), column.toString()));
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

    protected Cursor queryAll(UriMatcher uriMatcher, Uri uri, String tables, String[] projection,
                              String selection, String[] selectionArgs, String sortOrder) {
        return queryAll(uriMatcher, uri, tables, tables, projection, selection, selectionArgs,
                sortOrder);
    }

    protected Cursor queryAll(UriMatcher uriMatcher, Uri uri, String tables, String mainTable,
                              String[] projection, String selection, String[] selectionArgs,
                              String sortOrder) {
        int runCount = 0;

        while (true) {
            try {
                return queryInternal(uriMatcher, uri, tables, mainTable, projection,
                        selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                // just swallow it
                runCount++;
                if (runCount >= RUN_COUNT) {
                    return null;
                }
            }
        }
    }

    private Cursor queryInternal(UriMatcher uriMatcher, Uri uri, String tables, String mainTable,
                              String[] projection, String selection, String[] selectionArgs,
                              String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        //checkColumns(projection);

        // Set the table
        queryBuilder.setTables(tables);

        int uriType = uriMatcher.match(uri);
        switch (uriType) {
            case ALL_RECORDS:
                queryBuilder.appendWhere(mainTable + "." + DELETED_COLUMN + " IS NULL");
                break;
            case PARTICULAR_RECORD:
                // adding the ID to the original query
                queryBuilder.appendWhere(mainTable + "." + ID_COLUMN + "=" + getId(uri));
                break;
            default:
                throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_URI, uri));
        }

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext().getApplicationContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        queryBuilder.setProjectionMap(getColumnsMap());
        String queryStr = queryBuilder.buildQuery(projection, selection,
                null, null, sortOrder, null);
//        dumpPlan(database, queryStr, selectionArgs);
        Cursor cursor = database.rawQuery(queryStr, selectionArgs);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    protected Uri insert(String tableName, Uri uri, ContentValues values) {
        values.put(CHANGED_COLUMN, ContentUtils.getCurrentDateAndTime());
        values.put(STANDARD_COLUMN, ContentUtils.NONSTANDARD);

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext().getApplicationContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        long id = database.insert(tableName, null, values);

        if (id < 0)
            throw new IllegalStateException(String.format(ERROR_FAILED_TO_ADD_A_ROW, tableName, id));

        Uri inserted = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(inserted, null);
        return inserted;
    }

    protected int update(UriMatcher uriMatcher, Uri uri, String tableName, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        if (uriType != PARTICULAR_RECORD) {
            throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_URI, uri));
        }

        if (selection != null || selectionArgs != null) {
            throw new IllegalArgumentException(ERROR_SELECTION_SELECTION_ARGS_ARE_NOT_SUPPORTED);
        }

        values.put(CHANGED_COLUMN, ContentUtils.getCurrentDateAndTime());

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext().getApplicationContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int rowsAffected = database.update(tableName, values, ID_COLUMN + "=?",
                new String[]{getId(uri)});
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    protected int delete(UriMatcher uriMatcher, Uri uri, String tableName, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        if (uriType != PARTICULAR_RECORD) {
            throw new IllegalArgumentException(String.format(ERROR_UNKNOWN_URI, uri));
        }

        if (selection != null || selectionArgs != null) {
            throw new IllegalArgumentException(ERROR_SELECTION_SELECTION_ARGS_ARE_NOT_SUPPORTED);
        }

        ContentValues values = new ContentValues();
        values.put(DELETED_COLUMN, 1);
        values.put(CHANGED_COLUMN, ContentUtils.getCurrentDateAndTime());

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getContext().getApplicationContext());
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        int rowsAffected = database.update(tableName, values, ID_COLUMN + "=?",
                new String[]{getId(uri)});
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    protected void dump(Cursor cursor) {
        System.out.println("BaseContentProvider.dump");
        if (cursor == null) {
            System.out.println("Cursor is NULL");
            return;
        }
        int rowsCount = 0;

        while (cursor.moveToNext() && rowsCount++ < 20) {
            dumpRow(cursor);
        }

        cursor.moveToFirst();
    }

    protected void dumpRow(Cursor cursor) {
        System.out.print("ROW: ");
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            System.out.print(cursor.getColumnName(i) + ": " + cursor.getString(i) + " ");
        }
        System.out.print("\n");
    }

    protected void dumpPlan(SQLiteDatabase database, String sql, String[] selectionArgs) {
        Log.d(TAG, sql);
        Cursor cursor = database.rawQuery("EXPLAIN QUERY PLAN " + sql, selectionArgs);
        int columnsCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            System.out.print("BaseContentProvider.dumpPlan: ");
            for (int i = 0; i < columnsCount; i++) {
                System.out.print(cursor.getString(i) + " | ");
            }
            System.out.println();
        }
        cursor.close();
    }

    public static void addColumn(Context context, String tableName,
                                 String columnName, String columnType) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL(String.format("ALTER TABLE `%s` ADD COLUMN `%s` %s;",
                tableName, columnName, columnType));
    }
}
