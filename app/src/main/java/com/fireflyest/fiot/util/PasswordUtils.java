package com.fireflyest.fiot.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PasswordUtils {

    public static String isStrongPassword(String password){
        if(password.length() < 12){
            return "密码长度小于12";
        }

        if(!isContainsStr(password) || !isContainsNum(password)){
            return "密码必须包含字母和数字";
        }

        return "";
    }

    public static boolean isContainsStr(String str) {
        return Pattern.compile(".*[a-zA-Z]+.*").matcher(str).matches();
    }

    public static boolean isContainsNum(String str) {
        return Pattern.compile(".*[0-9]+.*").matcher(str).matches();
    }


}
