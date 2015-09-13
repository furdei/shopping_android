package com.furdey.shopping.test.content;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.furdey.shopping.content.bridge.CachingFactory;
import com.furdey.shopping.test.content.bridge.MockContentValuesFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Stepan Furdei
 */
public class BaseContentTest {

    private Uri baseUri;
    private ContentResolver contentResolver;
    private Context context;
    private MockContentValuesFactory mockContentValuesCreator;
    private CachingFactory<Uri> uriCachingFactory;

    public void setup() {
        baseUri = mock(Uri.class);
        contentResolver = mock(ContentResolver.class);

        context = mock(Context.class);
        when(context.getContentResolver()).thenReturn(contentResolver);

        mockContentValuesCreator = new MockContentValuesFactory();

        uriCachingFactory = new CachingFactory<Uri>() {
            @Override
            protected Uri createInstance() {
                return baseUri;
            }
        };
    }

    public Uri getBaseUri() {
        return baseUri;
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    public Context getContext() {
        return context;
    }

    public MockContentValuesFactory getMockContentValuesCreator() {
        return mockContentValuesCreator;
    }

    public CachingFactory<Uri> getUriCachingFactory() {
        return uriCachingFactory;
    }
}
