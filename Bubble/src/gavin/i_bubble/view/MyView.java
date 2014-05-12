package gavin.i_bubble.view;

import gavin.i_bubble.R;
import gavin.i_bubble.utils.ChatManagerUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder sh;
	private Context context;
	private boolean isRun;
	public float blow_Me;
	public float blow_Other;

	private Bitmap background, wood;
	private int ScreenH;
	private int ScreenW;
	private int woodHeight;
	private int woodWidth;
	private float x1, y1, x2, y2;
	private boolean isGroupOwner;

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
		isGroupOwner = ChatManagerUtil.getInstance().getIsGourpOwner();
		ScreenW = getWidth();
		ScreenH = getHeight();
		woodWidth = wood.getWidth();
		woodHeight = wood.getHeight();
		x1 = 0;
		x2 = ScreenW;
		y1 = ScreenH - woodHeight;
		y2 = ScreenH = woodHeight;

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
				ScreenW = getWidth();
				ScreenH = getHeight();
				woodWidth = wood.getWidth();
				woodHeight = wood.getHeight();

				x1 = 0;
				x2 = ScreenW;
				y1 = ScreenH - woodHeight;
				y2 = ScreenH;

				while (isRun) {
					Canvas c = sh.lockCanvas();
					// 反锯齿
					// c.setDrawFilter(new PaintFlagsDrawFilter(0,
					// Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

					// Paint paint = new Paint();
					// paint.setAntiAlias(true);// 反锯齿
					// paint.setFilterBitmap(true);
					// paint.setColor(Color.RED);// 设置红色
					// paint.setStrokeWidth(5.0f);

					// int width = getWidth();
					// int height = getHeight();
					c.drawBitmap(background, null, new RectF(0, 0, ScreenW, ScreenH), null); // 画背景

					if ((blow_Me > 5000) && (y1 >= 0)) {
						y1 -= 3;
					} else if (y1 <= ScreenH) {
						y1 += 1;
					}
					ChatManagerUtil.getInstance().getChatManager().write((y1 + "").getBytes());// 发送数据
					y2 = blow_Other;
					drawWoods(isGroupOwner, c, x1, y1, x2, y2);
					sh.unlockCanvasAndPost(c);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
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

	private void drawWoods(boolean isGroupOwner, Canvas c, float x1, float y1, float x2, float y2) {
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
		// bmp.getWidth(), 500分别表示重绘后的位图宽高
		Bitmap dstbmp = Bitmap.createBitmap(wood, 0, 0, wood.getWidth(), wood.getHeight(), matrix, false);
		float positionY = (y1 + y2 - dstbmp.getHeight()) / 2;
		float positionX = getWidth() / 2 - dstbmp.getWidth() / 2;
		// 在画布上绘制旋转后的位图
		// 放在坐标为0,200的位置
		c.drawBitmap(dstbmp, positionX, positionY, null);
	}

}