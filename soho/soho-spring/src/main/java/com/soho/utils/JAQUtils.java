package com.soho.utils;

import com.soho.shiro.utils.SessionUtils;

public class JAQUtils {

    private static final String JAQ = "jaq";

    public static void toStateBySuccess() {
        SessionUtils.setAttribute(JAQ, 1);
    }

    public static void toStateByRemove() {
        SessionUtils.removeAttribute(JAQ);
    }

    public static boolean toStateByValid() {
        if (SessionUtils.getAttribute(JAQ) != null) {
            return true;
        }
        return false;
    }

}
