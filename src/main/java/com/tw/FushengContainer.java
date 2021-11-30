package com.tw;

import com.tw.exception.ContainerStartupException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.reflections.Reflections;

import static com.tw.exception.BusinessExceptionCode.AMBIGUOUS_IMPLEMENTATION_CLASS;
import static com.tw.exception.BusinessExceptionCode.CIRCULAR_REFERENCE;
import static org.reflections.scanners.Scanners.SubTypes;

public class FushengContainer {

    private final Set<Class<?>> data;
    private final Map<Class<?>, Object> container = new HashMap<>();

    public FushengContainer(Set<Class<?>> data) {
        this.data = data;
    }

    public static FushengContainer startup(Class<?> primarySource) {
        String packageName = primarySource.getPackage().getName();
        Reflections reflections = new Reflections(packageName, SubTypes.filterResultsBy(c -> true));
        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
        return new FushengContainer(allClasses);
    }

    public <T> List<T> getComponent(Class<T> clazz) {
        return getComponent(clazz, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getComponent(Class<T> clazz, List<Class<?>> scannedClasses) {
        if (container.containsKey(clazz) && Objects.nonNull(clazz.getAnnotation(Singleton.class))) {
            return (List<T>) Collections.singletonList(container.get(clazz));
        }
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
                        .map(value -> getInstance(value, scannedClasses))
                        .collect(Collectors.toList());
            }
            T instance = getInstance(clazz, scannedClasses);
            return Collections.singletonList(instance);
        }
        throw new ContainerStartupException("Not found class with " + clazz.getName());
    }

    @SuppressWarnings("unchecked")
    private <T> T getInstance(Class<T> clazz, List<Class<?>> scannedClasses) {
        if (scannedClasses.contains(clazz)) {
            Object instance = container.get(clazz);
            if (Objects.nonNull(instance)) {
                return (T) instance;
            }
            throw new ContainerStartupException(CIRCULAR_REFERENCE);
        }
        T instanceByInjectAnnotation = createInstance(clazz, scannedClasses, constructorWithInjectAnnotationPredicate());
        if (Objects.nonNull(instanceByInjectAnnotation)) {
            return instanceByInjectAnnotation;
        }
        T instanceByNoArgs = createInstance(clazz, scannedClasses, noArgsConstructorPredicate());
        if (Objects.nonNull(instanceByNoArgs)) {
            return instanceByNoArgs;
        }
        throw new ContainerStartupException("Class should specify no args constructor or mark inject annotation");
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<T> clazz, List<Class<?>> scannedClasses, Predicate<Constructor<?>> predicate) {
        scannedClasses.add(clazz);
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        T instance = Arrays.stream(constructors)
                .filter(predicate)
                .map(constructor -> {
                    try {
                        if (Objects.nonNull(constructor.getAnnotation(Inject.class))) {
                            if (Objects.equals(constructor.getParameterCount(), 0)) {
                                return constructor.newInstance();
                            }
                            return constructor.newInstance(Arrays.stream(constructor.getParameterTypes())
                                    .map(type -> {
                                        List<?> components = getComponent(type, scannedClasses);
                                        if (Objects.equals(components.size(), 1)) {
                                            return components.get(0);
                                        }
                                        if (components.size() < 1) {
                                            throw new ContainerStartupException("Not found class with " + type.getName());
                                        }
                                        Named namedAnnotation = constructor.getAnnotation(Named.class);
                                        if (Objects.nonNull(namedAnnotation)) {
                                            String value = namedAnnotation.value();
                                            return components.stream().filter(component -> {
                                                Named componentNamedAnnotation = component.getClass().getAnnotation(Named.class);
                                                return Objects.nonNull(componentNamedAnnotation) && Objects.equals(componentNamedAnnotation.value(), value);
                                            }).findFirst().orElseThrow(() -> new ContainerStartupException(AMBIGUOUS_IMPLEMENTATION_CLASS));
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
        if (Objects.nonNull(instance)) {
            container.put(instance.getClass(), instance);
        }
        return instance;
    }

    private Predicate<Constructor<?>> constructorWithInjectAnnotationPredicate() {
        return constructor -> Objects.nonNull(constructor.getAnnotation(Inject.class));
    }

    private Predicate<Constructor<?>> noArgsConstructorPredicate() {
        return constructor -> Objects.equals(constructor.getParameterCount(), 0);
    }
}