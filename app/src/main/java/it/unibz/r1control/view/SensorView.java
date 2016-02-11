package it.unibz.r1control.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.view.View;

import it.unibz.r1control.controller.Consumer;
import it.unibz.r1control.model.SensorValues;

/**
 * Visualizes the most recent sensor values.
 * Implements the Consumer Interface for being updated with new SensorValues.
 *
 * Created by Matthias on 30.12.2015.
 */
public class SensorView extends View implements Consumer<SensorValues> {

    private final static int SENSOR_RADIUS = 10;                                    // Radius for circles for ultrasonic
    private final static int WIDTH  = 100;                                          // Total width
    private final static int HEIGHT = 80;                                           // Total height
    private final static float ASPECT_RATIO = (float)WIDTH / HEIGHT;                // Total aspect ratio
    private final static Rect BOUNDS = new Rect(0, 0, WIDTH, HEIGHT);               // Total bounds
    private final static int CENTER_X = BOUNDS.centerX();                           // Center x-coordinate
    private final static int CENTER_Y = BOUNDS.centerY();                           // Center y-coordinate
    private final static float IR_RADIUS = (float)Math.hypot(CENTER_X, CENTER_Y);   // Length of infrared beams


    // Left side of ultrasonic sensor positions relative from center
    private final static Point[] US_SIDE_POSITIONS = {
            new Point(-30, 80),
            new Point(-50, 80),
            new Point(-40, 40),
            new Point(-15,  5)
    };
    // Left side of infrared sensor positions relative from center
    private final static Point[] IR_SIDE_POSITIONS = {new Point(-WIDTH >> 1, SENSOR_RADIUS)};

    private final ShapeDrawable robotShape;     // Robot shape
    private final ShapeDrawable[] usShapes;     // Ultrasonic sensor shapes
    private final ShapeDrawable[] irShapes;     // Infrared sensor beam shapes
    private Paint textPaint;                    // Paint settings for text
    private final DangerVisuals dv;             // DangerVisuals helper

    private float scaleX;   // Ratio of pixels to path user coordinates in x-axis
    private float scaleY;   // Ratio of pixels to path user coordinates in y-axis
    private float left;     // Position of left canvas boundary
    private float top;      // Position of top canvas boundary

    private SensorValues values;    // Most recent sensor values to be displayed

    /** Creates and initializes a new SensorView. */
    public SensorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dv = new DangerVisuals((byte)16);
        usShapes = new ShapeDrawable[SensorValues.US_COUNT];
        irShapes = new ShapeDrawable[SensorValues.IR_COUNT];
        Rect bounds = new Rect(0, 0, WIDTH, HEIGHT);
        float r = Math.min(bounds.centerX(), bounds.centerY());
        for (int i = 0; i < SensorValues.IR_COUNT; i++) {
            boolean mirrored = isMirrored(i, IR_SIDE_POSITIONS);
            Path path = new Path();
            path.moveTo(bounds.centerX(), bounds.centerY());
            path.lineTo(mirrored ? WIDTH : 0, SENSOR_RADIUS << 1);
            path.lineTo(mirrored ? WIDTH - (SENSOR_RADIUS << 1): SENSOR_RADIUS << 1, 0);
            path.close();
            ShapeDrawable irDrawable = new ShapeDrawable(new PathShape(path, WIDTH, HEIGHT));
            irDrawable.getPaint().setShader(dv.radialGradientFor(CENTER_X, CENTER_Y, IR_RADIUS, 0));
            irShapes[i] = irDrawable;
        }
        Path robotPath = new Path();
        for (int i = 0; i < SensorValues.US_COUNT; i++) {
            int x = getUsX(i);
            int y = getUsY(i);
            if (i == 0)
                robotPath.moveTo(getUsX(i), getUsY(i)); // move to first
            else {
                if (i == SensorValues.US_COUNT >>> 1)
                    robotPath.lineTo(CENTER_X, 0);          // FRONT_POS; not a US sensor position
                robotPath.lineTo(x, y);                 // line from previous to current position
            }
            Path path = new Path();
            path.addCircle(x, y, SENSOR_RADIUS, Path.Direction.CW);
            ShapeDrawable usDrawable = new ShapeDrawable(new PathShape(path, WIDTH, HEIGHT));
            usDrawable.getPaint().setColor(dv.colorFor(0));
            usShapes[i] = usDrawable;
        }
        robotPath.close();
        robotShape = new ShapeDrawable(new PathShape(robotPath, WIDTH, HEIGHT));
        robotShape.getPaint().setColor(Color.GRAY);
    }

    /** Indicates whether the point at index i is to be calculated by mirroring. */
    private boolean isMirrored(int i, Point[] points) {
        return isMirrored(i, points.length);
    }

    /** Indicates whether the point at index i is to be calculated by mirroring. */
    private boolean isMirrored(int i, int len) {
        return i >= len;
    }

    /**
     * Returns the raw point for the given index i, which is possibly the point of a mirrored
     * index.
     */
    private Point getPoint(int i, Point[] points, boolean mirror) {
        int len = points.length;
        int iRaw = mirror ? 2 * len - 1 - i : i;
        return points[iRaw];
    }

    /**
     * Returns the raw point for the given index i, which is possibly the point of a mirrored
     * index.
     */
    private Point getPoint(int i, Point[] points) {
        return getPoint(i, points, isMirrored(i, points));
    }

    /** Returns the x-coordinate of the point at the given index. */
    private int getX(int i, Point[] points) {
        boolean mirror = isMirrored(i, points);
        int x = getPoint(i, points, mirror).x;
        return mirror ? CENTER_X - x : CENTER_X + x;
    }

    /** Returns the y-coordinate of the point at the given index. */
    private int getY(int i, Point[] points) {
        return getPoint(i, points).y;
    }

    /** Returns the x-coordinate of the ultrasonic sensor position at the given index. */
    private int getUsX(int i) {
        return getX(i, US_SIDE_POSITIONS);
    }

    /** Returns the y-coordinate of the ultrasonic sensor position at the given index. */
    private int getUsY(int i) {
        return getY(i, US_SIDE_POSITIONS);
    }

    /** Returns the x-coordinate of the infrared sensor (value text) position at the given index. */
    private int getIrX(int i) {
        return getX(i, IR_SIDE_POSITIONS);
    }

    /** Returns the y-coordinate of the infrared sensor (value text) position at the given index. */
    private int getIrY(int i) {
        return getY(i, IR_SIDE_POSITIONS);
    }

    /** Sets the bounds of the shapes to be drawn. */
    private void setBounds(Rect bounds) {
        int w = bounds.width();
        int h = bounds.height();
        boolean broader = (float)w / h > ASPECT_RATIO;
        float scale = broader ? (float)h / HEIGHT : (float)w / WIDTH;
        int sr = (int)(scale * SENSOR_RADIUS);
        int dx = ( broader ? (w - (int)(h * ASPECT_RATIO)) >>> 1 : 0) + sr;
        int dy = (!broader ? (h - (int)(w / ASPECT_RATIO)) >>> 1 : 0) + sr;
        int l = bounds.left + dx;
        int t = bounds.top + dy;
        int r = bounds.right - dx;
        int b = bounds.bottom - dy;
        scaleX = (float)(r - l) / WIDTH;
        scaleY = (float)(b - t) / HEIGHT;
        robotShape.setBounds(l, t, r, b);
        for (int i = 0; i < SensorValues.US_COUNT; i++)
            usShapes[i].setBounds(l, t, r, b);
        for (int i = 0; i < SensorValues.IR_COUNT; i++)
            irShapes[i].setBounds(l, t, r, b);
        Rect textBounds = new Rect();
        textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.getTextBounds("0123456789", 0, 10, textBounds);
        left = l;
        top = t -textBounds.centerY();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (scaleX == 0)
            setBounds(canvas.getClipBounds());
        for (int i = 0; i < SensorValues.IR_COUNT; i++) {
            irShapes[i].draw(canvas);
            if (values != null) {
                String s = String.valueOf(values.getIrData(i).get());
                canvas.drawText(s, left + scaleX * getIrX(i), top + scaleY * getIrY(i), textPaint);
            }
        }
        robotShape.draw(canvas);
        for (int i = 0; i < SensorValues.US_COUNT; i++) {
            usShapes[i].draw(canvas);
            if (values != null) {
                String s = String.valueOf(values.getUsData(i).get());
                canvas.drawText(s, left + scaleX * getUsX(i), top + scaleY * getUsY(i), textPaint);
            }
        }
    }

    @Override
    public void accept(SensorValues values) {
        this.values = values;
        for (int i = 0; i < SensorValues.US_COUNT; i++)
            usShapes[i].getPaint().setColor(dv.colorFor(values.getUsData(i)));
        for (int i = 0; i < SensorValues.IR_COUNT; i++)
            irShapes[i].getPaint().setShader(dv.radialGradientFor(CENTER_X, CENTER_Y, IR_RADIUS, values.getIrData(i)));
        postInvalidate();
    }
}
