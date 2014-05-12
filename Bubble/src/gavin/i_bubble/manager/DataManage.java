package gavin.i_bubble.manager;

import android.util.Log;
import gavin.i_bubble.view.MyView;

public class DataManage {
	private float dataY;

	private volatile static DataManage uniqueInstance;

	public static DataManage getInstance() {
		if (uniqueInstance == null) {
			synchronized (DataManage.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new DataManage();
				}
			}
		}
		return uniqueInstance;
	}

	public void setData(String readMessage) {
		try {
			this.dataY = Float.parseFloat(readMessage);
		} catch (Exception e) {
			Log.i("DataManage Exception:", "" + e);
		}
		update();
	}

	public void update() {
		try {
			MyView.getView().blow_Other = dataY;
		} catch (Exception e) {
			Log.i("DataManage:", e + "");
		}
	}

}
