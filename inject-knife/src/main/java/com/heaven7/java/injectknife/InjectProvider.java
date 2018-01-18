package com.heaven7.java.injectknife;

/**
 * the inject provider
 */
public interface InjectProvider {

    /**
     * get the method injector which help we fast inject method.
     * @return the method injector.
     */
    InjectKnife.MethodInjector getInjector();

}
