package gavin.i_bubble.utils;

import static gavin.i_bubble.utils.Constant.RATE;

import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import android.graphics.Bitmap;

//生成物理形状的工具类
public class Box2DUtil {

	/*
	 * param x:x坐标 param y: y坐标 param halfWidth:半宽 param:halfHeight: 半高 param isSattic:是否为静止的 param world:世界 param color:颜色
	 */
	public static MyRectColor createBox(float x, float y, float halfWidth, float halfHeight, boolean isStatic, World world, Bitmap bt) {
		synchronized (LockUtil.getInstance().getLock()) {
			// 创建多边形描述对象
			PolygonDef shape = new PolygonDef();
			// 设置密度
			if (isStatic) {
				shape.density = 0;
			} else {
				shape.density = 0.8f;
			}
			// 设置摩擦系数
			shape.friction = 0.0f;
			// 设置能量损失率（反弹）
			shape.restitution = 0.0f;
			shape.setAsBox(halfWidth / RATE, halfHeight / RATE);
			// 创建刚体描述对象
			BodyDef bodyDef = new BodyDef();
			// 设置位置
			bodyDef.position.set(x / RATE, y / RATE);
			bodyDef.angle = (float) Math.PI;
//			bodyDef.angularDamping = 5;
			// 在世界中创建刚体
			Body bodyTemp = world.createBody(bodyDef);
			// 指定刚体形状
			bodyTemp.createShape(shape);
			bodyTemp.setMassFromShapes();

			return new MyRectColor(bodyTemp, halfWidth, halfHeight, bt);
		}
	}

	// 创建圆形（颜色）
	public static MyCircleColor createCircle(float x, float y, float radius, boolean isStatic, World world, Bitmap bt) {
		synchronized (LockUtil.getInstance().getLock()) {
			// 创建圆描述对象
			CircleDef shape = new CircleDef();

			if (isStatic) {
				shape.density = 0;
			} else {
				shape.density = 5.0f;
			}
			// 设置密度
			// shape.density = 2;
			// 设置摩擦系数
			shape.friction = 0.0f;
			// 设置能量损失率（反弹）
			shape.restitution = 0.95f;
			// 设置半径
			shape.radius = radius / RATE;

			// 创建刚体描述对象
			BodyDef bodyDef = new BodyDef();
			// 设置位置
			bodyDef.position.set(x / RATE, y / RATE);
			// 在世界中创建刚体
			Body bodyTemp = world.createBody(bodyDef);
			// 指定刚体形状
			bodyTemp.createShape(shape);
			bodyTemp.setMassFromShapes();
			return new MyCircleColor(bodyTemp, radius, bt);
		}
	}
}