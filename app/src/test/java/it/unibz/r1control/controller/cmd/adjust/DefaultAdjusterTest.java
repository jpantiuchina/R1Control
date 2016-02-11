package it.unibz.r1control.controller.cmd.adjust;

import org.junit.Test;

import it.unibz.r1control.model.MotorSpeed;

import static it.unibz.r1control.model.SensorValues.US_ALL;
import static it.unibz.r1control.model.SensorValues.US_BACK;
import static it.unibz.r1control.model.SensorValues.US_BACK_LEFT;
import static it.unibz.r1control.model.SensorValues.US_BACK_RIGHT;
import static it.unibz.r1control.model.SensorValues.US_FRONT;
import static it.unibz.r1control.model.SensorValues.US_FRONT_LEFT;
import static it.unibz.r1control.model.SensorValues.US_FRONT_RIGHT;
import static it.unibz.r1control.model.SensorValues.US_LEFT;
import static it.unibz.r1control.model.SensorValues.US_RIGHT;
import static org.junit.Assert.assertEquals;

/**
 * Tests the obstacle avoidance
 * Created by Matthias on 06.01.2016.
 */
public class DefaultAdjusterTest {

    private static final int S_I = 2 * DefaultAdjuster.SPEED;
    private static final int S_A = DefaultAdjuster.SPEED;
    private static final int NOT_BL = US_BACK_RIGHT | US_FRONT;
    private static final int NOT_BR = US_BACK_LEFT | US_FRONT;
    private static final int NOT_FL = US_FRONT_RIGHT | US_BACK;
    private static final int NOT_FR = US_FRONT_LEFT | US_BACK;
    private static final int BL_FR = US_BACK_LEFT | US_FRONT_RIGHT;
    private static final int FL_BR = US_FRONT_LEFT | US_BACK_RIGHT;

    private final DefaultAdjuster adjuster = new DefaultAdjuster();
    private final MotorSpeed speed = new MotorSpeed();
    private final MotorSpeed expected = new MotorSpeed();

    private MotorSpeed avoid(int us, MotorSpeed speed) {
        return adjuster.avoid(us, speed, DefaultAdjuster.face(speed));
    }

    @Test
    public void testNoObstacle() {
        assertEquals(expected.move(0), avoid(0, speed.move(0)));
        assertEquals(expected.move(S_I), avoid(0, speed.move(S_I)));
        assertEquals(expected.move(-S_I), avoid(0, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(0, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(0, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleLeft() {
        assertEquals(expected.move(0), avoid(US_LEFT, speed.move(0)));
        assertEquals(expected.move(0), avoid(US_LEFT, speed.move(S_I)));
        assertEquals(expected.move(0), avoid(US_LEFT, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(US_LEFT, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(US_LEFT, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleRight() {
        assertEquals(expected.move(0), avoid(US_RIGHT, speed.move(0)));
        assertEquals(expected.move(0), avoid(US_RIGHT, speed.move(S_I)));
        assertEquals(expected.move(0), avoid(US_RIGHT, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(US_RIGHT, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(US_RIGHT, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleFront() {
        assertEquals(expected.move(-S_A), avoid(US_FRONT, speed.move(0)));
        assertEquals(expected.move(-S_A), avoid(US_FRONT, speed.move(S_I)));
        assertEquals(expected.move(-S_I), avoid(US_FRONT, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(US_FRONT, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(US_FRONT, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleBack() {
        assertEquals(expected.move(S_A), avoid(US_BACK, speed.move(0)));
        assertEquals(expected.move(S_I), avoid(US_BACK, speed.move(S_I)));
        assertEquals(expected.move(S_A), avoid(US_BACK, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(US_BACK, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(US_BACK, speed.turn(-S_I)));
    }

    @Test
     public void testObstacleBackLeft() {
        assertEquals(expected.set(S_A, 0), avoid(US_BACK_LEFT, speed.move(0)));
        assertEquals(expected.move(S_I), avoid(US_BACK_LEFT, speed.move(S_I)));
        assertEquals(expected.set(0, S_A), avoid(US_BACK_LEFT, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(US_BACK_LEFT, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(US_BACK_LEFT, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleFrontLeft() {
        assertEquals(expected.set(-S_A, 0), avoid(US_FRONT_LEFT, speed.move(0)));
        assertEquals(expected.set(0, -S_A), avoid(US_FRONT_LEFT, speed.move(S_I)));
        assertEquals(expected.move(-S_I), avoid(US_FRONT_LEFT, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(US_FRONT_LEFT, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(US_FRONT_LEFT, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleFrontRight() {
        assertEquals(expected.set(0, -S_A), avoid(US_FRONT_RIGHT, speed.move(0)));
        assertEquals(expected.set(-S_A, 0), avoid(US_FRONT_RIGHT, speed.move(S_I)));
        assertEquals(expected.move(-S_I), avoid(US_FRONT_RIGHT, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(US_FRONT_RIGHT, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(US_FRONT_RIGHT, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleBackRight() {
        assertEquals(expected.set(0, S_A), avoid(US_BACK_RIGHT, speed.move(0)));
        assertEquals(expected.move(S_I), avoid(US_BACK_RIGHT, speed.move(S_I)));
        assertEquals(expected.set(S_A, 0), avoid(US_BACK_RIGHT, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(US_BACK_RIGHT, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(US_BACK_RIGHT, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleNotBackLeft() {
        assertEquals(expected.set(-S_A, 0), avoid(NOT_BL, speed.move(0)));
        assertEquals(expected.move(0), avoid(NOT_BL, speed.move(S_I)));
        assertEquals(expected.move(0), avoid(NOT_BL, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(NOT_BL, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(NOT_BL, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleNotFrontLeft() {
        assertEquals(expected.set(S_A, 0), avoid(NOT_FL, speed.move(0)));
        assertEquals(expected.move(0), avoid(NOT_FL, speed.move(S_I)));
        assertEquals(expected.move(0), avoid(NOT_FL, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(NOT_FL, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(NOT_FL, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleNotFrontRight() {
        assertEquals(expected.set(0, S_A), avoid(NOT_FR, speed.move(0)));
        assertEquals(expected.move(0), avoid(NOT_FR, speed.move(S_I)));
        assertEquals(expected.move(0), avoid(NOT_FR, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(NOT_FR, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(NOT_FR, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleNotBackRight() {
        assertEquals(expected.set(0, -S_A), avoid(NOT_BR, speed.move(0)));
        assertEquals(expected.move(0), avoid(NOT_BR, speed.move(S_I)));
        assertEquals(expected.move(0), avoid(NOT_BR, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(NOT_BR, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(NOT_BR, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleBackLeftFrontRight() {
        assertEquals(expected.set(S_A, -S_A), avoid(BL_FR, speed.move(0)));
        assertEquals(expected.move(0), avoid(BL_FR, speed.move(S_I)));
        assertEquals(expected.move(0), avoid(BL_FR, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(BL_FR, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(BL_FR, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleFrontLeftBackRight() {
        assertEquals(expected.set(-S_A, S_A), avoid(FL_BR, speed.move(0)));
        assertEquals(expected.move(0), avoid(FL_BR, speed.move(S_I)));
        assertEquals(expected.move(0), avoid(FL_BR, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(FL_BR, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(FL_BR, speed.turn(-S_I)));
    }

    @Test
    public void testObstacleEverywhere() {
        assertEquals(expected.move(0), avoid(US_ALL, speed.move(0)));
        assertEquals(expected.move(0), avoid(US_ALL, speed.move(S_I)));
        assertEquals(expected.move(0), avoid(US_ALL, speed.move(-S_I)));
        assertEquals(expected.turn(S_I), avoid(US_ALL, speed.turn(S_I)));
        assertEquals(expected.turn(-S_I), avoid(US_ALL, speed.turn(-S_I)));
    }

}
