/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sg.com.smartmediasoft.storeclient.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DatabaseHelper {
	private static final String TAG = DatabaseHelper.class.getSimpleName();

	// Any changes to the database format *must* include update-in-place code.
	// Original version: 1
	public static final int DATABASE_VERSION = 1;
	
	private static void createOrderItemTable(SQLiteDatabase db) {
		String statement = " (" +
			LocalOrderItem._ID + " integer primary key autoincrement, " +
			LocalOrderItem.ORDER_ID + " text NOT NULL, " +
			LocalOrderItem.PRODUCT_ID + " text NOT NULL, " +
			LocalOrderItem.AMOUNT + " double, " +
			LocalOrderItem.UNIT_NAME + " text NOT NULL, " +
			LocalOrderItem.DESCIMAL_UNIT + " integer, " +
			LocalOrderItem.NAME_CN + " text NOT NULL, " +
			LocalOrderItem.NAME_EN + " text NOT NULL, " +
			LocalOrderItem.IMAGE_URL + " text NOT NULL" +
		");";
		
		db.execSQL("create table " + LocalOrderItem.TABLE_NAME + statement);
	}

	private static void resetOrderItemTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		try {
			db.execSQL("drop table " + LocalOrderItem.TABLE_NAME);
		} catch (SQLException e) {
		}

		createOrderItemTable(db);
	}
	
	private static void createOrderTamplateTable(SQLiteDatabase db) {
		String statement = " (" +
			LocalOrderTemplate._ID + " integer primary key autoincrement, " +
			LocalOrderTemplate.NAME + " text NOT NULL, " +
			LocalOrderTemplate.SUMMERY_CHINESE + " text NOT NULL, " +
			LocalOrderTemplate.SUMMERY_ENGLISH + " text NOT NULL, " +
			LocalOrderTemplate.PRODUCTS_MAP + " text NOT NULL" +
		");";
		
		db.execSQL("create table " + LocalOrderTemplate.TABLE_NAME + statement);
	}

	private static void resetOrderTamplateTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		try {
			db.execSQL("drop table " + LocalOrderTemplate.TABLE_NAME);
		} catch (SQLException e) {
		}

		createOrderTamplateTable(db);
	}

	private static void createCustomerTable(SQLiteDatabase db) {
		String statement = " (" +
			LocalCustomer._ID + " integer primary key autoincrement, " +
			LocalCustomer.UUID + " text NOT NULL, " +
			LocalCustomer.GROUP_ID + " text NOT NULL, " +
			LocalCustomer.CHINESE_GROUP_NAME + " text NOT NULL, " +
			LocalCustomer.ENGLISH_GROUP_NAME + " text NOT NULL, " +
			LocalCustomer.CHINESE_NAME + " text NOT NULL, " +
			LocalCustomer.ENGLISH_NAME + " text NOT NULL, " +
			LocalCustomer.CHANGE_ID + " long, " +
			LocalCustomer.STORE_CHANGE_ID + " long, " +
			LocalCustomer.STORE_STORAGE_MAP + " text, " +
			LocalCustomer.STORE_LAST_CHECK_TIME + " long" +
		");";

		db.execSQL("create table " + LocalCustomer.TABLE_NAME + statement);
	}

	private static void resetCustomerTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		try {
			db.execSQL("drop table " + LocalCustomer.TABLE_NAME);
		} catch (SQLException e) {
		}

		createCustomerTable(db);
	}
	
	private static void createOperateTable(SQLiteDatabase db) {
		String statement = " (" +
			LocalOperate._ID + " integer primary key autoincrement, " +
			LocalOperate.UUID + " long, " +
			LocalOperate.ORDER_ID + " text NOT NULL, " +
			LocalOperate.OPERATOR_NAME + " text NOT NULL, " +
			LocalOperate.OPERATE_ACTION + " text NOT NULL, " +
			LocalOperate.DATE + " long, " +
			LocalOperate.TARGET_TIME_FROM + " long, " +
			LocalOperate.TARGET_TIME_TO + " long, " +
			LocalOperate.DESCRIPTION + " text" +
		");";
		
		db.execSQL("create table " + LocalOperate.TABLE_NAME + statement);
	}

	private static void resetOperateTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		try {
			db.execSQL("drop table " + LocalOperate.TABLE_NAME);
		} catch (SQLException e) {
		}

		createOperateTable(db);
	}
	
	private static void createOperateItemTable(SQLiteDatabase db) {
		String statement = " (" +
			LocalOperateItem._ID + " integer primary key autoincrement, " +
			LocalOperateItem.ORDER_ID + " text NOT NULL, " +
			LocalOperateItem.OPERATE_UUID + " long, " +
			LocalOperateItem.OPERATE_UNIT_NAME + " text NOT NULL, " +
			LocalOperateItem.OPERATE_NAME_CN + " text NOT NULL, " +
			LocalOperateItem.OPERATE_NAME_EN + " text NOT NULL, " +
			LocalOperateItem.OPERATE_IMAGE_URL + " text NOT NULL, " +
			LocalOperateItem.OPERATE_VALUE_BEFORE + " double, " +
			LocalOperateItem.OPERATE_VALUE_AFTER + " double" +
		");";
		
		db.execSQL("create table " + LocalOperateItem.TABLE_NAME + statement);
	}

	private static void resetOperateItemTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		try {
			db.execSQL("drop table " + LocalOperateItem.TABLE_NAME);
		} catch (SQLException e) {
		}

		createOperateItemTable(db);
	}

	private static void createOrderTable(SQLiteDatabase db) {
		String statement = " (" +
			LocalOrder._ID + " integer primary key autoincrement, " +
			LocalOrder.UUID + " text NOT NULL, " +
			LocalOrder.CUSTOMER_ID + " text NOT NULL, " +
			LocalOrder.STATE + " text NOT NULL, " +
			LocalOrder.TARGET_DATE + " long, " +
			LocalOrder.CREATE_DATE + " long, " +
			LocalOrder.CONFIRM_DATE + " long, " +
			LocalOrder.DELIVER_DATE + " long, " +
			LocalOrder.FINISH_DATE + " long, " +
			LocalOrder.CHANGE_ID + " long, " +
			LocalOrder.HUMAN_ID + " text NOT NULL, " +
			LocalOrder.READ + " integer, " +
			LocalOrder.HIGH_LIGHT + " integer" +
		");";
		
		db.execSQL("create table " + LocalOrder.TABLE_NAME + statement);
	}

	private static void resetOrderTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		try {
			db.execSQL("drop table " + LocalOrder.TABLE_NAME);
		} catch (SQLException e) {
		}

		createOrderTable(db);
	}
	
	private static void createProductTable(SQLiteDatabase db) {
		String statement = " (" +
			LocalProduct._ID + " integer primary key autoincrement, " +
			LocalProduct.UUID + " text NOT NULL, " +
			LocalProduct.UNIT_NAME + " text NOT NULL, " +
			LocalProduct.DECIMAL_UNIT + " integer, " +
			LocalProduct.CHINESE_NAME + " text NOT NULL, " +
			LocalProduct.ENGLISH_NAME + " text NOT NULL, " +
			LocalProduct.CHINESE_SORT_NAME + " text NOT NULL, " +
			LocalProduct.ENGLISH_SORT_NAME + " text NOT NULL, " +
			LocalProduct.IMAGE_URL + " text NOT NULL, " +
			LocalProduct.TYPE + " text NOT NULL, " +
			LocalProduct.CHANGE_ID + " long, " +
			LocalProduct.DELETED + " integer" +
		");";

		db.execSQL("create table " + LocalProduct.TABLE_NAME + statement);
	}
	
	private static void createOrderDeleteTrigger(SQLiteDatabase db) {
		String statement = "CREATE TRIGGER order_delete_trigger BEFORE DELETE ON " + LocalOrder.TABLE_NAME +
							" FOR EACH ROW" +
							" BEGIN" +
								" DELETE FROM " + LocalOperate.TABLE_NAME +
								" WHERE " + LocalOperate.TABLE_NAME + "." + LocalOperate.ORDER_ID +
										" = old." + LocalOrder.UUID + ";" +
								" DELETE FROM " + LocalOperateItem.TABLE_NAME +
								" WHERE " + LocalOperateItem.TABLE_NAME + "." + LocalOperateItem.ORDER_ID +
										" = old." + LocalOrder.UUID + ";" +
								" DELETE FROM " + LocalOrderItem.TABLE_NAME +
								" WHERE " + LocalOrderItem.TABLE_NAME + "." + LocalOrderItem.ORDER_ID +
										" = old." + LocalOrder.UUID + ";" +
							" END;";
		db.execSQL(statement);
	}
	
	private static void createOperateDeleteTrigger(SQLiteDatabase db) {
/*		String statement = "CREATE TRIGGER operate_delete_trigger BEFORE DELETE ON " + LocalOperate.TABLE_NAME +
							" FOR EACH ROW" +
							" BEGIN" +
								" DELETE FROM " + LocalOperateItem.TABLE_NAME +
								" WHERE " + LocalOperateItem.TABLE_NAME + "." + LocalOperateItem.OPERATE_UUID +
										" = old." + LocalOperate.UUID + ";" +
							" END;";
		db.execSQL(statement);*/
	}

	private static void resetProductTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		try {
			db.execSQL("drop table " + LocalProduct.TABLE_NAME);
		} catch (SQLException e) {
		}

		createProductTable(db);
	}

	public static class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name) {
			super(context, name, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create all tables here; each class has its own method
			createOrderTamplateTable(db);
			createCustomerTable(db);
			createOperateTable(db);
			createOperateItemTable(db);
			createOrderTable(db);
			createProductTable(db);
			createOrderItemTable(db);
			
			// triggers
			createOrderDeleteTrigger(db);
			createOperateDeleteTrigger(db);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int oldVersion,
				int newVersion) {
			
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			resetOrderTamplateTable(db, oldVersion, newVersion);
			resetCustomerTable(db, oldVersion, newVersion);
			resetOperateTable(db, oldVersion, newVersion);
			resetOperateItemTable(db, oldVersion, newVersion);
			resetOrderTable(db, oldVersion, newVersion);
			resetProductTable(db, oldVersion, newVersion);
		}
	}
}
