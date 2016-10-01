package br.unifor.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        //primitivos
        boolean playing;
        boolean paused = true;
        long fps;
        private long timeThisFrame;

        //objetos
        Thread gameThread = null;
        Canvas canvas;
        Paint paint;
        SurfaceHolder surfaceHolder;


        public Arkanoid(Context context) {
            super(context);
            surfaceHolder = getHolder();
            paint = new Paint();
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

        }

        public void draw() {

            if (surfaceHolder.getSurface().isValid()) {
                canvas = surfaceHolder.lockCanvas();
                canvas.drawColor(Color.argb(1,  255, 255, 255));
                paint.setColor(Color.argb(1,  255, 255, 255));
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
                    break;

                case MotionEvent.ACTION_UP:
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
