package gavin.i_bubble.manager;

import android.util.Log;
import gavin.i_bubble.utils.ChatManagerUtil;
import gavin.i_bubble.view.GameView;
import gavin.i_bubble.view.PlayingGameFragment;

public class DataManage {
	private float dataY = 200;
	private float yPosition = 400;
	private float mAngle = 0;
	private String message = "";

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

	private int length = 7;

	private void setData(String readMessage) {
		if (readMessage.length() > 7) {
			length = 7;
		} else {
			length = readMessage.length() - 1;
		}
		try {
			this.message = readMessage;
			if (readMessage.charAt(0) == 'a') {
				this.mAngle = Float.parseFloat(message.substring(1, length));
			} else if (readMessage.charAt(0) == 'y') {
				this.yPosition = Float.parseFloat(message.substring(1, length));
			} else if (readMessage.charAt(0) == 'c') {
				this.dataY = Float.parseFloat(message.substring(1, length));
			}
			message = "";
			update();
		} catch (Exception e) {
			Log.e("DataManage�쳣", e + "");
		}
	}

	public void dataUtils(String readMessage) {
		String[] result = readMessage.split("#");
		for (int i = 0; i < result.length; i++) {
			setData(result[i]);
		}
	}

	public void update() {
		if (ChatManagerUtil.getInstance().getIsGourpOwner()) {// ����Ƿ����������տͻ���������������
			if (dataY > 5000) {
				PlayingGameFragment.getInstance().setOtherBlowed(dataY);
			}
		} else {// ����ǿͻ��������շ�����������������λ���Լ��Ƕ�
			GameView.getView().setYPosition(yPosition);
			GameView.getView().setAngle(mAngle);
		}
	}

}
