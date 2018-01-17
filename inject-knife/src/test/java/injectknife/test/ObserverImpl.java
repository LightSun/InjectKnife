package injectknife.test;


import com.heaven7.java.injectknife.InjectObserver;
import com.heaven7.java.injectknife.Insert;

public class ObserverImpl implements InjectObserver {

    @Insert(MainActivity__$Flags.FLAG_onDestroy | MainActivity__$Flags.FLAG_onStart)
    public void every(){
        System.out.println("every");
    }

    @Insert(MainActivity__$Flags.FLAG_onCreate)
    public void init(){
        System.out.println("init");
    }

    @Insert(MainActivity__$Flags.FLAG_onStart)
    public void start(){
        System.out.println("start");
    }

    @Insert(MainActivity__$Flags.FLAG_onDestroy)
    public void destroy(){
        System.out.println("destroy");
    }

}
