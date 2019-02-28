package com.tekartik.testmenu.example;

import android.util.Log;

import com.tekartik.testmenu.Test;

public class Utils {

    static String getPublicInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Test.TAG: " + Test.TAG + "\n");
        sb.append("Test.BuildConfig.DEBUG: " + Test.BuildConfig.DEBUG);
        return sb.toString();
    }
}
