package it.unibz.r1control.rotation.robot_control;

import junit.framework.TestCase;

import it.unibz.r1control.controller.robot_control.RotationSensorReaction;
import it.unibz.r1control.model.data.MotorSpeed;
import it.unibz.r1control.model.data.SensorValues;
import it.unibz.r1control.model.data.UltrasonicData;

/**
 * Created by Matthias on 06.01.2016.
 */
public class RotationSensorReactionTest extends TestCase {

    private static final UltrasonicData NEAR = new UltrasonicData((byte)0, (byte)1);
    private static final UltrasonicData FAR  = new UltrasonicData((byte)1, (byte)0);

    private RotationSensorReaction reaction = new RotationSensorReaction();
    private byte speed = MotorSpeed.STAY + 100;

    private static int bitNo(int i) {
        return 1 << i;
    }

    private void test(int obstacleFlags, MotorSpeed expected) {
        SensorValues values = new SensorValues();
        for (int i = 0; i < SensorValues.US_DATA_COUNT; i++)
            values.setUsData(i, ((obstacleFlags & (1 << i)) != 0) ? NEAR : FAR);
        MotorSpeed adjusted = reaction.adjust(values, speed, speed);
        assertEquals(adjusted, expected);
    }

    public void testNoObstacle() {
        test(0, new MotorSpeed(speed, speed));
    }

    public void testObstacleLeft() {
        test(bitNo(3), RotationSensorReaction.ROTATE_RIGHT);
    }

    public void testObstacleRight() {
        test(bitNo(4), RotationSensorReaction.ROTATE_LEFT);
    }
}
