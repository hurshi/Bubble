package gavin.i_bubble.activity;

import gavin.i_bubble.R;
import gavin.i_bubble.utils.Person;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GameOverActivity extends Activity implements OnClickListener {
	public static void startActivity(Context context) {
		Intent i = new Intent(context, GameOverActivity.class);
		context.startActivity(i);
	}

	private TextView scoreGot;
	private Button buttonAgain;
	private Button existGame;
	private long score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gameover);
		init();
		getScore();
		// dbWork();
	}

	private void init() {
		scoreGot = (TextView) findViewById(R.id.score_got);
		buttonAgain = (Button) findViewById(R.id.button_startgame_again);
		existGame = (Button) findViewById(R.id.button_exist_game);

		buttonAgain.setOnClickListener(this);
		existGame.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == buttonAgain) {
//			PlayingGameActivity.startActivity(GameOverActivity.this);
			finish();
		}
		if (v == existGame) {
			finish();
		}
	}

	private void getScore() {
		score = 300000 / (Person.stopTime - Person.startTime);
		scoreGot.setText(score + "");
	}

	// private void dbWork() {
	// int scoreFromDB =
	// DatabaseManager.getInstance(this).getScore(Person.getInstance().getName());
	// if (scoreFromDB < score) {
	// Person person = Person.getInstance();
	// person.setScore(score);
	// DatabaseManager.getInstance(this).updateScore(person);
	// }
	// }
}
