package com.tw;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.inject.Inject;
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

    public <T> List<T> getComponent(Class<T> clazz) {
        if (data.contains(clazz)) {
            return Collections.singletonList(getInstance(clazz));
        }
        throw new RuntimeException("Not found class with " + clazz.getName());
    }

    @SuppressWarnings("unchecked")
    private <T> T getInstance(Class<T> clazz) {
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        T instanceByInjectAnnotation = createInstance(constructors, constructorWithInjectAnnotationPredicate());
        if (Objects.nonNull(instanceByInjectAnnotation)) {
            return instanceByInjectAnnotation;
        }
        T instanceByNoArgs = createInstance(constructors, noArgsConstructorPredicate());
        if (Objects.nonNull(instanceByNoArgs)) {
            return instanceByNoArgs;
        }
        throw new RuntimeException("Class should specify no args constructor or mark inject annotation");
    }

    private <T> T createInstance(Constructor<T>[] constructors, Predicate<Constructor<?>> predicate) {
        return Arrays.stream(constructors)
                .filter(predicate)
                .map(constructor -> {
                    try {
                        if (Objects.nonNull(constructor.getAnnotation(Inject.class))) {
                            if (Objects.equals(constructor.getParameterCount(), 0)) {
                                return constructor.newInstance();
                            }
                            return constructor.newInstance(Arrays.stream(constructor.getParameterTypes())
                                    .map(this::getInstance).toArray());
                        }
                        return constructor.newInstance();
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).findFirst().orElse(null);
    }

    private Predicate<Constructor<?>> constructorWithInjectAnnotationPredicate() {
        return constructor -> Objects.nonNull(constructor.getAnnotation(Inject.class));
    }

    private Predicate<Constructor<?>> noArgsConstructorPredicate() {
        return constructor -> Objects.equals(constructor.getParameterCount(), 0);
    }
}