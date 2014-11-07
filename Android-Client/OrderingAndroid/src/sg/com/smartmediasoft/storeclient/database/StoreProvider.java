package sg.com.smartmediasoft.storeclient.database;

import sg.com.smartmediasoft.storeclient.LocalUtils;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

public class StoreProvider extends ContentProvider {
	private static final String TAG = "SfccaProvider";

	public static final String DATABASE_NAME = "StoreProvider.db";

	private static final int CUSTOMER_BASE = 0;
	private static final int CUSTOMER = CUSTOMER_BASE;
	private static final int CUSTOMER_ID = CUSTOMER_BASE + 1;

	private static final int OPERATE_BASE = 0x1000;
	private static final int OPERATE = OPERATE_BASE;
	private static final int OPERATE_ID = OPERATE_BASE + 1;
	
	private static final int ORDER_BASE = 0x2000;
	private static final int ORDER = ORDER_BASE;
	private static final int ORDER_ID = ORDER_BASE + 1;
	private static final int ORDER_FULL = ORDER_BASE + 2;
	
	private static final int PRODUCT_BASE = 0x3000;
	private static final int PRODUCT = PRODUCT_BASE;
	private static final int PRODUCT_ID = PRODUCT_BASE + 1;
	
	private static final int OPERATE_ITEM_BASE = 0x4000;
	private static final int OPERATE_ITEM = OPERATE_ITEM_BASE;
	private static final int OPERATE_ITEM_ID = OPERATE_ITEM_BASE + 1;
	
	private static final int ORDER_TAMPLATE_ITEM_BASE = 0x5000;
	private static final int ORDER_TAMPLATE = ORDER_TAMPLATE_ITEM_BASE;
	private static final int ORDER_TAMPLATE_ID = ORDER_TAMPLATE_ITEM_BASE + 1;

	private static final int ORDER_ITEM_BASE = 0x6000;
	private static final int ORDER_ITEM = ORDER_ITEM_BASE;
	private static final int ORDER_ITEM_ID = ORDER_ITEM_BASE + 1;
	
	private static final int BASE_SHIFT = 12; // 12 bits to the base type: 0,
												// 0x1000, 0x2000, etc.

	private static final SparseArray<String> TABLE_NAMES;
	static {
		SparseArray<String> array = new SparseArray<String>(6);
		array.put(CUSTOMER_BASE >> BASE_SHIFT, LocalCustomer.TABLE_NAME);
		array.put(OPERATE_BASE >> BASE_SHIFT, LocalOperate.TABLE_NAME);
		array.put(ORDER_BASE >> BASE_SHIFT, LocalOrder.TABLE_NAME);
		array.put(PRODUCT_BASE >> BASE_SHIFT, LocalProduct.TABLE_NAME);
		array.put(OPERATE_ITEM_BASE >> BASE_SHIFT, LocalOperateItem.TABLE_NAME);
		array.put(ORDER_TAMPLATE_ITEM_BASE >> BASE_SHIFT, LocalOrderTemplate.TABLE_NAME);
		array.put(ORDER_ITEM >> BASE_SHIFT, LocalOrderItem.TABLE_NAME);
		TABLE_NAMES = array;
	}

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalCustomer.PATH, CUSTOMER);
		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalCustomer.ITEM_PATH,
				CUSTOMER_ID);

		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalOperate.PATH, OPERATE);
		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalOperate.ITEM_PATH,
				OPERATE_ID);

		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalOrder.PATH, ORDER);
		sURIMatcher
				.addURI(ObjectBase.AUTHORITY, LocalOrder.ITEM_PATH, ORDER_ID);
		sURIMatcher
				.addURI(ObjectBase.AUTHORITY, LocalOrder.FULL_PATH, ORDER_FULL);

		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalProduct.PATH, PRODUCT);
		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalProduct.ITEM_PATH,
				PRODUCT_ID);

		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalOperateItem.PATH, OPERATE_ITEM);
		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalOperateItem.ITEM_PATH,
				OPERATE_ITEM_ID);
		
		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalOrderTemplate.PATH, ORDER_TAMPLATE);
		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalOrderTemplate.ITEM_PATH,
				ORDER_TAMPLATE_ID);
		
		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalOrderItem.PATH, ORDER_ITEM);
		sURIMatcher.addURI(ObjectBase.AUTHORITY, LocalOrderItem.ITEM_PATH,
				ORDER_ITEM_ID);
	}

	/**
	 * Functions which manipulate the database connection or files synchronize
	 * on this. It's static because there can be multiple provider objects.
	 */
	private static final Object sDatabaseLock = new Object();

	private SQLiteDatabase mDatabase;

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(TAG, "Delete: " + uri);
		final int match = findMatch(uri, "delete");
		Context context = getContext();

		SQLiteDatabase db = getDatabase(context);
		int table = match >> BASE_SHIFT;
		String id = "0";

		String tableName = TABLE_NAMES.valueAt(table);
		int result = -1;

		switch (match) {
		case CUSTOMER_ID:
		case OPERATE_ID:
		case ORDER_ID:
		case PRODUCT_ID:
		case OPERATE_ITEM_ID:
		case ORDER_TAMPLATE_ID:
		case ORDER_ITEM_ID:
			id = uri.getPathSegments().get(1);
			result = db.delete(tableName, whereWithId(id, selection),
					selectionArgs);
			break;
		case CUSTOMER:
		case ORDER:
		case PRODUCT:
		case OPERATE:
		case OPERATE_ITEM:
		case ORDER_TAMPLATE:
		case ORDER_ITEM:
			result = db.delete(tableName, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return result;
	}

	@Override
	public String getType(Uri uri) {
		int match = findMatch(uri, "getType");
		switch (match) {
		case CUSTOMER_ID:
			return LocalCustomer.MIME_ITEM_TYPE;
		case CUSTOMER:
			return LocalCustomer.MIME_TYPE;
		case OPERATE_ID:
			return LocalOperate.MIME_ITEM_TYPE;
		case OPERATE:
			return LocalOperate.MIME_TYPE;
		case ORDER_ID:
			return LocalOrder.MIME_ITEM_TYPE;
		case ORDER:
			return LocalOrder.MIME_TYPE;
		case PRODUCT_ID:
			return LocalProduct.MIME_ITEM_TYPE;
		case PRODUCT:
			return LocalProduct.MIME_TYPE;
		case OPERATE_ITEM_ID:
			return LocalOperate.MIME_ITEM_TYPE;
		case OPERATE_ITEM:
			return LocalOperate.MIME_TYPE;
		case ORDER_TAMPLATE:
			return LocalOrderTemplate.MIME_TYPE;
		case ORDER_TAMPLATE_ID:
			return LocalOrderTemplate.MIME_ITEM_TYPE;
		case ORDER_ITEM_ID:
			return LocalOrderItem.MIME_ITEM_TYPE;
		case ORDER_ITEM:
			return LocalOrderItem.MIME_TYPE;
		default:
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "Insert: " + uri);
		int match = findMatch(uri, "insert");
		Context context = getContext();

		// See the comment at delete(), above
		SQLiteDatabase db = getDatabase(context);
		int table = match >> BASE_SHIFT;
		long longId;

		final Uri resultUri;

		try {
			switch (match) {
			case CUSTOMER:
			case OPERATE:
			case ORDER:
			case PRODUCT:
			case OPERATE_ITEM:
			case ORDER_TAMPLATE:
			case ORDER_ITEM:
				longId = db.insert(TABLE_NAMES.valueAt(table), null, values);
				resultUri = ContentUris.withAppendedId(uri, longId);
				break;
			default:
				throw new IllegalArgumentException("Unknown URL " + uri);
			}
		} catch (SQLiteException e) {
			throw e;
		}

		return resultUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c = null;
		int match = findMatch(uri, "query");

		Context context = getContext();
		// See the comment at delete(), above
		SQLiteDatabase db = getDatabase(context);
		int table = match >> BASE_SHIFT;
		String limit = uri.getQueryParameter(ObjectBase.PARAMETER_LIMIT);
		String id;

		String tableName = TABLE_NAMES.valueAt(table);

		try {
			switch (match) {
			case OPERATE:
			case PRODUCT:
			case ORDER_ITEM:
			case OPERATE_ITEM:
			case ORDER_TAMPLATE: {
				c = db.query(tableName, projection, selection, selectionArgs,
						null, null, sortOrder);
				break;
			}
			case ORDER: {
				if (projection == LocalOrder.CONTENT_PROJECTION) {
					StringBuilder builder = new StringBuilder();
					builder.append("SELECT ")
							.append(LocalOrder.TABLE_NAME).append(".*, ")
							.append(LocalCustomer.TABLE_NAME).append(".").append(LocalCustomer.CHINESE_NAME).append(", ")
							.append(LocalCustomer.TABLE_NAME).append(".").append(LocalCustomer.ENGLISH_NAME)
						.append(" FROM ").append(LocalOrder.TABLE_NAME)
						.append(" LEFT OUTER JOIN ").append(LocalCustomer.TABLE_NAME)
						.append(" ON ").append(LocalOrder.TABLE_NAME).append(".").append(LocalOrder.CUSTOMER_ID)
										.append(" = ").append(LocalCustomer.TABLE_NAME).append(".").append(LocalCustomer.UUID);
					if (!TextUtils.isEmpty(selection)) {
						builder.append(" WHERE ").append(selection);
					}

					if (!TextUtils.isEmpty(sortOrder)) {
						builder.append(" ORDER BY ").append(sortOrder);
					}
					
					c = db.rawQuery(builder.toString(), selectionArgs);
				} else {
					c = db.query(tableName, projection, selection, selectionArgs,
							null, null, sortOrder);
				}
				break;
			}
			case CUSTOMER_ID:
			case OPERATE_ID:
			case ORDER_ID:
			case PRODUCT_ID:
			case OPERATE_ITEM_ID:
			case ORDER_TAMPLATE_ID:
			case ORDER_ITEM_ID:
				id = uri.getPathSegments().get(1);
				c = db.query(tableName, projection, whereWithId(id, selection),
						selectionArgs, null, null, sortOrder, limit);
				break;
			case ORDER_FULL: {
//				StringBuilder builder = new StringBuilder();
//				builder.append("SELECT ");
//				builder.append(LocalOrder.TABLE_NAME).append(".*, ");
//				builder.append(LocalOrder.TABLE_NAME).append(".*, ");
//				String rawQuery = "SELECT " + LocalOrder.TABLE_NAME + " FROM "
//						+ SfccaPublication.TABLE_NAME + " LEFT OUTER JOIN "
//						+ SfccaAssociation.TABLE_NAME + " ON "
//						+ SfccaPublication.TABLE_NAME + "."
//						+ SfccaPublication.ASSOCIATION_UUID + "=" + SfccaAssociation.TABLE_NAME
//						+ "." + SfccaAssociation.UUID + " ORDER BY " + sortOrder;
//
//				c = db.rawQuery(rawQuery, null);
				
//				SELECT TableA.*, TableB.*, TableC.*, TableD.*
//				FROM TableA
//				    JOIN TableB
//				        ON TableB.aID = TableA.aID
//				    JOIN TableC
//				        ON TableC.cID = TableB.cID
//				    JOIN TableD
//				        ON TableD.dID = TableA.dID
//				WHERE DATE(TableC.date)=date(now()) 
				break;
			}
//			case OPERATE_ITEM: {
//				StringBuilder builder = new StringBuilder();
//				builder.append("SELECT ")
//							.append(LocalOperateItem.TABLE_NAME).append(".*, ")
//							.append(LocalProduct.TABLE_NAME).append(".").append(LocalProduct.ENGLISH_NAME).append(", ")
//							.append(LocalProduct.TABLE_NAME).append(".").append(LocalProduct.CHINESE_NAME)
//						.append(" FROM ").append(LocalOperateItem.TABLE_NAME)
//						.append(" LEFT OUTER JOIN ").append(LocalProduct.TABLE_NAME)
//						.append(" ON ").append(LocalOperateItem.TABLE_NAME).append(".").append(LocalOperateItem.OPERATE_PRODUCT_ID)
//										.append(" = ").append(LocalProduct.TABLE_NAME).append(".").append(LocalProduct.UUID);
//				c = db.rawQuery(builder.toString(), null);
//				break;
//			}
			case CUSTOMER: {
				StringBuilder projectionBuilder = new StringBuilder();
				for (String column : projection) {
					projectionBuilder.append(LocalCustomer.TABLE_NAME).append(".").append(column).append(",");
				}
				String query =
						"SELECT " + projectionBuilder.toString() + " temp1.unconfirm_count, temp2.undlivered_count" +
						" FROM " + LocalCustomer.TABLE_NAME +
						" LEFT JOIN (SELECT " + LocalCustomer.TABLE_NAME + "." + LocalCustomer.UUID + " AS id, COUNT(" + LocalOrder.TABLE_NAME + "." + LocalOrder._ID + ") AS unconfirm_count" +
										" FROM " + LocalCustomer.TABLE_NAME +
										" LEFT JOIN " + LocalOrder.TABLE_NAME +
										" ON " + LocalCustomer.TABLE_NAME + "." + LocalCustomer.UUID + " = " + LocalOrder.TABLE_NAME + "." + LocalOrder.CUSTOMER_ID +
										" WHERE " + LocalOrder.TABLE_NAME + "." + LocalOrder.STATE + " = '" + LocalUtils.ORDER_STATE_PENDING + "'" +
										" GROUP BY id" +
									") temp1 " +
						" ON temp1.id = " + LocalCustomer.TABLE_NAME + "." + LocalCustomer.UUID +
						
						" LEFT JOIN (SELECT " + LocalCustomer.TABLE_NAME + "." + LocalCustomer.UUID + " AS id, COUNT(" + LocalOrder.TABLE_NAME + "." + LocalOrder._ID + ") AS undlivered_count" +
										" FROM " + LocalCustomer.TABLE_NAME +
										" LEFT JOIN " + LocalOrder.TABLE_NAME +
										" ON " + LocalCustomer.TABLE_NAME + "." + LocalCustomer.UUID + " = " + LocalOrder.TABLE_NAME + "." + LocalOrder.CUSTOMER_ID +
										" WHERE " + LocalOrder.STATE + " = '" + LocalUtils.ORDER_STATE_CONFIRMED + "'" +
										" GROUP BY id" +
									") temp2 " +
						" ON temp2.id = " + LocalCustomer.TABLE_NAME + "." + LocalCustomer.UUID;

				if (!TextUtils.isEmpty(selection)) {
					query += (" WHERE " + selection);
				}

				if (!TextUtils.isEmpty(sortOrder)) {
					query += (" ORDER BY " + sortOrder);
				}

				c = db.rawQuery(query, selectionArgs);
				break;
			}
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (c == null) {
				// This should never happen, but let's be sure to log it...
				Log.w(TAG, "Query returning null for uri: " + uri
						+ ", selection: " + selection);
			}
		}

		if ((c != null) && !isTemporary()) {
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int match = findMatch(uri, "update");
		Context context = getContext();
		ContentResolver resolver = context.getContentResolver();
		// See the comment at delete(), above
		SQLiteDatabase db = getDatabase(context);
		int table = match >> BASE_SHIFT;
		int result;

		String tableName = TABLE_NAMES.valueAt(table);

		switch (match) {
		case CUSTOMER_ID:
		case OPERATE_ID:
		case ORDER_ID:
		case PRODUCT_ID:
		case OPERATE_ITEM_ID:
		case ORDER_TAMPLATE_ID:
		case ORDER_ITEM_ID:
		case CUSTOMER:
		case OPERATE:
		case ORDER:
		case PRODUCT:
		case OPERATE_ITEM:
		case ORDER_TAMPLATE:
		case ORDER_ITEM:
			result = db.update(tableName, values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		resolver.notifyChange(uri, null);

		return result;
	}

	@Override
	public void shutdown() {
		if (mDatabase != null) {
			mDatabase.close();
			mDatabase = null;
		}

		super.shutdown();
	}

	private SQLiteDatabase getDatabase(Context context) {
		synchronized (sDatabaseLock) {
			// Always return the cached database, if we've got one
			if (mDatabase != null) {
				return mDatabase;
			}

			DatabaseHelper.SQLiteHelper helper = new DatabaseHelper.SQLiteHelper(
					context, DATABASE_NAME);
			mDatabase = helper.getWritableDatabase();

			return mDatabase;
		}
	}

	/**
	 * Wrap the UriMatcher call so we can throw a runtime exception if an
	 * unknown Uri is passed in
	 * 
	 * @param uri
	 *            the Uri to match
	 * @return the match value
	 */
	private static int findMatch(Uri uri, String methodName) {
		int match = sURIMatcher.match(uri);
		if (match < 0) {
			throw new IllegalArgumentException("Unknown uri: " + uri);
		}

		return match;
	}

	private static String whereWithId(String id, String selection) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("_id=");
		sb.append(id);
		if (selection != null) {
			sb.append(" AND (");
			sb.append(selection);
			sb.append(')');
		}
		return sb.toString();
	}
	
    /**
     * A fast re-implementation of {@link Uri#getQueryParameter}
     */
    /* package */ static String getQueryParameter(Uri uri, String parameter) {
        String query = uri.getEncodedQuery();
        if (query == null) {
            return null;
        }

        int queryLength = query.length();
        int parameterLength = parameter.length();

        String value;
        int index = 0;
        while (true) {
            index = query.indexOf(parameter, index);
            if (index == -1) {
                return null;
            }

            // Should match against the whole parameter instead of its suffix.
            // e.g. The parameter "param" must not be found in "some_param=val".
            if (index > 0) {
                char prevChar = query.charAt(index - 1);
                if (prevChar != '?' && prevChar != '&') {
                    // With "some_param=val1&param=val2", we should find second "param" occurrence.
                    index += parameterLength;
                    continue;
                }
            }

            index += parameterLength;

            if (queryLength == index) {
                return null;
            }

            if (query.charAt(index) == '=') {
                index++;
                break;
            }
        }

        int ampIndex = query.indexOf('&', index);
        if (ampIndex == -1) {
            value = query.substring(index);
        } else {
            value = query.substring(index, ampIndex);
        }

        return Uri.decode(value);
    }

}
