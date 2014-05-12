package gavin.i_bubble.view;

import gavin.i_bubble.R;
import gavin.i_bubble.manager.DataManage;
import gavin.i_bubble.utils.ChatManagerUtil;
import gavin.i_bubble.utils.MyThread;
import gavin.i_bubble.utils.Person;
import java.util.HashMap;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayingGameFragment extends Fragment {
	// private View inflaterView;
	private MyView myView;
	private MyThread mt;
	private double b;

	// private SensorManager mySensorManager;
	// private String show;
	// private int view = 1;

	public Handler myHandler = new Handler() {

		public void handleMessage(Message msg) {
			b = msg.what;
			if (b == -1111111111) {
				gameOver();
			} else {
				myView.blow_Me = (float) b;
			}
			super.handleMessage(msg);
		}

		// public void handleMessage(Message msg) {
		// b = msg.what;
		// if (b == -1111111111) {
		// // gameOver();
		// } else {
		// // myView.blow = (float) b;
		// String str = b + "";
		// DataManage.getInstance().setData(true, Float.parseFloat(str));//
		// 刷新自己的数据
		// ChatManagerUtil.getInstance().getChatManager().write(str.getBytes());//
		// 发送数据
		// Log.i("发送数据", "发送数据成功");
		// }
		// super.handleMessage(msg);
		// }
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		myView = MyView.getInstance(getActivity());
		// inflaterView = inflater.inflate((View)myView, container, false);
		// setContentView(myView);
		return myView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mt = new MyThread(getActivity(), myHandler);
		mt.start();
		initSounds();
		playSound(1, 0);
		Person.startTime = System.currentTimeMillis();
	}

	public void initSounds() {
		soundpool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(1, soundpool.load(getActivity(), R.raw.louqi, 1));
	}

	public void playSound(int sound, int loop) {
		AudioManager mgr = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		float Volume = streamVolumeCurrent / streamVolumeMax;
		soundpool.play(soundPoolMap.get(sound), Volume, Volume, 1, loop, 1f);
	}

	SoundPool soundpool;
	HashMap<Integer, Integer> soundPoolMap;

	public void gameOver() {
		Person.stopTime = System.currentTimeMillis();
		// GameOverActivity.startActivity(PlayingGameActivity.this);
		// Toast.makeText(this, "游戏结束", Toast.LENGTH_SHORT).show();
		MyThread.ar.release();
		MyThread.ar = null;
		// finish();
	}

}
