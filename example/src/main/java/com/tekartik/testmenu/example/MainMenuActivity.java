package com.tekartik.testmenu.example;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import com.tekartik.testmenu.Test;

public class MainMenuActivity extends Test.MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Test.BuildConfig.DEBUG = BuildConfig.DEBUG;
        Test.Menu.setStartMenu(new MainTestMenu());
    }

    static public class MainTestMenu extends Test.Menu {

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
                            startActivityForResult(new Intent(getActivity(), MainMenuActivity.class), SUB_MENU_REQUEST_CODE);
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

    static public class SubTestMenu extends Test.Menu {

        protected SubTestMenu() {
            super("Sub menu");
        }

        @Override
        protected void onCreate() {
            super.onCreate();
            Log.i(TAG, "MainTestMenu");
            initItems(
                    new Item("showToast") {
                        @Override
                        public void execute() {
                            showToast("Toast from Sub menu");
                        }
                    },
                    null
            );

        }
    }

    static public class TextTestMenu extends Test.Menu {

        String text;

        protected TextTestMenu() {
            super("Text");
        }

        @Override
        protected void onCreate() {
            super.onCreate();
            initItems(
                    new Item("getText") {
                        @Override
                        public void execute() {
                            getText(new Test.TextInputListener() {

                                @Override
                                public void onText(String text) {
                                    showToast("text: " + text);
                                }
                            });
                        }
                    },
                    new Item("getText+") {
                        @Override
                        public void execute() {
                            getText(new Test.TextInputWithCancelListener() {

                                @Override
                                public void onCancel() {
                                    showToast("cancelled");
                                }

                                @Override
                                public void onText(String text) {
                                    showToast("text: " + text);
                                    TextTestMenu.this.text = text;
                                }
                            }, text, "Enter some text", "With some guidance", InputType.TYPE_CLASS_TEXT);
                        }
                    },
                    new Item("getText number") {
                        @Override
                        public void execute() {
                            getText(new Test.TextInputListener() {

                                @Override
                                public void onText(String text) {
                                    showToast("text: " + text);
                                    TextTestMenu.this.text = text;
                                }
                            }, text, "Enter some number", null, InputType.TYPE_CLASS_NUMBER);
                        }
                    },
                    new Item("getText multi line") {
                        @Override
                        public void execute() {
                            getText(new Test.TextInputListener() {

                                @Override
                                public void onText(String text) {
                                    showToast("text: " + text);
                                    TextTestMenu.this.text = text;
                                }
                            }, text, null, null, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        }
                    }

            );

        }

    }
}
