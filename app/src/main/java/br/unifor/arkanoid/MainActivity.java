package br.unifor.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends Activity {

    Arkanoid arkanoid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        arkanoid = new Arkanoid(this);
        setContentView(arkanoid);
    }

    class Arkanoid extends SurfaceView implements Runnable {

        volatile boolean playing;
        boolean paused = true;
        int screenX;
        int screenY;
        long fps;
        private long timeThisFrame;

        Canvas canvas;
        Paint paint;
        Thread gameThread = null;
        SurfaceHolder surfaceHolder;
        Paddle paddle;


        public Arkanoid(Context context) {
            super(context);
            surfaceHolder = getHolder();
            paint = new Paint();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y;
            paddle = new Paddle(screenX, screenY);
        }

        @Override
        public void run() {
            while (playing) {
                long startFrameTime = System.currentTimeMillis();
                if (!paused) {
                    update();
                }

                draw();

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }

        }

        public void update() {
            paddle.update(fps);
        }

        public void draw() {

            if (surfaceHolder.getSurface().isValid()) {
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.argb(255,  238, 36, 54));
                paint.setColor(Color.argb(255,  255, 255, 255));
                canvas.drawRect(paddle.getRect(), paint);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        public void resume() {
            playing= true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    paused = false;

                    Log.d("Log", "action_down: tela clicada");

                    if (motionEvent.getX() > screenX / 2) {
                        paddle.setMovementState(paddle.RIGHT);
                    } else {
                        paddle.setMovementState(paddle.LEFT);
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    Log.d("Log", "action_up: clique concluído");
                    paddle.setMovementState(paddle.STOPPED);
                    break;

            }
            return true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        arkanoid.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        arkanoid.pause();
    }
}
