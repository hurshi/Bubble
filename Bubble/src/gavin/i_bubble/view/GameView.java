package gavin.i_bubble.view;

import gavin.i_bubble.R;
import gavin.i_bubble.utils.ChatManagerUtil;
import gavin.i_bubble.utils.DrawThread;
import gavin.i_bubble.utils.MyBody;

import org.jbox2d.common.Vec2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import static gavin.i_bubble.utils.Constant.*;

public class GameView extends SurfaceView implements SurfaceHolder.Callback // 实现生命周期回调接口
{
	private Activity activity;
	public PlayingGameFragment mPlayingGameFragment;
	private Paint paint;// 画笔
	private DrawThread dt;// 绘制线程
	private volatile static GameView uniqueInstance;

	private float yPosition;
	private float myAngle;
	private Bitmap[] bt;
	private Vec2 myPosition;

	private GameView(Activity activity2, PlayingGameFragment mPlayingGameFragment) {
		super(activity2);
		this.activity = activity2;
		this.mPlayingGameFragment = mPlayingGameFragment;
		myPosition = new Vec2(100, 100);
		// 设置生命周期回调接口的实现者
		this.getHolder().addCallback(this);
		// 初始化画笔
		paint = new Paint();// 创建画笔
		paint.setAntiAlias(true);// 打开抗锯齿
		// 启动绘制线程
		dt = new DrawThread(this);
		bt = new Bitmap[8];
		bt[0] = BitmapFactory.decodeResource(getResources(), R.drawable.bg1);
		bt[1] = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
		bt[2] = BitmapFactory.decodeResource(getResources(), R.drawable.bg3);
		bt[3] = BitmapFactory.decodeResource(getResources(), R.drawable.bg4);
		bt[4] = BitmapFactory.decodeResource(getResources(), R.drawable.bg5);
		bt[5] = BitmapFactory.decodeResource(getResources(), R.drawable.bg6);
		bt[6] = BitmapFactory.decodeResource(getResources(), R.drawable.bg7);
		bt[7] = BitmapFactory.decodeResource(getResources(), R.drawable.bg8);

	}

	public static GameView getInstance(Activity activity2, PlayingGameFragment mPlayingGameFragment) {
		if (uniqueInstance == null) {
			synchronized (GameView.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new GameView(activity2, mPlayingGameFragment);
				}
			}
		}
		return uniqueInstance;
	}

	public static GameView getView() {
		return uniqueInstance;
	}

	public void onDraw(Canvas canvas) {
		if (canvas == null) {
			return;
		}
		mPlayingGameFragment.bl.get(0).drawSelf(canvas, paint);

		MyBody mMybody = mPlayingGameFragment.bl.get(1);
		setYPosition(mMybody.getBody().getPosition().y);
		setAngle(mMybody.getBody().getAngle());
		mMybody.drawSelf(canvas, paint);

		mPlayingGameFragment.bl.get(2).drawSelf(canvas, paint);
		mPlayingGameFragment.bl.get(3).drawSelf(canvas, paint);

		// 发送服务器的坐标位置到客户机上去
		// ChatManagerUtil.getInstance().getChatManager().write(("x" + (float) getXPosition() + "#").getBytes());
		ChatManagerUtil.getInstance().getChatManager().write(("y" + (float) getYPosition() + "#").getBytes());
		ChatManagerUtil.getInstance().getChatManager().write(("a" + (float) getAngle() + "#").getBytes());
	}

	int temp = 0;

	public void onDrawJust(Canvas canvas) {
		if (canvas == null) {
			return;
		}
		try {
			mPlayingGameFragment.bl.get(1).getBody().setXForm(getPosition(), getAngle());
			mPlayingGameFragment.bl.get(1).drawSelf(canvas, paint);
		} catch (Exception e) {
			Log.e("onDrawJust异常：", "" + e);
		}
		canvas.drawText("temp" + temp, 100, 100, paint);
		temp++;
	}

	private void showLog(Canvas canvas) {
		canvas.drawText(ChatManagerUtil.getInstance().getIsGourpOwner() + "", 10, 10, paint);
		// canvas.drawText("x:" + getXPosition(), 10, 20, paint);
		canvas.drawText("y:" + getYPosition(), 10, 30, paint);
		canvas.drawText("a:" + getAngle(), 10, 40, paint);

		canvas.drawText("get.x:" + mPlayingGameFragment.bl.get(1).getBody().getPosition().x, 100, 20, paint);
		canvas.drawText("get.y:" + mPlayingGameFragment.bl.get(1).getBody().getPosition().y, 100, 30, paint);
		canvas.drawText("get.angle:" + mPlayingGameFragment.bl.get(1).getBody().getAngle(), 100, 40, paint);

	}

	int i = 0;

	private void drawBg(Canvas canvas) {
		canvas.drawBitmap(bt[i], null, new RectF(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT), paint);
		i++;
		if (i == 8) {
			i = 0;
		}
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	public void surfaceCreated(SurfaceHolder holder) {// 创建时被调用
		// repaint();
		dt.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {// 销毁时被调用

	}

	public void repaint() {
		SurfaceHolder holder = this.getHolder();
		Canvas canvas = holder.lockCanvas();// 获取画布
		try {
			synchronized (holder) {
				drawBg(canvas);
				if (ChatManagerUtil.getInstance().getIsGourpOwner()) {
					onDraw(canvas);// 绘制
				} else {
					onDrawJust(canvas);
				}
				showLog(canvas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (canvas != null) {
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public void setYPosition(float yPosition) {
		this.yPosition = yPosition;
	}

	// float oldAngle;

	public void setAngle(float myAngle) {
		// if (oldAngle == myAngle) {
		// this.myAngle = 0;
		// } else if (Math.abs(oldAngle - myAngle) > 1) {
		// this.myAngle = oldAngle;
		// } else {
		this.myAngle = myAngle;
		// }
		// oldAngle = myAngle;
	}

	public float getYPosition() {
		return yPosition;
	}

	public float getAngle() {
		return myAngle;
	}

	private Vec2 getPosition() {
		myPosition.x = SCREEN_WIDTH / 2 / RATE;
		// myPosition.y = SCREEN_HEIGHT / 2 / RATE;
		myPosition.y = getYPosition();
		if (myPosition.y > 100) {
			myPosition.y = 82;
		}

		return myPosition;
	}
}