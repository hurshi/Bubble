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

	private AABB worldAABB;// ���� һ��������ײ������
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

	// �����б�
	ArrayList<MyBody> bl = new ArrayList<MyBody>();

	public Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			b = msg.what;
			if (b == -1111111111) {
				gameOver();
			} else {
				// myView.blow_Me = (float) b;
				if ((float) b > 5000) {
					if (ChatManagerUtil.getInstance().getIsGourpOwner()) {// ����Ƿ�������ֱ�������Լ���λ��
						myWood.getBody().applyForce(new Vec2(0, -1000), new Vec2(-20, 0));
						// // ���ͷ�����������λ�õ��ͻ�����ȥ
						// ChatManagerUtil.getInstance().getChatManager().write(("x" + (float) gv.getXPosition() + "#").getBytes());
						// ChatManagerUtil.getInstance().getChatManager().write(("y" + (float) gv.getYPosition() + "#").getBytes());
						// ChatManagerUtil.getInstance().getChatManager().write(("a" + (float) gv.getAngle() + "#").getBytes());
					} else {// ������Ƿ������Ͱ��Լ��ռ��������ݷ��͵���������ȥ
						ChatManagerUtil.getInstance().getChatManager().write(("c" + (float) b + "#").getBytes());// ��������
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

		// ��ȡ��Ļ�ߴ�
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
		// ���½磬����Ļ�����Ϸ�Ϊ ԭ�㣬��������ĸ��嵽����Ļ�ı�Ե�Ļ�����ֹͣģ�⣬ע������ʹ�õ�����ʵ����ĵ�λ
		worldAABB.lowerBound.set(-200.0f, -200.0f);// ����
		worldAABB.upperBound.set(200.0f, 200.0f);// ����
		Vec2 gravity = new Vec2(0.0f, 9.8f);
		boolean doSleep = true;
		// ��������
		world = new World(worldAABB, gravity, doSleep);

		final int kd = 4;// ��Ȼ�߶�
		MyRectColor mrc = Box2DUtil.createBox(SCREEN_WIDTH / 2, SCREEN_HEIGHT - kd / 4, SCREEN_WIDTH / 20, kd / 4, true, world, null);// ����

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

		// ����һ����ת�ؽڵ�����ʵ��
		RevoluteJointDef rjd = new RevoluteJointDef();
		// ��ʼ����ת�ؽ�����
		rjd.initialize(myWood.getBody(), mPoint.getBody(), myWood.getBody().getWorldCenter());
		rjd.maxMotorTorque = 1;// ����Ԥ�����Ť��
		// rjd.motorSpeed =20;//�������Ť��
		// rjd.enableMotor = true;// �������
		// ����world����һ����ת�ؽ�
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
		// Toast.makeText(this, "��Ϸ����", Toast.LENGTH_SHORT).show();
		MyThread.ar.release();
		MyThread.ar = null;
	}

	public void setOtherBlowed(float power) {
		myWood.getBody().applyForce(new Vec2(0, -1000), new Vec2(20, 0));
	}
}
