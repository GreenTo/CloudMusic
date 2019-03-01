package com.example.cloudmusic.util;

public class CommonUtil {

    public String returnGender(int gender) {
        if (gender == 0) {
            return "保密";
        }else if (gender == 1) {
            return "男性";
        }else {
            return "女性";
        }
    }

}
