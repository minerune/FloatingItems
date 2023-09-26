package me.seetch.floatingitems.util;

public class StringUtil {

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            return false;
        }

        return true;
    }

    public static boolean isFloat(String string) {
        try {
            Float.parseFloat(string);
        } catch (NumberFormatException exception) {
            return false;
        }

        return true;
    }

    public static Integer getInteger(String string) {
        int result;

        try {
            result = Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            return null;
        }

        return result;
    }

    public static Float getFloat(String string) {
        float result;

        try {
            result = Float.parseFloat(string);
        } catch (NumberFormatException exception) {
            return null;
        }

        return result;
    }
}
