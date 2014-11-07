package sg.com.smartmediasoft.storeclient.ui.orderedit;

import java.util.ArrayList;
import java.util.Date;

import sg.com.smartmediasoft.storeclient.database.LocalProduct;

public interface EditOrderActivityInterface {

	public Date getSelectedOrderDate();

	public ArrayList<LocalProduct> getSelectedProductList();

}
