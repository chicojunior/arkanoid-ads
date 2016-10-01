package br.unifor.arkanoid;

import android.graphics.RectF;

/**
 * Created by chico on 30/09/16.
 */

public class Paddle {


    private RectF rect;

    private float length;
    private float height;
    private float x;
    private float y;
    private float paddleSpeed;

    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    private int paddleMoving = STOPPED;


    public Paddle(int screenX, int screenY) {
        length = 130;
        height = 20;

        x = screenX / 2;
        y = screenY - 35;

        rect = new RectF(x, y, x + length, y + height);

        paddleSpeed = 500;
    }


    public RectF getRect() {
        return rect;
    }


    public void setMovementState(int state) {
        paddleMoving = state;
    }


    public void update(long fps) {
        if (paddleMoving == LEFT) {
            x = x - paddleSpeed / fps;
        }

        if (paddleMoving == RIGHT) {
            x = x + paddleSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }

//    public void clearObstacleY(float y) {
//        rect.bottom = y;
//        rect.top = y - height;
//    }

    public void clearObstacleX(float x) {
        rect.left = x;
        rect.right = x + length;
    }

}
