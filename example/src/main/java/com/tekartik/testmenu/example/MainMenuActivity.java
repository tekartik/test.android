package com.tekartik.testmenu.example;

import android.os.Bundle;
import android.util.Log;

import com.tekartik.testmenu.Test;

public class MainMenuActivity extends Test.MenuActivity {

    static public class MainTestMenu extends Test.Menu {

        protected MainTestMenu() {
            super("Main");
        }

        @Override
        protected void onCreate() {
            super.onCreate();
            Log.i(TAG, "MainTestMenu");
            initItems(
                    new Item("showToast") {
                        @Override
                        public void execute() {
                            showToast("Hi");
                        }
                    },
                    new ActivityItem(MainMenuActivity.class)

            );

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Test.BuildConfig.DEBUG = BuildConfig.DEBUG;
        Test.Menu.setStartMenu(new MainTestMenu());
    }
}
