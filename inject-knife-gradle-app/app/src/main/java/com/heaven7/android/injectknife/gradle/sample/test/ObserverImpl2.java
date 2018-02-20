package com.heaven7.android.injectknife.gradle.sample.test;


import com.heaven7.java.injectknife.InjectObserver;
import com.heaven7.java.injectknife.Insert;
import com.heaven7.java.injectknife.Inserts;

public class ObserverImpl2 implements InjectObserver {
    public static final String TAG = "ObserverImpl2_";

    @Inserts({
            @Insert(from = SimpleTest.class,
                    value = SimpleTest_$InjectService$.FLAG_onDestroy | SimpleTest_$InjectService$.FLAG_onStart)
    })
    public void every() {
        System.out.println(TAG + "every");
    }

    @Insert(SimpleTest_$InjectService$.FLAG_onCreate)
    public void init() {
        System.out.println(TAG + "init");
    }

    @Insert(SimpleTest_$InjectService$.FLAG_onStart)
    public void start() {
        System.out.println(TAG + "start");
    }

    @Insert(SimpleTest_$InjectService$.FLAG_onDestroy)
    public void destroy() {
        System.out.println(TAG + "destroy");
    }

}
