package com.tekartik.testmenu.example;

import android.text.InputType;

import com.tekartik.testmenu.Test;

public class TextTestMenu extends Test.Menu {

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
