package com.tekartik.testmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class container for test menu helpers
 */
public class Test {
    static public String TAG = "Test";

    public interface TextInputListener {
        void onText(String text);
    }

    public interface TextInputWithCancelListener extends TextInputListener {
        void onCancel();
    }

    /**
     * Must be access using 'Test.BuildConfig.DEBUG'
     */
    static public class BuildConfig {
        // To override by caller if needed
        static public boolean DEBUG = com.tekartik.testmenu.BuildConfig.DEBUG;
    }

    /**
     * Menu helper
     */
    public abstract static class Menu {

        protected static final String TAG = Test.TAG;
        static final String ITEM_ON_START_KEY = "start_item"; // int
        static private Menu sStartMenu;
        boolean firstResume = true;
        private String mName;
        private MenuFragment mFragment;
        private Item[] items;
        private Adapter mAdapter;
        // Set in showMenu by framework
        private Menu parentMenu;

        protected Menu(String name) {
            mName = name;
        }

        /**
         * package private
         *
         * @return start menu
         */
        static Menu getStartMenu() {
            return sStartMenu;
        }

        /**
         * Set the main start menu
         *
         * @param startMenu
         */
        static public void setStartMenu(Menu startMenu) {
            sStartMenu = startMenu;
        }

        protected Context getContext() {
            return getActivity();
        }

        /**
         * @return the current activity
         */
        protected Activity getActivity() {
            return mFragment.getActivity();
        }

        protected String getName() {
            return mName;
        }

        /**
         * @return the fragment containing the menu
         */
        protected MenuFragment getFragment() {
            return mFragment;

        }

        /**
         * Called by test fragment, before onCreate
         */
        void setMenuFragment(MenuFragment fragment) {
            mFragment = fragment;
        }

        public Adapter getAdapter() {
            return mAdapter;
        }

        protected void initItems(Item... items) {
            // Log.i(TAG, "init " + items.length + " items");
            // Log.i(TAG, "BuildConfig.DEBUG: " + BuildConfig.DEBUG);

            ArrayList<Item> newItems = new ArrayList<>();

            for (Item item : items) {
                if (item != null) {
                    // set parant menu
                    newItems.add(item);
                }
            }

            if (BuildConfig.DEBUG) {
                newItems.add(0, new Item("Choose auto-start") {

                    @Override
                    public void execute() {
                        editAutoStart();

                    }
                });
            }
            this.items = newItems.toArray(new Item[newItems.size()]);
            mAdapter = new Adapter(this.items);
            getFragment().setListAdapter(mAdapter);
        }

        @Deprecated
        protected void start(Class<? extends Activity> activityClass) {
            startActivity(activityClass);

        }

        /**
         * start an activity
         *
         * @param activityClass
         */
        protected void startActivity(Class<? extends Activity> activityClass) {
            Log.i(TAG, "Starting " + activityClass);
            getActivity().startActivity(new Intent(getActivity(), activityClass));
        }

        protected void startActivity(Intent intent) {
            getActivity().startActivity(intent);
        }

        /**
         * start an activity with a request code
         *
         * @param intent
         * @param requestCode
         */
        protected void startActivityForResult(Intent intent, int requestCode) {
            getActivity().startActivityForResult(intent, requestCode);
        }

        private void removeAutoStart() {
            getPrefs().edit().remove(getPrefKey(ITEM_ON_START_KEY)).apply();
            showToast("Auto start removed");
        }

        private Integer getAutoStart() {
            int defaultItem = getPrefs().getInt(getPrefKey(ITEM_ON_START_KEY), -1);
            if (defaultItem < 0) {
                return null;
            }
            return defaultItem;
        }

        /**
         * remove the pref if index is null
         *
         * @param index
         */
        private void setAutoStart(Integer index) {

            // On debug long press on choose, discard the auto start
            if (index != null) {
                if (BuildConfig.DEBUG) {
                    if (index <= 0) {
                        index = null;
                    } else if (index < 0) {
                        index = null;
                    }
                }
            }

            if (index != null) {
                SharedPreferences.Editor editor = getPrefs().edit();
                editor.putInt(getPrefKey(ITEM_ON_START_KEY), index);
                editor.apply();

                String name = null;
                if (index < items.length) {
                    Item item = items[index];
                    name = item.getName();
                }
                showToast("Auto start: " + index + " " + name);

            } else {
                removeAutoStart();
            }


        }

        private void setAutoStartFrom(EditText editText) {
            Editable value = editText.getText();


            Integer index = null;
            try {
                index = Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                // Log.e(TAG, e.getMessage(), e);
            }
            setAutoStart(index);
        }

        protected void onCreate() {
            Log.d(TAG, "onCreate");
            firstResume = true;

        }

        protected void onDestroy() {
            Log.d(TAG, "onDestroy");
        }

        private String getPrefKeyPrefix() {
            if (parentMenu != null) {
                return parentMenu.getPrefKeyPrefix() + "/" + getName();
            } else {
                return getName();
            }
        }

        protected String getPrefKey(String key) {

            return getPrefKeyPrefix() + "-" + key;
        }

        public SharedPreferences getPrefs() {
            return getActivity().getPreferences(0);
        }

        private void editAutoStart() {
            final EditText editText = new EditText(getActivity());
            editText.setSingleLine();
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setHint("menu item index from 1 to " + (items.length - 1));
            int defaultItem = getPrefs().getInt(getPrefKey(ITEM_ON_START_KEY), -1);
            if (defaultItem >= 0 && defaultItem < items.length) {
                editText.setText(Integer.toString(defaultItem));
                editText.selectAll();
            }

            final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle("Auto start").setMessage("Enter auto-start index").setView(editText)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setAutoStartFrom(editText);
                        }
                    }).setNegativeButton(android.R.string.cancel, null).setNeutralButton("none", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeAutoStart();
                        }
                    }).show();
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });
            editText.setImeActionLabel("OK", EditorInfo.IME_ACTION_DONE);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        setAutoStartFrom(editText);
                        dialog.dismiss();
                        handled = true;
                    }
                    return handled;
                }
            });

            editText.requestFocus();
        }

        public void getText(final TextInputListener listener) {
            getText(listener, null, "Enter text", null, InputType.TYPE_CLASS_TEXT);
        }

        // InputType.TYPE_CLASS_NUMBER
        public void getText(final TextInputListener listener, String defaultText, String title, String message, int inputType) {
            final EditText editText = new EditText(getActivity());

            boolean singleLine = (inputType & (InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE | InputType.TYPE_TEXT_FLAG_MULTI_LINE)) == 0;
            if (singleLine) {
                editText.setSingleLine();
            }

            editText.setInputType(inputType);
            if (title != null) {
                editText.setHint(title);
            }
            if (defaultText != null) {
                editText.setText(defaultText);
                editText.selectAll();
            }

            if (title == null) {
                title = "Enter text";
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle(title).setView(editText)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            listener.onText(editText.getText().toString());
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (listener instanceof TextInputWithCancelListener) {
                                ((TextInputWithCancelListener) listener).onCancel();
                            }
                        }
                    });
            if (message != null) {
                builder.setMessage(message);
            }

            final AlertDialog dialog = builder.show();
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });
            if (singleLine) {
                editText.setImeActionLabel("OK", EditorInfo.IME_ACTION_DONE);
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            listener.onText(editText.getText().toString());
                            dialog.dismiss();
                            handled = true;
                        }
                        return handled;
                    }
                });
            }
            editText.requestFocus();
        }

        protected void onResume() {
            Log.d(TAG, "onResume");
            if (firstResume) {
                firstResume = false;
                SharedPreferences prefs = getActivity().getPreferences(0);
                int defaultItem = prefs.getInt(getPrefKey(ITEM_ON_START_KEY), -1);
                Log.i(TAG, String.format("Starting item %d", defaultItem));
                if (defaultItem >= 0 && defaultItem < items.length) {
                    items[defaultItem].execute();
                }
                // editor.commit();
            }
        }

        protected void onPause() {
            Log.d(TAG, "onPause");
        }

        /**
         * Show a bitmap
         *
         * @param bmp
         */
        public void showBitmap(Bitmap bmp) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(bmp);
            showImage(imageView);
        }

        /**
         * Show an image
         *
         * @param imageView
         */
        public void showImage(ImageView imageView) {
            Toast toast = new Toast(getActivity());
            toast.setView(imageView);
            toast.show();
        }

        /**
         * Show a toast
         *
         * @param text
         */
        public void showToast(String text) {
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "[toast] " + text);
        }

        /**
         * Show a sub menu
         *
         * @param menu
         */
        protected void showMenu(Menu menu) {
            menu.parentMenu = this;
            getFragment().showMenu(menu);
        }

        /**
         * back up one level
         */
        protected void back() {
            getFragment().back();
        }


        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.i(TAG, "request " + requestCode + " result " + resultCode); // + " data " + BundleUtils.toString(data));
        }

        @Override
        public String toString() {
            return getName() + (items == null ? "" : " " + items.length + "items");
        }

        public abstract class Item {
            public boolean autoStart;
            String name;

            public Item(String name, boolean autoStart) {
                this.name = name;
                this.autoStart = autoStart;
            }

            public Item(String name) {
                this(name, false);
            }

            public abstract void execute();

            String getName() {
                return name;
            }
        }

        private abstract class ItemBase {

        }

        public class MenuItem extends Item {

            Menu mMenu;

            public MenuItem(Menu menu) {
                super(menu.getName());
                mMenu = menu;
            }

            public MenuItem(String name, Menu menu) {
                super(name);
                mMenu = menu;
            }

            @Override
            public void execute() {
                showMenu(mMenu);

            }

        }

        public class ActivityItem extends Item {

            public Class<? extends Activity> activityClass;

            public ActivityItem(Class<? extends Activity> activityClass) {
                super(activityClass.getSimpleName());
                this.activityClass = activityClass;
            }

            public ActivityItem(String name, Class<? extends Activity> activityClass) {
                super(name);
                this.activityClass = activityClass;
            }

            @Override
            public void execute() {
                // Log.i(TAG, "Starting " + activityClass);
                startActivity(activityClass);
            }
        }

        class Adapter extends ArrayAdapter<Item> {

            public Adapter(Item[] items) {
                super(getActivity(), android.R.layout.simple_list_item_1, items);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                if (row == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                }

                TextView label = row.findViewById(android.R.id.text1);
                Item item = getItem(position);
                label.setText(position + " " + item.getName());
                Integer color = null;
                if (position == 0 && BuildConfig.DEBUG) {
                    color = Color.argb(15, 120, 120, 255);
                } else if (item instanceof MenuItem) {
                    color = Color.argb(15, 120, 190, 120);
                }
                if (color != null) {
                    row.setBackgroundColor(color);
                }

                return (row);
            }
        }
    }

    public static class MenuActivity extends AppCompatActivity {

        public static final String TAG = MenuActivity.class.getSimpleName();

        MenuFragment mMenuFragment;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Create the list fragment and add it as our sole content.
            Fragment fragment = getSupportFragmentManager().findFragmentById(android.R.id.content);
            if (fragment == null) {
                mMenuFragment = new MenuFragment();
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, mMenuFragment).commit();
            } else {
                mMenuFragment = (MenuFragment) fragment;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            //Log.i(TAG, "request " + requestCode + " result " + resultCode + " data " + ((data == null) ? null : data.getExtras()));

            Menu currentMenu = mMenuFragment.getCurrentMenu();
            if (currentMenu != null) {
                currentMenu.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    public static class MenuFragment extends ListFragment {

        static final String TAG = "MenuFragment";

        // Menu stack
        List<Menu> mMenus = new ArrayList<>();

        //boolean mActiveMenuCreated;
        boolean mActiveMenuResumed;

        public Menu getCurrentMenu() {
            if (mMenus.size() > 0) {
                return mMenus.get(mMenus.size() - 1);
            }
            return null;
        }

        private void resumeCurrent() {
            if (!mActiveMenuResumed) {
                if (getCurrentMenu() != null) {
                    getCurrentMenu().onResume();
                    mActiveMenuResumed = true;
                }
            }
        }

        private void pauseCurrent() {
            if (mActiveMenuResumed) {
                if (getCurrentMenu() != null) {
                    getCurrentMenu().onPause();
                    mActiveMenuResumed = false;
                }
            }
        }

        private void initMenu(final Menu menu) {

            if (mMenus.contains(menu)) {
                Log.e(TAG, "Menu already in hierarchy");
                return;
            }
            menu.setMenuFragment(this);

            mMenus.add(menu);

            menu.onCreate();
        }

        void showMenu(Menu menu) {
            initMenu(menu);
            resumeCurrent();
        }

        boolean back() {
            if (mMenus.size() > 1) {
                pauseCurrent();
                if (getCurrentMenu() != null) {
                    getCurrentMenu().onDestroy();
                    mMenus.remove(mMenus.size() - 1);
                    setListAdapter(getCurrentMenu().getAdapter());
                }
                resumeCurrent();
                return true;
            }
            return false;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.d(TAG, "onCreate");
            super.onCreate(savedInstanceState);

            mActiveMenuResumed = false;


        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);


            Menu startMenu = Menu.getStartMenu();
            if (startMenu == null) {
                Log.i(Menu.TAG, "Missing start menu");
            } else {
                showMenu(startMenu);
            }

            getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Menu menu = getCurrentMenu();
                    if (menu != null) {
                        Integer existing = menu.getAutoStart();
                        if (existing == null || position != existing) {
                            menu.setAutoStart(position);
                        } else {
                            menu.setAutoStart(null);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });

        }

        @Override
        public void onResume() {
            Log.d(TAG, "onResume");
            super.onResume();

            View view = getListView();
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    //Log.d(TAG, "keyCode: " + keyCode);
                    if (keyCode == KeyEvent.KEYCODE_BACK && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                        Log.d(TAG, "onKey Back listener is working!!!");
                        return back();
                    } else {
                        return false;
                    }
                }
            });

            Menu currentMenu = getCurrentMenu();
            if (currentMenu != null) {
                currentMenu.onResume();
            }
        }


        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.d(TAG, String.format("Test Item: %d", position));
            Menu.Item item = ((Menu.Adapter) getListAdapter()).getItem(position);
            if (item != null) {
                item.execute();
            } else {
                Toast.makeText(getActivity(), "Test item at " + position + " not found", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            //Log.i(TAG, "request " + requestCode + " result " + resultCode + " data " + ((data == null) ? null : data.getExtras()));

            Menu currentMenu = getCurrentMenu();
            if (currentMenu != null) {
                currentMenu.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
