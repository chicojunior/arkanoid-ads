package br.unifor.arkanoid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
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
        Ball ball;
        Brick[] bricks = new Brick[200];
        int numBricks = 0;
        int score = 0;
        int lives = 3;


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
            ball = new Ball(screenX, screenY);
            createBricksAndRestart();
        }


        public void createBricksAndRestart() {
            ball.reset(screenX, screenY);

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            numBricks = 0;

            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 3; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }

            score = 0;
            lives = 3;
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
            ball.update(fps);


            //================ COLISÕES DA BOLA ================

            // brick
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                    }
                }
            }

            // paddle
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);
            }

            // bottom (perde uma vida)
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);

                lives--;

                if (lives == 0) {
                    paused = true;
                    createBricksAndRestart();
                }
            }

            // top
            if (ball.getRect().top < 0) {
                ball.reverseYVelocity();
                ball.clearObstacleY(12);
            }

            // left wall
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
            }

            // right wall
            if (ball.getRect().right > screenX - 10) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);
            }
            //==================================================

            //Pausa o jogo caso não haja mais nenhum brick
            if (score == numBricks * 10) {
                paused = true;
                createBricksAndRestart();
            }

        }

        public void draw() {

            final int RED = Color.argb(255, 238, 36, 54);
            final int WHITE = Color.argb(255, 255, 255, 255);
            final int BLACK = Color.argb(255, 0, 0, 0);

            if (surfaceHolder.getSurface().isValid()) {
                canvas = surfaceHolder.lockCanvas();

                //cor do fundo
                canvas.drawColor(RED);

                //cor do paddle
                paint.setColor(WHITE);
                canvas.drawRect(paddle.getRect(), paint);

                //cor da bola
                paint.setColor(BLACK);
                canvas.drawRect(ball.getRect(), paint);

                //cor do brick
                paint.setColor(WHITE);
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                //cor do painel
                paint.setColor(BLACK);
                paint.setTextSize(40);
                canvas.drawText("Pontos: " + score + "   Vidas: " + lives, 10, 50, paint);

                if (score == numBricks * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("VOCÊ VENCEU!", 10, screenY / 2, paint);
                }

                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("VOCÊ PERDEU!", 10, screenY / 2, paint);
                }

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
            playing = true;
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
