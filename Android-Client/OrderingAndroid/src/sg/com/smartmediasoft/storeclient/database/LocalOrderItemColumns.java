package sg.com.smartmediasoft.storeclient.database;

import android.provider.BaseColumns;

public interface LocalOrderItemColumns extends BaseColumns {
	public static final String ORDER_ID = "order_id";
	public static final String PRODUCT_ID = "product_id";
	public static final String AMOUNT = "amount";
	public static final String UNIT_NAME = "unit_name";
	public static final String DESCIMAL_UNIT = "descimal";
	public static final String NAME_CN = "name_cn";
	public static final String NAME_EN = "name_en";
	public static final String IMAGE_URL = "image_url";
}
