package com.furdey.shopping.test.content.bridge;

import android.content.ContentValues;

import com.furdey.shopping.content.bridge.CachingFactory;

import static org.mockito.Mockito.mock;

/**
 * @author Stepan Furdei
 */
public class MockContentValuesFactory extends CachingFactory<ContentValues> {

    @Override
    protected ContentValues createInstance() {
        return mock(ContentValues.class);
    }

}
