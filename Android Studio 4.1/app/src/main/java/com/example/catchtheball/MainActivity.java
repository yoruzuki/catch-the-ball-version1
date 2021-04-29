package com.example.catchtheball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private TextView scoreLabel, startLabel;
    private ImageView box, orange, pink, black;

    // サイズ
    private int frameHeight;
    private int boxSize;
    private int screenWidth;

    // 位置
    private float boxY;
    private float orangeX, orangeY;
    private float pinkX, pinkY;
    private float blackX, blackY;

    // スピード
    private int boxSpeed, orangeSpeed, pinkSpeed, blackSpeed;

    // Score
    private int score = 0;

    // Handler & Timer
    private Handler handler = new Handler();
    private Timer timer = new Timer();

    // Status
    private boolean action_flg = false;
    private boolean start_flg = false;

    // Sound
    private SoundPlayer soundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);
        black = findViewById(R.id.black);

        // Screen Size
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        int screenHeight = size.y;

        boxSpeed = Math.round(screenHeight / 60f);
        orangeSpeed = Math.round(screenWidth / 60f);
        pinkSpeed = Math.round(screenWidth / 36f);
        blackSpeed = Math.round(screenWidth / 45f);

        orange.setX(-80.0f);
        orange.setY(-80.0f);
        pink.setX(-80.0f);
        pink.setY(-80.0f);
        black.setX(-80.0f);
        black.setY(-80.0f);

        //scoreLabel.setText("Score : 0");
        scoreLabel.setText(getString(R.string.score, 0));

        Log.v("TEST11", "HelloWorld！");
        Log.v("SIZE", "onCreate boxY: " + box.getY());
    }

    public void changePos() {

        hitCheck();

        // Orange
        orangeX -= orangeSpeed;
        if (orangeX < 0) {
            orangeX = screenWidth + 20;
            orangeY = (float)Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        // Black
        blackX -= blackSpeed;
        if (blackX < 0) {
            blackX = screenWidth + 10;
            blackY = (float)Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        // Pink
        pinkX -= pinkSpeed;
        if (pinkX < 0) {
            pinkX = screenWidth + 5000;
            pinkY = (float)Math.floor(Math.random() * (frameHeight - pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        // Box
        if (action_flg) {
            boxY -= boxSpeed;
        } else {
            boxY += boxSpeed;
        }

        if (boxY < 0) boxY = 0;
        if (boxY > frameHeight - boxSize) boxY = frameHeight - boxSize;

        box.setY(boxY);

        //scoreLabel.setText("Score : " + score);
        scoreLabel.setText(getString(R.string.score, score));
    }

    public void hitCheck() {

        // Orange
        float orangeCenterX = orangeX + orange.getWidth() / 2.0f;
        float orangeCenterY = orangeY + orange.getHeight() / 2.0f;

        if (hitStatus(orangeCenterX, orangeCenterY)) {
            orangeX = -10.0f;
            score += 10;
            soundPlayer.playHitSound();
        }

        // Pink
        float pinkCenterX = pinkX + pink.getWidth() / 2.0f;
        float pinkCenterY = pinkY + pink.getHeight() / 2.0f;

        if (hitStatus(pinkCenterX, pinkCenterY)) {
            pinkX = -10.0f;
            score += 30;
            soundPlayer.playHitSound();
        }

        // Black
        float blackCenterX = blackX + black.getWidth() / 2.0f;
        float blackCenterY = blackY + black.getHeight() / 2.0f;

        if (hitStatus(blackCenterX, blackCenterY)) {

            soundPlayer.playOverSound();

            // Game Over!
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            // 結果画面へ
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }
    }

    public boolean hitStatus(float centerX, float centerY) {
        return (0 <= centerX && centerX <= boxSize &&
                boxY <= centerY && centerY <= boxY + boxSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!start_flg) {

            start_flg = true;

            FrameLayout frame = findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            boxY = box.getY();
            boxSize = box.getHeight();
            Log.v("SIZE", "onTouch boxY: " + box.getY());

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);

        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() { }
}