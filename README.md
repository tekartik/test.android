# test.android

Simple test menu for android

## Setup

In your main build.gradle

<pre>
allprojects {
    repositories {
        jcenter()
        <b>maven { url "https://jitpack.io" }</b>
    }
}
</pre>

In your project

<pre>
dependencies {
    <b>compile 'com.github.tekartik:test.android:0.1.0'</b>
}
</pre>

## Usage

Create a simple activity like this

````
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
````

And add it to your manifest

````
<application
    android:allowBackup="true"
    android:label="@string/app_name"
    android:supportsRtl="true"
    android:theme="@style/AppTheme">
    <activity android:name=".MainMenuActivity">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
````