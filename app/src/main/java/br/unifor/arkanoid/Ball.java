package br.unifor.arkanoid;

import android.graphics.RectF;

import java.util.Random;

/**
 * Created by chico on 01/10/16.
 */

public class Ball {

    RectF rect;

    float xVelocity;
    float yVelocity;
    float ballWidth = 13;
    float ballHeight = 13;

    public Ball(int screenX, int screenY) {

        xVelocity = 200;
        yVelocity = -400;

        rect = new RectF();
    }

    public RectF getRect() {
        return rect;
    }

    public void update(long fps) {
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

    public void reverseYVelocity() {
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity() {
        xVelocity = -xVelocity;
    }

    public void setRandomXVelocity() {
        Random generator = new Random();
        int answer = generator.nextInt(2);
        if (answer == 0) {
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y) {
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void clearObstacleX(float x) {
        rect.left = x;
        rect.right = x + ballWidth;
    }

    public void reset(int x, int y) {
        rect.left = x / 2;
        rect.top = y - 35;
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - 35 - ballHeight;
    }


}
