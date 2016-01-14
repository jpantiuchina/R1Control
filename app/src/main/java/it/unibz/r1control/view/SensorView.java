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

import it.unibz.r1control.controller.robot_control.Consumer;
import it.unibz.r1control.model.data.SensorValues;
import it.unibz.r1control.model.data.UltrasonicData;

/**
 * Created by Matthias on 30.12.2015.
 */
public class SensorView extends View implements Consumer<SensorValues> {

    private final static int SENSOR_RADIUS = 10;
    private final static Point FRONT_POS = new Point(50, 0);
    private final static Point[] US_SIDE_POSITIONS = {
            new Point(-30, 80),
            new Point(-50, 80),
            new Point(-40, 40),
            new Point(-15,  5)
    };

    private final static int WIDTH  = 100;
    private final static int HEIGHT = 80;

    private float scale;
    private float left;
    private float top;

    private final static float ASPECT_RATIO = (float)WIDTH / HEIGHT;

    private final ShapeDrawable[] usShapes;
    private final ShapeDrawable robotShape;
    private final Paint textPaint;
    private boolean hasBounds;

    private SensorValues values;

    public SensorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint();
        usShapes = new ShapeDrawable[SensorValues.US_DATA_COUNT];
        Path robotPath = new Path();
        for (int i = 0; i < SensorValues.US_DATA_COUNT; i++) {
            int x = getX(i);
            int y = getY(i);
            if (i == SensorValues.US_DATA_COUNT / 2)
                robotPath.lineTo(FRONT_POS.x, 0);
            if (i == 0)
                robotPath.moveTo(getX(i), getY(i));
            else
                robotPath.lineTo(x, y);
            Path usPath = new Path();
            usPath.addCircle(x, y, SENSOR_RADIUS, Path.Direction.CW);
            ShapeDrawable usDrawable = new ShapeDrawable(new PathShape(usPath, WIDTH, HEIGHT));
            usDrawable.getPaint().setColor(Color.GREEN);
            usShapes[i] = usDrawable;
        }
        robotPath.close();
        robotShape = new ShapeDrawable(new PathShape(robotPath, WIDTH, HEIGHT));
        robotShape.getPaint().setColor(Color.GRAY);
    }

    private int getX(int i) {
        int l = US_SIDE_POSITIONS.length;
        boolean mirror = i >= l;
        return FRONT_POS.x + (mirror ? -1 : 1) * (US_SIDE_POSITIONS[mirror ? l - 1 - (i - l) : i].x);
    }

    private int getY(int i) {
        int l = US_SIDE_POSITIONS.length;
        boolean mirror = i >= l;
        return US_SIDE_POSITIONS[mirror ? l - 1 - (i - l) : i].y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!hasBounds) {
            hasBounds = true;
            Rect bounds = canvas.getClipBounds();
            int w = bounds.width();
            int h = bounds.height();
            boolean broader = (float)w / h > ASPECT_RATIO;
            scale = broader ? (float)h / HEIGHT : (float)w / WIDTH;
            int sr = (int)(scale * SENSOR_RADIUS);
            int dx = ( broader ? (w - (int)(h * ASPECT_RATIO)) >>> 1 : 0) + sr;
            int dy = (!broader ? (h - (int)(w / ASPECT_RATIO)) >>> 1 : 0) + sr;
            int l = bounds.left + dx;
            int t = bounds.top + dy;
            int r = bounds.right - dx;
            int b = bounds.bottom - dy;
            scale *= broader ? (float)(w - 2 * sr) / w : (float)(h - 2 * sr / h);
            robotShape.setBounds(l, t, r, b);
            for (int i = 0; i < SensorValues.US_DATA_COUNT; i++)
                usShapes[i].setBounds(l, t, r, b);
            left = l;
            top = t;
        }
        robotShape.draw(canvas);
        for (int i = 0; i < SensorValues.US_DATA_COUNT; i++) {
            ShapeDrawable usShape = usShapes[i];
            usShape.draw(canvas);
            if (values != null) {
                //textPaint.setColor(usShape.getPaint().getColor());
                canvas.drawText(String.valueOf(usValue(i)), left + scale * getX(i), top + scale * getY(i), textPaint);
            }
        }
    }

    private int usValue(int i) {
        return values.getUsData(i).getValue();
    }

    @Override
    public void accept(SensorValues values) {
        this.values = values;
        for (int i = 0; i < SensorValues.US_DATA_COUNT; i++) {
            int v = 0xFF * usValue(i) / UltrasonicData.MAX_SAFE_VALUE;
            int color = Color.rgb(1 - v, v, 0);
            usShapes[i].getPaint().setColor(color);
            invalidate();
        }
    }
}
