package com.tekartik.testmenu.example;

import android.content.Intent;
import android.util.Log;

import com.tekartik.testmenu.Test;

public class MainTestMenu extends Test.Menu {

    static int SUB_MENU_REQUEST_CODE = 1;

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
                        showToast(Utils.getPublicInfo());
                    }
                },
                new ActivityItem(MainMenuActivity.class),
                new Item("startActivityForResult") {
                    @Override
                    public void execute() {
                        startActivityForResult(new Intent(getActivity(), BasicActivity.class), SUB_MENU_REQUEST_CODE);
                    }
                },
                new MenuItem(new SubTestMenu()),
                new MenuItem(new TextTestMenu()),
                new MenuItem(new ApiTestMenu()),
                null

        );

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showToast("onActivityResult " + requestCode + " resultCode " + resultCode);
    }
}
