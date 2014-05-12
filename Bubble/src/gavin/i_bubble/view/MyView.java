package gavin.i_bubble.view;

import gavin.i_bubble.R;
import gavin.i_bubble.utils.ChatManagerUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder sh;
	private Context context;
	private boolean isRun;
	public float blow_Me;
	public float blow_Other;

	private Bitmap background, wood, duck;
	private int height;
	private int width;
	private int woodHeight;
	private int woodWidth;
	private int duckHeight;
	private float x1, y1, x2, y2;
	private boolean isGroupOwner;
	private int t = 1;

	private volatile static MyView uniqueInstance;

	private MyView(Context context) {
		super(context);
		sh = this.getHolder();
		sh.addCallback(this);
		this.setKeepScreenOn(true);// 保持屏幕常亮
		this.context = context;
		isRun = true;
		background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
		wood = BitmapFactory.decodeResource(getResources(), R.drawable.wood);
		duck = BitmapFactory.decodeResource(getResources(), R.drawable.duck);
		isGroupOwner = ChatManagerUtil.getInstance().getIsGourpOwner();
		// isStartFlop = true;
		// startFlopTime = System.currentTimeMillis();

		width = getWidth();
		height = getHeight();
		woodWidth = wood.getWidth();
		woodHeight = wood.getHeight();
		duckHeight = duck.getHeight();

		x1 = 0;
		x2 = width;
		y1 = height - woodHeight;
		y2 = height = woodHeight;
	}

	public static MyView getInstance(Context context) {
		if (uniqueInstance == null) {
			synchronized (MyView.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new MyView(context);
				}
			}
		}
		return uniqueInstance;
	}

	public static MyView getView() {
		return uniqueInstance;
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Thread MyThread = new Thread() {
			public void run() {
				width = getWidth();
				height = getHeight();
				woodWidth = wood.getWidth();
				woodHeight = wood.getHeight();

				x1 = 0;
				x2 = width;
				y1 = height - woodHeight;
				y2 = height;

				while (isRun) {
					Canvas c = sh.lockCanvas();
					// c.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

					Paint paint = new Paint();
					// paint.setAntiAlias(true);
					// paint.setFilterBitmap(true);
					// paint.setColor(Color.RED);// 设置红色
					// paint.setStrokeWidth(5.0f);

					int width = getWidth();
					int height = getHeight();
					c.drawBitmap(background, null, new RectF(0, 0, width, height), null); // 画背景
					if ((y1 > 0) && (y1 <= height)) {
						if (blow_Me > 5000) {
							y1 -= 8;
							t = 1;
						} else {
							y1 += 3.0f / 2.0f * 1.0f / 5.0f * t * t;
							t++;
						}
					}
					if (y1 < 0) {
						y1 = 0;
					} else if (y1 > height) {
						y1 = height;
					}

					ChatManagerUtil.getInstance().getChatManager().write((y1 + "").getBytes());// 发送数据
					y2 = blow_Other;

					drawWoodAndDuck(isGroupOwner, c, paint, x1, y1, x2, y2);

					sh.unlockCanvasAndPost(c);
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		MyThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		isRun = false;
	}

	private void drawWoodAndDuck(boolean isGroupOwner, Canvas c, Paint p, float x1, float y1, float x2, float y2) {
		if (!isGroupOwner) {
			y1 = y1 + y2;
			y2 = y1 - y2;
			y1 = y1 - y2;
		}
		double mAngle = Math.atan((y2 - y1) / getWidth()) * 180 / Math.PI;
		Matrix matrix = new Matrix();
		// 缩放原图
		matrix.postScale(1f, 1f);
		// 向左旋转45度，参数为正则向右旋转
		matrix.postRotate((float) mAngle);
		Bitmap woodBmp = Bitmap.createBitmap(wood, 0, 0, wood.getWidth(), wood.getHeight(), matrix, true);
		Bitmap duckBmp = Bitmap.createBitmap(duck, 0, 0, duck.getWidth(), duck.getHeight(), matrix, true);

		drawWood(c, p, y1, y2, woodBmp);
		drawDuck(c, p, y1, y2, duckBmp, mAngle);

	}

	private void drawWood(Canvas c, Paint p, float y1, float y2, Bitmap bmp) {
		float positionY = (y1 + y2 - bmp.getHeight()) / 2;
		float positionX = getWidth() / 2 - bmp.getWidth() / 2;
		c.drawBitmap(bmp, positionX, positionY, p);
	}

	private void drawDuck(Canvas c, Paint p, float y1, float y2, Bitmap bmp, double angle) {
		float positionY = (y1 + y2 - bmp.getHeight()) / 2;
		positionY -= (woodHeight + duckHeight) / (2);
		float positionX = getWidth() / 2 - bmp.getWidth() / 2;
		c.drawBitmap(bmp, positionX, positionY, p);

	}

}