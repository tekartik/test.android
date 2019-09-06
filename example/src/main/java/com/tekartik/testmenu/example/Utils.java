package com.tekartik.testmenu.example;

import com.tekartik.testmenu.Test;

public class Utils {

    static String getPublicInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Test.TAG: ").append(Test.TAG).append("\n");
        sb.append("Test.BuildConfig.DEBUG: ").append(Test.BuildConfig.DEBUG);
        return sb.toString();
    }
}
