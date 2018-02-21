package com.heaven7.android.injectknife.gradle.sample.test;

import com.heaven7.java.injectknife.InjectKnife;
import com.heaven7.java.injectknife.InjectObserver;
import com.heaven7.java.injectknife.InjectParameterSupplier;
import com.heaven7.java.injectknife.InjectProvider;
import com.heaven7.java.injectknife.internal.InjectService;
import com.heaven7.java.injectknife.internal.ProvideMethod;

@InjectService(SimpleTest_$InjectService$.class)
public class SimpleTest implements InjectProvider , InjectParameterSupplier{

    private final InjectKnife.MethodInjector injector;

    public SimpleTest() {
        injector = InjectKnife.from(this, new ObserverImpl())
                .withInjectObserver(new ObserverImpl2());
    }

    //最终可以字节码注入
    @ProvideMethod
    public void onCreate(){
    }

    @ProvideMethod
    public void onStart(){
    }

    @ProvideMethod
    public void onDestroy(){
    }
    @Override
    public InjectKnife.MethodInjector getInjector() {
        return injector;
    }

    @Override
    public Object[] getParameters(InjectProvider provider, InjectObserver observer, int methodFlag) {
        System.out.println("getParameters() : provider = " + provider
                + " ,methodFlag = " + methodFlag);
        return new Object[0];
    }

}
