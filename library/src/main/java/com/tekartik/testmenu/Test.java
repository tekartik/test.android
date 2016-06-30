package com.tekartik.testmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 27/06/16.
 */
public class Test {

    static public class BuildConfig {
        // To override by caller if needed
        static public boolean DEBUG = com.tekartik.testmenu.BuildConfig.DEBUG;
    }

    public abstract static class Menu {

        public static final String TAG = "Menu";
        static final String ITEM_ON_START_KEY = "start_item"; // int
        static private Menu sStartMenu;
        boolean firstResume = true;
        private String mName;
        private MenuFragment mFragment;
        private Item[] items;
        private Adapter mAdapter;

        protected Menu(String name) {
            mName = name;
        }

        /**
         * package private
         *
         * @return start menu
         */
        static public Menu getStartMenu() {
            return sStartMenu;
        }

        static public void setStartMenu(Menu startMenu) {
            sStartMenu = startMenu;
        }

        protected Context getContext() {
            return getActivity();
        }

        protected Activity getActivity() {
            return mFragment.getActivity();
        }

        protected String getName() {
            return mName;
        }

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
            Log.i(TAG, "init " + items.length + " items");
            Log.i(TAG, "BuildConfig.DEBUG: " + BuildConfig.DEBUG);

            ArrayList<Item> newItems = new ArrayList<>();

            for (Item item : items) {
                if (item != null) {
                    newItems.add(item);
                }
            }

            if (BuildConfig.DEBUG) {
                newItems.add(0, new Item("Choose auto-start") {

                    @Override
                    public void execute() {
                        getAutoStart();

                    }
                });
            }
            this.items = newItems.toArray(new Item[newItems.size()]);
            mAdapter = new Adapter(this.items);
            getFragment().setListAdapter(mAdapter);
        }

        protected void start(Class<? extends Activity> activityClass) {
            Log.i(TAG, "Starting " + activityClass);
            getActivity().startActivity(new Intent(getActivity(), activityClass));

        }

        private void setAutoStartFrom(SharedPreferences prefs, EditText editText) {
            Editable value = editText.getText();

            SharedPreferences.Editor editor = prefs.edit();

            int defaultItem = -1;
            try {
                defaultItem = Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            editor.putInt(getPrefKey(ITEM_ON_START_KEY), defaultItem);
            editor.commit();
            Log.i(TAG, "Entered: " + value);
        }

        protected void onCreate() {
            Log.d(TAG, "onCreate");
            firstResume = true;

        }

        protected void onDestroy() {
            Log.d(TAG, "onDestroy");
        }

        protected String getPrefKey(String key) {
            return getName() + "-" + key;
        }

        public SharedPreferences getPrefs() {
            return getActivity().getPreferences(0);
        }

        public void getAutoStart() {
            final EditText editText = new EditText(getActivity());
            editText.setSingleLine();
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setHint("menu item index from 1 to " + (items.length - 1));
            final SharedPreferences prefs = getActivity().getPreferences(0);
            int defaultItem = prefs.getInt(getPrefKey(ITEM_ON_START_KEY), -1);
            if (defaultItem >= 0 && defaultItem < items.length) {
                editText.setText(Integer.toString(defaultItem));
                editText.selectAll();
            }
            editText.setImeActionLabel("OK", KeyEvent.KEYCODE_ENTER);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == KeyEvent.KEYCODE_ENTER) {
                        setAutoStartFrom(prefs, editText);
                        handled = true;
                    }
                    return handled;
                }
            });
            new AlertDialog.Builder(getActivity()).setTitle("Auto start").setMessage("Enter auto-start index").setView(editText)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            setAutoStartFrom(prefs, editText);
                        }
                    }).setNegativeButton(android.R.string.cancel, null).show();
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

        public void showBitmap(Bitmap bmp) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageBitmap(bmp);
            showImage(imageView);
        }

        public void showImage(ImageView imageView) {
            Toast toast = new Toast(getActivity());
            toast.setView(imageView);
            toast.show();
        }

        public void showToast(String text) {
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }

        //	@Override
        //	protected void onCreate(Bundle savedInstanceState) {
        //		super.onCreate(savedInstanceState);
        //
        //		// Create the list fragment and add it as our sole content.
        //		Fragment fragment = getFragmentManager().findFragmentById(android.R.id.content);
        //		if (fragment == null) {
        //			list = new ArrayListFragment();
        //			getFragmentManager().beginTransaction().add(android.R.id.content, list).commit();
        //		} else {
        //			list = (ArrayListFragment) fragment;
        //		}
        //	}

        protected void showMenu(Menu menu) {
            getFragment().showMenu(menu);
        }

        protected void back() {
            getFragment().back();
        }

        //	static public class ArrayListFragment extends ListFragment {
        //
        //		public ArrayListFragment() {
        //		}
        //
        //		/** Called when the activity is first created. */
        //		@Override
        //		public void onActivityCreated(Bundle savedInstanceState) {
        //			super.onCreate(savedInstanceState);
        //
        //			Log.v(TAG, "onCreate");
        //
        //			// BaseMenuActivity activity = (BaseMenuActivity)getActivity();
        //
        //		}
        //
        //		@Override
        //		public void onListItemClick(ListView l, View v, int position, long id) {
        //			Item item = ((Adapter) getListAdapter()).getItem(position);
        //			Log.i(TAG, String.format("Test Item: %d", position));
        //			item.execute();
        //		}
        //	}

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.i(TAG, "request " + requestCode + " result " + resultCode); // + " data " + BundleUtils.toString(data));
        }

        public void startActivityForResult(Intent intent, int requestCode) {
            //getActivity().startActivityForResult(intent, requestCode);
            getFragment().startActivityForResult(intent, requestCode);
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
                start(activityClass);
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
                TextView label = (TextView) row.findViewById(android.R.id.text1);
                label.setText(position + " " + getItem(position).getName());

                return (row);
            }
        }
    }

    public static class MenuActivity extends Activity {

        public static final String TAG = MenuActivity.class.getSimpleName();

        MenuFragment mMenuFragment;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Create the list fragment and add it as our sole content.
            Fragment fragment = getFragmentManager().findFragmentById(android.R.id.content);
            if (fragment == null) {
                mMenuFragment = new MenuFragment();
                getFragmentManager().beginTransaction().add(android.R.id.content, mMenuFragment).commit();
            } else {
                mMenuFragment = (MenuFragment) fragment;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            //Log.i(TAG, "request " + requestCode + " result " + resultCode + " data " + ((data == null) ? null : data.getExtras()));

            Menu currentMenu = mMenuFragment.getCurrentMenu();
            if (currentMenu != null) {
                currentMenu.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    public static class MenuFragment extends ListFragment {

        static final String TAG = "MenuFragment";

        List<Menu> mMenus = new ArrayList<>();

        boolean mActiveMenuCreated;
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

        private void initMenu(Menu menu) {

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

            Menu startMenu = Menu.getStartMenu();
            if (startMenu == null) {
                Log.i(Menu.TAG, "Missing start menu");
            } else {
                showMenu(startMenu);
            }

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
            Menu.Item item = ((Menu.Adapter) getListAdapter()).getItem(position);
            Log.d(TAG, String.format("Test Item: %d", position));
            item.execute();
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
