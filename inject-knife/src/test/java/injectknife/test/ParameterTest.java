package injectknife.test;

import com.heaven7.java.injectknife.InjectKnife;
import com.heaven7.java.injectknife.InjectProvider;
import com.heaven7.java.injectknife.internal.InjectService;
import com.heaven7.java.injectknife.internal.ProvideMethod;

@InjectService(ParameterTest__$Inject$Service.class)
public class ParameterTest implements InjectProvider {

    public ParameterTest() {
    }

    @Override
    public InjectKnife.MethodInjector getInjector() {
        return null;
    }


    public void testParam1(){

    }
    @ProvideMethod
    public void testParam2(){

    }
    @ProvideMethod
    public void testParam3(){

    }
    public void testParam4(){

    }
}
