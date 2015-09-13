package com.furdey.shopping.content.bridge;

import android.content.ContentUris;
import android.net.Uri;

/**
 * @author Stepan Furdei
 */
public class ContentUriImpl implements ContentUri {
    @Override
    public Uri withAppendedId(Uri contentUri, long id) {
        return ContentUris.withAppendedId(contentUri, id);
    }
}
