package gavin.i_bubble.utils;

public class LockUtil {
	private byte[] lock = new byte[0];
	private volatile static LockUtil uniqueInstance;

	private LockUtil() {
	}

	public static LockUtil getInstance() {
		if (uniqueInstance == null) {
			synchronized (LockUtil.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new LockUtil();
				}
			}
		}
		return uniqueInstance;
	}

	public byte[] getLock() {
		return lock;
	}

	public void setLock(byte[] b) {
		this.lock = b;
	}

}
