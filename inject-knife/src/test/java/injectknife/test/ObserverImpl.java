package injectknife.test;


import com.heaven7.java.injectknife.InjectObserver;
import com.heaven7.java.injectknife.Insert;

public class ObserverImpl implements InjectObserver {

    @Insert(SimpleTest__$Inject$Service.FLAG_onDestroy | SimpleTest__$Inject$Service.FLAG_onStart)
    public void every(){
        System.out.println("every");
    }

    @Insert(SimpleTest__$Inject$Service.FLAG_onCreate)
    public void init(){
        System.out.println("init");
    }

    @Insert(SimpleTest__$Inject$Service.FLAG_onStart)
    public void start(){
        System.out.println("start");
    }

    @Insert(SimpleTest__$Inject$Service.FLAG_onDestroy)
    public void destroy(){
        System.out.println("destroy");
    }

}
