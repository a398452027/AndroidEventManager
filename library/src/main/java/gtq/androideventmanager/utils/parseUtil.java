/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtq.androideventmanager.utils;

/**
 *
 * @author laodao
 */
public class parseUtil {

    public static byte parseByte(String sz, int defv) {
        try {
            return Byte.parseByte(sz);
        } catch (Exception e) {
            return (byte)defv;
        }
    }

    public static short parseShort(String sz, int defv) {
        try {
            return Short.parseShort(sz);
        } catch (Exception e) {
            return (short)defv;
        }
    }

    public static int parseInt(String sz, int defv) {
        try {
            return Integer.parseInt(sz);
        } catch (Exception e) {
            return defv;
        }
    }

    public static long parseLong(String sz, long defv) {
        try {
            return Long.parseLong(sz);
        } catch (Exception e) {
            return defv;
        }
    }

    public static float parseFloat(String sz, float defv) {
        try {
            return Float.parseFloat(sz);
        } catch (Exception e) {
            return defv;
        }
    }

    public static double parseDouble(String sz, double defv) {
        try {
            return Double.parseDouble(sz);
        } catch (Exception e) {
            return defv;
        }
    }
}
