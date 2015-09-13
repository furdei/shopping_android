package com.furdey.shopping.content.bridge;

import android.content.ContentValues;

/**
 * @author Stepan Furdei
 */
public class ContentValuesFactory implements Factory<ContentValues> {
    @Override
    public ContentValues getInstance() {
        return new ContentValues();
    }
}
