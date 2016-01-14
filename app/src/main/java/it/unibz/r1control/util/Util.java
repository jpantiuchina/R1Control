package it.unibz.r1control.util;

/**
 * Created by Matthias on 30.12.2015.
 */
public class Util {
    public static int toInt(byte high, byte low) {
        // http://stackoverflow.com/questions/7401550/how-to-convert-int-to-unsigned-byte-and-back
        int hi = (int)high & 0xFF;
        int lo = (int)low  & 0xFF;
        // merging high and low bytes to 16-bit integer
        return (hi << 8) + lo;
    }
}
