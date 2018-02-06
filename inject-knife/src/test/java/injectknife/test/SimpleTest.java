package injectknife.test;

import com.heaven7.java.injectknife.*;
import com.heaven7.java.injectknife.internal.InjectService;
import com.heaven7.java.injectknife.internal.ProvideMethod;
import org.junit.Test;

@InjectService(SimpleTest__$Inject$Service.class)
public class SimpleTest implements InjectProvider , InjectParameterSupplier{

    private final InjectKnife.MethodInjector injector;

    public SimpleTest() {
        injector = InjectKnife.from(this, new ObserverImpl());
    }

    //最终可以字节码注入
    @Test
    @ProvideMethod
    public void onCreate(){
        getInjector().inject();
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
