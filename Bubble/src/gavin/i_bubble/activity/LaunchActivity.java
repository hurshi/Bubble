package gavin.i_bubble.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import gavin.i_bubble.R;

public class LaunchActivity extends Activity {
	private static final int MIN_SHOW_TIME = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// ȫ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_launch);
		InitTask initTask = new InitTask();
		initTask.execute();
	}

	private class InitTask extends AsyncTask<Object, String, Boolean> {
		@Override
		protected Boolean doInBackground(Object... params) {
			boolean result = false;

			long delay = MIN_SHOW_TIME - doSomething();

			if (delay > 0) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			WiFiConnectionActivity.startActivity(LaunchActivity.this);
			finish();
			return result;
		}
	}

	private long doSomething() {
		long start = System.currentTimeMillis();
		// // do sth
		// if
		// (!WordFactory.getInstance(this).tabbleIsExist(WordFactory.TABLE_WORDS))
		// {
		// // Log.e("db version",WordFactory.getInstance(this).getVersion()+"");
		// copyDB(this);
		// // Log.e("db version",WordFactory.getInstance(this).getVersion()+"");
		// WordFactory.getInstance(this); // ��ʼ�����ݿ⣬ִ������
		// }
		//
		// //
		// Ϊ���޸�����1.1.4����û��ִ��DatabaseHelper�е�updateDatabaseVersion2to3()��bug���˴���һ������
		// // WordFactory.getInstance(this).updateWithoutTimeLockInWords();
		//
		// // ��ѹ��rawĿ¼����Ƶѹ����
		// // if(FileManager.isFirstPartVoiceFileExist() == false)
		// // unzipRawVoiceFile(WelcomeActivity.this);
		//
		long end = System.currentTimeMillis();
		return end - start;
	}

}
