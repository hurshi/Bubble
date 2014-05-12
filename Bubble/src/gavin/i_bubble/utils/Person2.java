package gavin.i_bubble.utils;

public class Person2 {
	public String name;
	public String password;
	public long score = 0;

	public Person2(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public Person2(String name, String password, int score) {
		this(name, password);
		this.score = score;
	}

	public Person2() {

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
}
