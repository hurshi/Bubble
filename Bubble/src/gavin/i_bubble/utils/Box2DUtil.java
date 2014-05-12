package gavin.i_bubble.utils;

import static gavin.i_bubble.utils.Constant.RATE;

import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import android.graphics.Bitmap;

//����������״�Ĺ�����
public class Box2DUtil {

	/*
	 * param x:x���� param y: y���� param halfWidth:��� param:halfHeight: ��� param isSattic:�Ƿ�Ϊ��ֹ�� param world:���� param color:��ɫ
	 */
	public static MyRectColor createBox(float x, float y, float halfWidth, float halfHeight, boolean isStatic, World world, Bitmap bt) {
		synchronized (LockUtil.getInstance().getLock()) {
			// �����������������
			PolygonDef shape = new PolygonDef();
			// �����ܶ�
			if (isStatic) {
				shape.density = 0;
			} else {
				shape.density = 0.8f;
			}
			// ����Ħ��ϵ��
			shape.friction = 0.0f;
			// ����������ʧ�ʣ�������
			shape.restitution = 0.0f;
			shape.setAsBox(halfWidth / RATE, halfHeight / RATE);
			// ����������������
			BodyDef bodyDef = new BodyDef();
			// ����λ��
			bodyDef.position.set(x / RATE, y / RATE);
			bodyDef.angle = (float) Math.PI;
//			bodyDef.angularDamping = 5;
			// �������д�������
			Body bodyTemp = world.createBody(bodyDef);
			// ָ��������״
			bodyTemp.createShape(shape);
			bodyTemp.setMassFromShapes();

			return new MyRectColor(bodyTemp, halfWidth, halfHeight, bt);
		}
	}

	// ����Բ�Σ���ɫ��
	public static MyCircleColor createCircle(float x, float y, float radius, boolean isStatic, World world, Bitmap bt) {
		synchronized (LockUtil.getInstance().getLock()) {
			// ����Բ��������
			CircleDef shape = new CircleDef();

			if (isStatic) {
				shape.density = 0;
			} else {
				shape.density = 5.0f;
			}
			// �����ܶ�
			// shape.density = 2;
			// ����Ħ��ϵ��
			shape.friction = 0.0f;
			// ����������ʧ�ʣ�������
			shape.restitution = 0.95f;
			// ���ð뾶
			shape.radius = radius / RATE;

			// ����������������
			BodyDef bodyDef = new BodyDef();
			// ����λ��
			bodyDef.position.set(x / RATE, y / RATE);
			// �������д�������
			Body bodyTemp = world.createBody(bodyDef);
			// ָ��������״
			bodyTemp.createShape(shape);
			bodyTemp.setMassFromShapes();
			return new MyCircleColor(bodyTemp, radius, bt);
		}
	}
}