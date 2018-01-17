package injectknife.test;

import com.heaven7.java.injectknife.*;
import org.junit.Test;

@InjectService(MainActivity__$Flags.class)
public class MainActivity implements InjectProvider , InjectParameterSupplier{

    private static final String TAG = "MainActivity";

    private final InjectKnife.MethodInjector injector;

    public MainActivity() {
        injector = InjectKnife.from(this, new ObserverImpl());
    }

    //最终可以字节码注入
    @Test
    @ProvideMethod
    public void onCreate(){
        getInjector().inject();
    }

    public void onCreate(int x){
        //不支持重载
        //getInjector().inject(new Class<?>[]{int.class}, x);
    }

    @Test
    @ProvideMethod
    public void onStart(){
        getInjector().inject();
    }
    @Test
    @ProvideMethod
    public void onDestroy(){
        getInjector().inject();
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
