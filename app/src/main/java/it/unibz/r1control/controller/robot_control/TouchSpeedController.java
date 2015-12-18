package it.unibz.r1control.controller.robot_control;

import android.view.MotionEvent;
import android.view.View;

import it.unibz.r1control.model.data.MotorSpeed;

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

    private static final byte STAY = (byte)128;

    // Bitsets encoding wheels whose speed was set
    private static final int LEFT_SPEED_SET  = 0b10;
    private static final int RIGHT_SPEED_SET = 0b01;
    private static final int BOTH_SPEEDS_SET = 0b11;

    private View touchableArea;
    private MotorSpeed requestedSpeed;

    private float center;
    private float height;

    public TouchSpeedController(View touchableArea) {
        this.touchableArea = touchableArea;
        requestedSpeed = new MotorSpeed();
    }

    @Override
    public void start() {
        touchableArea.setOnTouchListener(this);
    }

    @Override
    public MotorSpeed getRequestedSpeed() {
        return requestedSpeed;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (center == 0)
            center = v.getWidth() >>> 2;
        if (height == 0)
            height = v.getHeight();
        requestedSpeed.reset();
        if (center > 0 && height > 0 && e.getAction() != MotionEvent.ACTION_UP) {
            int speedsBitSet = 0;
            int count = e.getPointerCount();
            boolean pointerUp = e.getActionMasked() == MotionEvent.ACTION_POINTER_UP;
            for (int i = 0; i < count && speedsBitSet != BOTH_SPEEDS_SET; i++) {
                byte speed = pointerUp && i == e.getActionIndex()
                    ? MotorSpeed.STAY
                    : (byte)(0xFF * (1 - e.getY(i) / height));
                if (e.getX(i) - center < 0) {
                    speedsBitSet |= LEFT_SPEED_SET;
                    requestedSpeed.setLeftSpeed(speed);
                } else {
                    speedsBitSet |= RIGHT_SPEED_SET;
                    requestedSpeed.setRightSpeed(speed);
                }
            }
        }
        return true;
    }
}
