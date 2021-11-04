package com.tekartik.testmenu.example;

import android.util.Log;

import com.tekartik.testmenu.Test;

public class SubTestMenu extends Test.Menu {

    protected SubTestMenu() {
        super("Sub menu");
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initItems(
                new Item("showToast") {
                    @Override
                    public void execute() {
                        showToast("Toast from Sub menu");
                    }
                },
                new Item("back()") {
                    @Override
                    public void execute() {
                        // showToast("Should go back to previous menu");
                        back();
                    }
                },

                null
        );

    }
}
