package com.soho.utils;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/5/17.
 */
public class NumUtils {

    public static String add(String s1, String s2, int scale) {
        BigDecimal b1 = new BigDecimal(s1);
        BigDecimal b2 = new BigDecimal(s2);
        return add(b1, b2, scale);
    }

    public static String add(BigDecimal b1, BigDecimal b2, int scale) {
        BigDecimal ret = b1.add(b2);
        ret = ret.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
        return ret.toString();
    }

    public static String subtract(String s1, String s2, int scale) {
        BigDecimal b1 = new BigDecimal(s1);
        BigDecimal b2 = new BigDecimal(s2);
        return subtract(b1, b2, scale);
    }

    public static String subtract(BigDecimal b1, BigDecimal b2, int scale) {
        BigDecimal ret = b1.subtract(b2);
        ret = ret.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
        return ret.toString();
    }

    public static String multiply(String s1, String s2, int scale) {
        BigDecimal b1 = new BigDecimal(s1);
        BigDecimal b2 = new BigDecimal(s2);
        return multiply(b1, b2, scale);
    }

    public static String multiply(BigDecimal b1, BigDecimal b2, int scale) {
        BigDecimal ret = b1.multiply(b2);
        ret = ret.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
        return ret.toString();
    }

    public static String divide(String s1, String s2, int scale) {
        BigDecimal b1 = new BigDecimal(s1);
        BigDecimal b2 = new BigDecimal(s2);
        return divide(b1, b2, scale);
    }

    public static String divide(BigDecimal b1, BigDecimal b2, int scale) {
        if (b2.compareTo(new BigDecimal(0)) == 0) {
            return "0";
        }
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_DOWN).toString();
    }

    public static String digitNone(String b1, int digit) {
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

    public static void main(String[] args) {
        System.out.println(digitNone("123.1341", 6));
    }

}
