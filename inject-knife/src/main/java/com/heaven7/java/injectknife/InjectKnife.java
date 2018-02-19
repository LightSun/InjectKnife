package com.heaven7.java.injectknife;

import com.heaven7.java.injectknife.internal.InjectService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * the inject knife
 * @author heaven7
 */
public class InjectKnife {

    private static final WeakHashMap<Class<? extends InjectProvider>, Class<?>> sGenClassMap;

    static {
        sGenClassMap = new WeakHashMap<>();
    }

    /**
     * create method injector from provider.
     * @param provider the inject provider
     * @return  method injector
     */
    public static MethodInjector from(InjectProvider provider){
        return new MethodInjector(provider);
    }
    /**
     * create method injector from provider and parameter supplier
     * @param provider the inject provider
     * @param supplier the parameter supplier
     * @return  method injector
     */
    public static MethodInjector from(InjectProvider provider, InjectParameterSupplier supplier){
        return new MethodInjector(provider, supplier);
    }
    /**
     * create method injector from provider and add target observer.
     * @param provider the inject provider
     * @param observer the inject observer
     * @return  method injector
     */
    public static MethodInjector from(InjectProvider provider, InjectObserver observer){
        return from(provider).withInjectObserver(observer);
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

    /**
     * the method injector.
     * @author heaven7
     */
    public static class MethodInjector {
        private static final String PREX_FLAG = "FLAG_";
        private static final WeakHashMap<Class<?>, List<Method>> sMethodsMap; //insertor, methods

        private final ArrayList<InjectObserver> mObservers;
        private final InjectProvider mProvider;
        private final Class<?> mFlagClass;
        private final InjectParameterSupplier mParamSupplier;

        static {
            sMethodsMap = new WeakHashMap<>();
        }
        MethodInjector(InjectProvider provider){
            this(provider, null);
        }
        MethodInjector(InjectProvider provider, InjectParameterSupplier supplier) {
            this.mProvider = provider;
            this.mObservers = new ArrayList<>();
            this.mFlagClass = getGenClass(provider);
            this.mParamSupplier = supplier == null ? (provider instanceof InjectParameterSupplier ?
                    (InjectParameterSupplier) provider : null): supplier;
        }

        /**
         * convenient method for {@linkplain #addInjectObserver(InjectObserver)}
         * @param observer the inject observer
         * @return this
         */
        public MethodInjector withInjectObserver(InjectObserver observer){
            addInjectObserver(observer);
            return this;
        }

        /**
         * add inject observer
         * @param observer the observer to add
         */
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

        /**
         * remove inject observer
         * @param observer the inject observer
         */
        public void removeInjectObserver(InjectObserver observer){
            mObservers.remove(observer);
        }

        /**
         * inject by target method parameters.
         * @param params the method parameters from provider's method.
         */
        public void inject(Object...params){
           // logCallStack();
            final String callMethodName = getCallMethodName();
            int flag;
            try {
                Field field = mFlagClass.getField(PREX_FLAG + callMethodName);
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
                        invokeImpl(o, method, flag, params);
                })
            );
        }

        private void invokeImpl(InjectObserver o, Method method, int flag, Object[] params) {
            //prefer supplier params.
            final Object[] parameters = mParamSupplier != null ? mParamSupplier.getParameters(
                    mProvider, o, flag) : null;
            try {
                if(parameters == null || parameters.length == 0) {
                    method.invoke(o, params);
                }else{
                    final int expectCount = method.getParameterCount();
                    final int length = parameters.length;
                    if(length == expectCount){
                        method.invoke(o, parameters);
                    }else if(length > expectCount){
                        //enough
                        method.invoke(o, truncate(parameters, expectCount));
                    }else{
                        String msg = "method parameter count is not enough. count is "
                                + length + " ,but expect count is = " + expectCount;
                        if(params == null || params.length ==0){
                            throw new IllegalStateException(msg);
                        }
                        int totalLength = length + params.length;
                        if(totalLength < expectCount){
                            throw new IllegalStateException(msg);
                        }
                        Object[] ps = new Object[expectCount];
                        System.arraycopy(parameters, 0, ps, 0, length);
                        System.arraycopy(params, 0, ps ,length,
                                expectCount - length);
                        method.invoke(o, ps);
                    }
                }
            }catch (IllegalAccessException | IllegalArgumentException
                    |InvocationTargetException e){
                throw new RuntimeException(e);
            }
        }

        private Object[] truncate(Object[] parameters, int expectCount) {
            Object[] dst = new Object[expectCount];
            System.arraycopy(parameters, 0, dst, 0, expectCount);
            return dst;
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
