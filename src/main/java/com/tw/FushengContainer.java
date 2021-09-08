package com.tw;

import com.tw.exception.ContainerStartupException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import static com.tw.exception.BusinessExceptionCode.AMBIGUOUS_IMPLEMENTATION_CLASS;
import static com.tw.exception.BusinessExceptionCode.CIRCULAR_REFERENCE;

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
        return getComponent(clazz, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getComponent(Class<T> clazz, List<Class<?>> registerClasses) {
        if (data.contains(clazz)) {
            if (clazz.isInterface()) {
                List<Class<T>> implementationClass = new ArrayList<>();
                for (Class<?> value : data) {
                    if (clazz.isAssignableFrom(value) && !Objects.equals(value, clazz)) {
                        implementationClass.add((Class<T>) value);
                    }
                }
                if (implementationClass.size() < 1) {
                    throw new ContainerStartupException("Not found class with " + clazz.getName());
                }
                return implementationClass.stream()
                        .map(value -> getInstance(value, registerClasses))
                        .collect(Collectors.toList());
            }
            return Collections.singletonList(getInstance(clazz, registerClasses));
        }
        throw new ContainerStartupException("Not found class with " + clazz.getName());
    }

    private <T> T getInstance(Class<T> clazz, List<Class<?>> registerClasses) {
        if (registerClasses.contains(clazz)) {
            throw new ContainerStartupException(CIRCULAR_REFERENCE);
        }
        T instanceByInjectAnnotation = createInstance(clazz, registerClasses, constructorWithInjectAnnotationPredicate());
        if (Objects.nonNull(instanceByInjectAnnotation)) {
            return instanceByInjectAnnotation;
        }
        T instanceByNoArgs = createInstance(clazz, registerClasses, noArgsConstructorPredicate());
        if (Objects.nonNull(instanceByNoArgs)) {
            return instanceByNoArgs;
        }
        throw new ContainerStartupException("Class should specify no args constructor or mark inject annotation");
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<T> clazz, List<Class<?>> registerClasses, Predicate<Constructor<?>> predicate) {
        registerClasses.add(clazz);
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        return Arrays.stream(constructors)
                .filter(predicate)
                .map(constructor -> {
                    try {
                        if (Objects.nonNull(constructor.getAnnotation(Inject.class))) {
                            if (Objects.equals(constructor.getParameterCount(), 0)) {
                                return constructor.newInstance();
                            }
                            return constructor.newInstance(Arrays.stream(constructor.getParameterTypes())
                                    .map(type -> {
                                        List<?> components = getComponent(type, registerClasses);
                                        if (Objects.equals(components.size(), 1)) {
                                            return components.get(0);
                                        }
                                        if (components.size() < 1) {
                                            throw new ContainerStartupException("Not found class with " + type.getName());
                                        }
                                        throw new ContainerStartupException(AMBIGUOUS_IMPLEMENTATION_CLASS);
                                    }).toArray());
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