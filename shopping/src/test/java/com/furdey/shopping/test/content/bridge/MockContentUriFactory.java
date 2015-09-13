package com.furdey.shopping.test.content.bridge;

import com.furdey.shopping.content.bridge.CachingFactory;
import com.furdey.shopping.content.bridge.ContentUri;

/**
 * @author Stepan Furdei
 */
public class MockContentUriFactory extends CachingFactory<ContentUri> {
    @Override
    protected ContentUri createInstance() {
        return new MockContentUri();
    }
}
