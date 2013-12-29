package com.as.slotmachine;

/*
 * Игровой автомат.
 */
import java.util.Random;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author
 */
public class MainActivity extends Activity {
	/**
	 * 
	 * @author
	 */
	private static class Music {
		private static MediaPlayer mp = null;

		private static MediaPlayer sp = null;

		public static void play(Context context, int resource) {
			stop(context);
			mp = MediaPlayer.create(context, resource);
			mp.setLooping(true);
			mp.start();
		}

		public static void sound(Context context, int resource) {
			sp = MediaPlayer.create(context, resource);
			sp.start();
		}

		public static void stop(Context context) {
			if (mp != null) {
				mp.stop();
				mp.release();
				mp = null;
			}
		}
	}

	private final static int STANDARD_BET = 5;

	private final static int MAXIMAL_BET = 1000;

	private final static Random PRNG = new Random();

	int bet = 5;

	int balance;

	int imageIdSize, i;

	int FruitCount = 3;

	SharedPreferences sPref;

	TextView tvBet, tvBalance;

	Button btnBetUp, btnBetDown, btnStart;

	/**
	 * Массив сссылок на картинки. fruit00 - Нулевая фишка.
	 */
	private int[] imageId = { R.drawable.fruit00, R.drawable.fruit01,
			R.drawable.fruit02, R.drawable.fruit03, R.drawable.fruit04,
			R.drawable.fruit05, R.drawable.fruit06, R.drawable.fruit07 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * Связывание UI и переменных.
		 */
		imageIdSize = imageId.length;
		btnBetUp = (Button) findViewById(R.id.btnBetUp);
		btnBetDown = (Button) findViewById(R.id.btnBetDown);
		btnStart = (Button) findViewById(R.id.buttonStart);
		tvBet = (TextView) findViewById(R.id.textViewBet);
		tvBalance = (TextView) findViewById(R.id.textViewBalance);
		LoadBalance();
	}

	/**
	 * Нажатие на кнопку повышения ставки.
	 * 
	 * @param v
	 */
	public void btnBetUp_Click(View v) {
		/*
		 * Проверка на максимальную ставку.
		 */
		if (bet + STANDARD_BET < MAXIMAL_BET) {
			bet += STANDARD_BET;
			tvBet.setText("$" + bet);
		} else {
			ShowMessage("Дальше повысить ставку нельзя");
		}
	}

	/**
	 * Нажатие на кнопку понижения ставки.
	 * 
	 * @param v
	 */
	public void btnBetDown_Click(View v) {
		/*
		 * Проверка на минимальную ставку.
		 */
		if (bet - STANDARD_BET > 0) {
			bet -= STANDARD_BET;
			tvBet.setText("$" + bet);
		} else {
			ShowMessage("Дальше понизить ставку нельзя");
		}
	}

	/**
	 * Нажатие на кнопку запуска автомата.
	 * 
	 * @param v
	 */
	public void btnStart_Click(View v) {
		/*
		 * Проверка баланса с вычетом ставки.
		 */
		if (balance - bet >= 0) {
			balance -= bet;
			tvBalance.setText("$" + balance);
			StartGame();
		} else {
			ShowMessage("У вас недостаточно средств!");
			bet = 5;
			tvBet.setText("$" + bet);
		}

	}

	/**
	 * Функция запуска автомата с проверкой выигрыша
	 */
	public void StartGame() {
		int prize;

		Animation ScaleAnim;

		int randBuffer[] = new int[FruitCount];

		ImageView[] ivFruit = { (ImageView) findViewById(R.id.imageView1),
				(ImageView) findViewById(R.id.imageView2),
				(ImageView) findViewById(R.id.imageView3) };

		/*
		 * Создание случайного набора чисел и вывод в виде картинок.
		 */
		for (i = 0; i < FruitCount; i++) {
			randBuffer[i] = PRNG.nextInt(imageIdSize);
			ivFruit[i].setImageResource(imageId[randBuffer[i]]);

			/*
			 * Запуск анимации.
			 */
			ScaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
			ivFruit[i].startAnimation(ScaleAnim);
		}

		/*
		 * Проверка выигрыша и в случае победы вывод сообщения на экран.
		 */
		prize = CheckPrize(randBuffer) * bet;
		if (prize != 0) {
			Music.sound(this, R.raw.slotcoin);
			ShowMessage("Ваш выигрыш: $" + prize);
			AddMoney(prize);
		}
	}

	/**
	 * Функция проверки выигрыша.
	 * 
	 * @param imageNum
	 * 
	 * @return
	 */
	public int CheckPrize(int imageNum[]) {
		if (imageNum[0] == imageNum[1] && imageNum[1] == imageNum[2])
			return imageNum[1] * 2;
		if (imageNum[0] == imageNum[1] || imageNum[1] == imageNum[2])
			return imageNum[1];
		if (imageNum[0] == imageNum[2])
			return imageNum[0];
		return 0;
	}

	/**
	 * Функция вывода сообещния на экран.
	 * 
	 * @param s
	 */
	public void ShowMessage(String s) {
		Toast toast1 = Toast.makeText(getApplicationContext(), s,
				Toast.LENGTH_SHORT);
		toast1.setGravity(Gravity.CENTER, 0, 0);
		toast1.show();
	}

	/**
	 * Добавление денег.
	 * 
	 * @param money
	 */
	public void AddMoney(int money) {
		balance += money;
		tvBalance.setText("$" + balance);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Включение музыки при возврате или заходе в игру.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Music.play(this, R.raw.main);
	}

	/**
	 * Отключение музыки при паузе или выходе.
	 */
	@Override
	protected void onPause() {
		super.onPause();
		SaveBalance(balance);
		Music.stop(this);
	}

	/**
	 * Создание окна выхода из приложения.
	 */
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Выход из приложения")
				.setMessage("Вы действительно хотите выйти?")
				.setNegativeButton("НЕТ", null)
				.setPositiveButton("ДА", new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						/*
						 * Очищает память от данного Activity.
						 */
						finish();
					}
				}).create().show();
	}

	/**
	 * 
	 * @param balance
	 */
	public void SaveBalance(int balance) {
		sPref = getSharedPreferences("FruitMachin_data", MODE_PRIVATE);
		Editor ed = sPref.edit();
		ed.putInt("Balance", balance);
		ed.commit();
	}

	/**
	 * 
	 */
	public void LoadBalance() {
		sPref = getSharedPreferences("FruitMachin_data", MODE_PRIVATE);
		balance = sPref.getInt("Balance", 150);
		tvBalance.setText("$" + balance);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * Операции для выбранного пункта меню.
		 */
		switch (item.getItemId()) {
		case R.id.menu_AddMoney:
			AddMoney(500);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
