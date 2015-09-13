package com.furdey.shopping.content.bridge;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author Stepan Furdei
 */
public interface ContentResolverClient {

    Cursor query(ContentResolver contentResolver, String[] projection, String selection,
                 String[] selectionArgs, String sortOrder);
    Uri insert(ContentResolver contentResolver, ContentValues values);
    int update(ContentResolver contentResolver, ContentValues values, String where,
               String[] selectionArgs);
    int delete(ContentResolver contentResolver, String where, String[] selectionArgs);

}
