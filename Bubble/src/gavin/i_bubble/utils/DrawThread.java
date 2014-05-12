package gavin.i_bubble.utils;

import static gavin.i_bubble.utils.Constant.*;
import gavin.i_bubble.view.GameView;

//�����߳�
public class DrawThread extends Thread {
	GameView gv;

	public DrawThread(GameView gv) {
		this.gv = gv;
	}

	@Override
	public void run() {
		while (DRAW_THREAD_FLAG) {
			synchronized (LockUtil.getInstance().getLock()) {
				gv.mPlayingGameFragment.world.step(TIME_STEP, ITERA);// ��ʼģ��
				gv.repaint();
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
