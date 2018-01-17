package com.heaven7.java.injectknife;

/**
 * the method parameter supplier/provider.
 * Created by heaven7 on 2018/1/17.
 */
public interface InjectParameterSupplier {

    /**
     * get the additional method  parameters.
     * @param provider the inject provider
     * @param observer the inject observer
     * @param methodFlag the method flag
     * @return the additional method  parameters
     */
    Object[] getParameters(InjectProvider provider, InjectObserver observer, int methodFlag);
}
