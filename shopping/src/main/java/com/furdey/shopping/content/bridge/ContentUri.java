package com.furdey.shopping.content.bridge;

import android.net.Uri;

/**
 * @author Stepan Furdei
 */
public interface ContentUri {
    Uri withAppendedId(Uri contentUri, long id);
}
