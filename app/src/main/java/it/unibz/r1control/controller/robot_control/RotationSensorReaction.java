package it.unibz.r1control.controller.robot_control;

import android.util.Log;

import it.unibz.r1control.model.data.MotorSpeed;
import it.unibz.r1control.model.data.SensorValues;
import it.unibz.r1control.model.data.UltrasonicData;

/**
 * An implementation of SensorReaction that looks at the current UltrasonicData. If there are
 * sensors that return a value which {@link UltrasonicData#isTooClose()}, the robot rotates away
 * from the closest value.
 * Created by Matthias on 18.12.2015.
 */
public class RotationSensorReaction implements SensorReaction {

    public static final int ROTATION_SPEED      = 50;
    public static final MotorSpeed ROTATE_LEFT  = createRotation(ROTATION_SPEED);
    public static final MotorSpeed ROTATE_RIGHT = createRotation(-ROTATION_SPEED);

    private static MotorSpeed createRotation(int speed) {
        return new MotorSpeed((byte)(MotorSpeed.STAY - speed), (byte)(MotorSpeed.STAY + speed));
    }

    @Override
    public MotorSpeed adjust(SensorValues values, byte leftSpeed, byte rightSpeed) {
        MotorSpeed res = null;
        if (values != null) {
            int minIndex = -1;
            UltrasonicData minData = null;
            for (int i = 2; i <= 5; i++) {
                UltrasonicData usData = values.getUsData(i);
                if (minData == null || usData.getValue() < minData.getValue()) {
                    minIndex = i;
                    minData = usData;
                }
            }
            if (minData.isTooClose()) {
                res = minIndex <= 3 ? ROTATE_RIGHT : ROTATE_LEFT;
            }
        }
        if (res == null)
            res = new MotorSpeed(leftSpeed, rightSpeed);
        Log.d("adjust", res.toString());
        return res;
    }
}
