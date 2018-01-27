package com.soho.utils;

import java.math.BigDecimal;

/**
 * Created by shadow on 2017/5/17.
 */
public class NumUtils {

    public static String add(Object s1, Object s2, int scale) {
        BigDecimal b1 = new BigDecimal(s1.toString());
        BigDecimal b2 = new BigDecimal(s2.toString());
        BigDecimal ret = b1.add(b2);
        ret = ret.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
        return ret.toString();
    }

    public static String subtract(Object s1, Object s2, int scale) {
        BigDecimal b1 = new BigDecimal(s1.toString());
        BigDecimal b2 = new BigDecimal(s2.toString());
        BigDecimal ret = b1.subtract(b2);
        ret = ret.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
        return ret.toString();
    }

    public static String multiply(Object s1, Object s2, int scale) {
        BigDecimal b1 = new BigDecimal(s1.toString());
        BigDecimal b2 = new BigDecimal(s2.toString());
        BigDecimal ret = b1.multiply(b2);
        ret = ret.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
        return ret.toString();
    }

    public static String divide(Object s1, Object s2, int scale) {
        BigDecimal b1 = new BigDecimal(s1.toString());
        BigDecimal b2 = new BigDecimal(s2.toString());
        if (b2.compareTo(new BigDecimal(0)) == 0) {
            return "0";
        }
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_DOWN).toString();
    }

    // s1 < s2
    public static boolean compareToLT(Object s1, Object s2) {
        if (new BigDecimal(s1.toString()).compareTo(new BigDecimal(s2.toString())) < 0) {
            return true;
        }
        return false;
    }

    // s1 > s2
    public static boolean compareToGT(Object s1, Object s2) {
        if (new BigDecimal(s1.toString()).compareTo(new BigDecimal(s2.toString())) > 0) {
            return true;
        }
        return false;
    }

    // s1 <= s2
    public static boolean compareToLTE(Object s1, Object s2) {
        if (new BigDecimal(s1.toString()).compareTo(new BigDecimal(s2.toString())) <= 0) {
            return true;
        }
        return false;
    }

    // s1 >= s2
    public static boolean compareToGTE(Object s1, Object s2) {
        if (new BigDecimal(s1.toString()).compareTo(new BigDecimal(s2.toString())) >= 0) {
            return true;
        }
        return false;
    }

    // s1 = s2
    public static boolean compareToEQ(Object s1, Object s2) {
        if (new BigDecimal(s1.toString()).compareTo(new BigDecimal(s2.toString())) == 0) {
            return true;
        }
        return false;
    }

    public static String digitNone(Object number, int digit) {
        String b1 = number.toString();
        if (b1.indexOf(".") != -1) {
            String[] strings = b1.split("\\.");
            if (strings.length >= 2) {
                String s1 = strings[0];
                String s2 = strings[1];
                if (s2.length() > digit) {
                    s2 = s2.substring(0, digit);
                }
                if (!"".equals(s2)) {
                    b1 = s1 + "." + s2;
                } else {
                    b1 = s1;
                }
            }
        }
        return b1;
    }

}
