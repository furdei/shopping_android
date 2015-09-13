package com.furdey.shopping.test.content.bridge;

import android.net.Uri;

import com.furdey.shopping.content.bridge.ContentUri;

/**
 * @author Stepan Furdei
 */
public class MockContentUri implements ContentUri {
    @Override
    public Uri withAppendedId(Uri contentUri, long id) {
        return contentUri;
    }
}
