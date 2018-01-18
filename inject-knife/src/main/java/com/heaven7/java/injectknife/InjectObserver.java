package com.heaven7.java.injectknife;

/**
 * the inject observer which will be inject into the provider({@linkplain InjectProvider}) as a callback object.
 * here is a observer demo.
 * <code><pre>
 public class ObserverImpl implements InjectObserver {

   {@literal }@Insert(SimpleTest__$Inject$Service.FLAG_onDestroy | SimpleTest__$Inject$Service.FLAG_onStart)
    public void every(){
        System.out.println("every");
    }
   {@literal }@Insert(SimpleTest__$Inject$Service.FLAG_onCreate)
    public void init(){
        System.out.println("init");
    }
    {@literal }@Insert(SimpleTest__$Inject$Service.FLAG_onStart)
    public void start(){
        System.out.println("start");
    }
    {@literal }@Insert(SimpleTest__$Inject$Service.FLAG_onDestroy)
    public void destroy(){
        System.out.println("destroy");
    }
}

 </pre></code>
 * @author heaven7
 */
public interface InjectObserver {
}
