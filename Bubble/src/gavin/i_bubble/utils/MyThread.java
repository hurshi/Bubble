package gavin.i_bubble.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

public class MyThread extends Thread {
	public static AudioRecord ar;
	private int bs;
	private static int SAMPLE_RATE_IN_HZ = 8000;
	private boolean isRun = false;

	private double volume;
	private Context context;
	private Handler myHandler;

	public MyThread(Context context, Handler myHandler) {
		super();
		this.context = context;
		this.myHandler = myHandler;
		bs = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		ar = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bs);
	}

	public void run() {
		super.run();
		try {
			ar.startRecording();
			byte[] buffer = new byte[bs];
			isRun = true;
			while (isRun) {
				int r = ar.read(buffer, 0, bs);
				int v = 0;
				for (int i = 0; i < buffer.length; i++) {
					v += buffer[i] * buffer[i];
				}
				volume = Double.parseDouble((String.valueOf(v / (float) r)));
				myHandler.sendEmptyMessage((int) (volume));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		isRun = false;
	}

	public void start() {
		if (!isRun) {
			super.start();
		}
	}
}