package gavin.i_bubble.utils;

import org.jbox2d.dynamics.Body;

import android.graphics.Canvas;
import android.graphics.Paint;

//自定义刚体根类
public abstract class MyBody {
	private Body body;// 对应物理引擎中的刚体

	public abstract Body getBody();

	public abstract void drawSelf(Canvas canvas, Paint paint);

}
