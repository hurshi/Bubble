package gavin.i_bubble.utils;

import org.jbox2d.dynamics.Body;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import static gavin.i_bubble.utils.Constant.*;

//�Զ���ľ�����(��ɫ)
public class MyRectColor extends MyBody {
	private Body body;// ��Ӧ���������еĸ���
	private float pixelHalfWidth;// ���
	private float pixelHalfHeight;// ���
	private Bitmap bt;

	public MyRectColor(Body body, float pixelHalfWidth, float pixelHalfHeight, Bitmap bt) {
		this.body = body;// ��Ӧ���������еĸ���
		this.pixelHalfWidth = pixelHalfWidth;// ���
		this.pixelHalfHeight = pixelHalfHeight;// ���
		this.bt = bt;
	}

	public float getPixelHalfWidth() {
		return pixelHalfWidth;
	}

	public float getPixelHalfHeight() {
		return pixelHalfHeight;
	}

	@Override
	public Body getBody() {
		return body;
	}

	@Override
	public void drawSelf(Canvas canvas, Paint paint) {
		float x = body.getPosition().x * RATE;
		float y = body.getPosition().y * RATE;
		float angle = body.getAngle();
		canvas.save();
		Matrix m1 = new Matrix();
		m1.setRotate((float) Math.toDegrees(angle), x, y);
		canvas.setMatrix(m1);
		if (bt != null) {
			canvas.drawBitmap(bt, null, new RectF(x - pixelHalfWidth, y - pixelHalfHeight, x + pixelHalfWidth, y + pixelHalfHeight), paint);
		} else {
			canvas.drawRect(x - pixelHalfWidth, y - pixelHalfHeight, x + pixelHalfWidth, y + pixelHalfHeight, paint);
		}
		paint.reset();
		canvas.restore();
	}

}
