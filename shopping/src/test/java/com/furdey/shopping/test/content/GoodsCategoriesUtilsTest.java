package com.furdey.shopping.test.content;

import android.net.Uri;

import com.furdey.shopping.content.GoodsCategoriesUtils;
import com.furdey.shopping.content.model.GoodsCategory;
import com.furdey.shopping.contentproviders.GoodsCategoriesContentProvider;
import com.furdey.shopping.contentproviders.GoodsStatisticsContentProvider;
import com.furdey.shopping.test.content.bridge.MockContentUriFactory;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Stepan Furdei
 */
public class GoodsCategoriesUtilsTest extends BaseContentTest {

    private static final String TEST_NAME = "testName";
    private static final String TEST_ICON = "testIcon";
    private static final String TEST_DESCR = "testDescr";

    @Before
    public void setup() {
        super.setup();
        GoodsCategoriesUtils.setContentValuesFactory(getMockContentValuesCreator());
        GoodsCategoriesUtils.setUriFactory(getUriCachingFactory());
        GoodsCategoriesUtils.setContentUriFactory(new MockContentUriFactory());
    }

    @Test
    public void newCategoryTest() {
        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setName(TEST_NAME);
        goodsCategory.setIcon(TEST_ICON);
        goodsCategory.setDescr(TEST_DESCR);

        Uri insertedUri = mock(Uri.class);

        when(getContentResolver().insert(
                getBaseUri(),
                getMockContentValuesCreator().getInstance()))
                .thenReturn(insertedUri);

        Uri uri = GoodsCategoriesUtils.saveGoodsCategory(getContext(), goodsCategory);
        Assert.assertEquals(insertedUri, uri);
        verify(getMockContentValuesCreator().getInstance())
                .put(GoodsCategoriesContentProvider.Columns.NAME.toString(),
                        goodsCategory.getName());
        verify(getMockContentValuesCreator().getInstance())
                .put(GoodsCategoriesContentProvider.Columns.ICON.toString(),
                        goodsCategory.getIcon());
        verify(getMockContentValuesCreator().getInstance())
                .put(GoodsCategoriesContentProvider.Columns.DESCR.toString(),
                        goodsCategory.getDescr());
    }

    @Test
    public void editCategoryTest() {
        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setId(1l);
        goodsCategory.setName(TEST_NAME);
        goodsCategory.setIcon(TEST_ICON);
        goodsCategory.setDescr(TEST_DESCR);

        Uri insertedUri = mock(Uri.class);
        Uri updatedUri = mock(Uri.class);

        when(getContentResolver().insert(
                getBaseUri(),
                getMockContentValuesCreator().getInstance()))
                .thenReturn(insertedUri);
        when(getContentResolver().update(
                getBaseUri(),
                getMockContentValuesCreator().getInstance(),
                GoodsStatisticsContentProvider.Columns._id.toString() + "=?",
                new String[]{goodsCategory.getId().toString()}))
                .thenReturn(1);

        Uri uri = GoodsCategoriesUtils.saveGoodsCategory(getContext(), goodsCategory);
        Assert.assertEquals(getBaseUri(), uri);
        Assert.assertNotSame(insertedUri, uri);
        Assert.assertNotSame(updatedUri, uri);

        verify(getMockContentValuesCreator().getInstance())
                .put(GoodsCategoriesContentProvider.Columns.NAME.toString(),
                        goodsCategory.getName());
        verify(getMockContentValuesCreator().getInstance())
                .put(GoodsCategoriesContentProvider.Columns.ICON.toString(),
                        goodsCategory.getIcon());
        verify(getMockContentValuesCreator().getInstance())
                .put(GoodsCategoriesContentProvider.Columns.DESCR.toString(),
                        goodsCategory.getDescr());
    }

}
