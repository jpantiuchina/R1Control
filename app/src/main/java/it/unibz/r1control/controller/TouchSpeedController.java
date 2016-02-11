package it.unibz.r1control.controller;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import it.unibz.r1control.model.MotorSpeed;
import it.unibz.r1control.model.RobotState;

/**
 * Implements a Speed controller by listening to touch inputs on a given View. This View is split in
 * a left and a right part to set the speed of the left and right wheel, respectively. In each
 * region, we check the y-coordinate in relation to the view's full height. Touching at the bottom
 * will tell the corresponding wheel to turn backward at full speed, while touching at the top will
 * tell it to turn forward at full speed. Similarly, touching at the center will tell the wheel to
 * stop.
 *
 * Created by Matthias on 10.12.2015.
 */
public class TouchSpeedController implements SpeedController, View.OnTouchListener {

    // Bit-sets encoding wheels whose speed is set.
    private static final int LEFT_SPEED_SET  = 0b10;
    private static final int RIGHT_SPEED_SET = 0b01;
    private static final int BOTH_SPEEDS_SET = 0b11;

    private final View touchableArea;

    private int centerX;
    private int centerY;

    private MotorSpeed speed;

    public TouchSpeedController(View touchableArea) {
        this.touchableArea = touchableArea;
        this.speed = RobotState.instance.requestedSpeed();
    }

    @Override
    public void start() {
        touchableArea.setOnTouchListener(this);
    }

    @Override
    public MotorSpeed getRequestedSpeed() {
        return speed;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (centerX == 0 || centerY == 0) {
            centerX = v.getWidth() >>> 1;
            centerY = v.getHeight() >>> 1;
        }
        boolean processed = true;
        int left  = 0;
        int right = 0;
        if (centerX > 0 && centerY > 0 && e.getAction() != MotionEvent.ACTION_UP) {
            int speedsBitSet = 0;
            int count = e.getPointerCount();
            boolean pointerUp = e.getActionMasked() == MotionEvent.ACTION_POINTER_UP;
            for (int i = 0; i < count && speedsBitSet != BOTH_SPEEDS_SET; i++) {
                int dx = (int)e.getX(i) - centerX;
                if (Math.abs(dx) > centerX / 2) {
                    boolean isLeft = dx < 0;
                    int side = isLeft ? LEFT_SPEED_SET : RIGHT_SPEED_SET;
                    if ((side & speedsBitSet) == 0) {
                        speedsBitSet |= side;
                        if (!(pointerUp && i == e.getActionIndex())) {
                            int dy = centerY - (int)e.getY(i);
                            if (Math.abs(dy) <= centerY) {
                                int speed = (byte)(RobotState.instance.maxAbsSpeed() * dy / centerY);
                                if (isLeft) {
                                    Log.d("leftCtrl", "ctrl: " + dy + "->" + speed);
                                    left = speed;
                                }
                                else
                                    right = speed;
                            }
                        }
                    }
                }
                processed = (speedsBitSet & BOTH_SPEEDS_SET) != 0;
            }
        }
        speed.set(left, right);
        return processed;
    }
}
