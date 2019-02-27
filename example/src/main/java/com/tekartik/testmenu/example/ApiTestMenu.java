package com.tekartik.testmenu.example;

import com.tekartik.testmenu.Test;

/**
 * Created by alex on 06/02/18.
 * <p>
 * Test public api
 */

public class ApiTestMenu extends Test.Menu {
    protected ApiTestMenu() {
        super("Api test menu");
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        initItems(
                new Item("getContext()") {
                    @Override
                    public void execute() {
                        showToast("getContext().getPackageName(): " + getContext().getPackageName());
                    }
                },
                new Item("getActivity()") {
                    @Override
                    public void execute() {
                        showToast("getActivity().getTitle(): " + getActivity().getTitle());
                    }
                },
                new Item("getFragment()") {
                    @Override
                    public void execute() {
                        showToast("getFragment().getActivity().getTitle(): " + getFragment().getActivity().getTitle());
                    }
                },
                new Item("back()") {
                    @Override
                    public void execute() {
                        showToast("Should go back to previous menu");
                        back();
                    }
                },
                null

        );

    }

}
