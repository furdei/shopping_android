package com.furdey.shopping.content.bridge;

/**
 * @author Stepan Furdei
 */
public class ContentUriFactory extends CachingFactory<ContentUri> {
    @Override
    protected ContentUri createInstance() {
        return new ContentUriImpl();
    }
}
