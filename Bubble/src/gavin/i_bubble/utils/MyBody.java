package gavin.i_bubble.utils;

import org.jbox2d.dynamics.Body;

import android.graphics.Canvas;
import android.graphics.Paint;

//�Զ���������
public abstract class MyBody {
	private Body body;// ��Ӧ���������еĸ���

	public abstract Body getBody();

	public abstract void drawSelf(Canvas canvas, Paint paint);

}
