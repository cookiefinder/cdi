package com.tw;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class FushengContainer {

    private final Set<Class<?>> data;

    public FushengContainer(Set<Class<?>> data) {
        this.data = data;
    }

    public static FushengContainer startup(Class<?> primarySource) {
        String packageName = primarySource.getPackage().getName();
        Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
        return new FushengContainer(allClasses);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getComponent(Class<T> clazz) {
        if (data.contains(clazz)) {
            Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
            return Arrays.stream(constructors)
                    .filter(constructorPredicate())
                    .map(constructor -> {
                        try {
                            return constructor.newInstance();
                        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Not found class with " + clazz.getName());
    }

    private Predicate<Constructor<?>> constructorPredicate() {
        return constructor -> Objects.equals(constructor.getParameterCount(), 0);
    }
}
