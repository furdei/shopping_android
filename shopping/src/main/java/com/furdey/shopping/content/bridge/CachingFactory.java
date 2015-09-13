package com.furdey.shopping.content.bridge;

/**
 * @author Stepan Furdei
 */
public abstract class CachingFactory<T> implements Factory<T> {

    private T instance;

    @Override
    public T getInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = createInstance();
                }
            }
        }
        return instance;
    }

    protected abstract T createInstance();

    public void reset() {
        instance = null;
    }
}
