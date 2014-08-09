package com.furdey.shopping.contentproviders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.furdey.engine.common.utils.FileHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "shopping.db";
	private static final int DATABASE_VERSION = 5;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		System.out.println("DatabaseHelper.onUpgrade() oldVer: " + oldVer + " newVer: " + newVer);
		switch (oldVer) {
		case 1:
		case 2:
		case 3:
			db.execSQL("CREATE INDEX `ix_categories_statistics_prev_next_deleted` ON `categories_statistics` (`prev_category_id`, `next_category_id`, `deleted`);");
			db.execSQL("CREATE INDEX `ix_goods_categories_name_deleted` ON `goods_categories` (`name`, `deleted`);");
			db.execSQL("CREATE INDEX `ix_goods_categories_descr_deleted` ON `goods_categories` (`descr`, `deleted`);");
			db.execSQL("CREATE INDEX `ix_purchases_goodid_state_deleted` ON `purchases` (`good_id`, `state`, `deleted`);");
			db.execSQL("CREATE INDEX `ix_goods_name_deleted` ON `goods` (`name`, `deleted`);");
			db.execSQL("CREATE INDEX `ix_goods_categoryid_deleted` ON `goods` (`category_id`, `deleted`);");
			db.execSQL("CREATE INDEX `ix_goods_statistics_prev_next_deleted` ON `goods_statistics` (`prev_good_id`, `next_good_id`, `deleted`);");
			db.execSQL("CREATE INDEX `ix_purchases_strdate_findate_deleted` ON `purchases` (`strdate`, `findate`, `deleted`);");
			db.execSQL("CREATE INDEX `ix_units_deleted_name` ON `units` (`deleted`, `name`);");
			db.execSQL("CREATE INDEX `ix_units_unitType_isDefault_deleted` ON `units` (`unitType`, `isDefault`, `deleted`);");
			db.execSQL("ALTER TABLE `goods_categories` ADD COLUMN `synchronizedtm` VARCHAR;");
			db.execSQL("ALTER TABLE `units` ADD COLUMN `synchronizedtm` VARCHAR;");
			db.execSQL("ALTER TABLE `goods` ADD COLUMN `synchronizedtm` VARCHAR;");
			db.execSQL("ALTER TABLE `purchases` ADD COLUMN `synchronizedtm` VARCHAR;");
			db.execSQL("ALTER TABLE `categories_statistics` ADD COLUMN `synchronizedtm` VARCHAR;");
			db.execSQL("ALTER TABLE `goods_statistics` ADD COLUMN `synchronizedtm` VARCHAR;");
			// without break, it helps us to go through all upgrades we need
		case 4:
			db.execSQL("ALTER TABLE `goods_categories` ADD COLUMN `icon` VARCHAR;");
		}
		System.out.println("DatabaseHelper.onUpgrade() done");
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	public static void copyDataBase(Context context) throws Exception {
		/*
		 * Open the database in the assets folder as the input stream.
		 */
		InputStream myInput = context.getAssets().open(DATABASE_NAME);

		/*
		 * Open the empty db in interal storage as the output stream.
		 */
		File dbFolder = new File(context.getDatabasePath(DATABASE_NAME).getParent());
		if (!dbFolder.exists())
			dbFolder.mkdirs();

		OutputStream myOutput = new FileOutputStream(context.getDatabasePath(DATABASE_NAME).getPath());

		/*
		 * Copy over the empty db in internal storage with the database in the
		 * assets folder.
		 */
		FileHelper.copyFile(myInput, myOutput);
	}

	private SQLiteDatabase db;

	public SQLiteDatabase getDb() {
		if (db == null) {
			synchronized (this) {
				if (db == null) {
					db = getWritableDatabase();
				}
			}
		}

		return db;
	}
}
