package com.furdey.shopping.tasks;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.furdey.shopping.content.GoodsCategoriesUtils;
import com.furdey.shopping.content.GoodsUtils;
import com.furdey.shopping.content.SearchUtils;
import com.furdey.shopping.contentproviders.GoodsCategoriesContentProvider;
import com.furdey.shopping.contentproviders.GoodsContentProvider;
import com.furdey.shopping.utils.IntentUtils;

/**
 * Created by Masya on 07.01.2015.
 */
public class RebuildIndicesTask extends AsyncTask<Context, Integer, Context> {

    public interface ProgressListener {
        void updateProgress(int progress);
    }

    private static final int PROGRESS_STEP = 50;

    private ProgressListener progressListener;

    public RebuildIndicesTask(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    protected Context doInBackground(Context... params) {
        GoodsContentProvider.addColumn(params[0],
                GoodsContentProvider.Columns.NAME_LOWER.toString(), "VARCHAR");

        String[] projection = new String[] { GoodsContentProvider.Columns._id.toString(),
                GoodsContentProvider.Columns.NAME.toString() };
        Cursor cursor = GoodsUtils.getGoods(params[0], projection, null, null, null);
        final int goodsCount = cursor.getCount();

        SearchUtils.rebuildSearchField(params[0], new SearchUtils.ObjectsSaver() {
            @Override
            public void saveObject(Context context, Cursor c) {
                GoodsUtils.saveGoods(context, GoodsUtils.fromCursor(c));
                int recordNum = c.getPosition();

                if (recordNum % PROGRESS_STEP == 0) {
                    publishProgress(recordNum * 90 / goodsCount);
                }
            }
        }, cursor);

        cursor.close();

        GoodsCategoriesContentProvider.addColumn(params[0],
                GoodsCategoriesContentProvider.Columns.NAME_LOWER.toString(), "VARCHAR");

        projection = new String[] { GoodsCategoriesContentProvider.Columns._id.toString(),
                GoodsCategoriesContentProvider.Columns.NAME.toString() };
        cursor = GoodsCategoriesUtils.getGoodsCategories(params[0], projection, null, null, null);
        final int categoriesCount = cursor.getCount();

        SearchUtils.rebuildSearchField(params[0], new SearchUtils.ObjectsSaver() {
            @Override
            public void saveObject(Context context, Cursor c) {
                GoodsCategoriesUtils.saveGoodsCategory(context, GoodsCategoriesUtils.fromCursor(c));
                int recordNum = c.getPosition();

                if (recordNum % PROGRESS_STEP == 0) {
                    publishProgress(recordNum * 10 / categoriesCount + 90);
                }
            }
        }, cursor);

        cursor.close();
        return params[0];
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (progressListener != null) {
            progressListener.updateProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Context context) {
        context.startActivity(IntentUtils.purchaseActivityIntent(context));
        ((Activity)context).finish();
    }
}
