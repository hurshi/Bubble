package gavin.i_bubble.utils;

public class Person {
	public String name;
	public String password;
	public long score = 0;
	public static long startTime;
	public static long stopTime;
	private static Person instance;

	public Person(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public Person(String name, String password, int score) {
		this(name, password);
		this.score = score;
	}

	public static synchronized Person getInstance() {
		if (instance == null) {
			instance = new Person();
		}
		return instance;
	}

	public Person() {

	}

	public long getScore() {
		return score;
	}

	public void setScore(long score2) {
		this.score = score2;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getStartTime() {
		return startTime;
	}

	public float getStopTime() {
		return stopTime;
	}
}
