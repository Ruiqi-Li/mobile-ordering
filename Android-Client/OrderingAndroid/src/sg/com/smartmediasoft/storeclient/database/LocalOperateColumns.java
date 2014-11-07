package sg.com.smartmediasoft.storeclient.database;

import android.provider.BaseColumns;


public interface LocalOperateColumns extends BaseColumns {
	public static final String UUID = "uuid";
	public static final String ORDER_ID = "order_id";
	public static final String OPERATOR_NAME = "operator_name";
	public static final String OPERATE_ACTION = "operate_action";
	public static final String DATE = "date";
	public static final String DESCRIPTION = "description";
	public static final String TARGET_TIME_FROM = "time_from";
	public static final String TARGET_TIME_TO = "time_to";
}
