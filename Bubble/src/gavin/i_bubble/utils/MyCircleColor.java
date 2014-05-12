package gavin.i_bubble.utils;

import org.jbox2d.dynamics.Body;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import static gavin.i_bubble.utils.Constant.*;

public class MyCircleColor extends MyBody {
	float radius;// 半径
	private Body body;// 对应物理引擎中的刚体
	private Bitmap bt;

	public MyCircleColor(Body body, float radius, Bitmap bt) {
		this.body = body;
		this.radius = radius;
		this.bt = bt;
	}

	@Override
	public void drawSelf(Canvas canvas, Paint paint) {
		float x = body.getPosition().x * RATE;
		float y = body.getPosition().y * RATE;
		if (bt != null) {
			canvas.drawCircle(x, y, radius, paint);
		} else
			canvas.drawCircle(x, y, radius, paint);
		paint.reset();
	}

	public float getRadius() {
		return radius;
	}

	public Body getBody() {
		return body;
	}
}
