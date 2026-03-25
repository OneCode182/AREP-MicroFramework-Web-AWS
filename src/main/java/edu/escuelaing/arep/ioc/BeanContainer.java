package edu.escuelaing.arep.ioc;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeanContainer {

    private final Map<Class<?>, Object> beans = new LinkedHashMap<>();

    public void registerBean(Class<?> beanClass, Object instance) {
        beans.put(beanClass, instance);
    }

    public <T> T getBean(Class<T> beanClass) {
        return beanClass.cast(beans.get(beanClass));
    }

    public boolean containsBean(Class<?> beanClass) {
        return beans.containsKey(beanClass);
    }

    public Collection<Object> allBeans() {
        return beans.values();
    }
}
