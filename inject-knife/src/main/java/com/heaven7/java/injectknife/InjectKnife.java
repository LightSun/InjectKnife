package com.heaven7.java.injectknife;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InjectKnife {

    private static final WeakHashMap<Class<? extends InjectProvider>, Class<?>> sGenClassMap;

    static {
        sGenClassMap = new WeakHashMap<>();
    }

    public static MethodInjector from(InjectProvider provider){
        return new MethodInjector(provider);
    }

    public static MethodInjector from(InjectProvider provider, InjectObserver observer){
        return from(provider).withInjectObserver(observer);
    }

    public static void clearCache(){
        sGenClassMap.clear();
        MethodInjector.sMethodsMap.clear();
    }

    private static Class<?> getGenClass(InjectProvider registry) {
        Class<? extends InjectProvider> clazz = registry.getClass();
        Class<?> target = sGenClassMap.get(clazz);
        if(target != null){
            return target;
        }else {
            InjectService pro = getInjectService(clazz);
            if (pro == null) {
                throw new NullPointerException();
            }
            target = pro.value();
            sGenClassMap.put(clazz, target);
        }
        return target;
    }

    private static InjectService getInjectService(Class<?> clazz) {
        InjectService pro;
        do{
            pro = clazz.getAnnotation(InjectService.class);
            clazz = clazz.getSuperclass();
        }while (pro == null && !clazz.getName().startsWith("java.")
                && !clazz.getName().startsWith("android."));
        return pro;
    }

    private static void logCallStack() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            System.out.println("index=" + i + "----------------------------------");
            System.out.println("className=" + stackTraceElement.getClassName());
            //System.out.println("fileName=" + stackTraceElement.getFileName());
            System.out.println("methodName=" + stackTraceElement.getMethodName());
           // System.out.println("lineNumber=" + stackTraceElement.getLineNumber());
        }
    }

    public static class MethodInjector {
        private static final String PREX_FLAG = "FLAG_";
        private static final WeakHashMap<Class<?>, List<Method>> sMethodsMap; //insertor, methods
        private final ArrayList<InjectObserver> mObservers;
        private final Class<?> mFlagClass;
        private InjectProvider mProvider;

        static {
            sMethodsMap = new WeakHashMap<>();
        }

        MethodInjector(InjectProvider provider) {
            this.mProvider = provider;
            this.mObservers = new ArrayList<>();
            this.mFlagClass = getGenClass(provider);
        }

        public MethodInjector withInjectObserver(InjectObserver observer){
            addInjectObserver(observer);
            return this;
        }

        public void addInjectObserver(InjectObserver observer){
            if(mObservers.contains(observer)){
                return;
            }
            mObservers.add(observer);
            Class<?> clazz = observer.getClass();
            if(sMethodsMap.get(clazz) == null) {
                List<Method> methods = StreamSupport.stream( Arrays.spliterator(clazz.getMethods()), false)
                        .filter(method -> (method.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC
                                && (method.getAnnotation(Insert.class) != null
                                || method.getAnnotation(Inserts.class) != null)
                        ).collect(Collectors.toList());
                sMethodsMap.put(clazz, methods);
            }
        }
        public void removeInjectObserver(InjectObserver observer){
            mObservers.remove(observer);
        }

        /**
         * 重载问题 (不支持)
         * 方法参数问题
         * 继承问题
         * inject 到多个的问题 (ok)
         */
        public void inject(Object...params){
           // logCallStack();
            final String callMethodName = getCallMethodName();
            int flag;
            try {
                Field field = mFlagClass.getField(PREX_FLAG + callMethodName); //重载问题
                flag = (int) field.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return;
            }
            //filter callMethodName
            mObservers.forEach(o ->
                    sMethodsMap.get(o.getClass()).stream().filter(
                        method -> {
                            Insert insert = method.getAnnotation(Insert.class);
                            if(insert != null){
                                return ( insert.value() & flag)== flag;
                            }
                            Inserts inserts = method.getAnnotation(Inserts.class);
                            for (Insert insert1 : inserts.value()){
                                if(insert1.from() == mProvider.getClass()){
                                    return ( insert1.value() & flag) == flag;
                                }
                            }
                            return false;
                        }
                 ).forEachOrdered(method -> {
                    try {
                        method.invoke(o, params);
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                })
            );
        }

        private static String getCallMethodName(){
            /*
             * 0 is current
             */
            StackTraceElement[] stes = new Throwable().getStackTrace();
            return stes[2].getMethodName();
        }
    }
}
