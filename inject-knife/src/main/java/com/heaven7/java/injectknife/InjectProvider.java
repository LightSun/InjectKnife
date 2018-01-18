package com.heaven7.java.injectknife;

/**
 * the inject provider. here is a simple demo.
 * <code><pre>
        public class SimpleTest implements InjectProvider , InjectParameterSupplier{
            private final InjectKnife.MethodInjector injector;
            public SimpleTest() {
                  injector = InjectKnife.from(this, new ObserverImpl());
            }
             public void onCreate(){
                getInjector().inject();
             }
             public void onStart(){
                getInjector().inject();
             }
             public void onDestroy(){
                getInjector().inject();
             }
             public InjectKnife.MethodInjector getInjector() {
                return injector;
             }
             {@literal }@Override
             public Object[] getParameters(InjectProvider provider, InjectObserver observer, int methodFlag) {
                 System.out.println("getParameters() : provider = " + provider
                 + " ,methodFlag = " + methodFlag);
                 return new Object[0];
             }
         }

  * </pre></code>
 * @author heaven7.
 */
public interface InjectProvider {

    /**
     * get the method injector which help we fast inject method.
     * @return the method injector.
     */
    InjectKnife.MethodInjector getInjector();

}
