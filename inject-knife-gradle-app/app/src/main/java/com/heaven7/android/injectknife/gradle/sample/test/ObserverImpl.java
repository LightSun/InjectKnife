package com.heaven7.android.injectknife.gradle.sample.test;


import com.heaven7.java.injectknife.InjectObserver;
import com.heaven7.java.injectknife.Insert;

public class ObserverImpl implements InjectObserver {

    @Insert(SimpleTest_$InjectService$.FLAG_onDestroy | SimpleTest_$InjectService$.FLAG_onStart)
    public void every(){
        System.out.println("ObserverImpl_every");
    }

    @Insert(SimpleTest_$InjectService$.FLAG_onCreate)
    public void init(){
        System.out.println("ObserverImpl_init");
    }

    @Insert(SimpleTest_$InjectService$.FLAG_onStart)
    public void start(){
        System.out.println("ObserverImpl_start");
    }

    @Insert(SimpleTest_$InjectService$.FLAG_onDestroy)
    public void destroy(){
        System.out.println("ObserverImpl_destroy");
    }


}
