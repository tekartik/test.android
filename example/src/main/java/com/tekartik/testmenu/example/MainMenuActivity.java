package com.tekartik.testmenu.example;

import android.os.Bundle;
import android.text.InputType;

import com.tekartik.testmenu.Test;

public class MainMenuActivity extends Test.MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Test.BuildConfig.DEBUG = BuildConfig.DEBUG;
        Test.Menu.setStartMenu(new MainTestMenu());
    }

}
