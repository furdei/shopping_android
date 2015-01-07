package com.furdey.shopping.content;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by Furdey on 07.01.2015.
 */
public class SearchUtils {

    public interface ObjectsProvider {
        Cursor getObjects(Context context, String[] projection, String selection);
//        void addColumn(Context context, String columnName);
    }

    public interface ObjectsSaver {
        void saveObject(Context context, Cursor c);
    }

    public static boolean isSearchFieldExist(Context context, String searchField,
                                                 ObjectsProvider objectsProvider) {
        String[] projection = new String[]{searchField};
        boolean fieldExists = true;
        try {
            Cursor c = objectsProvider.getObjects(context, projection, null);

            if (c != null) {
                c.close();
            } else {
                fieldExists = false;
            }
        } catch (Exception e) {
            fieldExists = false;
        }

        return fieldExists;
    }
//
//    public static Cursor addSearchField(Context context, String searchField,
//                                                 String[] rebuildProjection,
//                                                 ObjectsProvider objectsProvider) {
//        // there is no NAME_LOWER field yet, so let's add one
//        objectsProvider.addColumn(context, searchField);
//
//        // fill name_lower field with lower names
//        // we can't use SQLite's lower() function 'cause it doesn't
//        // work with unicode characters outside ASCII codes
//        Cursor c = objectsProvider.getObjects(context, rebuildProjection, null);
//        return c;
//    }

    public static void rebuildSearchField(Context context, ObjectsSaver objectsSaver, Cursor c) {
        if (c != null) {
            while (c.moveToNext()) {
                objectsSaver.saveObject(context, c);
            }

            c.close();
        }
    }

}
