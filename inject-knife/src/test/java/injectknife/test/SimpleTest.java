package injectknife.test;

import com.heaven7.java.injectknife.*;
import com.heaven7.java.injectknife.internal.InjectService;
import com.heaven7.java.injectknife.internal.ProvideMethod;
import org.junit.Test;

@InjectService(SimpleTest_$InjectService$.class)
public class SimpleTest implements InjectProvider , InjectParameterSupplier{

    private final InjectKnife.MethodInjector injector;

    public SimpleTest() {
        injector = InjectKnife.from(this, new ObserverImpl())
                .withInjectObserver(new ObserverImpl2());
    }

    //最终可以字节码注入
    @Test
    @ProvideMethod
    public void onCreate(){
        getInjector().inject(null);
        System.out.println(InjectProvider.class.isAssignableFrom(getClass()));
    }

    @Test
    @ProvideMethod
    public void onStart(){
        getInjector().inject(null);
    }

    @Test
    @ProvideMethod
    public void onDestroy(){
        getInjector().inject(null);
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
