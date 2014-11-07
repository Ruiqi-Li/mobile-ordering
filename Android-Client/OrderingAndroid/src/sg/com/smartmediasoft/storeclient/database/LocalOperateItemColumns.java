package sg.com.smartmediasoft.storeclient.database;

import android.provider.BaseColumns;

public interface LocalOperateItemColumns extends BaseColumns {
	public static final String ORDER_ID = "order_id";
	public static final String OPERATE_UUID = "operate_uuid";
	public static final String OPERATE_UNIT_NAME = "unit_name";
	public static final String OPERATE_NAME_CN = "name_cn";
	public static final String OPERATE_NAME_EN = "name_en";
	public static final String OPERATE_IMAGE_URL = "image_url";
	public static final String OPERATE_VALUE_BEFORE = "value_before";
	public static final String OPERATE_VALUE_AFTER = "value_after";
}
