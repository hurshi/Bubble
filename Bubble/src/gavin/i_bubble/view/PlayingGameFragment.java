package gavin.i_bubble.view;

import static gavin.i_bubble.utils.Constant.*;
import gavin.i_bubble.R;
import gavin.i_bubble.utils.Box2DUtil;
import gavin.i_bubble.utils.ChatManagerUtil;
import gavin.i_bubble.utils.LockUtil;
import gavin.i_bubble.utils.MyBody;
import gavin.i_bubble.utils.MyCircleColor;
import gavin.i_bubble.utils.MyRectColor;
import gavin.i_bubble.utils.MyThread;
import gavin.i_bubble.utils.Person;

import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayingGameFragment extends Fragment {
	private MyThread mt;
	private double b;

	private AABB worldAABB;// 创建 一个管理碰撞的世界
	public World world;
	private Random random = new Random();
	public Bitmap bt;
	public Bitmap duckBt;
	private MyRectColor myWood;
	private MyRectColor myDuck;

	private GameView gv;

	private volatile static PlayingGameFragment uniqueInstance;

	private PlayingGameFragment() {
	}

	public static PlayingGameFragment getInstance() {
		if (uniqueInstance == null) {
			synchronized (PlayingGameFragment.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new PlayingGameFragment();
				}
			}
		}
		return uniqueInstance;
	}

	// 物体列表
	ArrayList<MyBody> bl = new ArrayList<MyBody>();

	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			b = msg.what;
			if (b == -1111111111) {
				gameOver();
			} else {
				// myView.blow_Me = (float) b;
				if ((float) b > 5000) {
					if (ChatManagerUtil.getInstance().getIsGourpOwner()) {// 如果是服务器就直接设置自己的位置
						myWood.getBody().applyForce(new Vec2(0, -1000), new Vec2(-20, 0));
						// // 发送服务器的坐标位置到客户机上去
						// ChatManagerUtil.getInstance().getChatManager().write(("x" + (float) gv.getXPosition() + "#").getBytes());
						// ChatManagerUtil.getInstance().getChatManager().write(("y" + (float) gv.getYPosition() + "#").getBytes());
						// ChatManagerUtil.getInstance().getChatManager().write(("a" + (float) gv.getAngle() + "#").getBytes());
					} else {// 如果不是服务器就把自己收集到的数据发送到服务器上去
						ChatManagerUtil.getInstance().getChatManager().write(("c" + (float) b + "#").getBytes());// 发送数据
					}
				}
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// myView = MyView.getInstance(getActivity());
		// return myView;

		gv = GameView.getInstance(getActivity(), PlayingGameFragment.this);
		return gv;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mt = new MyThread(getActivity(), myHandler);
		mt.start();
		Person.startTime = System.currentTimeMillis();
		bt = BitmapFactory.decodeResource(getResources(), R.drawable.wood);
		duckBt = BitmapFactory.decodeResource(getResources(), R.drawable.duck);
		LockUtil.getInstance().setLock(new byte[0]);

		// 获取屏幕尺寸
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (dm.widthPixels < dm.heightPixels) {
			SCREEN_WIDTH = dm.widthPixels;
			SCREEN_HEIGHT = dm.heightPixels;
		} else {
			SCREEN_WIDTH = dm.heightPixels;
			SCREEN_HEIGHT = dm.widthPixels;
		}
		worldAABB = new AABB();
		// 上下界，以屏幕的左上方为 原点，如果创建的刚体到达屏幕的边缘的话，会停止模拟，注意这里使用的是现实世界的单位
		worldAABB.lowerBound.set(-200.0f, -200.0f);// 左上
		worldAABB.upperBound.set(200.0f, 200.0f);// 右下
		Vec2 gravity = new Vec2(0.0f, 9.8f);
		boolean doSleep = true;
		// 创建世界
		world = new World(worldAABB, gravity, doSleep);

		final int kd = 4;// 宽度或高度
		MyRectColor mrc = Box2DUtil.createBox(SCREEN_WIDTH / 2, SCREEN_HEIGHT - kd / 4, SCREEN_WIDTH / 20, kd / 4, true, world, null);// 下面

		bl.add(mrc);
		float btWidth = SCREEN_WIDTH * 4 / 5;
		float btHeight = btWidth * bt.getHeight() / bt.getWidth();

		myDuck = Box2DUtil.createBox(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 - duckBt.getHeight(), duckBt.getWidth() / 2, duckBt.getHeight() / 2, !ChatManagerUtil.getInstance().getIsGourpOwner(), world,
				duckBt);
		myWood = Box2DUtil.createBox(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, btWidth / 2, btHeight / 2, !ChatManagerUtil.getInstance().getIsGourpOwner(), world, bt);
		MyCircleColor mPoint = Box2DUtil.createCircle(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, bt.getHeight() / 3, false, world, null);

		PrismaticJointDef pjd = new PrismaticJointDef();
		pjd.maxMotorForce = 10;
		pjd.lowerTranslation = -800.0f / RATE;
		pjd.upperTranslation = 800.0f / RATE;
		pjd.enableLimit = true;
		pjd.initialize(world.getGroundBody(), mPoint.getBody(), mPoint.getBody().getWorldCenter(), new Vec2(0, 1));
		PrismaticJoint pj = (PrismaticJoint) world.createJoint(pjd);

		// 创建一个旋转关节的数据实例
		RevoluteJointDef rjd = new RevoluteJointDef();
		// 初始化旋转关节数据
		rjd.initialize(myWood.getBody(), mPoint.getBody(), myWood.getBody().getWorldCenter());
		rjd.maxMotorTorque = 1;// 马达的预期最大扭矩
		// rjd.motorSpeed =20;//马达最终扭矩
		// rjd.enableMotor = true;// 启动马达
		// 利用world创建一个旋转关节
		RevoluteJoint rj = (RevoluteJoint) world.createJoint(rjd);

		bl.add(myWood);
		if (ChatManagerUtil.getInstance().getIsGourpOwner()) {
			bl.add(mPoint);
			bl.add(myDuck);
		}
	}

	public void gameOver() {
		Person.stopTime = System.currentTimeMillis();
		// GameOverActivity.startActivity(PlayingGameActivity.this);
		// Toast.makeText(this, "游戏结束", Toast.LENGTH_SHORT).show();
		MyThread.ar.release();
		MyThread.ar = null;
	}

	public void setOtherBlowed(float power) {
		myWood.getBody().applyForce(new Vec2(0, -1000), new Vec2(20, 0));
	}
}
