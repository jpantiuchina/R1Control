package it.unibz.r1control.view;

import android.graphics.Color;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import it.unibz.r1control.model.DistanceData;

/**
 * Contains methods for calculating colors and gradients based on danger values in the range [0, 1].
 * These values are linearly mapped to a limited number of shades between green and red.
 *
 * Created by Matthias on 25.01.2016.
 */
public class DangerVisuals {

    /** Specifies the highest shade number. */
    private final int maxShade;
    /** Caches RadialGradients for shades between 0 and maxShade (maxShade+1 objects). */
    private final RadialGradient[] gsCache;

    /**
     * Creates a new DangerVisual for the given number color shades.
     * @param shades The number of shades between red and green to be distinguished.
     */
    public DangerVisuals(byte shades) {
        this.maxShade = shades - 1;
        gsCache = new RadialGradient[shades];
    }

    /**
     * Returns the shade index for the given danger value.
     * @param danger A danger value.
     * @return A shade between 0 and maxShade.
     */
    private int shade(float danger) {
        return (int)(danger * maxShade);
    }

    /**
     * Returns a color between green and red for the given distance value. A high distance will
     * result in a green color, a low distance will result in a red color.
     * @param value A distance value.
     * @return A color value for the given distance.
     */
    public int colorFor(DistanceData value) {
        return colorFor(value.getDanger());
    }

    /**
     * Returns a color for the given danger. A high danger will result in a red color, a low danger
     * will result in a green color.
     * @param danger A danger indicator from 0 to maxShade.
     * @return a color value for the given danger.
     */
    public int colorFor(float danger) {
        return colorForShade(shade(danger));
    }

    /**
     * Returns a color for the given shade index. A high shade index will result in a red color, a
     * low shade index will result in a green color.
     * @param shade A shade index from 0 to maxShade.
     * @return a color value for the given shade.
     */
    public int colorForShade(int shade) {
        int c = shade * 0xFF / maxShade;
        return Color.rgb(c, 0xFF - c, 0);
    }

    /**
     * Returns a Shader for the given distance value based on its danger. The Shader will be a
     * RadialGradient that starts with {@link #colorFor(float)} at the middle of the canvas and ends
     * with white at the boundaries. Uses gsCache for not allocating the same Shader
     * twice.
     * @param value A distance value.
     * @return A Shader fir the given danger
     * @see #shade(float)
     * @see #colorFor(float)
     */
    public RadialGradient radialGradientFor(float x, float y, float r, DistanceData value) {
        return radialGradientFor(x, y, r, value.getDanger());
    }

    /**
     * Returns a Shader for the given danger. The Shader will be a RadialGradient that starts with
     * {@link #colorFor(float)} at the middle of the canvas and ends with white at the boundaries.
     * Uses gsCache for not allocating the same Shader twice.
     * @param danger A danger indicator from 0 to 1.
     * @return A Shader fir the given danger
     * @see #shade(float)
     * @see #colorFor(float)
     */
    public RadialGradient radialGradientFor(float x, float y, float r, float danger) {
        int i = shade(danger);
        RadialGradient res = gsCache[i];
        if (res == null)
            res = gsCache[i] = new RadialGradient(x, y, r, colorForShade(i), Color.WHITE, Shader.TileMode.REPEAT);
        return res;
    }
}
