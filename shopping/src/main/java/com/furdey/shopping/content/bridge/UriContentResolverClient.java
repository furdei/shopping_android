package com.furdey.shopping.content.bridge;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author Stepan Furdei
 */
public class UriContentResolverClient implements ContentResolverClient {

    private final Uri uri;

    public UriContentResolverClient(Uri uri) {
        this.uri = uri;
    }

    @Override
    public Cursor query(ContentResolver contentResolver, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Uri insert(ContentResolver contentResolver, ContentValues values) {
        return contentResolver.insert(uri, values);
    }

    @Override
    public int update(ContentResolver contentResolver, ContentValues values, String where,
                      String[] selectionArgs) {
        return contentResolver.update(uri, values, where, selectionArgs);
    }

    @Override
    public int delete(ContentResolver contentResolver, String where, String[] selectionArgs) {
        return contentResolver.delete(uri, where, selectionArgs);
    }
}
